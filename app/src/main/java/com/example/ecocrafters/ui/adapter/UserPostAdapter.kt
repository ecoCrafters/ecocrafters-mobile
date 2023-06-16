package com.example.ecocrafters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocrafters.data.remote.response.PostItem
import com.example.ecocrafters.data.remote.response.UserPostsResponse
import com.example.ecocrafters.databinding.ItemPostBinding
import com.example.ecocrafters.utils.loadRectImage
import com.example.ecocrafters.utils.loadRoundImage
import com.google.android.material.color.MaterialColors

class UserPostAdapter(private val userPostsResponse: UserPostsResponse) :
    RecyclerView.Adapter<UserPostAdapter.ViewHolder>() {

    private var onItemClickCallback: ((postItem: PostItem) -> Unit)? = null

    fun setOnItemClickCallback(callback: (postItem: PostItem) -> Unit) {
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

    class ViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: PostItem, username: String, userAvatar: String,
            onItemClickCallback: ((postItem: PostItem) -> Unit)?,
            onLikeCallback: ((postsId: Int) -> Unit)?,
            onCommentCallback: ((postsId: Int) -> Unit)?,
            onSaveCallback: ((postsId: Int) -> Unit)?
        ) {
            binding.apply {
                tvPostTitle.text = item.title
                tvPostComments.text = item.numOfComments.toString()
                tvPostEcopoints.text = item.numOfLikes.toString()
                tvPostContent.text = item.content
                tvPostUsername.text = username
                ivPostAvatars.loadRoundImage(userAvatar)
                if (item.thumbnail.isNotBlank()) {
                    ivPostImage.isVisible = true
                    ivPostImage.loadRectImage(item.thumbnail)
                } else {
                    ivPostImage.isVisible = false
                }
                if (onItemClickCallback != null) {
                    root.setOnClickListener {
                        onItemClickCallback(item)
                    }
                }
                if (onLikeCallback != null) {
                    btnPostLike.setOnClickListener {
                        onLikeCallback(item.id)
                        btnPostLike.iconTint = MaterialColors.getColorStateListOrNull(
                            btnPostLike.context,
                            com.google.android.material.R.attr.colorPrimary
                        )
                    }
                }
                if (onCommentCallback != null) {
                    btnPostComments.setOnClickListener {
                        onCommentCallback(item.id)
                    }
                }
                if (onSaveCallback != null) {
                    btnPostSave.setOnClickListener {
                        onSaveCallback(item.id)
                        btnPostSave.iconTint = MaterialColors.getColorStateListOrNull(
                            btnPostSave.context,
                            com.google.android.material.R.attr.colorPrimary
                        )
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
            userPostsResponse.posts[position],
            userPostsResponse.username,
            userPostsResponse.avatar,
            onItemClickCallback,
            onLikeCallback,
            onCommentCallback,
            onSaveCallback
        )
    }

    override fun getItemCount(): Int = userPostsResponse.posts.size
}