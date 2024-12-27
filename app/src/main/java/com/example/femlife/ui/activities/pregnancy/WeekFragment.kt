package com.example.femlife.ui.activities.pregnancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.femlife.R
import com.example.femlife.databinding.FragmentWeekBinding

class WeekFragment : Fragment() {

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val week = arguments?.getInt(ARG_WEEK) ?: 0
        val month = arguments?.getInt(ARG_MONTH) ?: 0

        binding.tvWeek.text = "Minggu ke-$week"
        binding.tvMonth.text = "Bulan ke-$month"

        // Set pregnancy image
        binding.ivPregnancyImage.setImageResource(R.drawable.pregnancy_week_4)

        // Add what happens items
        val whatHappensList = listOf(
            "Mual dan muntah (morning sickness).",
            "Kelelahan yang berlebihan.",
            "Perubahan suasana hati akibat hormon.",
            "Payudara membesar dan sensitif."
        )

        // Add tips items
        val tipsList = listOf(
            "Konsultasi dokter, periksa HCG.",
            "Konsumsi makanan sehat dan kaya nutrisi.",
            "Istirahat cukup untuk mengatasi kelelahan.",
            "Hindari kafein dan makanan pedas."
        )

        populateWhatHappensList(whatHappensList)
        populateTipsList(tipsList)
    }

    private fun populateWhatHappensList(items: List<String>) {
        val inflater = LayoutInflater.from(context)
        binding.sectionWhatHappens.removeAllViews()

        for (item in items) {
            val itemView = inflater.inflate(R.layout.item_pregnancy_happens, binding.sectionWhatHappens, false)
            val tvItem = itemView.findViewById<TextView>(R.id.tvWhatHappensItem)
            tvItem.text = item
            binding.sectionWhatHappens.addView(itemView)
        }
    }

    private fun populateTipsList(tips: List<String>) {
        val inflater = LayoutInflater.from(context)
        binding.sectionTips.removeAllViews()

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

    companion object {
        private const val ARG_WEEK = "week"
        private const val ARG_MONTH = "month"

        fun newInstance(week: Int, month: Int): WeekFragment {
            return WeekFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_WEEK, week)
                    putInt(ARG_MONTH, month)
                }
            }
        }
    }
}

