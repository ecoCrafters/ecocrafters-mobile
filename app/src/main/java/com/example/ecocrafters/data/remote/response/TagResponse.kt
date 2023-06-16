package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class TagResponse(

	@field:SerializedName("updated_at")
	val updatedAt: Any,

	@field:SerializedName("created_at")
	val createdAt: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("tag")
	val tag: String
)
