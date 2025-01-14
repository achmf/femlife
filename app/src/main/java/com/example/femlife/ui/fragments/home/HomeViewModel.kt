package com.example.femlife.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.femlife.R
import com.example.femlife.data.menu.MenuItem
import com.example.femlife.data.workshop.WorkshopItem
import com.example.femlife.repository.WorkshopRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val workshopRepository = WorkshopRepository()

    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> get() = _menuItems

    private val _workshopItems = MutableLiveData<List<WorkshopItem>>()
    val workshopItems: LiveData<List<WorkshopItem>> get() = _workshopItems

    init {
        loadMenuItems()
        loadWorkshopItems()  // Memuat workshop saat pertama kali
    }

    private fun loadMenuItems() {
        val menuItemsList = listOf(
            MenuItem(R.drawable.ic_calendar, "Siklus Menstruasi"),
            MenuItem(R.drawable.ic_pregnancy, "Masa Kehamilan"),
            MenuItem(R.drawable.ic_after_pregnancy, "Pasca Melahirkan"),
            MenuItem(R.drawable.ic_alarm, "Alarm Kontrol"),
            MenuItem(R.drawable.ic_education, "Edukasi"),
            MenuItem(R.drawable.ic_shop, "Produk")
        )
        _menuItems.value = menuItemsList
    }

    // Fungsi untuk memuat workshop dari Firestore atau repository
    private fun loadWorkshopItems() {
        viewModelScope.launch {
            val workshops = workshopRepository.getWorkshops()
            _workshopItems.value = workshops
        }
    }

    // Fungsi untuk menambahkan workshop baru setelah berhasil
    fun addNewWorkshop(workshopItem: WorkshopItem) {
        viewModelScope.launch {
            // Misalnya menambahkan workshop baru ke Firestore atau repository
            workshopRepository.addWorkshop(workshopItem)
            // Memuat ulang daftar workshop untuk mendapatkan data terbaru
            loadWorkshopItems()
        }
    }

    // Fungsi untuk memperbarui daftar workshop jika ada perubahan
    fun refreshWorkshopItems() {
        loadWorkshopItems()
    }

    fun deleteWorkshop(workshopItem: WorkshopItem) {
        viewModelScope.launch {
            workshopRepository.deleteWorkshop(workshopItem)
            loadWorkshopItems() // Refresh the list after deletion
        }
    }
}
