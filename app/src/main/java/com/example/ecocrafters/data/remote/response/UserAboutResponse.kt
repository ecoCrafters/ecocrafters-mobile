package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserAboutResponse(

	@field:SerializedName("ecopoints")
	val ecopoints: Int,

	@field:SerializedName("followers")
	val followers: Int,

	@field:SerializedName("comment_created")
	val commentCreated: Int,

	@field:SerializedName("followings")
	val followings: Int,

	@field:SerializedName("account_age")
	val accountAge: String,

	@field:SerializedName("post_created")
	val postCreated: Int
)
