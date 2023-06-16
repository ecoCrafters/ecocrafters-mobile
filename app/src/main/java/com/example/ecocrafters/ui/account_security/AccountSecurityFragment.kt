package com.example.ecocrafters.ui.account_security

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
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.databinding.FragmentAccountSecurityBinding
import com.example.ecocrafters.ui.check_code.CheckCodeActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class AccountSecurityFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentAccountSecurityBinding? = null
    private val viewModel: AccountSecurityViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountSecurityBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            cvChangePassword.setOnClickListener(this@AccountSecurityFragment)
            toolbarAccountSecurity.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbarAccountSecurity.setNavigationOnClickListener{
                findNavController().popBackStack()
            }
        }
        startSubscription()
    }

    override fun onClick(v: View) {
        binding?.apply {
            when (v.id) {
                cvChangePassword.id -> {
                    lifecycleScope.launch {
                        viewModel.sendChangePasswordRequest()
                    }
                }
            }
        }
    }

    private fun startSubscription() {
        lifecycleScope.launch {
                viewModel.passwordRequest.collect {
                    renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<PostApiResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                requireContext().showToast(result.data.message)
                val intent = Intent(requireActivity(), CheckCodeActivity::class.java)
                startActivity(intent)
            }

            is ResultOf.Error -> {
                showLoading(false)
                requireContext().showToast(result.error)
            }
            else -> {
                // Do Nothing
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            overlayAccountSecurity.isVisible = isLoading
            pbAccountSecurity.isVisible = isLoading
        }
    }
}