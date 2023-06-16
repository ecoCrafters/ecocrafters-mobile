package com.example.ecocrafters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import com.example.ecocrafters.databinding.ItemPostBinding
import com.example.ecocrafters.utils.InstantHelper
import com.example.ecocrafters.utils.isInvalidUrl
import com.example.ecocrafters.utils.loadRectImage
import com.example.ecocrafters.utils.loadRoundImage
import com.example.ecocrafters.utils.removeHtmlTag
import com.google.android.material.color.MaterialColors

class PostInfoAdapter(private val postList: List<PostInfoResponse>) :
    RecyclerView.Adapter<PostInfoAdapter.ViewHolder>() {

    private var onItemClickCallback: ((postInfoResponse: PostInfoResponse) -> Unit)? = null

    fun setOnItemClickCallback(callback: (postInfoResponse: PostInfoResponse) -> Unit) {
        onItemClickCallback = callback
    }

    private var onLikeCallback: ((postsId: Int) -> Unit)? = null

    fun setOnLikeCallback(callback: (postsId: Int) -> Unit) {
        onLikeCallback = callback
    }

    private var onCommentCallback: ((postsId: Int) -> Unit)? = null

    fun setOnCommentCallback(callback: (postsId: Int) -> Unit) {
        onCommentCallback = callback
    }

    private var onSaveCallback: ((postsId: Int) -> Unit)? = null

    fun setOnSaveCallback(callback: (postsId: Int) -> Unit) {
        onSaveCallback = callback
    }

    private var onUserClickCallback: ((username: String) -> Unit)? = null

    fun setOnUserClickCallback(callback: (username: String) -> Unit) {
        onUserClickCallback = callback
    }

    class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: PostInfoResponse,
            onClickCallback: ((postInfoResponse: PostInfoResponse) -> Unit)?,
            onLikeCallback: ((postsId: Int) -> Unit)?,
            onCommentCallback: ((postsId: Int) -> Unit)?,
            onSaveCallback: ((postsId: Int) -> Unit)?,
            onUserClickCallback: ((username: String) -> Unit)?
        ) {
            binding.apply {
                item.apply {
                    tvPostTitle.text = title
                    tvPostComments.text = numOfComments.toString()
                    tvPostEcopoints.text = numOfLikes.toString()
                    tvPostContent.text =
                        content.removeHtmlTag().trim().replace("[\r\n]+", "\n")
                    val durationString = createdAt?.let{ InstantHelper.toBetweenNowString(it)} ?: "..."
                    tvPostUsername.text = String.format("%1\$s • %2\$s",user.username, durationString)
                    val avatarUrl =
                        if (user.avatar.isInvalidUrl()) user.avatarUrl else user.avatar
                    ivPostAvatars.loadRoundImage(avatarUrl)
                    if (thumbnail.isNotBlank()) {
                        ivPostImage.isVisible = true
                        ivPostImage.loadRectImage(thumbnail)
                    } else {
                        ivPostImage.isVisible = false
                    }
                    if (onClickCallback != null) {
                        root.setOnClickListener {
                            onClickCallback(item)
                        }
                    }
                    if (onLikeCallback != null) {
                        btnPostLike.setOnClickListener {
                            onLikeCallback(id)
                            btnPostLike.iconTint = MaterialColors.getColorStateListOrNull(
                                btnPostLike.context,
                                com.google.android.material.R.attr.colorPrimary
                            )
                        }
                    }
                    if (onCommentCallback != null) {
                        btnPostComments.setOnClickListener {
                            onCommentCallback(id)
                        }
                    }
                    if (onSaveCallback != null) {
                        btnPostSave.setOnClickListener {
                            onSaveCallback(id)
                            btnPostSave.iconTint = MaterialColors.getColorStateListOrNull(
                                btnPostSave.context,
                                com.google.android.material.R.attr.colorPrimary
                            )
                        }
                    }
                    if (onUserClickCallback != null) {
                        cvPostUser.setOnClickListener {
                            onUserClickCallback(userId.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            postList[position],
            onItemClickCallback,
            onLikeCallback,
            onCommentCallback,
            onSaveCallback,
            onUserClickCallback
        )
    }

    override fun getItemCount(): Int = postList.size
}