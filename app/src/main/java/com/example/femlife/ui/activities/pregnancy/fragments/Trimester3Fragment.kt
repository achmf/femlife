package com.example.femlife.ui.activities.pregnancy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.femlife.R
import com.example.femlife.databinding.FragmentTrimester3Binding

class Trimester3Fragment : Fragment() {

    private var _binding: FragmentTrimester3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrimester3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up data using ViewBinding
        binding.tvWeek.text = "Minggu ke-28"
        binding.tvMonth.text = "Bulan ke-7"
        binding.ivPregnancyImage.setImageResource(R.drawable.trimester_3)

        // List of "What Happens"
        val whatHappensList = listOf(
            "Janin semakin besar dan berat bertambah.",
            "Sering buang air kecil karena tekanan pada kandung kemih.",
            "Kram kaki dan nyeri punggung meningkat.",
            "Perubahan pada pola tidur ibu."
        )

        // List of Tips
        val tipsList = listOf(
            "Siapkan diri untuk melahirkan dengan mengikuti kelas prenatal.",
            "Konsumsi makanan kaya serat untuk mencegah sembelit.",
            "Jaga postur tubuh saat duduk dan tidur.",
            "Lakukan pemeriksaan rutin dengan dokter."
        )

        // Add items to sectionWhatHappens dynamically
        populateWhatHappensList(whatHappensList)

        // Add items to sectionTips dynamically
        populateTipsList(tipsList)
    }

    private fun populateWhatHappensList(items: List<String>) {
        val inflater = LayoutInflater.from(context)
        for (item in items) {
            val itemView = inflater.inflate(R.layout.item_pregnancy_happens, binding.sectionWhatHappens, false)
            val tvItem = itemView.findViewById<TextView>(R.id.tvWhatHappensItem)
            tvItem.text = item
            binding.sectionWhatHappens.addView(itemView)
        }
    }

    private fun populateTipsList(tips: List<String>) {
        val inflater = LayoutInflater.from(context)
        for (tip in tips) {
            val tipView = inflater.inflate(R.layout.item_pregnancy_tips, binding.sectionTips, false)
            val tvTip = tipView.findViewById<TextView>(R.id.tvTipsItem)
            tvTip.text = tip
            binding.sectionTips.addView(tipView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
