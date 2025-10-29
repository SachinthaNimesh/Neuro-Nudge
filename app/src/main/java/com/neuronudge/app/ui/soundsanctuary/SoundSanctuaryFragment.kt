package com.neuronudge.app.ui.soundsanctuary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.neuronudge.app.databinding.FragmentSoundSanctuaryBinding
import com.neuronudge.app.service.SoundSanctuaryService
import com.neuronudge.app.util.PreferencesHelper

/**
 * Fragment for SoundSanctuary feature.
 * Controls sound monitoring and playback settings.
 */
class SoundSanctuaryFragment : Fragment() {
    
    private var _binding: FragmentSoundSanctuaryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var preferencesHelper: PreferencesHelper
    private var isServiceRunning = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundSanctuaryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesHelper = PreferencesHelper(requireContext())
        
        setupUI()
        loadSettings()
    }
    
    private fun setupUI() {
        // Toggle service button
        binding.btnToggleService.setOnClickListener {
            if (isServiceRunning) {
                stopService()
            } else {
                startService()
            }
        }
        
        // Threshold SeekBar
        binding.seekBarThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val threshold = progress + 30.0  // Range: 30-120 dB
                binding.tvThresholdValue.text = "${threshold.toInt()} dB"
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val threshold = (seekBar?.progress ?: 0) + 30.0
                preferencesHelper.setSoundThreshold(threshold)
            }
        })
        
        // Volume SeekBar
        binding.seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                binding.tvVolumeValue.text = "$progress%"
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val volume = (seekBar?.progress ?: 0) / 100f
                preferencesHelper.setPlaybackVolume(volume)
            }
        })
        
        // Pause/Resume buttons
        binding.btnPause.setOnClickListener {
            val intent = Intent(requireContext(), SoundSanctuaryService::class.java)
            intent.action = SoundSanctuaryService.ACTION_PAUSE_PLAYBACK
            requireContext().startService(intent)
        }
        
        binding.btnResume.setOnClickListener {
            val intent = Intent(requireContext(), SoundSanctuaryService::class.java)
            intent.action = SoundSanctuaryService.ACTION_RESUME_PLAYBACK
            requireContext().startService(intent)
        }
    }
    
    private fun loadSettings() {
        val threshold = preferencesHelper.getSoundThreshold()
        binding.seekBarThreshold.progress = (threshold - 30).toInt()
        binding.tvThresholdValue.text = "${threshold.toInt()} dB"
        
        val volume = preferencesHelper.getPlaybackVolume()
        binding.seekBarVolume.progress = (volume * 100).toInt()
        binding.tvVolumeValue.text = "${(volume * 100).toInt()}%"
    }
    
    private fun startService() {
        val intent = Intent(requireContext(), SoundSanctuaryService::class.java)
        intent.action = SoundSanctuaryService.ACTION_START
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
        
        isServiceRunning = true
        updateServiceButton()
    }
    
    private fun stopService() {
        val intent = Intent(requireContext(), SoundSanctuaryService::class.java)
        intent.action = SoundSanctuaryService.ACTION_STOP
        requireContext().stopService(intent)
        
        isServiceRunning = false
        updateServiceButton()
    }
    
    private fun updateServiceButton() {
        if (isServiceRunning) {
            binding.btnToggleService.text = "Stop Sound Sanctuary"
        } else {
            binding.btnToggleService.text = "Start Sound Sanctuary"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
