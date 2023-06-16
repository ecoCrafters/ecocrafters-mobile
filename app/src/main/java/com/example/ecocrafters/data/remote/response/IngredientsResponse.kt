package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class IngredientsResponse(

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int
)
