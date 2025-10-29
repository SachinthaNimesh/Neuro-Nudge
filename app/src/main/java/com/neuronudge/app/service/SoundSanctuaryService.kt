package com.neuronudge.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.neuronudge.app.R
import com.neuronudge.app.ui.MainActivity
import com.neuronudge.app.util.PreferencesHelper
import kotlinx.coroutines.*
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Foreground service for SoundSanctuary feature.
 * Monitors ambient sound levels and plays soothing audio when threshold is exceeded.
 */
class SoundSanctuaryService : Service() {
    
    companion object {
        const val CHANNEL_ID = "SoundSanctuaryChannel"
        const val NOTIFICATION_ID = 1
        
        const val ACTION_START = "com.neuronudge.app.ACTION_START_SOUND_SANCTUARY"
        const val ACTION_STOP = "com.neuronudge.app.ACTION_STOP_SOUND_SANCTUARY"
        const val ACTION_PAUSE_PLAYBACK = "com.neuronudge.app.ACTION_PAUSE_PLAYBACK"
        const val ACTION_RESUME_PLAYBACK = "com.neuronudge.app.ACTION_RESUME_PLAYBACK"
        
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val MONITORING_INTERVAL_MS = 500L
    }
    
    private var isRunning = false
    private var audioRecord: AudioRecord? = null
    private var mediaPlayer: MediaPlayer? = null
    private var monitoringJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private lateinit var preferencesHelper: PreferencesHelper
    
    override fun onCreate() {
        super.onCreate()
        preferencesHelper = PreferencesHelper(this)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP -> stopSelf()
            ACTION_PAUSE_PLAYBACK -> pausePlayback()
            ACTION_RESUME_PLAYBACK -> resumePlayback()
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * Start monitoring sound levels
     */
    private fun startMonitoring() {
        if (isRunning) return
        
        isRunning = true
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Initialize audio recording for sound level monitoring
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )
            
            audioRecord?.startRecording()
            
            // Start monitoring loop
            monitoringJob = serviceScope.launch {
                val buffer = ShortArray(bufferSize)
                
                while (isActive && isRunning) {
                    val readSize = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    
                    if (readSize > 0) {
                        val soundLevel = calculateSoundLevel(buffer, readSize)
                        checkThreshold(soundLevel)
                    }
                    
                    delay(MONITORING_INTERVAL_MS)
                }
            }
            
        } catch (e: SecurityException) {
            android.util.Log.e("SoundSanctuary", "Permission denied for audio recording", e)
            stopSelf()
        } catch (e: Exception) {
            android.util.Log.e("SoundSanctuary", "Error starting audio recording", e)
            stopSelf()
        }
    }
    
    /**
     * Calculate sound level in dB from audio buffer
     */
    private fun calculateSoundLevel(buffer: ShortArray, size: Int): Double {
        var sum = 0.0
        for (i in 0 until size) {
            sum += buffer[i] * buffer[i]
        }
        val rms = sqrt(sum / size)
        
        // Convert to approximate dB (reference: 32768 = max amplitude for 16-bit)
        val db = if (rms > 0) {
            20 * log10(rms / 32768.0) + 90  // Adding 90 to make it positive
        } else {
            0.0
        }
        
        return db.coerceIn(0.0, 120.0)  // Clamp to reasonable range
    }
    
    /**
     * Check if sound level exceeds threshold and trigger playback if needed
     */
    private fun checkThreshold(soundLevel: Double) {
        val threshold = preferencesHelper.getSoundThreshold()
        
        if (soundLevel > threshold && mediaPlayer?.isPlaying != true) {
            startSoothingAudio()
        } else if (soundLevel <= threshold && mediaPlayer?.isPlaying == true) {
            // Optionally stop playback when sound drops below threshold
            if (preferencesHelper.getAutoStopPlayback()) {
                stopSoothingAudio()
            }
        }
    }
    
    /**
     * Start playing soothing audio
     */
    private fun startSoothingAudio() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.rain_loop)
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(
                    preferencesHelper.getPlaybackVolume(),
                    preferencesHelper.getPlaybackVolume()
                )
            }
            mediaPlayer?.start()
            updateNotification("Playing soothing audio")
        } catch (e: Exception) {
            android.util.Log.e("SoundSanctuary", "Error starting playback", e)
        }
    }
    
    /**
     * Stop playing soothing audio
     */
    private fun stopSoothingAudio() {
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
        updateNotification("Monitoring sound levels")
    }
    
    /**
     * Pause playback (for LostToFound priority)
     */
    fun pausePlayback() {
        mediaPlayer?.pause()
        updateNotification("Playback paused")
    }
    
    /**
     * Resume playback
     */
    fun resumePlayback() {
        mediaPlayer?.start()
        updateNotification("Playing soothing audio")
    }
    
    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sound Sanctuary",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors ambient sound and plays soothing audio"
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Create notification for foreground service
     */
    private fun createNotification(contentText: String = "Monitoring sound levels"): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sound Sanctuary Active")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    /**
     * Update notification text
     */
    private fun updateNotification(contentText: String) {
        val notification = createNotification(contentText)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        
        monitoringJob?.cancel()
        
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        
        serviceScope.cancel()
    }
}
