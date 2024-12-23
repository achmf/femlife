package com.example.femlife.ui.activities.pregnancy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.femlife.databinding.ActivityPregnancyBinding
import com.google.android.material.tabs.TabLayoutMediator

class PregnancyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPregnancyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPregnancyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle padding for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup ViewPager2 with Adapter
        val pagerAdapter = PregnancyPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Trimester 1"
                1 -> "Trimester 2"
                2 -> "Trimester 3"
                else -> null
            }
        }.attach()
    }
}