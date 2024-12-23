package com.example.femlife.data.menstrual

import java.util.Date

data class Symptom(
    val date: Date,
    val type: SymptomType,
    val severity: Int // 1-5 scale
)

enum class SymptomType {
    KRAM,
    SAKIT_KEPALA,
    KEMBUNG,
    PERUBAHAN_MOOD,
    KELELAHAN,
    NYERI_PAYUDARA
}