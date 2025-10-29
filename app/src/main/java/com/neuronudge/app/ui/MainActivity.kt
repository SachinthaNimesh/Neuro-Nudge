package com.neuronudge.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.neuronudge.app.R
import com.neuronudge.app.data.model.LostItem
import com.neuronudge.app.databinding.ActivityMainBinding
import com.neuronudge.app.service.SoundSanctuaryService
import com.neuronudge.app.ui.losttofound.LostToFoundViewModel
import com.neuronudge.app.util.CommandParser

/**
 * Main Activity for Neuro-Nudge app.
 * Provides tab-based navigation between LostToFound and SoundSanctuary features.
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var lostToFoundViewModel: LostToFoundViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionsResult(permissions)
    }
    
    private val voiceInputLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            
            if (spokenText != null) {
                processVoiceCommand(spokenText)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        // Initialize ViewModels
        lostToFoundViewModel = ViewModelProvider(this)[LostToFoundViewModel::class.java]
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        setupUI()
        observeViewModels()
        checkAndRequestPermissions()
    }
    
    private fun setupUI() {
        // Setup view pager and tabs
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "LostToFound"
                1 -> "SoundSanctuary"
                else -> ""
            }
        }.attach()
        
        // Setup FAB for voice input
        binding.fab.setOnClickListener {
            startVoiceInput()
        }
    }
    
    private fun observeViewModels() {
        lostToFoundViewModel.operationResult.observe(this) { result ->
            when (result) {
                is LostToFoundViewModel.OperationResult.Success -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                is LostToFoundViewModel.OperationResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
                is LostToFoundViewModel.OperationResult.ItemFound -> {
                    showItemFoundDialog(result.item)
                }
                is LostToFoundViewModel.OperationResult.ItemNotFound -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                null -> { /* No operation */ }
            }
            lostToFoundViewModel.clearOperationResult()
        }
    }
    
    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }
    
    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filter { !it.value }
        
        if (deniedPermissions.isNotEmpty()) {
            val message = buildString {
                append("Some features may not work without permissions:\n")
                deniedPermissions.keys.forEach { permission ->
                    when (permission) {
                        Manifest.permission.RECORD_AUDIO -> append("- Voice commands and sound monitoring\n")
                        Manifest.permission.ACCESS_FINE_LOCATION -> append("- Location tracking for items\n")
                        Manifest.permission.POST_NOTIFICATIONS -> append("- Service notifications\n")
                    }
                }
            }
            
            MaterialAlertDialogBuilder(this)
                .setTitle("Permissions Required")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something like: 'I left my keys on the table' or 'Where are my keys?'")
        }
        
        try {
            voiceInputLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice input not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun processVoiceCommand(command: String) {
        val parsed = CommandParser.parseCommand(command)
        
        if (parsed.isQuery) {
            // User is asking for an item's location
            if (parsed.itemName != null) {
                lostToFoundViewModel.findItemByName(parsed.itemName)
            } else {
                Toast.makeText(this, "Could not understand item name", Toast.LENGTH_SHORT).show()
            }
        } else {
            // User is storing an item's location
            if (parsed.itemName != null) {
                getCurrentLocationAndSave(parsed.itemName, parsed.locationName ?: "Unknown location")
            } else {
                Toast.makeText(this, "Could not understand command", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun getCurrentLocationAndSave(itemName: String, locationName: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val item = LostItem(
                    itemName = itemName,
                    locationName = locationName,
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                lostToFoundViewModel.addItem(item)
            } else {
                // If location is null, save with default coordinates
                val item = LostItem(
                    itemName = itemName,
                    locationName = locationName,
                    latitude = 0.0,
                    longitude = 0.0
                )
                lostToFoundViewModel.addItem(item)
                Toast.makeText(this, "Location unavailable, saved without coordinates", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showItemFoundDialog(item: LostItem) {
        val message = """
            Item: ${item.itemName}
            Location: ${item.locationName}
            Coordinates: ${item.getCoordinatesString()}
            Saved: ${item.getFormattedDate()}
        """.trimIndent()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Item Found")
            .setMessage(message)
            .setPositiveButton("Navigate") { _, _ ->
                openMaps(item.latitude, item.longitude)
            }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun openMaps(latitude: Double, longitude: Double) {
        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to generic geo intent
            val genericIntent = Intent(Intent.ACTION_VIEW, uri)
            if (genericIntent.resolveActivity(packageManager) != null) {
                startActivity(genericIntent)
            } else {
                Toast.makeText(this, "No maps app available", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_toggle_sound_sanctuary -> {
                toggleSoundSanctuary()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun toggleSoundSanctuary() {
        val intent = Intent(this, SoundSanctuaryService::class.java)
        intent.action = SoundSanctuaryService.ACTION_START
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        
        Toast.makeText(this, "Sound Sanctuary started", Toast.LENGTH_SHORT).show()
    }
}
