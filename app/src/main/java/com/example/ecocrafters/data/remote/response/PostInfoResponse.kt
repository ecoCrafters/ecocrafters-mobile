package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostInfoResponse(

	@field:SerializedName("thumbnail")
	val thumbnail: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("num_of_comments")
	val numOfComments: Int,

	@field:SerializedName("created_at")
	val createdAt: String?,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("slug")
	val slug: String,

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("num_of_likes")
	val numOfLikes: Int,

	@field:SerializedName("user")
	val user: UserProfileResponse
)
