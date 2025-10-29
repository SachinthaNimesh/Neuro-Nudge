package com.neuronudge.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neuronudge.app.data.model.LostItem

/**
 * DAO (Data Access Object) for LostItem database operations.
 * Provides methods to insert, query, update, and delete items.
 */
@Dao
interface LostItemDao {
    
    /**
     * Insert a new item into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LostItem): Long
    
    /**
     * Update an existing item
     */
    @Update
    suspend fun update(item: LostItem)
    
    /**
     * Delete an item
     */
    @Delete
    suspend fun delete(item: LostItem)
    
    /**
     * Get all items ordered by timestamp (most recent first)
     */
    @Query("SELECT * FROM lost_items ORDER BY timestamp DESC")
    fun getAllItems(): LiveData<List<LostItem>>
    
    /**
     * Search for items by name (case-insensitive)
     */
    @Query("SELECT * FROM lost_items WHERE LOWER(itemName) LIKE '%' || LOWER(:query) || '%' ORDER BY timestamp DESC")
    fun searchItems(query: String): LiveData<List<LostItem>>
    
    /**
     * Find an item by exact name (case-insensitive, most recent match)
     */
    @Query("SELECT * FROM lost_items WHERE LOWER(itemName) LIKE '%' || LOWER(:itemName) || '%' ORDER BY timestamp DESC LIMIT 1")
    suspend fun findItemByName(itemName: String): LostItem?
    
    /**
     * Delete items older than the specified timestamp
     * Used for 30-day cleanup
     */
    @Query("DELETE FROM lost_items WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOlderThan(cutoffTimestamp: Long): Int
    
    /**
     * Get count of all items
     */
    @Query("SELECT COUNT(*) FROM lost_items")
    suspend fun getItemCount(): Int
}
