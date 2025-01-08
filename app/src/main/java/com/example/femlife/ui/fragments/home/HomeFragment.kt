package com.example.femlife.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.data.menu.MenuItem
import com.example.femlife.databinding.FragmentHomeBinding
import com.example.femlife.ui.activities.article.ArticleActivity
import com.example.femlife.ui.activities.product.ProductActivity
import com.example.femlife.ui.activities.alarm.AlarmActivity
import com.example.femlife.ui.activities.menstrual.MenstrualTrackerActivity
import com.example.femlife.ui.activities.pregnancy.PregnancyActivity
import com.example.femlife.ui.activities.postpregnancy.PostPregnancyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeMenuAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()
        fetchUserName() // Ambil nama user dari Firestore pertama kali

        return binding.root
    }

    // Memanggil ulang fungsi fetchUserName saat fragment kembali ke layar
    override fun onResume() {
        super.onResume()
        fetchUserName() // Pastikan data nama user selalu up-to-date
    }

    private fun setupRecyclerView() {
        adapter = HomeMenuAdapter(emptyList()) { menuItem ->
            handleMenuClick(menuItem)
        }

        binding.recyclerViewMenu.layoutManager = GridLayoutManager(context, 4)
        binding.recyclerViewMenu.adapter = adapter
    }

    private fun observeViewModel() {
        homeViewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            adapter.updateMenuItems(menuItems)
        }
    }

    private fun fetchUserName() {
        // Mendapatkan userId dari Firebase Authentication
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            // Ambil nama user dari Firestore berdasarkan userId
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name") ?: "User"
                        binding.tvWelcomeMessage.text = "Halo, \n$userName"
                    }
                }
                .addOnFailureListener {
                    // Tangani error jika gagal mengambil data
                    binding.tvWelcomeMessage.text = "Halo, \nUser"
                }
        } else {
            binding.tvWelcomeMessage.text = "Halo, User"
        }
    }

    private fun handleMenuClick(menuItem: MenuItem) {
        when (menuItem.title) {
            "Edukasi" -> {
                val intent = Intent(activity, ArticleActivity::class.java)
                startActivity(intent)
            }
            "Alarm Kontrol" -> {
                val intent = Intent(activity, AlarmActivity::class.java)
                startActivity(intent)
            }
            "Produk" -> {
                val intent = Intent(activity, ProductActivity::class.java)
                startActivity(intent)
            }
            "Masa Kehamilan" -> {
                val intent = Intent(activity, PregnancyActivity::class.java)
                startActivity(intent)
            }
            "Pasca Melahirkan" -> {
                val intent = Intent(activity, PostPregnancyActivity::class.java)
                startActivity(intent)
            }
            "Siklus Menstruasi" -> {
                val intent = Intent(activity, MenstrualTrackerActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
