package com.example.ecocrafters.ui.user_comment

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
import com.example.ecocrafters.data.remote.response.UserCommentResponse
import com.example.ecocrafters.databinding.FragmentUserCommentBinding
import com.example.ecocrafters.ui.adapter.UserCommentAdapter
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class UserCommentFragment : Fragment() {

    private var binding: FragmentUserCommentBinding? = null
    private val viewModel: UserCommentViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }

    private var username: String? = null

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
        binding = FragmentUserCommentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()
        viewModel.updateUserComment(username?: getString(R.string.admin))
    }

    private fun startSubscription() {
        lifecycleScope.launch{
                viewModel.userCommentState.collect{
                    renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<UserCommentResponse>?) {
        when(result){
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.comments.isNotEmpty()) {
                    val rvAdapter = UserCommentAdapter(result.data)
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    binding?.rvUserComment?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyUserComment?.isVisible = true
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
            rvUserComment.isVisible = isLoading
            tvEmptyUserComment.isVisible = false
            rvUserComment.isVisible = !isLoading
        }
    }

    companion object {
        private const val ARG_USERNAME = "username"
        @JvmStatic
        fun newInstance(username: String) =
            UserCommentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}