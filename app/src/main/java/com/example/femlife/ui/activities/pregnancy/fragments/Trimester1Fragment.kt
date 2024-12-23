package com.example.femlife.ui.activities.pregnancy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.femlife.R
import com.example.femlife.databinding.FragmentTrimester1Binding

class Trimester1Fragment : Fragment() {

    private var _binding: FragmentTrimester1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrimester1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up data using ViewBinding
        binding.tvWeek.text = "Minggu ke-4"
        binding.tvMonth.text = "Bulan ke-1"
        binding.ivPregnancyImage.setImageResource(R.drawable.trimester_1)

        // List of "What Happens"
        val whatHappensList = listOf(
            "Mual dan muntah (morning sickness)",
            "Kelelahan yang berlebihan.",
            "Perubahan suasana hati akibat hormon.",
            "Payudara membesar dan sensitif."
        )

        // List of Tips
        val tipsList = listOf(
            "Konsumsi makanan sehat dan kaya nutrisi.",
            "Istirahat cukup untuk mengatasi kelelahan.",
            "Hindari kafein dan makanan pedas.",
            "Minum air putih yang cukup setiap hari."
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
