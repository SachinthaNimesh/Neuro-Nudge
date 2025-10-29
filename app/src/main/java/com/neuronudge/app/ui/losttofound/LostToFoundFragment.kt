package com.neuronudge.app.ui.losttofound

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.neuronudge.app.R
import com.neuronudge.app.databinding.FragmentLostToFoundBinding
import com.neuronudge.app.data.model.LostItem

/**
 * Fragment for LostToFound feature.
 * Shows list of saved items with search functionality.
 */
class LostToFoundFragment : Fragment() {
    
    private var _binding: FragmentLostToFoundBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: LostToFoundViewModel
    private lateinit var adapter: LostItemAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLostToFoundBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity())[LostToFoundViewModel::class.java]
        
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = LostItemAdapter(
            onItemClick = { item -> showItemDetails(item) },
            onDeleteClick = { item -> confirmDelete(item) },
            onNavigateClick = { item -> navigateToItem(item) }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.allItems.observe(viewLifecycleOwner) { items ->
                        adapter.submitList(items)
                        updateEmptyState(items.isEmpty())
                    }
                } else {
                    viewModel.searchItems(newText).observe(viewLifecycleOwner) { items ->
                        adapter.submitList(items)
                        updateEmptyState(items.isEmpty())
                    }
                }
                return true
            }
        })
    }
    
    private fun observeViewModel() {
        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            if (binding.searchView.query.isEmpty()) {
                adapter.submitList(items)
                updateEmptyState(items.isEmpty())
            }
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
    
    private fun showItemDetails(item: LostItem) {
        val message = """
            Item: ${item.itemName}
            Location: ${item.locationName}
            Coordinates: ${item.getCoordinatesString()}
            Saved: ${item.getFormattedDate()}
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Item Details")
            .setMessage(message)
            .setPositiveButton("Navigate") { _, _ -> navigateToItem(item) }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun confirmDelete(item: LostItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${item.itemName}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteItem(item)
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun navigateToItem(item: LostItem) {
        val uri = Uri.parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            val genericIntent = Intent(Intent.ACTION_VIEW, uri)
            if (genericIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(genericIntent)
            } else {
                Toast.makeText(requireContext(), "No maps app available", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
