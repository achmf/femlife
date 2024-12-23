package com.example.femlife.ui.activities.postpregnancy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.femlife.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PostPregnancyActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_pregnancy)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        // Set up ViewPager
        val pagerAdapter = PostPregnancyPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Set up TabLayout with ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Pemulihan"
                1 -> "Perawatan"
                2 -> "Menyusui"
                3 -> "Mental"
                else -> null
            }
        }.attach()

        // Set up back button
        findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }
}

