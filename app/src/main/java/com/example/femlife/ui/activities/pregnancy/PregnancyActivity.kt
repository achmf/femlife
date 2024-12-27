package com.example.femlife.ui.activities.pregnancy

import android.os.Bundle
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

        val pagerAdapter = PregnancyPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = "Trimester ${position + 1}"
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

