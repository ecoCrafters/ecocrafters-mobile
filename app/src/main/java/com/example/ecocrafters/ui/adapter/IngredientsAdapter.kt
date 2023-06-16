package com.example.ecocrafters.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecocrafters.databinding.ItemIngredientsBinding

class IngredientsAdapter:
    ListAdapter<String,IngredientsAdapter.ViewHolder>(IngredientDiffCallback()) {

    private var onRemoveCallback: ((ingredient: String) -> Unit)? = null

    fun setOnRemoveCallback(callback: (ingredient: String) -> Unit) {
        onRemoveCallback = callback
    }

    class ViewHolder(private val binding: ItemIngredientsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, onRemoveCallback: ((ingredient: String) -> Unit)?) {
            binding.apply {
                tvIngredients.text = item
                onRemoveCallback?.let {
                    btnRemoveIngredients.setOnClickListener {
                        onRemoveCallback(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onRemoveCallback)
    }

    class IngredientDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.lowercase() == newItem.lowercase()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
}