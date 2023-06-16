package com.example.ecocrafters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocrafters.data.remote.response.CommentsItem
import com.example.ecocrafters.databinding.ItemCommentDetailPostBinding
import com.example.ecocrafters.utils.InstantHelper
import com.example.ecocrafters.utils.isInvalidUrl
import com.example.ecocrafters.utils.loadRoundImage
import com.google.android.material.color.MaterialColors

class PostDetailCommentAdapter(private val commentList: List<CommentsItem>) :
    RecyclerView.Adapter<PostDetailCommentAdapter.ViewHolder>() {

    private var onLikeCallback: ((commentsId: Int) -> Unit)? = null

    fun setOnLikeCallback(callback: (commentsId: Int) -> Unit) {
        onLikeCallback = callback
    }

    class ViewHolder(private val binding: ItemCommentDetailPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var isClicked = false
        fun bind(item: CommentsItem, onLikeCallback: ((commentsId: Int) -> Unit)?) {
            binding.apply {
                val durationString =
                    item.createdAt?.let { InstantHelper.toBetweenNowString(it) } ?: "..."
                tvUsernameCommentUser.text =
                    String.format("%1\$s • %2\$s", item.user.username, durationString)
                tvContentCommentUser.text = item.comment
                tvNumLikesCommentUser.text =
                    String.format("%1\$,d", item.numOfLikes + isClicked.compareTo(false))
                val avatarUrl =
                    if (item.user.avatar.isInvalidUrl()) item.user.avatarUrl else item.user.avatar
                ivAvatarsCommentUser.loadRoundImage(avatarUrl)
                onLikeCallback?.let {
                    btnLikeCommentUser.setOnClickListener {
                        isClicked = true
                        btnLikeCommentUser.iconTint = MaterialColors.getColorStateListOrNull(
                            btnLikeCommentUser.context,
                            com.google.android.material.R.attr.colorPrimary
                        )
                        onLikeCallback(item.id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCommentDetailPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position]){
            if (onLikeCallback!= null){
                notifyItemChanged(position)
                onLikeCallback
            }
        }
    }

    override fun getItemCount(): Int = commentList.size
}