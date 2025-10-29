package com.neuronudge.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.neuronudge.app.data.AppDatabase
import com.neuronudge.app.data.repository.LostItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WorkManager worker to cleanup items older than 30 days.
 * Runs periodically in the background.
 */
class CleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        const val WORK_NAME = "cleanup_old_items"
        const val DAYS_TO_KEEP = 30
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = LostItemRepository(database.lostItemDao())
            
            // Delete items older than 30 days
            val deletedCount = repository.deleteOlderThan(DAYS_TO_KEEP)
            
            android.util.Log.i("CleanupWorker", "Deleted $deletedCount items older than $DAYS_TO_KEEP days")
            
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("CleanupWorker", "Error during cleanup", e)
            Result.retry()
        }
    }
}
