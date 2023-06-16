package com.example.ecocrafters.ui.saved_post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.SavedPostsResponse
import com.example.ecocrafters.databinding.FragmentSavedPostBinding
import com.example.ecocrafters.ui.adapter.PostInfoAdapter
import com.example.ecocrafters.ui.post.PostActivity
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showCommentBottomSheetDialog
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch


class SavedPostFragment : Fragment() {
    private var binding: FragmentSavedPostBinding? = null
    private val viewModel: SavedPostViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            toolbarSavedPost.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbarSavedPost.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        lifecycleScope.launch {
            viewModel.updateSavedPost()
        }
        startSubscription()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.savedPostState.collect {
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

    private fun renderResult(result: ResultOf<SavedPostsResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.posts?.isNotEmpty() == true) {
                    val rvAdapter = PostInfoAdapter(result.data.posts).apply {
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
                        setOnCommentCallback { postId ->
                            showCommentBottomSheetDialog(requireContext()) {
                                viewModel.updateCommentPost(postId, it)
                            }
                        }
                        setOnUserClickCallback {
                            val intent = Intent(requireActivity(), UserActivity::class.java).apply {
                                putExtra(UserActivity.ARG_USERNAME, it)
                            }
                            startActivity(intent)
                        }
                    }
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    binding?.rvSavedPost?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyHome?.isVisible = true
                }
            }

            is ResultOf.Error -> {
                showLoading(false)
                requireContext().showToast(result.error)
            }

            else -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            pbHome.isVisible = isLoading
            tvEmptyHome.isVisible = false
            rvSavedPost.isVisible = !isLoading
        }
    }
}