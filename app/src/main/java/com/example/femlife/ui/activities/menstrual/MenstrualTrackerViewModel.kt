package com.example.femlife.ui.activities.menstrual

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.femlife.data.menstrual.CycleInfo
import com.example.femlife.data.menstrual.MenstrualCycle
import com.example.femlife.data.menstrual.Symptom
import com.example.femlife.data.menstrual.SymptomType
import com.example.femlife.data.menstrual.room.MenstrualCycleEntity
import com.example.femlife.data.menstrual.room.MenstrualTrackerDatabase
import com.example.femlife.data.menstrual.room.SymptomEntity
import com.example.femlife.data.notification.NotificationDatabase
import com.example.femlife.repository.NotificationRepository
import com.example.femlife.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class MenstrualTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MenstrualTrackerDatabase.getDatabase(application)
    private val dao = database.menstrualTrackerDao()
    private val notificationRepository = NotificationRepository(NotificationDatabase.getDatabase(application).notificationDao())
    private var notificationHelper: NotificationHelper
    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _userCycleLength = MutableLiveData<Int>(MenstrualCycle.DEFAULT_CYCLE_LENGTH)
    val userCycleLength: LiveData<Int> = _userCycleLength

    private val _cycleInfo = MutableLiveData<CycleInfo>()
    val cycleInfo: LiveData<CycleInfo> = _cycleInfo

    val cycles: LiveData<List<MenstrualCycle>> = dao.getAllMenstrualCycles().map { entities ->
        entities.map { entity ->
            MenstrualCycle(entity.startDate, entity.endDate, entity.cycleLength, entity.periodLength)
        }
    }.asLiveData()

    val symptoms: LiveData<List<Symptom>> = dao.getAllSymptoms().map { entities ->
        entities.map { entity ->
            Symptom(entity.date, entity.type, entity.severity)
        }
    }.asLiveData()

    init {
        notificationHelper = NotificationHelper(application, notificationRepository, userId)
        setupNotificationChecks()
    }

    fun logPeriod(startDate: Date, endDate: Date) {
        val periodLength = calculateDaysBetween(startDate, endDate) + 1 // Include both start and end dates

        if (periodLength < MenstrualCycle.MIN_PERIOD_LENGTH) {
            _error.value = "Durasi menstruasi minimal ${MenstrualCycle.MIN_PERIOD_LENGTH} hari."
            return
        }

        if (periodLength > MenstrualCycle.MAX_PERIOD_LENGTH) {
            _error.value = "Durasi menstruasi maksimal ${MenstrualCycle.MAX_PERIOD_LENGTH} hari."
            return
        }

        viewModelScope.launch {
            val lastCycle = dao.getLatestMenstrualCycle().firstOrNull()
            val cycleLength = if (lastCycle != null) {
                calculateDaysBetween(lastCycle.startDate, startDate)
            } else {
                MenstrualCycle.DEFAULT_CYCLE_LENGTH
            }

            if (cycleLength < MenstrualCycle.MIN_CYCLE_LENGTH || cycleLength > MenstrualCycle.MAX_CYCLE_LENGTH) {
                _error.value = "Panjang siklus harus antara ${MenstrualCycle.MIN_CYCLE_LENGTH} dan ${MenstrualCycle.MAX_CYCLE_LENGTH} hari."
                return@launch
            }

            val newCycle = MenstrualCycleEntity(
                startDate = startDate,
                endDate = endDate,
                cycleLength = cycleLength,
                periodLength = periodLength
            )
            dao.insertMenstrualCycle(newCycle)
            updateCycleInfo(Calendar.getInstance().time)
        }
    }

    fun logSymptom(date: Date, type: SymptomType, severity: Int) {
        if (severity < 1 || severity > 5) {
            _error.value = "Tingkat keparahan harus antara 1 dan 5."
            return
        }

        viewModelScope.launch {
            val symptom = SymptomEntity(date = date, type = type, severity = severity)
            dao.insertSymptom(symptom)
        }
    }

    fun getPredictedNextPeriod(): Date? {
        val lastCycle = cycles.value?.firstOrNull() ?: return null
        val calendar = Calendar.getInstance()
        calendar.time = lastCycle.startDate
        calendar.add(Calendar.DAY_OF_MONTH, _userCycleLength.value ?: MenstrualCycle.DEFAULT_CYCLE_LENGTH)
        return calendar.time
    }

    fun getCycleInfo(currentDate: Date): CycleInfo {
        val lastCycle = cycles.value?.firstOrNull()
        val userCycleLength = _userCycleLength.value ?: MenstrualCycle.DEFAULT_CYCLE_LENGTH

        if (lastCycle == null) {
            return CycleInfo(0, 0, userCycleLength)
        }

        val daysSinceStart = calculateDaysBetween(lastCycle.startDate, currentDate)
        val dayOfCycle = (daysSinceStart % userCycleLength) + 1 // Add 1 because the start day is day 1, not day 0
        val dayOfPeriod = if (dayOfCycle <= lastCycle.periodLength) dayOfCycle else 0

        return CycleInfo(dayOfCycle, dayOfPeriod, userCycleLength)
    }

    fun updateUserCycleLength(length: Int) {
        if (length in MenstrualCycle.MIN_CYCLE_LENGTH..MenstrualCycle.MAX_CYCLE_LENGTH) {
            _userCycleLength.value = length
            updateCycleInfo(Calendar.getInstance().time)
        } else {
            _error.value = "Panjang siklus harus antara ${MenstrualCycle.MIN_CYCLE_LENGTH} dan ${MenstrualCycle.MAX_CYCLE_LENGTH} hari."
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun updateCycleInfo(currentDate: Date) {
        _cycleInfo.value = getCycleInfo(currentDate)
    }

    fun calculateDaysBetween(start: Date, end: Date): Int {
        val startCalendar = Calendar.getInstance().apply { time = start }
        val endCalendar = Calendar.getInstance().apply { time = end }

        startCalendar.set(Calendar.HOUR_OF_DAY, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        startCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)

        endCalendar.set(Calendar.HOUR_OF_DAY, 0)
        endCalendar.set(Calendar.MINUTE, 0)
        endCalendar.set(Calendar.SECOND, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)

        return ((endCalendar.timeInMillis - startCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun setupNotificationChecks() {
        viewModelScope.launch {
            // Check for notifications daily
            while (true) {
                checkAndSendNotifications()
                delay(24 * 60 * 60 * 1000) // Wait 24 hours
            }
        }
    }

    private fun checkAndSendNotifications() {
        val nextPeriod = getPredictedNextPeriod() ?: return
        val today = Calendar.getInstance().time
        val daysUntilNext = calculateDaysBetween(today, nextPeriod)

        // Pre-menstrual notifications
        when (daysUntilNext) {
            2 -> notificationHelper.showPreMenstrualNotification(2)
            3 -> notificationHelper.showPreMenstrualNotification(3)
        }

        // Get current cycle info
        val currentCycleInfo = getCycleInfo(today)

        // First day notification
        if (currentCycleInfo.dayOfPeriod == 1) {
            notificationHelper.showFirstDayNotification(true)
        }

        // Ovulation notification (typically around day 14 in a 28-day cycle)
        if (currentCycleInfo.dayOfCycle == currentCycleInfo.cycleLength / 2) {
            notificationHelper.showOvulationNotification(false)
        }

        // Last day notification
        cycles.value?.firstOrNull()?.let { lastCycle ->
            if (calculateDaysBetween(today, lastCycle.endDate) == 1) {
                notificationHelper.showLastDayNotification(true)
            }
        }
    }

    fun testNotification() {
        notificationHelper.showPreMenstrualNotification(2)
        notificationHelper.showFirstDayNotification(true)
        notificationHelper.showOvulationNotification(false)
        notificationHelper.showLastDayNotification(true)
    }
}
