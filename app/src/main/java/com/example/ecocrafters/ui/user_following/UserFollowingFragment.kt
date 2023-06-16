package com.example.ecocrafters.ui.user_following

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
import com.example.ecocrafters.data.remote.response.UserFollowingResponse
import com.example.ecocrafters.databinding.FragmentUserFollowingBinding
import com.example.ecocrafters.ui.adapter.UserFollowingAdapter
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class UserFollowingFragment : Fragment() {
    private var binding: FragmentUserFollowingBinding? = null
    private var username: String? = null
    private val viewModel: UserFollowingViewModel by viewModels {
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
        binding = FragmentUserFollowingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()
//        viewModel.updateUserFollowing(username ?: getString(R.string.admin))
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUserFollowing(username ?: getString(R.string.admin))
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.userFollowingState.collect {
                    renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<UserFollowingResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.followings.isNotEmpty()) {
                    val rvAdapter = UserFollowingAdapter(result.data.followings)
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    rvAdapter.setOnItemClickCallback { followingItem ->
                        val intent = Intent(requireActivity(), UserActivity::class.java).apply {
                            putExtra(UserActivity.ARG_USERNAME, followingItem.username)
                        }
                        startActivity(intent)
                    }
                    binding?.rvUserFollowing?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyUserFollowing?.isVisible = true
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
            pbUserFollowing.isVisible = isLoading
            tvEmptyUserFollowing.isVisible = false
            rvUserFollowing.isVisible = !isLoading
        }
    }

    companion object {
        private const val ARG_USERNAME = "username"

        @JvmStatic
        fun newInstance(username: String) =
            UserFollowingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}