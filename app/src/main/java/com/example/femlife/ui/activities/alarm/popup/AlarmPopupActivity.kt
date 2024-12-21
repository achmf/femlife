package com.example.femlife.ui.activities.alarm.popup

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.femlife.R
import com.example.femlife.ui.activities.alarm.receiver.AlarmReceiver

class AlarmPopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_popup)

        val notes = intent.getStringExtra("alarm_notes")

        val tvNotes = findViewById<TextView>(R.id.tvAlarmNotes)
        val btnDismiss = findViewById<Button>(R.id.btnDismiss)

        tvNotes.text = notes

        btnDismiss.setOnClickListener {
            dismissAlarm()
        }
    }

    private fun dismissAlarm() {
        AlarmReceiver.stopAlarm() // Hentikan suara alarm
        finish() // Tutup aktivitas alarm
    }
}
