package com.example.ecocrafters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocrafters.data.remote.response.CommentItem
import com.example.ecocrafters.data.remote.response.UserCommentResponse
import com.example.ecocrafters.databinding.ItemCommentUserBinding
import com.example.ecocrafters.utils.InstantHelper

class UserCommentAdapter(private val userCommentResponse: UserCommentResponse) :
    RecyclerView.Adapter<UserCommentAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemCommentUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommentItem, username: String) {
            binding.apply {
                val durationString = item.createdAt?.let{InstantHelper.toBetweenNowString(it)} ?: "..."
                tvOverlineCommentUser.text = String.format("%1\$s • %2\$s • %3\$,d Like", username, durationString, item.numOfLikes)
                tvTitleCommentUser.text = item.posts.title
                tvSupportingCommentUser.text = item.comment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userCommentResponse.comments[position], userCommentResponse.username)
    }

    override fun getItemCount(): Int = userCommentResponse.comments.size
}