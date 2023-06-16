package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserPostsResponse(

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("avatar_url")
	val avatarUrl: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("avatar")
	val avatar: String,

	@field:SerializedName("posts")
	val posts: List<PostItem>,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("eco_points")
	val ecoPoints: Int,

	@field:SerializedName("username")
	val username: String
)

data class PostItem(

	@field:SerializedName("thumbnail")
	val thumbnail: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("num_of_comments")
	val numOfComments: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("slug")
	val slug: String,

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("num_of_likes")
	val numOfLikes: Int
)
