package com.example.femlife.config

import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object ApiConfig {
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val supabase: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://mjjbntettezifjixpfhq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1qamJudGV0dGV6aWZqaXhwZmhxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzQzOTQyMjQsImV4cCI6MjA0OTk3MDIyNH0.a4QGDtdc53QKDyxZVgjnOcbN8P7F63KYV6nd6XPo_Y8"
        ) {
            install(Storage)
        }
    }
}

