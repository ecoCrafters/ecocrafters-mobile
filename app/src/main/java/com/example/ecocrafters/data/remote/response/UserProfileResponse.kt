package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("avatar_url")
	val avatarUrl: String,

	@field:SerializedName("created_at")
	val createdAt: String?,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any?,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("avatar")
	val avatar: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("eco_points")
	val ecoPoints: Int,

	@field:SerializedName("username")
	val username: String
)
