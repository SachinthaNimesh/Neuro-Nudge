package com.neuronudge.app.ui.losttofound

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neuronudge.app.data.AppDatabase
import com.neuronudge.app.data.model.LostItem
import com.neuronudge.app.data.repository.LostItemRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for LostToFound feature.
 * Manages UI state and coordinates with repository for data operations.
 */
class LostToFoundViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: LostItemRepository
    val allItems: LiveData<List<LostItem>>
    
    // UI state
    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult
    
    init {
        val lostItemDao = AppDatabase.getDatabase(application).lostItemDao()
        repository = LostItemRepository(lostItemDao)
        allItems = repository.allItems
    }
    
    /**
     * Add a new item to the database
     */
    fun addItem(item: LostItem) {
        viewModelScope.launch {
            try {
                val id = repository.insert(item)
                _operationResult.value = OperationResult.Success("Item saved successfully (ID: $id)")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Failed to save item: ${e.message}")
            }
        }
    }
    
    /**
     * Update an existing item
     */
    fun updateItem(item: LostItem) {
        viewModelScope.launch {
            try {
                repository.update(item)
                _operationResult.value = OperationResult.Success("Item updated successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Failed to update item: ${e.message}")
            }
        }
    }
    
    /**
     * Delete an item
     */
    fun deleteItem(item: LostItem) {
        viewModelScope.launch {
            try {
                repository.delete(item)
                _operationResult.value = OperationResult.Success("Item deleted successfully")
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Failed to delete item: ${e.message}")
            }
        }
    }
    
    /**
     * Search for items
     */
    fun searchItems(query: String): LiveData<List<LostItem>> {
        return repository.searchItems(query)
    }
    
    /**
     * Find an item by name and return result
     */
    fun findItemByName(itemName: String) {
        viewModelScope.launch {
            try {
                val item = repository.findItemByName(itemName)
                if (item != null) {
                    _operationResult.value = OperationResult.ItemFound(item)
                } else {
                    _operationResult.value = OperationResult.ItemNotFound("Item '$itemName' not found")
                }
            } catch (e: Exception) {
                _operationResult.value = OperationResult.Error("Error searching: ${e.message}")
            }
        }
    }
    
    /**
     * Clear operation result (e.g., after showing message)
     */
    fun clearOperationResult() {
        _operationResult.value = null
    }
    
    /**
     * Sealed class for operation results
     */
    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
        data class ItemFound(val item: LostItem) : OperationResult()
        data class ItemNotFound(val message: String) : OperationResult()
    }
}
