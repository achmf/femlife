package com.example.femlife.ui.activities.alarm.create

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.femlife.data.alarm.Alarm
import com.example.femlife.data.alarm.AlarmDatabase
import com.example.femlife.databinding.ActivityCreateAlarmBinding
import com.example.femlife.ui.activities.alarm.receiver.AlarmReceiver
import kotlinx.coroutines.launch
import java.util.*

class CreateAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAlarmBinding
    private lateinit var alarmDatabase: AlarmDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmDatabase = AlarmDatabase.getDatabase(this)

        setupToolbar()
        setupDateAndTimePickers()

        // Button Create Alarm
        binding.btnCreateAlarm.setOnClickListener {
            saveAlarmToDatabaseAndSchedule()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAlarm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Tambah Alarm"
        binding.toolbarAlarm.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupDateAndTimePickers() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                binding.etDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                binding.etTime.setText(String.format("%02d:%02d", hour, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun saveAlarmToDatabaseAndSchedule() {
        val alarm = Alarm(
            date = binding.etDate.text.toString(),
            time = binding.etTime.text.toString(),
            doctorName = binding.etDoctorName.text.toString(),
            location = binding.etLocation.text.toString(),
            notes = binding.etNotes.text.toString(),
            oneDayBefore = binding.switchOneDayBefore.isChecked,
            dayOf = binding.switchDayOf.isChecked,
            soundEnabled = binding.switchSound.isChecked
        )

        lifecycleScope.launch {
            // Simpan alarm ke database
            alarmDatabase.alarmDao().insertAlarm(alarm)
            // Jadwalkan alarm
            scheduleAlarm(alarm)

            // Tutup activity setelah berhasil
            finish()
        }
    }

    private fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Alarm Tepat Waktu
        val calendarExact = Calendar.getInstance().apply {
            val dateParts = alarm.date.split("/")
            val timeParts = alarm.time.split(":")
            set(Calendar.YEAR, dateParts[2].toInt())
            set(Calendar.MONTH, dateParts[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
        }

        val intentExact = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("alarm_notes", alarm.notes)
            putExtra("sound_enabled", alarm.soundEnabled)
        }

        val pendingIntentExact = PendingIntent.getBroadcast(
            this, alarm.id, intentExact, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarExact.timeInMillis, pendingIntentExact)

        // Alarm 1 Hari Sebelum (Jika Diaktifkan)
        if (alarm.oneDayBefore) {
            val calendarOneDayBefore = calendarExact.clone() as Calendar
            calendarOneDayBefore.add(Calendar.DAY_OF_MONTH, -1) // Kurangi 1 hari

            val intentOneDayBefore = Intent(this, AlarmReceiver::class.java).apply {
                putExtra("alarm_notes", "Pengingat 1 Hari Sebelum: ${alarm.notes}")
                putExtra("sound_enabled", alarm.soundEnabled)
            }

            val pendingIntentOneDayBefore = PendingIntent.getBroadcast(
                this, alarm.id + 1, intentOneDayBefore, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarOneDayBefore.timeInMillis, pendingIntentOneDayBefore)
        }
    }
}
