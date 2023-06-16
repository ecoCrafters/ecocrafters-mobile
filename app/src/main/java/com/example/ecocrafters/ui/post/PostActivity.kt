package com.example.ecocrafters.ui.post

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostDetailResponse
import com.example.ecocrafters.databinding.ActivityPostBinding
import com.example.ecocrafters.ui.adapter.PostDetailCommentAdapter
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.InstantHelper
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.isInvalidUrl
import com.example.ecocrafters.utils.loadRectImage
import com.example.ecocrafters.utils.loadRoundImage
import com.example.ecocrafters.utils.showCommentBottomSheetDialog
import com.example.ecocrafters.utils.showToast
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPostBinding
    private val viewModel: PostViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }

    private var slug: String? = null
    private var postId: Int? = null
    private var isSaved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentSlug = intent?.getStringExtra(ARG_SLUG)
        val intentPostId = intent?.getIntExtra(ARG_POST_ID, -1)

        slug = if (intentSlug != null) {
            intentSlug
        } else {
            showToast(getString(R.string.tidak_ada_post_yang_didapat))
            finish()
            null
        }

        postId = if (intentPostId != null) {
            intentPostId
        } else {
            showToast(getString(R.string.tidak_ada_post_yang_didapat))
            finish()
            null
        }
        startSubscription()

        lifecycleScope.launch {
            viewModel.updatePost(slug ?: "", postId ?: -1)
            viewModel.checkSavedPost(postId ?: -1)
        }

        setSupportActionBar(binding.toolbarDetailPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.apply {
            toolbarDetailPost.setNavigationOnClickListener {
                finish()
            }
            btnLikeDetailPost.setOnClickListener(this@PostActivity)
            btnBookmarkDetailPost.setOnClickListener(this@PostActivity)
            btnCommentDetailPost.setOnClickListener(this@PostActivity)
        }

    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.postState.collect {
                renderResultPost(it)
            }
        }

        lifecycleScope.launch {
            viewModel.likePostState.collect {
                renderResultApiResponse(it)
            }
        }

        lifecycleScope.launch {
            viewModel.likeCommentState.collect {
                renderResultApiResponse(it)
            }
        }

        lifecycleScope.launch {
            viewModel.commentPostState.collect {
                renderResultComment(it)
            }
        }

        lifecycleScope.launch {
            viewModel.savePostState.collect {
                renderResultSave(it)
            }
        }

        lifecycleScope.launch {
            viewModel.savedState.collect { result ->
                when (result) {
                    is ResultOf.Error -> showToast(result.error)
                    is ResultOf.Success -> {
                        isSaved = result.data
                        val buttonColor = if (isSaved) {
                            com.google.android.material.R.attr.colorPrimary
                        } else {
                            com.google.android.material.R.attr.colorOnBackground
                        }
                        binding.btnBookmarkDetailPost.iconTint =
                            MaterialColors.getColorStateListOrNull(
                                this@PostActivity,
                                buttonColor
                            )
                    }

                    else -> {}
                }
            }
        }
    }

    private fun renderResultComment(result: ResultOf<PostApiResponse>?) {
        when (result) {
            is ResultOf.Error -> {
                showToast(result.error)
            }

            is ResultOf.Success -> {
                showToast(result.data.message)
                lifecycleScope.launch {
                    viewModel.updatePost(slug ?: "", postId ?: -1)
                }
            }

            else -> {}
        }
    }

    private fun renderResultPost(result: ResultOf<PostDetailResponse>?) {
        when (result) {
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                setPostContent(result.data)
            }

            is ResultOf.Error -> {
                showLoading(false)
            }

            null -> {}
        }
    }

    private fun renderResultApiResponse(result: ResultOf<PostApiResponse>?) {
        when (result) {
            is ResultOf.Error -> {
                showToast(result.error)
            }

            is ResultOf.Success -> {
                showToast(result.data.message)
            }

            else -> {}
        }
    }

    private fun renderResultSave(result: ResultOf<PostApiResponse>?) {
        when (result) {
            is ResultOf.Error -> {
                showToast(result.error)
                viewModel.checkSavedPost(postId?:-1)
            }

            is ResultOf.Success -> {
                showToast(result.data.message)
            }

            else -> {}
        }
    }

    private fun setPostContent(post: PostDetailResponse) {
        binding.apply {
            webView.loadData(
                "<style>img{" +
                        "display: block;" +
                        "margin-top: 16px;" +
                        "margin-bottom: 16px;" +
                        "margin-left: auto;" +
                        "margin-right: auto;" +
                        "height: auto;" +
                        "max-width: 100%;}</style>${post.content}",
                "text/html",
                "UTF-8"
            )
            val backgroundColor = MaterialColors.getColor(
                this@PostActivity,
                com.google.android.material.R.attr.backgroundColor,
                Color.TRANSPARENT
            )
            webView.setBackgroundColor(backgroundColor)

            if (!post.thumbnail.isInvalidUrl()) {
                ivThumbnailDetailPost.loadRectImage(post.thumbnail)
            }
            val durationString =
                if (post.createdAt.isBlank()) "..." else InstantHelper.toBetweenNowString(post.createdAt)
            tvUsernameDetailPost.text =
                String.format("%1\$s • %2\$s", post.user.username, durationString)
            val avatarUrl =
                if (post.user.avatar.isInvalidUrl()) post.user.avatarUrl else post.user.avatar
            ivAvatarsDetailPost.loadRoundImage(avatarUrl)

            tvTitleDetailPost.text = post.title
            tvNumLikesDetailPost.text = post.numOfLikes.toString()
            tvNumCommentsDetailPost.text = post.numOfComments.toString()

            cvUserDetailPost.setOnClickListener {
                val intent = Intent(this@PostActivity, UserActivity::class.java).apply {
                    putExtra(UserActivity.ARG_USERNAME, post.user.username)
                }
                startActivity(intent)
            }

            val rvAdapter = PostDetailCommentAdapter(post.comments).apply {
                setOnLikeCallback {
                    viewModel.updateLikeComment(it)
                }
            }
            val rvLayoutManager = LinearLayoutManager(this@PostActivity)
            rvCommentDetailPost.apply {
                adapter = rvAdapter
                layoutManager = rvLayoutManager
            }

            post.tag.forEach {
                chipGroupDetailPost.addView(
                    Chip(this@PostActivity).apply {
                        text = it.tag
                        id = it.id
                    }
                )
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            webView.isVisible = !isLoading
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v?.id) {
                btnLikeDetailPost.id -> {
                    viewModel.updateLikePost(postId ?: -1)
                }

                btnCommentDetailPost.id -> {
                    showCommentBottomSheetDialog(this@PostActivity) {
                        viewModel.updateCommentPost(postId ?: -1, it)
                    }
                }

                btnBookmarkDetailPost.id -> {
                    if (isSaved) {
                        viewModel.updateUnsavePost(postId ?: -1)
                    } else {
                        viewModel.updateSavePost(postId ?: -1)
                    }
                }
            }
        }
    }

    companion object {
        const val ARG_SLUG = "slug"
        const val ARG_POST_ID = "post_id"
    }

}