package com.example.femlife.ui.fragments.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.data.chat.ChatGroup
import com.example.femlife.databinding.FragmentChatBinding
import com.example.femlife.ui.fragments.chats.activity.chat.ChatDetailActivity
import com.example.femlife.ui.fragments.chats.activity.chat.ChatViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatGroupsAdapter
    private val viewModel: ChatViewModel by viewModels()

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
        observeChatGroups()
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

    private fun observeChatGroups() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatGroups.collect { chatGroups ->
                    chatAdapter.submitList(chatGroups)
                }
            }
        }
    }

    private fun navigateToChatDetail(chatGroup: ChatGroup) {
        ChatDetailActivity.start(requireContext(), chatGroup.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

