package com.neuronudge.app.ui.losttofound

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neuronudge.app.data.model.LostItem
import com.neuronudge.app.databinding.ItemLostItemBinding

/**
 * RecyclerView adapter for LostItem list
 */
class LostItemAdapter(
    private val onItemClick: (LostItem) -> Unit,
    private val onDeleteClick: (LostItem) -> Unit,
    private val onNavigateClick: (LostItem) -> Unit
) : ListAdapter<LostItem, LostItemAdapter.LostItemViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostItemViewHolder {
        val binding = ItemLostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LostItemViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: LostItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class LostItemViewHolder(
        private val binding: ItemLostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: LostItem) {
            binding.itemName.text = item.itemName
            binding.locationName.text = item.locationName
            binding.timestamp.text = item.getFormattedDate()
            
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
            binding.btnNavigate.setOnClickListener { onNavigateClick(item) }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<LostItem>() {
        override fun areItemsTheSame(oldItem: LostItem, newItem: LostItem): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: LostItem, newItem: LostItem): Boolean {
            return oldItem == newItem
        }
    }
}
