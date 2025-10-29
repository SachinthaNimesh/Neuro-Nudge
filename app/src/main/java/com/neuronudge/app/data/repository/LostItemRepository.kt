package com.neuronudge.app.data.repository

import androidx.lifecycle.LiveData
import com.neuronudge.app.data.dao.LostItemDao
import com.neuronudge.app.data.model.LostItem

/**
 * Repository for LostToFound feature.
 * Provides a clean API for data access operations.
 */
class LostItemRepository(private val lostItemDao: LostItemDao) {
    
    /**
     * Get all items as LiveData
     */
    val allItems: LiveData<List<LostItem>> = lostItemDao.getAllItems()
    
    /**
     * Insert a new item
     */
    suspend fun insert(item: LostItem): Long {
        return lostItemDao.insert(item)
    }
    
    /**
     * Update an existing item
     */
    suspend fun update(item: LostItem) {
        lostItemDao.update(item)
    }
    
    /**
     * Delete an item
     */
    suspend fun delete(item: LostItem) {
        lostItemDao.delete(item)
    }
    
    /**
     * Search items by query
     */
    fun searchItems(query: String): LiveData<List<LostItem>> {
        return lostItemDao.searchItems(query)
    }
    
    /**
     * Find item by name (most recent)
     */
    suspend fun findItemByName(itemName: String): LostItem? {
        return lostItemDao.findItemByName(itemName)
    }
    
    /**
     * Delete items older than specified days
     */
    suspend fun deleteOlderThan(days: Int): Int {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return lostItemDao.deleteOlderThan(cutoffTime)
    }
    
    /**
     * Get total item count
     */
    suspend fun getItemCount(): Int {
        return lostItemDao.getItemCount()
    }
}
