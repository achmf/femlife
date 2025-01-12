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
        val imageResId = arguments?.getInt(ARG_IMAGE_RES_ID) ?: R.drawable.ic_after_pregnancy

        binding.tvWeek.text = "Minggu ke-$week"
        binding.tvMonth.text = "Bulan ke-$month"
        binding.ivPregnancyImage.setImageResource(imageResId)

        val whatHappensList = getWhatHappensList(week)
        populateWhatHappensList(whatHappensList)

        val tipsList = getTipsList(week)
        populateTipsList(tipsList)
    }

    private fun getWhatHappensList(week: Int): List<String> {
        return when {
            // Trimester 1 (weeks 1-13)
            week <= 13 -> listOf(
                "Mual dan muntah (morning sickness).",
                "Kelelahan yang berlebihan.",
                "Perubahan suasana hati akibat hormon.",
                "Payudara membesar dan sensitif."
            )
            // Trimester 2 (weeks 14-26)
            week <= 26 -> listOf(
                "Nafsu makan meningkat.",
                "Perut mulai membesar.",
                "Merasakan gerakan janin.",
                "Sakit punggung dan kram kaki."
            )
            // Trimester 3 (weeks 27-40)
            else -> listOf(
                "Perut semakin besar",
                "Sesak napas.",
                "Sulit tidur, kaki bengkak, dan kontraksi palsu (Braxton Hicks).",
                "Sering buang air kecil dan nyeri panggul."
            )
        }
    }

    private fun getTipsList(week: Int): List<String> {
        return when {
            // Trimester 1 tips
            week <= 13 -> listOf(
                "Konsultasi dokter, periksa HCG.",
                "Konsumsi makanan sehat dan kaya nutrisi.",
                "Istirahat cukup untuk mengatasi kelelahan.",
                "Hindari kafein dan makanan pedas."
            )
            // Trimester 2 tips
            week <= 26 -> listOf(
                "Jaga pola makan seimbang.",
                "Lakukan olahraga ringan yang aman.",
                "Perhatikan postur tubuh saat beraktivitas.",
                "Rutin kontrol ke dokter."
            )
            // Trimester 3 tips
            else -> listOf(
                "Persiapkan kelahiran dengan matang.",
                "Lakukan senam hamil jika diizinkan.",
                "Hindari aktivitas berat.",
                "Perhatikan tanda-tanda persalinan."
            )
        }
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
        private const val ARG_IMAGE_RES_ID = "image_res_id"

        fun newInstance(week: Int, month: Int, imageResId: Int): WeekFragment {
            return WeekFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_WEEK, week)
                    putInt(ARG_MONTH, month)
                    putInt(ARG_IMAGE_RES_ID, imageResId)
                }
            }
        }
    }
}