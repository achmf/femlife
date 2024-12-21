package com.example.femlife.ui.activities.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.femlife.R
import com.example.femlife.ui.activities.alarm.popup.AlarmPopupActivity

class AlarmReceiver : BroadcastReceiver() {

    // Deklarasikan MediaPlayer sebagai variabel statis (singleton)
    companion object {
        private var mediaPlayer: MediaPlayer? = null

        fun stopAlarm() {
            // Hentikan suara alarm jika masih berjalan
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notes = intent.getStringExtra("alarm_notes")
        val soundEnabled = intent.getBooleanExtra("sound_enabled", false)

        // Tampilkan notifikasi alarm
        showAlarmNotification(context, notes)

        // Mulai suara alarm jika diaktifkan
        if (soundEnabled) {
            playAlarmSound(context)
        }

        // Tampilkan tampilan popup untuk alarm
        val popupIntent = Intent(context, AlarmPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("alarm_notes", notes)
        }
        context.startActivity(popupIntent)

        Toast.makeText(context, "Alarm Berbunyi: $notes", Toast.LENGTH_LONG).show()
    }

    private fun playAlarmSound(context: Context) {
        // Set audio stream ke alarm dan maksimalkan volumenya
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )

        // Buat MediaPlayer dan atur looping
        stopAlarm() // Pastikan MediaPlayer sebelumnya dihentikan
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound).apply {
            isLooping = true // Putar berulang kali
            setVolume(1.0f, 1.0f) // Volume maksimum
            start()
        }
    }

    private fun showAlarmNotification(context: Context, notes: String?) {
        val channelId = "alarm_channel_id"
        val notificationId = 1234

        // Buat channel notifikasi (Android O+)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId, "Alarm Notifications",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Alarm")
            .setContentText(notes ?: "Alarm berbunyi")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
