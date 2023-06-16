package com.example.ecocrafters.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.databinding.FragmentMoreBinding
import com.example.ecocrafters.ui.myaccount.MyAccountActivity
import com.example.ecocrafters.ui.starting.StartingActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.loadRectImage
import com.example.ecocrafters.utils.loadRoundImage
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch
import java.util.Locale

class MoreFragment : Fragment(), View.OnClickListener {

    private var binding: FragmentMoreBinding? = null
    private val viewModel: MoreViewModel by viewModels {
        ViewModelFactory
            .getInstance(requireContext())
    }
    private var username: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            cvSecurityMore.setOnClickListener(this@MoreFragment)
            cvLogoutMore.setOnClickListener(this@MoreFragment)
            cvProfileMore.setOnClickListener(this@MoreFragment)
            cvSavedPostMore.setOnClickListener(this@MoreFragment)
        }

        lifecycleScope.launch {
            viewModel.updateMyProfile()
        }
        startSubscription()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
                viewModel.logOutState.collect {
                    renderResultLogOut(it)
            }
        }

        lifecycleScope.launch {
                viewModel.myProfileState.collect {
                    renderResultAccount(it)
            }
        }
    }

    private fun renderResultAccount(result: ResultOf<MyProfile?>) {
        when (result) {
            is ResultOf.Success -> {
                if (result.data != null) {
                    setAccountInfo(result.data)
                }
            }
            is ResultOf.Error -> {
                requireContext().showToast(result.error)
            }
            else -> {

            }
        }
    }

    private fun setAccountInfo(accountInfo: MyProfile) {
        username = accountInfo.username
        binding?.apply {
            tvUsernameMore.text = accountInfo.username
            tvUserPointsMore.text =
                String.format(
                    Locale.getDefault(),
                    getString(R.string.ecopoints_format),
                    accountInfo.ecoPoints
                )
            ivUserPhotoMore.loadRoundImage(accountInfo.avatars)
            ivBannerMore.loadRectImage(accountInfo.avatars)
        }
    }

    override fun onClick(v: View) {
        binding?.apply {
            when (v.id) {
                cvSecurityMore.id -> {
                    findNavController().navigate(R.id.navigation_account_security)
                }

                cvLogoutMore.id -> {
                    lifecycleScope.launch {
                        viewModel.logOut()
                    }
                }

                cvSavedPostMore.id -> {
                    findNavController().navigate(R.id.navigation_saved_post)
                }

                cvProfileMore.id -> {
                    val intent = Intent(requireContext(), MyAccountActivity::class.java).apply {
                        putExtra(MyAccountActivity.ARG_USERNAME, username)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun renderResultLogOut(result: ResultOf<PostApiResponse>?) {
        binding?.apply {
            when (result) {
                ResultOf.Loading -> cvLogoutMore.isEnabled = false
                is ResultOf.Success -> {
                    cvLogoutMore.isEnabled = true
                    requireContext().showToast(result.data.message)
                    val intent = Intent(requireActivity(), StartingActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }

                is ResultOf.Error -> {
                    cvLogoutMore.isEnabled = true
                    requireContext().showToast(result.error)
                    if (result.error == "Token Expired"){
                        val intent = Intent(requireActivity(), StartingActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    }
                }

                else -> {}
            }
        }
    }
}

