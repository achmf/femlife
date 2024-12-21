package com.example.femlife.ui.activities.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.femlife.data.article.Article
import com.example.femlife.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter(
    private var articles: MutableList<Article>,
    private val onArticleClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position], onArticleClick)
    }

    override fun getItemCount() = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article, onArticleClick: (Article) -> Unit) {
            binding.textViewTitle.text = article.title
            binding.textViewDescription.text = article.description

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id"))
            binding.textViewCreatedAt.text = dateFormat.format(article.createdAt)

            binding.buttonViewArticle.setOnClickListener { onArticleClick(article) }

            Glide.with(binding.root.context)
                .load(article.imageUrl)
                .centerCrop()
                .into(binding.imageViewThumbnail)
        }
    }
}

