package com.example.femlife.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.femlife.R
import com.example.femlife.data.menu.MenuItem

class HomeViewModel : ViewModel() {

    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> get() = _menuItems

    init {
        loadMenuItems()
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
}
