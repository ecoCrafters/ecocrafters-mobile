package com.example.ecocrafters.ui.myaccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import com.example.ecocrafters.databinding.ActivityMyAccountBinding
import com.example.ecocrafters.ui.adapter.UserStateAdapter
import com.example.ecocrafters.ui.edit_account.EditAccountActivity
import com.example.ecocrafters.utils.InstantHelper
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.isInvalidUrl
import com.example.ecocrafters.utils.loadRectImage
import com.example.ecocrafters.utils.loadRoundImage
import com.example.ecocrafters.utils.showToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.util.Locale

class MyAccountActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMyAccountBinding
    private val viewModel: MyAccountViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }
    private var username: String? = null
    private var fullName: String? = null
    private var avatar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startSubscription()

        binding.apply {
            btnBackMyAccount.setOnClickListener(this@MyAccountActivity)
            btnEditMyAccount.setOnClickListener(this@MyAccountActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            Log.d("Updated", "Updated Ture")
            viewModel.updateMyAccountProfile()
        }
    }

    private fun loadTabLayout(username: String) {
        val mAccountAdapter = UserStateAdapter(this, username)
        val viewPager: ViewPager2 = binding.vpMyAccount
        viewPager.adapter = mAccountAdapter
        val tabs: TabLayout = binding.tlMyAccount
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.myAccountState.collect {
                renderResult(it)
            }
        }
    }

    private fun renderResult(result: ResultOf<UserProfileResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                setProfile(result.data)
                loadTabLayout(result.data.username)
            }

            is ResultOf.Error -> {
                showLoading(false)
                showToast(result.error)
            }

            null -> showLoading(false)
        }
    }

    private fun setProfile(response: UserProfileResponse) {
        val avatarUrl =
            if (response.avatar.isInvalidUrl()) response.avatarUrl else response.avatar
        username = response.username
        fullName = response.fullName
        avatar = avatarUrl
        val myProfile = MyProfile(
            response.email,
            response.fullName,
            response.username,
            avatarUrl,
            response.ecoPoints
        )
        lifecycleScope.launch {
            viewModel.saveMyProfile(myProfile)
        }
        binding.apply {
            ivAvatarsMyAccount.loadRoundImage(avatarUrl)
            ivBannerMyAccount.loadRectImage(avatarUrl)
            tvUsernameMyAccount.text = response.username
            tvFullnameMyAccount.text = response.fullName
            tvSubtitleMyAccount.text = String.format(
                Locale.getDefault(),
                getString(R.string.user_subtitle_placeholder),
                response.ecoPoints,
                response.createdAt.let {
                    if (it.isNullOrBlank()) {
                        getString(R.string.tidak_diketahui)
                    } else {
                        InstantHelper.toDateString(it)
                    }
                }
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbMyAccount.isVisible = isLoading
            ivAvatarsMyAccount.isVisible = !isLoading
            ivBannerMyAccount.isVisible = !isLoading
            tvSubtitleMyAccount.isVisible = !isLoading
            tvUsernameMyAccount.isVisible = !isLoading
        }
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v.id) {
                btnBackMyAccount.id -> {
                    finish()
                }

                btnEditMyAccount.id -> {
                    if (!pbMyAccount.isVisible) {
                        val intent =
                            Intent(
                                this@MyAccountActivity,
                                EditAccountActivity::class.java
                            ).apply {
                                putExtra(EditAccountActivity.ARG_USERNAME, username)
                                putExtra(EditAccountActivity.ARG_FULLNAME, fullName)
                                putExtra(EditAccountActivity.ARG_AVATAR, avatar)
                            }
                        startActivity(intent)
                    }
                }
            }

        }
    }

    companion object {
        const val ARG_USERNAME = "username"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.post,
            R.string.komen,
            R.string.pengikut,
            R.string.mengikuti,
            R.string.tentang
        )
    }
}