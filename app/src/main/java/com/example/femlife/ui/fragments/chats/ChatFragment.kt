package com.example.femlife.ui.fragments.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.data.chat.ChatGroup
import com.example.femlife.databinding.FragmentChatBinding
import com.example.femlife.ui.fragments.chats.activity.chat.ChatDetailActivity

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatGroupsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadChatGroups()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatGroupsAdapter { chatGroup ->
            navigateToChatDetail(chatGroup)
        }

        binding.rvChats.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun loadChatGroups() {
        val femnonymChat = ChatGroup(
            id = "1",
            name = "Femnonym",
            imageUrl = "https://example.com/femnonym_avatar.jpg",
            lastMessage = "Welcome to Femnonym chat!",
            lastMessageTimestamp = System.currentTimeMillis()
        )
        chatAdapter.submitList(listOf(femnonymChat))
    }

    private fun navigateToChatDetail(chatGroup: ChatGroup) {
        ChatDetailActivity.start(requireContext(), chatGroup.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}