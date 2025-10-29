package com.neuronudge.app.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.neuronudge.app.data.model.LostItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for Room database operations
 */
@RunWith(AndroidJUnit4::class)
class LostItemDaoTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AppDatabase
    private lateinit var dao: com.neuronudge.app.data.dao.LostItemDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        dao = database.lostItemDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveItem() = runBlocking {
        val item = LostItem(
            itemName = "Test Keys",
            locationName = "Test Location",
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        val id = dao.insert(item)
        assertTrue(id > 0)
        
        val count = dao.getItemCount()
        assertEquals(1, count)
    }
    
    @Test
    fun findItemByName() = runBlocking {
        val item = LostItem(
            itemName = "Car Keys",
            locationName = "Kitchen Table",
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        dao.insert(item)
        
        val found = dao.findItemByName("car keys")
        assertNotNull(found)
        assertEquals("Car Keys", found?.itemName)
        assertEquals("Kitchen Table", found?.locationName)
    }
    
    @Test
    fun deleteOldItems() = runBlocking {
        val oldItem = LostItem(
            itemName = "Old Item",
            locationName = "Old Place",
            latitude = 0.0,
            longitude = 0.0,
            timestamp = System.currentTimeMillis() - (31 * 24 * 60 * 60 * 1000L) // 31 days ago
        )
        
        val newItem = LostItem(
            itemName = "New Item",
            locationName = "New Place",
            latitude = 0.0,
            longitude = 0.0
        )
        
        dao.insert(oldItem)
        dao.insert(newItem)
        
        val cutoffTime = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        val deletedCount = dao.deleteOlderThan(cutoffTime)
        
        assertEquals(1, deletedCount)
        assertEquals(1, dao.getItemCount())
    }
}
