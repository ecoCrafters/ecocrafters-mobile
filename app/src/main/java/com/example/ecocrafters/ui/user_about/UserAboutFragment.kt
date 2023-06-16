package com.example.ecocrafters.ui.user_about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.UserAboutResponse
import com.example.ecocrafters.databinding.FragmentUserAboutBinding
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class UserAboutFragment : Fragment() {
    private var binding: FragmentUserAboutBinding? = null
    private var username: String? = null
    private val viewModel: UserAboutViewModel by viewModels {
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
        binding = FragmentUserAboutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()
        lifecycleScope.launch {
            viewModel.updateUserAbout(username?: "")
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
                viewModel.userAboutState.collect{
                    renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<UserAboutResponse>?) {
        when(result){
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                val data = result.data
                binding?.apply {
                    tvEcopointsAbout.text = data.ecopoints.toString()
                    tvAgeAbout.text = data.accountAge
                    tvFollowerAbout.text = data.followers.toString()
                    tvFollowingAbout.text = data.followings.toString()
                    tvPostAbout.text = data.postCreated.toString()
                    tvCommentAbout.text = data.commentCreated.toString()
                }
            }
            is ResultOf.Error -> {
                showLoading(false)
                requireContext().showToast(result.error)
            }
            null -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.pbUserAbout?.isVisible = isLoading
    }

    companion object {
        private const val ARG_USERNAME = "username"
        @JvmStatic
        fun newInstance(username: String) =
            UserAboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}