package com.example.femlife.ui.activities.menstrual

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.CalendarView
import android.widget.Button
import android.widget.TextView
import com.example.femlife.R
import java.util.*

class CalendarPopupView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val calendarView: CalendarView
    private val btnConfirm: Button
    private val tvLabel: TextView
    private var onDateSelectedListener: ((Date) -> Unit)? = null
    private var selectedDate: Date = Date()

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_calendar_popup, this, true)

        calendarView = findViewById(R.id.calendarView)
        btnConfirm = findViewById(R.id.btnConfirm)
        tvLabel = findViewById(R.id.tvLabel)

        setupCalendarView()
        setupConfirmButton()
    }

    private fun setupCalendarView() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        }
    }

    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {
            onDateSelectedListener?.invoke(selectedDate)
        }
    }

    fun setLabel(text: String) {
        tvLabel.text = text
    }

    fun setOnDateSelectedListener(listener: (Date) -> Unit) {
        onDateSelectedListener = listener
    }
}

