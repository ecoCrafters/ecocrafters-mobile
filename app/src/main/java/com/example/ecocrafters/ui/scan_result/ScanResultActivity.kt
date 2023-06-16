package com.example.ecocrafters.ui.scan_result

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.DetectImageResponse
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.databinding.ActivityScanResultBinding
import com.example.ecocrafters.ui.adapter.PostInfoAdapter
import com.example.ecocrafters.ui.post.PostActivity
import com.example.ecocrafters.ui.user.UserActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showCommentBottomSheetDialog
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch
import java.io.File

class ScanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanResultBinding
    private var file: File? = null
    private val viewModel: ScanResultViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFile = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> intent?.getSerializableExtra(
                ARG_IMAGE_CAPTURED,
                File::class.java
            )

            else -> @Suppress("DEPRECATION") intent?.getSerializableExtra(ARG_IMAGE_CAPTURED) as File
        }

        file = if (intentFile != null) {
            intentFile
        } else {
            showToast("Tidak Ada Gambar Yang Ditemukan")
            finish()
            null
        }

        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarScanResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        startSubscription()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.detectImageState.collect{
                renderResult(it)
            }
        }
        lifecycleScope.launch {
            viewModel.likePostState.collect {
                renderResultApiResponse(it)
            }
        }

        lifecycleScope.launch {
            viewModel.commentPostState.collect {
                renderResultApiResponse(it)
            }
        }

        lifecycleScope.launch {
            viewModel.savePostState.collect {
                renderResultApiResponse(it)
            }
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

    private fun renderResult(result: ResultOf<DetectImageResponse>?) {
        when(result){
            ResultOf.Loading -> showLoading(true)
            is ResultOf.Success -> {
                showLoading(false)
                if (result.data.posts.isNotEmpty()) {
                    val rvAdapter = PostInfoAdapter(result.data.posts).apply {
                        setOnLikeCallback {
                            lifecycleScope.launch {
                                viewModel.updateLikePost(it)
                            }
                        }
                        setOnSaveCallback {
                            lifecycleScope.launch {
                                viewModel.updateSavePost(it)
                            }
                        }
                        setOnItemClickCallback {
                            val intent = Intent(this@ScanResultActivity, PostActivity::class.java).apply {
                                putExtra(PostActivity.ARG_SLUG, it.slug)
                                putExtra(PostActivity.ARG_POST_ID, it.id)
                            }
                            startActivity(intent)
                        }
                        setOnCommentCallback { postId ->
                            showCommentBottomSheetDialog(this@ScanResultActivity) {
                                viewModel.updateCommentPost(postId, it)
                            }
                        }
                        setOnUserClickCallback {
                            val intent = Intent(this@ScanResultActivity, UserActivity::class.java).apply {
                                putExtra(UserActivity.ARG_USERNAME, it)
                            }
                            startActivity(intent)
                        }
                    }
                    val rvLayoutManager = LinearLayoutManager(this@ScanResultActivity)
                    binding.rvSearchPost.apply {
                        adapter = rvAdapter
                        layoutManager = rvLayoutManager
                    }
                } else {
                    binding.tvEmptyPostSearch.isVisible = true
                }
            }
            is ResultOf.Error -> {
                showLoading(false)
            }
            null -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            rvSearchPost.isVisible = !isLoading
            pbSearchPost.isVisible = isLoading
            tvEmptyPostSearch.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ARG_IMAGE_CAPTURED = "image_captured"
    }
}