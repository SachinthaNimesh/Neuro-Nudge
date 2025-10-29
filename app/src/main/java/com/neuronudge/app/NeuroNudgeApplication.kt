package com.neuronudge.app

import android.app.Application
import androidx.work.*
import com.neuronudge.app.worker.CleanupWorker
import java.util.concurrent.TimeUnit

/**
 * Application class for Neuro-Nudge.
 * Initializes WorkManager for periodic cleanup tasks.
 */
class NeuroNudgeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic cleanup of old items (30+ days)
        scheduleCleanupWork()
    }
    
    /**
     * Schedule periodic cleanup work
     * Runs once daily to remove items older than 30 days
     */
    private fun scheduleCleanupWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(
            1, TimeUnit.DAYS  // Run once daily
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CleanupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,  // Keep existing if already scheduled
            cleanupRequest
        )
    }
}
