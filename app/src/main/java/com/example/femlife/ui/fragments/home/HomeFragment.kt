package com.example.femlife.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.R
import com.example.femlife.data.MenuItem
import com.example.femlife.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Mengisi data user pada header
        val userName = "John Doe"
        binding.tvWelcomeMessage.text = "Hello,\n$userName"

        // Data menu untuk RecyclerView
        val menuItems = listOf(
            MenuItem(R.drawable.ic_calendar, "Siklus Menstruasi"),
            MenuItem(R.drawable.ic_pregnancy, "Masa Kehamilan"),
            MenuItem(R.drawable.ic_after_pregnancy, "Pasca Melahirkan"),
            MenuItem(R.drawable.ic_alarm, "Alarm Kontrol"),
            MenuItem(R.drawable.ic_education, "Edukasi"),
            MenuItem(R.drawable.ic_shop, "Produk")
        )

        // Mengatur RecyclerView
        val adapter = HomeMenuAdapter(menuItems) { menuItem ->
            // Tindakan saat menu diklik
            handleMenuClick(menuItem)
        }

        binding.recyclerViewMenu.layoutManager = GridLayoutManager(context, 4).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1 // Semua item memiliki ukuran span yang sama
                }
            }
        }
        binding.recyclerViewMenu.adapter = adapter

        return binding.root
    }

    // Fungsi untuk menangani klik pada menu
    private fun handleMenuClick(menuItem: MenuItem) {
        // Contoh: Menampilkan menu yang diklik di log
        println("Menu clicked: ${menuItem.title}")
        // Anda bisa menavigasi ke fragment lain atau melakukan aksi tertentu
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
