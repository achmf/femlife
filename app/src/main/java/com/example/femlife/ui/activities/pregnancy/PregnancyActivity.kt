package com.example.femlife.ui.activities.pregnancy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.femlife.databinding.ActivityPregnancyBinding
import com.google.android.material.tabs.TabLayoutMediator

class PregnancyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPregnancyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPregnancyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Panduan kehamilan"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}

