package com.example.femlife.ui.activities.postpregnancy.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.style.BulletSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.femlife.R

class MenyusuiFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.ivHeader).setImageResource(R.drawable.img_menyusui)
        view.findViewById<TextView>(R.id.tvTitle).text = "Tips Menyusui Bayi"

        val tipsContainer = view.findViewById<LinearLayout>(R.id.llTipsContainer)
        val tips = listOf(
            "Lakukan IMD (Inisiasi Menyusui Dini) untuk mendekatkan hubungan ibu dan bayi",
            "Pastikan posisi menyusui yang benar untuk menghindari nyeri pada puting",
            "Minum air putih yang cukup untuk mendukung produksi ASI",
            "Konsumsi makanan sehat seperti sayuran hijau, protein, dan kacang kacangan"
        )

        tips.forEach { tip ->
            addTipItem(tipsContainer, tip)
        }

        val warningsContainer = view.findViewById<LinearLayout>(R.id.llWarningsContainer)
        val warnings = listOf(
            "Menahan buang air kecil yang dapat menyebabkan infeksi",
            "Mengabaikan tanda infeksi seperti demam tinggi",
            "Diet ketat yang mengganggu produksi ASI"
        )

        warnings.forEach { warning ->
            addWarningItem(warningsContainer, warning)
        }
    }

    private fun addTipItem(container: LinearLayout, text: String) {
        val spannableString = SpannableString(text).apply {
            setSpan(BulletSpan(24), 0, text.length, 0) // Mengatur jarak antara titik dan teks
        }
        val textView = TextView(requireContext()).apply {
            this.text = spannableString
            setPadding(0, 0, 0, 16)
            textSize = 14f
            setTextColor(resources.getColor(R.color.black)) // Warna teks
        }
        container.addView(textView)
    }

    private fun addWarningItem(container: LinearLayout, text: String) {
        val spannableString = SpannableString(text).apply {
            setSpan(BulletSpan(24), 0, text.length, 0) // Mengatur jarak antara titik dan teks
        }
        val textView = TextView(requireContext()).apply {
            this.text = spannableString
            setPadding(0, 0, 0, 16)
            textSize = 14f
            setTextColor(resources.getColor(R.color.red)) // Warna teks untuk peringatan
        }
        container.addView(textView)
    }
}

