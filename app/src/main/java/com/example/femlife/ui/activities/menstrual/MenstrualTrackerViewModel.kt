package com.example.femlife.ui.activities.menstrual

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.femlife.data.menstrual.MenstrualCycle
import com.example.femlife.data.menstrual.Symptom
import com.example.femlife.data.menstrual.SymptomType
import java.util.*

class MenstrualTrackerViewModel : ViewModel() {

    private val _cycles = MutableLiveData<List<MenstrualCycle>>(emptyList())
    val cycles: LiveData<List<MenstrualCycle>> = _cycles

    private val _symptoms = MutableLiveData<List<Symptom>>(emptyList())
    val symptoms: LiveData<List<Symptom>> = _symptoms

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun logPeriod(startDate: Date, endDate: Date) {
        val periodLength = calculateDaysBetween(startDate, endDate) + 1 // Include both start and end dates

        if (periodLength < MenstrualCycle.MIN_PERIOD_LENGTH || periodLength > MenstrualCycle.MAX_PERIOD_LENGTH) {
            _error.value = "Durasi menstruasi harus antara ${MenstrualCycle.MIN_PERIOD_LENGTH} dan ${MenstrualCycle.MAX_PERIOD_LENGTH} hari."
            return
        }

        val currentCycles = _cycles.value ?: emptyList()
        val lastCycle = currentCycles.lastOrNull()
        val cycleLength = if (lastCycle != null) {
            calculateDaysBetween(lastCycle.startDate, startDate)
        } else {
            MenstrualCycle.DEFAULT_CYCLE_LENGTH
        }

        if (cycleLength < MenstrualCycle.MIN_CYCLE_LENGTH || cycleLength > MenstrualCycle.MAX_CYCLE_LENGTH) {
            _error.value = "Panjang siklus harus antara ${MenstrualCycle.MIN_CYCLE_LENGTH} dan ${MenstrualCycle.MAX_CYCLE_LENGTH} hari."
            return
        }

        val newCycle = MenstrualCycle(startDate, endDate, cycleLength, periodLength)
        _cycles.value = currentCycles + newCycle
    }

    fun logSymptom(date: Date, type: SymptomType, severity: Int) {
        if (severity < 1 || severity > 5) {
            _error.value = "Tingkat keparahan harus antara 1 dan 5."
            return
        }

        val currentSymptoms = _symptoms.value ?: emptyList()
        _symptoms.value = currentSymptoms + Symptom(date, type, severity)
    }

    fun getPredictedNextPeriod(): Date? {
        val lastCycle = _cycles.value?.lastOrNull() ?: return null
        val calendar = Calendar.getInstance()
        calendar.time = lastCycle.startDate
        calendar.add(Calendar.DAY_OF_MONTH, lastCycle.cycleLength)
        return calendar.time
    }

    fun getCycleInfo(currentDate: Date): CycleInfo {
        val lastCycle = _cycles.value?.lastOrNull() ?: return CycleInfo(0, 0, 0)

        val daysSinceStart = calculateDaysBetween(lastCycle.startDate, currentDate)
        val dayOfCycle = daysSinceStart + 1 // Add 1 because the start day is day 1, not day 0

        val dayOfPeriod = if (dayOfCycle <= lastCycle.periodLength) dayOfCycle else 0

        return CycleInfo(dayOfCycle, dayOfPeriod, lastCycle.cycleLength)
    }

    fun getAverageCycleLength(): Int {
        val cycles = _cycles.value ?: return MenstrualCycle.DEFAULT_CYCLE_LENGTH
        return if (cycles.size > 1) {
            cycles.dropLast(1).map { it.cycleLength }.average().toInt()
        } else {
            MenstrualCycle.DEFAULT_CYCLE_LENGTH
        }
    }

    fun clearError() {
        _error.value = null
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
}

data class CycleInfo(
    val dayOfCycle: Int,
    val dayOfPeriod: Int,
    val cycleLength: Int
)

