package com.example.femlife.ui.activities.menstrual

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.CalendarView
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
import com.example.femlife.data.menstrual.SymptomType
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class MenstrualTrackerActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var tvCurrentCycleInfo: TextView
    private lateinit var tvNextPeriodInfo: TextView
    private lateinit var tvAverageCycleLength: TextView
    private lateinit var btnLogPeriod: MaterialButton
    private lateinit var btnLogSymptoms: MaterialButton

    private lateinit var viewModel: MenstrualTrackerViewModel

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menstrual_tracker)

        viewModel = ViewModelProvider(this).get(MenstrualTrackerViewModel::class.java)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        calendarView = findViewById(R.id.calendarView)
        tvCurrentCycleInfo = findViewById(R.id.tvCurrentCycleInfo)
        tvNextPeriodInfo = findViewById(R.id.tvNextPeriodInfo)
        tvAverageCycleLength = findViewById(R.id.tvAverageCycleLength)
        btnLogPeriod = findViewById(R.id.btnLogPeriod)
        btnLogSymptoms = findViewById(R.id.btnLogSymptoms)

        setupCalendarView()
        setupButtons()
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
            showPeriodLogInfo()
            showDateRangePicker()
        }

        btnLogSymptoms.setOnClickListener {
            showSymptomDialog()
        }
    }

    private fun showDateRangePicker() {
        val startDatePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val startDate = Calendar.getInstance()
                startDate.set(year, month, dayOfMonth)

                val endDatePicker = DatePickerDialog(
                    this,
                    { _, endYear, endMonth, endDayOfMonth ->
                        val endDate = Calendar.getInstance()
                        endDate.set(endYear, endMonth, endDayOfMonth)

                        if (endDate.before(startDate)) {
                            Toast.makeText(this, "Tanggal akhir tidak boleh sebelum tanggal mulai", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.logPeriod(startDate.time, endDate.time)
                            updateCycleInfo()
                        }
                    },
                    year,
                    month,
                    dayOfMonth
                )
                endDatePicker.setTitle("Pilih Tanggal Akhir Menstruasi")
                endDatePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        startDatePicker.setTitle("Pilih Tanggal Mulai Menstruasi")
        startDatePicker.show()
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
        viewModel.cycles.observe(this) { cycles ->
            updateCycleInfo()
        }

        viewModel.error.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun updateCycleInfo() {
        val currentDate = calendar.time
        val cycleInfo = viewModel.getCycleInfo(currentDate)
        val nextPeriodDate = viewModel.getPredictedNextPeriod()
        val averageCycleLength = viewModel.getAverageCycleLength()

        if (cycleInfo.dayOfPeriod > 0) {
            tvCurrentCycleInfo.text = "Hari ${cycleInfo.dayOfCycle} dari ${cycleInfo.cycleLength} (Hari ${cycleInfo.dayOfPeriod} menstruasi)"
        } else {
            tvCurrentCycleInfo.text = "Hari ${cycleInfo.dayOfCycle} dari ${cycleInfo.cycleLength}"
        }

        if (nextPeriodDate != null) {
            val daysUntilNextPeriod = viewModel.calculateDaysBetween(currentDate, nextPeriodDate)
            tvNextPeriodInfo.text = when {
                daysUntilNextPeriod > 0 -> "Menstruasi berikutnya dalam $daysUntilNextPeriod hari (${dateFormat.format(nextPeriodDate)})"
                daysUntilNextPeriod == 0 -> "Menstruasi diperkirakan dimulai hari ini"
                else -> "Menstruasi diperkirakan sudah dimulai ${-daysUntilNextPeriod} hari yang lalu"
            }
        } else {
            tvNextPeriodInfo.text = "Belum cukup data untuk memprediksi menstruasi berikutnya"
        }

        tvAverageCycleLength.text = "Rata-rata panjang siklus: $averageCycleLength hari"
    }

    private fun showPeriodLogInfo() {
        Toast.makeText(this, "Pilih tanggal mulai dan akhir menstruasi", Toast.LENGTH_SHORT).show()
    }
}