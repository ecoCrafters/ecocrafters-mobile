package com.example.ecocrafters.ui.user_post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.UserPostsResponse
import com.example.ecocrafters.databinding.FragmentUserPostBinding
import com.example.ecocrafters.ui.adapter.UserPostAdapter
import com.example.ecocrafters.ui.post.PostActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showCommentBottomSheetDialog
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class UserPostFragment : Fragment() {
    private var username: String? = null

    private var binding: FragmentUserPostBinding? = null
    private val viewModel: UserPostViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()
        viewModel.updateUserPosts(username ?: getString(R.string.admin))
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.userPostsState.collect {
                renderResult(it)
            }
        }
        lifecycleScope.launch {
            viewModel.likePostState.collect {
                renderResultApiResponse(it)
            }
        }
        lifecycleScope.launch {
            viewModel.commentPostState.collect {
                renderResultApiResponse(it)
            }
        }
        lifecycleScope.launch {
            viewModel.savePostState.collect {
                renderResultApiResponse(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<UserPostsResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.posts.isNotEmpty()) {
                    val rvAdapter = UserPostAdapter(result.data).apply {
                        setOnLikeCallback {
                            lifecycleScope.launch {
                                viewModel.updateLikePost(it)
                            }
                        }
                        setOnSaveCallback {
                            lifecycleScope.launch {
                                viewModel.updateSavePost(it)
                            }
                        }
                        setOnItemClickCallback {
                            val intent = Intent(requireActivity(), PostActivity::class.java).apply {
                                putExtra(PostActivity.ARG_SLUG, it.slug)
                                putExtra(PostActivity.ARG_POST_ID, it.id)
                            }
                            startActivity(intent)
                        }
                        setOnCommentCallback {postId ->
                            showCommentBottomSheetDialog(requireContext()) {
                                viewModel.updateCommentPost(postId,it)
                            }
                        }
                    }
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    binding?.rvUserPosts?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyUserPosts?.isVisible = true
                }
            }

            is ResultOf.Error -> {
                showLoading(false)
                requireContext().showToast(result.error)
            }

            else -> {}
        }
    }

    private fun renderResultApiResponse(result: ResultOf<PostApiResponse>?) {
        when (result) {
            is ResultOf.Error -> {
                requireContext().showToast(result.error)
            }

            is ResultOf.Success -> {
                requireContext().showToast(result.data.message)
            }

            else -> {}
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            pbUserPosts.isVisible = isLoading
            tvEmptyUserPosts.isVisible = false
            rvUserPosts.isVisible = !isLoading
        }
    }

    companion object {
        const val ARG_USERNAME = "username"

        @JvmStatic
        fun newInstance(username: String) =
            UserPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}