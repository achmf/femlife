package com.example.femlife.ui.fragments.femtalk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.data.femtalk.Post
import com.example.femlife.databinding.FragmentFemTalkBinding
import com.example.femlife.ui.fragments.femtalk.create.CreateTalkActivity

class FemTalkFragment : Fragment() {

    private var _binding: FragmentFemTalkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FemTalkViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    private val createPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.refreshPosts()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFemTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onLikeClick = { post -> viewModel.likePost(post) },
            onCommentClick = { post -> showCommentDialog(post) }
        )
        binding.postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }
    }

    private fun setupFab() {
        binding.fabNewPost.setOnClickListener {
            val intent = Intent(requireContext(), CreateTalkActivity::class.java)
            createPostLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postAdapter.submitList(posts)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun showCommentDialog(post: Post) {
        // TODO: Implement comment dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}