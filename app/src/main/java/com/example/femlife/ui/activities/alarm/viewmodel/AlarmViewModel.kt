package com.example.femlife.ui.activities.alarm.viewmodel

import androidx.lifecycle.*
import com.example.femlife.data.alarm.Alarm
import com.example.femlife.data.alarm.AlarmDatabase
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmDatabase: AlarmDatabase) : ViewModel() {

    private val _alarms = MutableLiveData<List<Alarm>>() // LiveData untuk daftar alarm
    val alarms: LiveData<List<Alarm>> get() = _alarms

    init {
        fetchAlarms()
    }

    private fun fetchAlarms() {
        viewModelScope.launch {
            _alarms.postValue(alarmDatabase.alarmDao().getAllAlarms())
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmDatabase.alarmDao().deleteAlarm(alarm)
            fetchAlarms() // Refresh daftar alarm setelah menghapus
        }
    }

    fun reloadAlarms() {
        fetchAlarms()
    }
}
