package com.example.femlife.ui.activities.pregnancy

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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
        return WeekFragment.newInstance(week, month)
    }

    fun getWeekNumber(position: Int): Int = weeks[position]
}

