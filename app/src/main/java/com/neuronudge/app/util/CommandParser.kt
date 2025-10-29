package com.neuronudge.app.util

/**
 * Utility class for parsing voice/text commands for LostToFound feature.
 * Uses simple regex and keyword extraction to parse commands like:
 * - "I am leaving my car keys on the kitchen table"
 * - "Where did I leave my car keys?"
 * - "My phone is in the bedroom"
 */
object CommandParser {
    
    // Common location prepositions
    private val locationPrepositions = setOf(
        "on", "in", "at", "under", "near", "by", "beside", "inside",
        "on the", "in the", "at the", "under the", "near the", "by the"
    )
    
    // Common action verbs for storing items
    private val storeVerbs = setOf(
        "leaving", "putting", "placed", "left", "put", "keeping", "stored"
    )
    
    // Common query verbs
    private val queryVerbs = setOf(
        "where", "find", "locate", "search"
    )
    
    /**
     * Result of parsing a command
     */
    data class ParsedCommand(
        val isQuery: Boolean,        // true if asking for location, false if storing
        val itemName: String?,        // extracted item name
        val locationName: String?     // extracted location name (null for queries)
    )
    
    /**
     * Parse a voice/text command
     */
    fun parseCommand(input: String): ParsedCommand {
        val normalized = input.trim().lowercase()
        
        // Check if this is a query or storage command
        val isQuery = queryVerbs.any { normalized.contains(it) }
        
        return if (isQuery) {
            parseQueryCommand(normalized)
        } else {
            parseStoreCommand(normalized)
        }
    }
    
    /**
     * Parse a query command like "Where did I leave my car keys?"
     */
    private fun parseQueryCommand(input: String): ParsedCommand {
        // Remove common query phrases
        var cleaned = input
            .replace("where did i leave", "")
            .replace("where is", "")
            .replace("where are", "")
            .replace("find", "")
            .replace("locate", "")
            .replace("my", "")
            .replace("the", "")
            .replace("?", "")
            .trim()
        
        // The remaining text is likely the item name
        val itemName = cleaned.ifEmpty { null }
        
        return ParsedCommand(
            isQuery = true,
            itemName = itemName,
            locationName = null
        )
    }
    
    /**
     * Parse a store command like "I am leaving my car keys on the kitchen table"
     */
    private fun parseStoreCommand(input: String): ParsedCommand {
        var working = input
        
        // Remove common prefixes
        working = working
            .replace("hey", "")
            .replace("i am", "")
            .replace("i'm", "")
            .replace("my", "")
            .replace("the", "")
        
        // Remove store verbs
        storeVerbs.forEach { verb ->
            working = working.replace(verb, "")
        }
        
        // Try to find location by prepositions
        var itemName: String? = null
        var locationName: String? = null
        
        // Look for location prepositions to split item and location
        for (prep in locationPrepositions.sortedByDescending { it.length }) {
            val index = working.indexOf(prep)
            if (index > 0) {
                // Everything before the preposition is the item
                itemName = working.substring(0, index).trim()
                // Everything after is the location
                locationName = working.substring(index + prep.length).trim()
                break
            }
        }
        
        // If we couldn't parse properly, try to extract what we can
        if (itemName == null && locationName == null) {
            // Just use the whole input as item name
            itemName = working.trim()
        }
        
        return ParsedCommand(
            isQuery = false,
            itemName = itemName?.ifEmpty { null },
            locationName = locationName?.ifEmpty { null }
        )
    }
    
    /**
     * Helper method to validate if a command is well-formed
     */
    fun isValidCommand(input: String): Boolean {
        val parsed = parseCommand(input)
        return parsed.itemName?.isNotEmpty() == true
    }
    
    /**
     * Extract just the item name from any command
     */
    fun extractItemName(input: String): String? {
        return parseCommand(input).itemName
    }
}
