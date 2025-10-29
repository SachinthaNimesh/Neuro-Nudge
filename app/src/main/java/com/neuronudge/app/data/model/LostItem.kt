package com.neuronudge.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing an item stored in the LostToFound feature.
 * Stores item name, location details, coordinates, and timestamp.
 */
@Entity(tableName = "lost_items")
data class LostItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Item information
    val itemName: String,
    
    // Location information
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    
    // Timestamp when item was stored
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Returns a human-readable date string
     */
    fun getFormattedDate(): String {
        return Date(timestamp).toString()
    }
    
    /**
     * Returns coordinates as a formatted string
     */
    fun getCoordinatesString(): String {
        return "($latitude, $longitude)"
    }
}
