package com.example.femlife.repository

import com.example.femlife.data.notification.NotificationDao
import com.example.femlife.data.notification.NotificationData
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {
    val allNotifications: Flow<List<NotificationData>> = notificationDao.getAllNotifications()

    suspend fun insertNotification(notification: NotificationData) {
        notificationDao.insertNotification(notification)
    }

    suspend fun deleteNotification(notification: NotificationData) {
        notificationDao.deleteNotification(notification)
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }
}

