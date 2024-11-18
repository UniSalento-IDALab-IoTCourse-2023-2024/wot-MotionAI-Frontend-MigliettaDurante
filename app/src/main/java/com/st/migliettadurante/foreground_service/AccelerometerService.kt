package com.st.migliettadurante.foreground_service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.har.migliettadurante.R
import com.st.migliettadurante.feature_detail.RecognitionData

class AccelerometerService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "AccelerometerServiceChannel"
        private const val NOTIFICATION_CHANNEL_NAME = "Accelerometer Service Channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("AccelerometerService", "Service started in foreground")

        RecognitionData.activity.observeForever { newActivity ->
            Log.d("AccelerometerService", "Activity updated: $newActivity")
            updateNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AccelerometerService", "onStartCommand")

        val deviceId = intent?.getStringExtra("deviceId") ?: return START_NOT_STICKY

        enqueueRecognitionWorker(deviceId)

        return START_STICKY
    }

    private fun enqueueRecognitionWorker(deviceId: String) {
        val inputData = workDataOf("deviceId" to deviceId)
        val workRequest = OneTimeWorkRequestBuilder<RecognitionWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "RecognitionWorker-$deviceId",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            workRequest
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        RecognitionData.activity.removeObserver { }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for Accelerometer Service notifications"
            enableLights(true)
            enableVibration(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("MotionAI")
            .setContentText("Attività: ${RecognitionData.activity.value ?: "Unknown"}")
            .setSmallIcon(R.drawable.ic_logo_removebg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}

