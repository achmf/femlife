// DetailArticleActivity.kt
package com.example.femlife.ui.activities.article.detail

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var textViewContent: TextView
    private lateinit var imageViewArticle: ImageView

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)

        // Bind views
        textViewTitle = findViewById(R.id.textViewTitle)
        textViewCreatedAt = findViewById(R.id.textViewCreatedAt)
        textViewDescription = findViewById(R.id.textViewDescription)
        textViewContent = findViewById(R.id.textViewContent)
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
            }
    }

    private fun displayArticle(article: Article) {
        // Menampilkan data artikel
        textViewTitle.text = article.title
        textViewCreatedAt.text = article.createdAt.toString()
        textViewDescription.text = article.description
        textViewContent.text = article.content

        // Menampilkan gambar artikel menggunakan Glide
        Glide.with(this)
            .load(article.imageUrl)
            .into(imageViewArticle) // Menampilkan gambar artikel
    }
}
