package internal.automator.automator.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat


class ScreenCaptureService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val screenCaptureChannel = NotificationChannel("SCREEN_CAPTURE_CHANNEL_ID", "SCREEN_CAPTURE_CHANNEL_NAME", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(screenCaptureChannel)
        startForeground(1, NotificationCompat.Builder(this, "SCREEN_CAPTURE_CHANNEL_ID").build())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
