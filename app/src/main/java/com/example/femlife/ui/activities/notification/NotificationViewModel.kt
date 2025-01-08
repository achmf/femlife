package com.example.femlife.ui.activities.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.data.notification.NotificationData
import com.example.femlife.data.notification.NotificationDatabase
import com.example.femlife.repository.NotificationRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application, private val userId: String) : AndroidViewModel(application) {
    private val repository: NotificationRepository

    init {
        val notificationDao = NotificationDatabase.getDatabase(application).notificationDao()
        repository = NotificationRepository(notificationDao)
    }

    val allNotifications: LiveData<List<NotificationData>> = repository.allNotifications
        .map { notifications -> notifications.filter { it.userId == userId } }
        .asLiveData()

    fun deleteNotification(notification: NotificationData) {
        viewModelScope.launch {
            repository.deleteNotification(notification)
        }
    }

    fun insertNotification(notification: NotificationData) {
        viewModelScope.launch {
            repository.insertNotification(notification)
        }
    }
}
