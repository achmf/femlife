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
import com.example.femlife.ui.activities.alarm.AlarmActivity
import com.example.femlife.ui.activities.article.ArticleActivity
import com.example.femlife.ui.activities.product.ProductActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = HomeMenuAdapter(emptyList()) { menuItem ->
            handleMenuClick(menuItem)
        }

        binding.recyclerViewMenu.layoutManager = GridLayoutManager(context, 4)
        binding.recyclerViewMenu.adapter = adapter
    }

    private fun observeViewModel() {
        // Mengamati data menuItems dari ViewModel
        homeViewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            adapter.updateMenuItems(menuItems)
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
            // Tambahkan navigasi lainnya jika diperlukan...
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
