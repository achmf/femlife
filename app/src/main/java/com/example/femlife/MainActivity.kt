package com.example.femlife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.femlife.databinding.ActivityMainBinding // Import Binding Class

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // View Binding Variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inisialisasi Binding
        setContentView(binding.root)

        // Set listener untuk item menu yang diklik
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
//                R.id.nav_message -> loadFragment(MessageFragment())
//                R.id.nav_heart -> loadFragment(HeartFragment())
//                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }

        // Muat fragment default (Home)
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    // Fungsi untuk mengganti fragment
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment) // Menggunakan binding
            .commit()
        return true
    }
}
