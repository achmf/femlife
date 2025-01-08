// AlarmActivity.kt
package com.example.femlife.ui.activities.alarm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.R
import com.example.femlife.data.alarm.AlarmDatabase
import com.example.femlife.databinding.ActivityAlarmBinding
import com.example.femlife.ui.activities.alarm.create.CreateAlarmActivity
import com.example.femlife.ui.activities.alarm.viewmodel.AlarmViewModel
import com.example.femlife.ui.activities.alarm.viewmodel.AlarmViewModelFactory

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private val alarmDatabase: AlarmDatabase by lazy { AlarmDatabase.getDatabase(this) }

    // ViewModel untuk Alarm
    private val alarmViewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(alarmDatabase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeAlarms()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Daftar Alarm"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_alarm, menu) // Inflate the menu resource
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create_alarm -> {
                // Navigasi ke CreateAlarmActivity
                val intent = Intent(this, CreateAlarmActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        alarmAdapter = AlarmAdapter(
            alarmList = emptyList(),
            onEditClick = { alarm ->
                // Logika Edit Alarm
                val intent = Intent(this, CreateAlarmActivity::class.java).apply {
                    putExtra("alarm_id", alarm.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { alarm ->
                alarmViewModel.deleteAlarm(alarm)
            }
        )
        binding.recyclerViewAlarms.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAlarms.adapter = alarmAdapter
    }

    private fun observeAlarms() {
        // Observe LiveData untuk mendapatkan pembaruan alarm secara realtime
        alarmViewModel.alarms.observe(this) { alarms ->
            alarmAdapter.updateAlarms(alarms)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data saat aktivitas kembali ke foreground
        alarmViewModel.reloadAlarms() // This will refresh the alarm list
    }
}
