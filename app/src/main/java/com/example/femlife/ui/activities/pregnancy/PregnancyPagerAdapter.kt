package com.example.femlife.ui.activities.pregnancy

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.femlife.ui.activities.pregnancy.fragments.Trimester1Fragment
import com.example.femlife.ui.activities.pregnancy.fragments.Trimester2Fragment
import com.example.femlife.ui.activities.pregnancy.fragments.Trimester3Fragment

class PregnancyPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Jumlah tab (trimester)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Trimester1Fragment()
            1 -> Trimester2Fragment()
            2 -> Trimester3Fragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
