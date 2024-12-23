package com.example.femlife.ui.activities.postpregnancy

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.femlife.ui.activities.postpregnancy.fragments.MentalFragment
import com.example.femlife.ui.activities.postpregnancy.fragments.MenyusuiFragment
import com.example.femlife.ui.activities.postpregnancy.fragments.PemulihanFragment
import com.example.femlife.ui.activities.postpregnancy.fragments.PerawatanFragment

class PostPregnancyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PemulihanFragment()
            1 -> PerawatanFragment()
            2 -> MenyusuiFragment()
            3 -> MentalFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}

