package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostDetailResponse(

	@field:SerializedName("thumbnail")
	val thumbnail: String,

	@field:SerializedName("comments")
	val comments: List<CommentsItem>,

	@field:SerializedName("num_of_comments")
	val numOfComments: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("num_of_likes")
	val numOfLikes: Int,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("tag")
	val tag: List<TagItem>,

	@field:SerializedName("user")
	val user: UserProfileResponse,

	@field:SerializedName("slug")
	val slug: String
)

data class CommentsItem(

	@field:SerializedName("post_id")
	val postId: Int,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("created_at")
	val createdAt: String?,

	@field:SerializedName("comment")
	val comment: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("user")
	val user: UserProfileResponse,

	@field:SerializedName("num_of_likes")
	val numOfLikes: Int
)

data class TagItem(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("tag")
	val tag: String
)
