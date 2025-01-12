package com.example.femlife.ui.activities.article.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.femlife.R
import com.example.femlife.data.article.Article
import com.example.femlife.databinding.ItemArticleManagerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleManagerAdapter(
    private val onEdit: (Article) -> Unit,
    private val onDelete: (Article) -> Unit
) : ListAdapter<Article, ArticleManagerAdapter.ViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleManagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class ViewHolder(private val binding: ItemArticleManagerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.textArticleTitle.text = article.title
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.textArticleDate.text = dateFormat.format(article.createdAt)

            Glide.with(binding.root.context)
                .load(article.imageUrl)
                .into(binding.imageArticle)

            binding.buttonOptions.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.menu_article_manager_item)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_edit_article -> {
                            onEdit(article)
                            true
                        }
                        R.id.action_delete_article -> {
                            onDelete(article)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
