package com.example.ecocrafters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocrafters.data.remote.response.FollowingItem
import com.example.ecocrafters.databinding.ItemUserBinding
import com.example.ecocrafters.utils.loadRoundImage

class UserFollowingAdapter(private val listFollowing: List<FollowingItem>) :
    RecyclerView.Adapter<UserFollowingAdapter.ViewHolder>() {

    private var onItemClickCallback: ((followingItem: FollowingItem) -> Unit)? = null

    fun setOnItemClickCallback(callback: (followingItem: FollowingItem) -> Unit) {
        onItemClickCallback = callback
    }

    class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FollowingItem, onItemCallback: ((followingItem: FollowingItem) -> Unit)?) {
            binding.apply {
                tvUserName.text = item.username
                tvUserDesc.text = item.fullName
                ivUserAvatars.loadRoundImage(item.avatar)
                if (onItemCallback != null){
                    binding.root.setOnClickListener{
                        onItemCallback(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFollowing[position], onItemClickCallback)
    }

    override fun getItemCount(): Int = listFollowing.size
}