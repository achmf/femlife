package com.example.femlife.ui.fragments.chats.activity.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.databinding.ActivityChatDetailBinding
import kotlinx.coroutines.launch

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var messageAdapter: MessageAdapter
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatId = intent.getStringExtra(EXTRA_CHAT_ID)
            ?: throw IllegalStateException("Chat ID is required")

        setupUI()
        observeMessages(chatId)
        setupMessageSending(chatId)
    }

    private fun setupUI() {
        messageAdapter = MessageAdapter()
        binding.rvMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun observeMessages(chatId: String) {
        viewModel.loadMessages(chatId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collect { messages ->
                    messageAdapter.submitList(messages)
                    binding.rvMessages.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

    private fun setupMessageSending(chatId: String) {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(chatId, message)
                binding.etMessage.text.clear()
            }
        }
    }

    companion object {
        const val EXTRA_CHAT_ID = "extra_chat_id"

        fun start(context: Context, chatId: String) {
            context.startActivity(Intent(context, ChatDetailActivity::class.java).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
            })
        }
    }
}