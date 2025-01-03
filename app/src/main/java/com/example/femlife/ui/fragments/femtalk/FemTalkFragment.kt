package com.example.femlife.ui.fragments.femtalk

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.femlife.R
import com.example.femlife.data.femtalk.Post
import com.example.femlife.databinding.FragmentFemTalkBinding
import com.example.femlife.ui.fragments.femtalk.create.CreateTalkActivity
import com.example.femlife.ui.fragments.femtalk.detail.PostDetailActivity
import com.example.femlife.ui.fragments.femtalk.edit.EditTalkActivity
import com.google.firebase.auth.FirebaseAuth

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
            onPostClick = { post -> navigateToPostDetail(post) },
            onCommentClick = { post -> navigateToPostDetail(post) },
            onMenuClick = { post, view -> showPostMenu(post, view) }
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

        viewModel.individualPost.observe(viewLifecycleOwner) { updatedPost ->
            postAdapter.updatePost(updatedPost)
        }
    }

    private fun navigateToPostDetail(post: Post) {
        val intent = Intent(requireContext(), PostDetailActivity::class.java).apply {
            putExtra("POST_ID", post.id)
        }
        startActivity(intent)
    }

    private fun showPostMenu(post: Post, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.post_menu, popupMenu.menu)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val isPostOwner = post.userId == currentUserId

        popupMenu.menu.findItem(R.id.action_edit).isVisible = isPostOwner
        popupMenu.menu.findItem(R.id.action_delete).isVisible = isPostOwner

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    editPost(post)
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmationDialog(post)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun editPost(post: Post) {
        val intent = Intent(requireContext(), EditTalkActivity::class.java).apply {
            putExtra("POST_ID", post.id)
        }
        startActivityForResult(intent, EDIT_POST_REQUEST_CODE)
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                deletePost(post)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePost(post: Post) {
        viewModel.deletePost(post)
    }

    private fun showHelpToast() {
        Toast.makeText(requireContext(), "Help: This is a post in FemTalk", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EDIT_POST_REQUEST_CODE = 2
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_POST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.refreshPosts()
        }
    }
}

