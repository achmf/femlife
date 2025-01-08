package com.example.femlife.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.femlife.R
import com.example.femlife.data.notification.NotificationData
import com.example.femlife.data.notification.NotificationType
import com.example.femlife.repository.NotificationRepository
import com.example.femlife.ui.activities.menstrual.MenstrualTrackerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NotificationHelper(
    private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val userId: String
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID_MENSTRUAL = "menstrual_notifications"
        private const val CHANNEL_NAME_MENSTRUAL = "Menstrual Cycle Notifications"

        private const val NOTIFICATION_ID_PRE_MENSTRUAL = 1
        private const val NOTIFICATION_ID_FIRST_DAY = 2
        private const val NOTIFICATION_ID_OVULATION = 3
        private const val NOTIFICATION_ID_LAST_DAY = 4
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_MENSTRUAL,
                CHANNEL_NAME_MENSTRUAL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi terkait siklus menstruasi"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, message: String, notificationId: Int): NotificationCompat.Builder {
        val intent = Intent(context, MenstrualTrackerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_MENSTRUAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    private fun showNotification(title: String, message: String, notificationId: Int, type: NotificationType) {
        val notification = buildNotification(title, message, notificationId)
        notificationManager.notify(notificationId, notification.build())

        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.insertNotification(
                NotificationData(
                    userId = userId,
                    title = title,
                    message = message,
                    timestamp = Date(),
                    type = type
                )
            )
        }
    }

    fun showPreMenstrualNotification(daysUntil: Int) {
        val message = if (daysUntil == 2) {
            "Siklus menstruasi Anda diperkirakan akan dimulai dalam 2 hari. Jangan lupa siapkan pembalut atau kebutuhan lainnya."
        } else {
            "Apakah Anda sudah siap? Siklus menstruasi Anda akan dimulai dalam 3 hari."
        }

        showNotification(
            "Peringatan Awal Menstruasi",
            message,
            NOTIFICATION_ID_PRE_MENSTRUAL,
            NotificationType.PRE_MENSTRUAL
        )
    }

    fun showFirstDayNotification(isTracking: Boolean) {
        val message = if (isTracking) {
            "Catat gejala dan intensitas menstruasi Anda di aplikasi untuk membantu pemantauan siklus ke depannya."
        } else {
            "Hari pertama menstruasi Anda dimulai hari ini. Jaga kenyamanan dan minum air putih yang cukup."
        }

        showNotification(
            "Hari Pertama Menstruasi",
            message,
            NOTIFICATION_ID_FIRST_DAY,
            NotificationType.FIRST_DAY
        )
    }

    fun showOvulationNotification(isPlanning: Boolean) {
        val message = if (isPlanning) {
            "Sedang dalam fase ovulasi? Ini waktu terbaik untuk perencanaan kehamilan atau catatan kesehatan reproduksi Anda."
        } else {
            "Fase ovulasi Anda diperkirakan dimulai hari ini. Catat gejala Anda dan tingkatkan asupan makanan bernutrisi."
        }

        showNotification(
            "Fase Ovulasi",
            message,
            NOTIFICATION_ID_OVULATION,
            NotificationType.OVULATION
        )
    }

    fun showLastDayNotification(isComplete: Boolean) {
        val message = if (isComplete) {
            "Selamat! Anda telah menyelesaikan satu siklus lagi. Kami siap membantu Anda di siklus berikutnya."
        } else {
            "Hari terakhir menstruasi Anda diperkirakan telah berakhir. Jangan lupa untuk mencatat siklus Anda untuk pelacakan yang lebih akurat."
        }

        showNotification(
            "Akhir Siklus Menstruasi",
            message,
            NOTIFICATION_ID_LAST_DAY,
            NotificationType.LAST_DAY
        )
    }
}

