package com.example.ecocrafters.ui.user

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import com.example.ecocrafters.databinding.ActivityUserBinding
import com.example.ecocrafters.ui.adapter.UserStateAdapter
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

class UserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserBinding
    private var username: String? = null
    private var userId: Int? = null
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentUsername = intent?.getStringExtra(ARG_USERNAME)

        username = if (intentUsername != null) {
            intentUsername
        } else {
            showToast(getString(R.string.tidak_ada_username_yang_ditemukan))
            finish()
            null
        }

        startSubscription()

        loadTabLayout()

        lifecycleScope.launch {
            viewModel.updateUserProfile(username ?: getString(R.string.admin))
        }

        binding.apply {
            btnBackUser.setOnClickListener(this@UserActivity)
            btnFollowUser.setOnClickListener(this@UserActivity)
            btnUnfollowUser.setOnClickListener(this@UserActivity)
        }
    }

    private fun loadTabLayout() {
        val mAccountAdapter = UserStateAdapter(this, username ?: getString(R.string.admin))
        val viewPager: ViewPager2 = binding.vpUser
        viewPager.adapter = mAccountAdapter
        val tabs: TabLayout = binding.tlUser
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect {
                    renderResultUser(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.followState.collect {
                    renderResultFollow(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.followActionState.collect {
                    renderResultAction(it)
                }
            }
        }
    }

    private fun renderResultAction(result: ResultOf<PostApiResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoadingFollow(true)
            is ResultOf.Success -> {
                showLoadingFollow(false)
                lifecycleScope.launch {
                    viewModel.updateFollowStatus(userId ?: -1)
                }
            }

            is ResultOf.Error -> {
                showLoadingFollow(false)
                showToast(result.error)
                Log.d("error", result.error)
            }

            else -> {}
        }
    }

    private fun renderResultFollow(result: ResultOf<Boolean>?) {
        when (result) {
            ResultOf.Loading -> showLoadingFollow(true)
            is ResultOf.Success -> {
                showLoadingFollow(false)
                val isUserFollowed = result.data
                binding.apply {
                    btnFollowUser.isVisible = !isUserFollowed
                    btnUnfollowUser.isVisible = isUserFollowed
                }
            }

            is ResultOf.Error -> {
                showLoadingFollow(false)
                showToast(result.error)
                Log.d("error", result.error)
            }

            else -> {}
        }
    }

    private fun showLoadingFollow(isLoading: Boolean) {
        binding.apply {
            pbUser.isVisible = isLoading
            btnFollowUser.isVisible = false
            btnUnfollowUser.isVisible = false
        }
    }

    private fun renderResultUser(result: ResultOf<UserProfileResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                setProfile(result.data)
            }

            is ResultOf.Error -> {
                showLoading(false)
                showToast(result.error)
                Log.d("error", result.error)
            }

            else -> {}
        }
    }

    private fun setProfile(response: UserProfileResponse) {
        binding.apply {
            val avatarUrl =
                if (response.avatar.isInvalidUrl()) response.avatarUrl else response.avatar
            ivAvatarsUser.loadRoundImage(avatarUrl)
            ivBannerUser.loadRectImage(avatarUrl)
            tvUsernameUser.text = response.username
            tvFullnameUser.text = response.fullName
            tvSubtitleUser.text = String.format(
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
        userId = response.id
        lifecycleScope.launch {
            viewModel.updateFollowStatus(response.id)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbUser.isVisible = isLoading
            ivAvatarsUser.isVisible = !isLoading
            ivBannerUser.isVisible = !isLoading
            tvSubtitleUser.isVisible = !isLoading
            tvUsernameUser.isVisible = !isLoading
        }
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v.id) {
                btnBackUser.id -> {
                    finish()
                }

                btnFollowUser.id -> {
                    if (!pbUser.isVisible) {
                        lifecycleScope.launch {
                            viewModel.followUser(userId ?: -1)
                        }
                    }
                }

                btnUnfollowUser.id -> {
                    if (!pbUser.isVisible) {
                        lifecycleScope.launch {
                            viewModel.unfollowUser(userId ?: -1)
                        }
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