package com.neuronudge.app.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CommandParser
 */
class CommandParserTest {
    
    @Test
    fun testParseStoreCommand_Simple() {
        val result = CommandParser.parseCommand("I am leaving my keys on the table")
        
        assertFalse(result.isQuery)
        assertEquals("keys", result.itemName)
        assertEquals("table", result.locationName)
    }
    
    @Test
    fun testParseStoreCommand_WithLocationDetail() {
        val result = CommandParser.parseCommand("I left my phone in the bedroom")
        
        assertFalse(result.isQuery)
        assertEquals("phone", result.itemName)
        assertEquals("bedroom", result.locationName)
    }
    
    @Test
    fun testParseQueryCommand_Simple() {
        val result = CommandParser.parseCommand("Where did I leave my keys?")
        
        assertTrue(result.isQuery)
        assertEquals("keys", result.itemName)
        assertNull(result.locationName)
    }
    
    @Test
    fun testParseQueryCommand_WhereIs() {
        val result = CommandParser.parseCommand("Where is my wallet?")
        
        assertTrue(result.isQuery)
        assertEquals("wallet", result.itemName)
    }
    
    @Test
    fun testParseQueryCommand_Find() {
        val result = CommandParser.parseCommand("Find my glasses")
        
        assertTrue(result.isQuery)
        assertEquals("glasses", result.itemName)
    }
    
    @Test
    fun testIsValidCommand_Valid() {
        assertTrue(CommandParser.isValidCommand("I left my keys on the table"))
        assertTrue(CommandParser.isValidCommand("Where are my keys?"))
    }
    
    @Test
    fun testExtractItemName() {
        assertEquals("keys", CommandParser.extractItemName("Where are my keys?"))
        assertEquals("phone", CommandParser.extractItemName("I left my phone in the room"))
    }
}
