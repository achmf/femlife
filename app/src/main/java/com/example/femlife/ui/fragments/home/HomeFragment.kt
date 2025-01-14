package com.example.femlife.ui.fragments.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.femlife.data.menu.MenuItem
import com.example.femlife.data.workshop.WorkshopItem
import com.example.femlife.databinding.FragmentHomeBinding
import com.example.femlife.ui.activities.alarm.AlarmActivity
import com.example.femlife.ui.activities.article.ArticleActivity
import com.example.femlife.ui.activities.menstrual.MenstrualTrackerActivity
import com.example.femlife.ui.activities.postpregnancy.PostPregnancyActivity
import com.example.femlife.ui.activities.pregnancy.PregnancyActivity
import com.example.femlife.ui.activities.product.ProductActivity
import com.example.femlife.ui.fragments.home.workshop.WorkshopAdapter
import com.example.femlife.ui.fragments.home.workshop.create.CreateWorkshopActivity
import com.example.femlife.ui.fragments.home.workshop.edit.EditWorkshopActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var menuAdapter: HomeMenuAdapter
    private lateinit var workshopAdapter: WorkshopAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupMenuRecyclerView()
        setupWorkshopRecyclerView()
        observeViewModel()
        fetchUserName()

        binding.fabHome.setOnClickListener {
            // Contoh: Aksi saat FAB diklik (misalnya membuka ProfileActivity)
            val intent = Intent(activity, CreateWorkshopActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchUserName()
        homeViewModel.refreshWorkshopItems()  // Refresh workshop list to show newly added workshop
    }

    private fun setupMenuRecyclerView() {
        menuAdapter = HomeMenuAdapter(emptyList()) { menuItem ->
            handleMenuClick(menuItem)
        }

        binding.recyclerViewMenu.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerViewMenu.adapter = menuAdapter
    }

    private fun setupWorkshopRecyclerView() {
        workshopAdapter = WorkshopAdapter(
            emptyList(),
            onItemClick = { workshopItem -> handleWorkshopClick(workshopItem) },
            onItemLongClick = { workshopItem, action -> handleWorkshopLongClick(workshopItem, action) }
        )

        binding.recyclerViewWorkshop.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerViewWorkshop.adapter = workshopAdapter
    }

    private fun observeViewModel() {
        homeViewModel.menuItems.observe(viewLifecycleOwner) { menuItems ->
            menuAdapter.updateMenuItems(menuItems)
        }

        homeViewModel.workshopItems.observe(viewLifecycleOwner) { workshopItems ->
            workshopAdapter.updateWorkshopItems(workshopItems)
        }
    }

    private fun fetchUserName() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name") ?: "User"
                        binding.tvWelcomeMessage.text = "Halo, \n$userName"
                    }
                }
                .addOnFailureListener {
                    binding.tvWelcomeMessage.text = "Halo, \nUser"
                }
        } else {
            binding.tvWelcomeMessage.text = "Halo, User"
        }
    }

    private fun handleMenuClick(menuItem: MenuItem) {
        val intent = when (menuItem.title) {
            "Masa Kehamilan" -> Intent(activity, PregnancyActivity::class.java)
            "Pasca Melahirkan" -> Intent(activity, PostPregnancyActivity::class.java)
            "Produk" -> Intent(activity, ProductActivity::class.java)
            "Siklus Menstruasi" -> Intent(activity, MenstrualTrackerActivity::class.java)
            "Alarm Kontrol" -> Intent(activity, AlarmActivity::class.java)
            "Edukasi" -> Intent(activity, ArticleActivity::class.java)
            else -> null // For unknown menu items, no action
        }
        intent?.let {
            startActivity(it)
        } ?: Toast.makeText(context, "Menu item not found!", Toast.LENGTH_SHORT).show()
    }

    private fun handleWorkshopClick(workshopItem: WorkshopItem) {
        // Open the link of the workshop item in the browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(workshopItem.link))
        startActivity(intent)
    }

    private fun handleWorkshopLongClick(workshopItem: WorkshopItem, action: String) {
        when (action) {
            "edit" -> {
                // Navigate to EditWorkshopActivity to edit the workshop
                val intent = Intent(activity, EditWorkshopActivity::class.java)
                intent.putExtra("workshopItem", workshopItem) // Pass the selected workshop item
                startActivity(intent)
            }
            "delete" -> {
                // Show a confirmation dialog for deleting
                showDeleteConfirmationDialog(workshopItem)
            }
        }
    }

    private fun showDeleteConfirmationDialog(workshopItem: WorkshopItem) {
        // You can use an AlertDialog to confirm the delete action
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Workshop")
            .setMessage("Anda yakin ingin menghapus workshop ini?")
            .setPositiveButton("Ya") { _, _ ->
                // Call delete function from ViewModel or Repository
                homeViewModel.deleteWorkshop(workshopItem)
                // Optionally show a toast or snackbar confirmation
                Toast.makeText(context, "Workshop berhasil dihapus.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
