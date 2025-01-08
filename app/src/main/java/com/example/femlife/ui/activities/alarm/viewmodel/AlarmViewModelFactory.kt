package com.example.femlife.ui.activities.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.femlife.data.alarm.AlarmDatabase

class AlarmViewModelFactory(private val alarmDatabase: AlarmDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(alarmDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}