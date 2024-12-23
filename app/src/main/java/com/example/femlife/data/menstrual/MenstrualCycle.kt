package com.example.femlife.data.menstrual

import java.util.Date

data class MenstrualCycle(
    val startDate: Date,
    val endDate: Date,
    val cycleLength: Int,
    val periodLength: Int
) {
    companion object {
        const val MIN_CYCLE_LENGTH = 21
        const val MAX_CYCLE_LENGTH = 35
        const val MIN_PERIOD_LENGTH = 2
        const val MAX_PERIOD_LENGTH = 7
        const val DEFAULT_CYCLE_LENGTH = 28
    }
}