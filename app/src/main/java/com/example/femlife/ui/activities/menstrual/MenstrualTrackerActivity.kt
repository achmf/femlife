package com.example.femlife.ui.activities.menstrual

import android.app.Dialog
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.femlife.R
import com.example.femlife.data.menstrual.CycleInfo
import com.example.femlife.data.menstrual.SymptomType
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.*

class MenstrualTrackerActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvCurrentCycleInfo: TextView
    private lateinit var tvNextPeriodInfo: TextView
    private lateinit var tvAverageCycleLength: TextView
    private lateinit var btnLogPeriod: MaterialButton
    private lateinit var btnLogSymptoms: MaterialButton
    private lateinit var sliderCycleLength: Slider
    private lateinit var progressBarNextPeriod: ProgressBar

    private lateinit var viewModel: MenstrualTrackerViewModel

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menstrual_tracker)

        viewModel = ViewModelProvider(this)[MenstrualTrackerViewModel::class.java]

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        calendarView = findViewById(R.id.calendarView)
        tvCurrentCycleInfo = findViewById(R.id.tvCurrentCycleInfo)
        tvNextPeriodInfo = findViewById(R.id.tvNextPeriodInfo)
        tvAverageCycleLength = findViewById(R.id.tvAverageCycleLength)
        btnLogPeriod = findViewById(R.id.btnLogPeriod)
        btnLogSymptoms = findViewById(R.id.btnLogSymptoms)
        sliderCycleLength = findViewById(R.id.sliderCycleLength)
        progressBarNextPeriod = findViewById(R.id.progressBarNextPeriod)

        setupCalendarView()
        setupButtons()
        setupToolbar(toolbar)
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Siklus Menstruasi"

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupCalendarView() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            updateCycleInfo()
        }
    }

    private fun setupButtons() {
        btnLogPeriod.setOnClickListener {
            showCalendarPopup()
        }

        btnLogSymptoms.setOnClickListener {
            showSymptomDialog()
        }

        sliderCycleLength.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.updateUserCycleLength(value.toInt())
                updateCycleInfo()
            }
        }
    }

    private fun showCalendarPopup() {
        showStartDateCalendar()
    }

    private fun showStartDateCalendar() {
        val dialog = Dialog(this)
        val calendarPopupView = CalendarPopupView(this)
        dialog.setContentView(calendarPopupView)

        calendarPopupView.setLabel("Pilih tanggal mulai menstruasi")
        calendarPopupView.setOnDateSelectedListener { startDate ->
            dialog.dismiss()
            showEndDateCalendar(startDate)
        }

        dialog.show()
    }

    private fun showEndDateCalendar(startDate: Date) {
        val dialog = Dialog(this)
        val calendarPopupView = CalendarPopupView(this)
        dialog.setContentView(calendarPopupView)

        calendarPopupView.setLabel("Pilih tanggal selesai menstruasi")
        calendarPopupView.setOnDateSelectedListener { endDate ->
            dialog.dismiss()
            viewModel.logPeriod(startDate, endDate)
            updateCycleInfo()
        }

        dialog.show()
    }

    private fun showSymptomDialog() {
        val symptoms = SymptomType.values()
        val items = symptoms.map { it.name.replace("_", " ").capitalize() }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Pilih Gejala")
            .setItems(items) { _, which ->
                showSeverityDialog(symptoms[which])
            }
            .show()
    }

    private fun showSeverityDialog(symptomType: SymptomType) {
        val severities = arrayOf("1 (Ringan)", "2", "3", "4", "5 (Parah)")

        AlertDialog.Builder(this)
            .setTitle("Pilih Tingkat Keparahan")
            .setItems(severities) { _, which ->
                viewModel.logSymptom(calendar.time, symptomType, which + 1)
                Toast.makeText(this, "Gejala berhasil dicatat", Toast.LENGTH_SHORT).show()
                updateCycleInfo()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.cycles.observe(this) { _ ->
            updateCycleInfo()
        }

        viewModel.error.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.userCycleLength.observe(this) { cycleLength ->
            sliderCycleLength.value = cycleLength.toFloat()
            updateCycleInfo()
        }

        viewModel.cycleInfo.observe(this) { cycleInfo ->
            updateUIWithCycleInfo(cycleInfo)
        }
    }

    private fun updateCycleInfo() {
        viewModel.updateCycleInfo(calendar.time)
    }

    private fun updateUIWithCycleInfo(cycleInfo: CycleInfo) {
        if (cycleInfo.dayOfCycle == 0) {
            tvCurrentCycleInfo.text = "Belum ada data siklus"
            tvNextPeriodInfo.text = "Belum cukup data untuk memprediksi menstruasi berikutnya"
            progressBarNextPeriod.progress = 0
        } else {
            if (cycleInfo.dayOfPeriod > 0) {
                tvCurrentCycleInfo.text = "Hari ${cycleInfo.dayOfCycle} dari ${cycleInfo.cycleLength} (Hari ${cycleInfo.dayOfPeriod} menstruasi)"
            } else {
                tvCurrentCycleInfo.text = "Hari ${cycleInfo.dayOfCycle} dari ${cycleInfo.cycleLength}"
            }

            val nextPeriodDate = viewModel.getPredictedNextPeriod()
            if (nextPeriodDate != null) {
                val today = Calendar.getInstance().time
                val daysUntilNextPeriod = viewModel.calculateDaysBetween(today, nextPeriodDate)
                val progressPercentage = ((cycleInfo.cycleLength - daysUntilNextPeriod) * 100) / cycleInfo.cycleLength
                progressBarNextPeriod.progress = progressPercentage.coerceIn(0, 100)

                tvNextPeriodInfo.text = when {
                    daysUntilNextPeriod > 0 -> "Menstruasi berikutnya dalam $daysUntilNextPeriod hari (${dateFormat.format(nextPeriodDate)})"
                    daysUntilNextPeriod == 0 -> "Menstruasi diperkirakan dimulai hari ini"
                    else -> "Menstruasi diperkirakan sudah dimulai ${-daysUntilNextPeriod} hari yang lalu"
                }
            } else {
                progressBarNextPeriod.progress = 0
                tvNextPeriodInfo.text = "Belum cukup data untuk memprediksi menstruasi berikutnya"
            }
        }

        tvAverageCycleLength.text = "Rata-rata panjang siklus: ${cycleInfo.cycleLength} hari"
    }
}

