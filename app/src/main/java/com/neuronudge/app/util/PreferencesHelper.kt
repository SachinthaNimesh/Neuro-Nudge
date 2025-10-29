package com.neuronudge.app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class for managing app preferences.
 * Stores settings for both LostToFound and SoundSanctuary features.
 */
class PreferencesHelper(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "neuronudge_prefs"
        
        // SoundSanctuary preferences
        private const val KEY_SOUND_THRESHOLD = "sound_threshold"
        private const val KEY_AUTO_STOP_PLAYBACK = "auto_stop_playback"
        private const val KEY_PLAYBACK_VOLUME = "playback_volume"
        private const val KEY_PLAYBACK_BEHAVIOR = "playback_behavior"
        
        // Default values
        private const val DEFAULT_SOUND_THRESHOLD = 70.0  // dB
        private const val DEFAULT_AUTO_STOP = true
        private const val DEFAULT_VOLUME = 0.7f
        const val BEHAVIOR_AUTO_PAUSE = "auto_pause"
        const val BEHAVIOR_DUCK = "duck"
        const val BEHAVIOR_IGNORE = "ignore"
    }
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Get sound threshold in dB
     */
    fun getSoundThreshold(): Double {
        return prefs.getFloat(KEY_SOUND_THRESHOLD, DEFAULT_SOUND_THRESHOLD.toFloat()).toDouble()
    }
    
    /**
     * Set sound threshold in dB
     */
    fun setSoundThreshold(threshold: Double) {
        prefs.edit().putFloat(KEY_SOUND_THRESHOLD, threshold.toFloat()).apply()
    }
    
    /**
     * Get auto-stop playback setting
     */
    fun getAutoStopPlayback(): Boolean {
        return prefs.getBoolean(KEY_AUTO_STOP_PLAYBACK, DEFAULT_AUTO_STOP)
    }
    
    /**
     * Set auto-stop playback setting
     */
    fun setAutoStopPlayback(autoStop: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_STOP_PLAYBACK, autoStop).apply()
    }
    
    /**
     * Get playback volume (0.0 to 1.0)
     */
    fun getPlaybackVolume(): Float {
        return prefs.getFloat(KEY_PLAYBACK_VOLUME, DEFAULT_VOLUME)
    }
    
    /**
     * Set playback volume (0.0 to 1.0)
     */
    fun setPlaybackVolume(volume: Float) {
        prefs.edit().putFloat(KEY_PLAYBACK_VOLUME, volume.coerceIn(0f, 1f)).apply()
    }
    
    /**
     * Get playback behavior when LostToFound is active
     */
    fun getPlaybackBehavior(): String {
        return prefs.getString(KEY_PLAYBACK_BEHAVIOR, BEHAVIOR_AUTO_PAUSE) ?: BEHAVIOR_AUTO_PAUSE
    }
    
    /**
     * Set playback behavior
     */
    fun setPlaybackBehavior(behavior: String) {
        prefs.edit().putString(KEY_PLAYBACK_BEHAVIOR, behavior).apply()
    }
}
