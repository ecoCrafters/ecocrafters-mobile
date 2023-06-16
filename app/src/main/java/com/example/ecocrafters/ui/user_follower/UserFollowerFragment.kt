package com.example.ecocrafters.ui.user_follower

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
import com.example.ecocrafters.data.remote.response.UserFollowersResponse
import com.example.ecocrafters.databinding.FragmentUserFollowerBinding
import com.example.ecocrafters.ui.adapter.UserFollowerAdapter
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class UserFollowerFragment : Fragment() {
    private var binding: FragmentUserFollowerBinding? = null
    private var username: String? = null
    private val viewModel: UserFollowerViewModel by viewModels {
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
        binding = FragmentUserFollowerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()
        viewModel.updateUserFollower(username ?: getString(R.string.admin))
    }

    private fun startSubscription() {
        lifecycleScope.launch {
                viewModel.userFollowerState.collect {
                    renderResult(it)
                }
        }
    }

    private fun renderResult(result: ResultOf<UserFollowersResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.followers.isNotEmpty()) {
                    val rvAdapter = UserFollowerAdapter(result.data.followers)
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    rvAdapter.setOnItemClickCallback { followerItem ->
                        val intent = Intent(requireActivity(), UserActivity::class.java).apply {
                            putExtra(UserActivity.ARG_USERNAME, followerItem.username)
                        }
                        startActivity(intent)
                    }
                    binding?.rvUserFollower?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyUserFollower?.isVisible = true
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
            pbUserFollower.isVisible = isLoading
            tvEmptyUserFollower.isVisible = false
            rvUserFollower.isVisible = !isLoading
        }
    }

    companion object {
        private const val ARG_USERNAME = "username"

        @JvmStatic
        fun newInstance(username: String) =
            UserFollowerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}