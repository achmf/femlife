package com.example.femlife.ui.activities.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.femlife.data.alarm.Alarm
import com.example.femlife.databinding.ItemAlarmBinding

class AlarmAdapter(
    private var alarmList: List<Alarm>,
    private val onEditClick: (Alarm) -> Unit,
    private val onDeleteClick: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            // Menampilkan semua informasi alarm
            binding.tvAlarmDateTime.text = "Tanggal: ${alarm.date}, Waktu: ${alarm.time}"
            binding.tvAlarmDoctorName.text = "Dokter: ${alarm.doctorName ?: "Tidak Ada"}"
            binding.tvAlarmLocation.text = "Lokasi: ${alarm.location}"
            binding.tvAlarmNotes.text = "Catatan: ${alarm.notes}"

            // Event listener untuk Edit dan Delete
            binding.ivEditAlarm.setOnClickListener { onEditClick(alarm) }
            binding.ivDeleteAlarm.setOnClickListener { onDeleteClick(alarm) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding =
            ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarmList[position])
    }

    override fun getItemCount(): Int = alarmList.size

    fun updateAlarms(newAlarms: List<Alarm>) {
        alarmList = newAlarms
        notifyDataSetChanged()
    }
}
