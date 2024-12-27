package com.example.femlife.ui.activities.pregnancy

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PregnancyPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Number of trimesters

    override fun createFragment(position: Int): Fragment {
        return TrimesterFragment.newInstance(position + 1)
    }
}

