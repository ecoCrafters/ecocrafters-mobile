package com.example.ecocrafters.ui.search_post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import com.example.ecocrafters.databinding.FragmentSearchPostBinding
import com.example.ecocrafters.ui.adapter.PostInfoAdapter
import com.example.ecocrafters.ui.post.PostActivity
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showCommentBottomSheetDialog
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class SearchPostFragment : Fragment() {
    private var binding: FragmentSearchPostBinding? = null
    private var searchTerm: String? = null

    private val viewModel: SearchPostViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchTerm = it.getString(ARG_SEARCH_TERM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchPostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()

        val mSearchTerm = searchTerm
        Log.d("mSearchTerm", mSearchTerm ?: "Kosong")
        if (!mSearchTerm.isNullOrBlank()) {
            viewModel.updateSearchUser(mSearchTerm)
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
                viewModel.searchPostState.collect {
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

    private fun renderResult(result: ResultOf<List<PostInfoResponse>>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.isNotEmpty()) {
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    val rvAdapter = PostInfoAdapter(result.data).apply {
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
                        setOnUserClickCallback {
                            val intent = Intent(requireActivity(), UserActivity::class.java).apply {
                                putExtra(UserActivity.ARG_USERNAME, it)
                            }
                            startActivity(intent)
                        }
                    }
                    binding?.rvSearchPost?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyPostSearch?.isVisible = true
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
            pbSearchPost.isVisible = isLoading
            tvEmptyPostSearch.isVisible = false
            rvSearchPost.isVisible = !isLoading
        }
    }

    companion object {
        const val ARG_SEARCH_TERM = "search_term"

        @JvmStatic
        fun newInstance(searchTerm: String) =
            SearchPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SEARCH_TERM, searchTerm)
                }
            }
    }
}