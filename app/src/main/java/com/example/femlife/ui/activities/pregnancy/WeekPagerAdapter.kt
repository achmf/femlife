package com.example.femlife.ui.activities.pregnancy

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.femlife.R

class WeekPagerAdapter(fragment: Fragment, private val trimester: Int) : FragmentStateAdapter(fragment) {
    private val weeks = when (trimester) {
        1 -> listOf(4, 8, 13)
        2 -> listOf(17, 21, 26)
        3 -> listOf(30, 35, 40)
        else -> throw IllegalArgumentException("Invalid trimester: $trimester")
    }

    override fun getItemCount(): Int = weeks.size

    override fun createFragment(position: Int): Fragment {
        val week = weeks[position]
        val month = when (trimester) {
            1 -> position + 1
            2 -> position + 4
            3 -> position + 7
            else -> throw IllegalArgumentException("Invalid trimester: $trimester")
        }
        val imageResId = getImageResourceForWeek(week)
        return WeekFragment.newInstance(week, month, imageResId)
    }

    fun getWeekNumber(position: Int): Int = weeks[position]

    private fun getImageResourceForWeek(week: Int): Int {
        return when (week) {
            4 -> R.drawable.pregnancy_week_4
            8 -> R.drawable.pregnancy_week_8
            13 -> R.drawable.pregnancy_week_13
            17 -> R.drawable.pregnancy_week_17
            21 -> R.drawable.pregnancy_week_21
            26 -> R.drawable.pregnancy_week_26
            30 -> R.drawable.pregnancy_week_30
            35 -> R.drawable.pregnancy_week_35
            40 -> R.drawable.pregnancy_week_40
            else -> R.drawable.ic_pregnancy
        }
    }
}

