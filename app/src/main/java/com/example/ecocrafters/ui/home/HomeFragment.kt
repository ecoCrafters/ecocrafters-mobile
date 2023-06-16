package com.example.ecocrafters.ui.home

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
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import com.example.ecocrafters.databinding.FragmentHomeBinding
import com.example.ecocrafters.ui.adapter.PostInfoAdapter
import com.example.ecocrafters.ui.myaccount.MyAccountActivity
import com.example.ecocrafters.ui.post.PostActivity
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.loadRoundImage
import com.example.ecocrafters.utils.showCommentBottomSheetDialog
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startSubscription()
        viewModel.updateMyProfile()
        viewModel.getAllPost()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.postListState.collect {
                renderResult(it)
            }
        }
        lifecycleScope.launch {
            viewModel.myProfileState.collect {
                renderResultProfile(it)
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

    private fun renderResultProfile(result: ResultOf<MyProfile?>) {
        when (result) {
            is ResultOf.Success -> {
                binding?.apply {
                    if (result.data != null) {
                        ivUserAvatarsHome.elevation = 3f
                        ivUserAvatarsHome.loadRoundImage(result.data.avatars)
                        ivUserAvatarsHome.contentDescription = result.data.username
                        ivUserAvatarsHome.setOnClickListener {
                            val intent = Intent(requireContext(), MyAccountActivity::class.java).apply {
                                putExtra(MyAccountActivity.ARG_USERNAME, result.data.username)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }

            is ResultOf.Error -> {
                requireContext().showToast(result.error)
            }

            else -> {}
        }
    }

    private fun renderResult(result: ResultOf<List<PostInfoResponse>>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.isEmpty()) {
                    binding?.tvEmptyHome?.isVisible = true
                } else {
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
                    binding?.rvHome?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
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
            rvHome.isVisible = !isLoading
            pbHome.isVisible = isLoading
            tvEmptyHome.isVisible = false
        }
    }

}