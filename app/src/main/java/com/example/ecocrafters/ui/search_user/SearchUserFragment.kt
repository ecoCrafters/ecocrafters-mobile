package com.example.ecocrafters.ui.search_user

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
import com.example.ecocrafters.data.remote.response.UserInfo
import com.example.ecocrafters.databinding.FragmentSearchUserBinding
import com.example.ecocrafters.ui.adapter.SearchUserAdapter
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class SearchUserFragment : Fragment() {

    private var binding: FragmentSearchUserBinding? = null
    private val viewModel: SearchUserViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }
    private var searchTerm: String? = null

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
        binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSubscription()
        if(!searchTerm.isNullOrBlank()) {
            lifecycleScope.launch {
                val formattedSearchTerm = (searchTerm ?: "").replace(' ', '_')
                viewModel.updateSearchUser(formattedSearchTerm)
            }
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
                viewModel.searchUserState.collect {
                    renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<List<UserInfo>>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.isNotEmpty()) {
                    val rvAdapter = SearchUserAdapter(result.data)
                    val rvLayoutManager = LinearLayoutManager(requireContext())
                    rvAdapter.setOnItemClickCallback { userInfo ->
                        val intent = Intent(requireActivity(), UserActivity::class.java).apply {
                            putExtra(UserActivity.ARG_USERNAME, userInfo.username)
                        }
                        startActivity(intent)
                    }
                    binding?.rvSearchUser?.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding?.tvEmptyUserSearch?.isVisible = true
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
            pbSearchUser.isVisible = isLoading
            tvEmptyUserSearch.isVisible = false
            rvSearchUser.isVisible = !isLoading
        }
    }

    companion object {
        private const val ARG_SEARCH_TERM = "username"

        @JvmStatic
        fun newInstance(searchTerm: String) =
            SearchUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SEARCH_TERM, searchTerm)
                }
            }
    }
}