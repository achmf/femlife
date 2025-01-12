package com.example.femlife.ui.activities.article.detail

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.data.article.Article
import com.google.firebase.firestore.FirebaseFirestore

class DetailArticleActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID"
    }

    private lateinit var textViewTitle: TextView
    private lateinit var textViewCreatedAt: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var linearLayoutContentContainer: LinearLayout
    private lateinit var imageViewArticle: ImageView

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)

        // Bind views
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewCreatedAt = findViewById(R.id.textViewCreatedAt)
        textViewDescription = findViewById(R.id.textViewDescription)
        linearLayoutContentContainer = findViewById(R.id.linearLayoutContentContainer)
        imageViewArticle = findViewById(R.id.imageViewArticle)

        // Set up toolbar
        setupToolbar()

        val articleId = intent.getStringExtra(EXTRA_ARTICLE_ID)

        if (articleId != null) {
            fetchArticle(articleId)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar)) // Mengatur toolbar sebagai ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Menampilkan tombol kembali
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail Artikel" // Mengatur judul toolbar

        // Menambahkan aksi pada tombol kembali
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed() // Fungsi untuk kembali ke activity sebelumnya
        }
    }

    private fun fetchArticle(articleId: String) {
        firestore.collection("articles").document(articleId).get()
            .addOnSuccessListener { document ->
                val article = document.toObject(Article::class.java)
                if (article != null) {
                    displayArticle(article)
                }
            }
            .addOnFailureListener {
                // Tambahkan penanganan error jika diperlukan
            }
    }

    private fun displayArticle(article: Article) {
        textViewTitle.text = article.title
        textViewCreatedAt.text = article.createdAt.toString()
        textViewDescription.text = article.description

        // Render konten berdasarkan tipe
        article.content.forEach { item ->
            when (item.type) {
                "heading" -> addHeadingView(item.text)
                "paragraph" -> addParagraphView(item.text)
            }
        }

        Glide.with(this)
            .load(article.imageUrl)
            .into(imageViewArticle)
    }

    private fun addHeadingView(heading: String) {
        val headingTextView = TextView(this).apply {
            text = heading
            // Set text size to normal (default size)
            textSize = 16f // You can set this to any value you prefer
            setTypeface(null, android.graphics.Typeface.NORMAL) // Make the heading bold
            setPadding(0, 16, 0, 16)
        }
        linearLayoutContentContainer.addView(headingTextView, createLayoutParams())
    }

    private fun addParagraphView(paragraph: String) {
        val paragraphTextView = TextView(this).apply {
            text = paragraph
            // Set text size to normal (default size)
            textSize = 16f // You can adjust the paragraph size as needed
            setTypeface(null, android.graphics.Typeface.NORMAL) // Make the paragraph regular (non-bold)
            setPadding(0, 8, 0, 8)
        }
        linearLayoutContentContainer.addView(paragraphTextView, createLayoutParams())
    }


    private fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
