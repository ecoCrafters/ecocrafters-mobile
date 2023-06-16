package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any,

	@field:SerializedName("avatar")
	val avatar: String,

	@field:SerializedName("token_type")
	val tokenType: String,

	@field:SerializedName("token")
	val token: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("avatar_url")
	val avatarUrl: String,

	@field:SerializedName("token_expires_in")
	val tokenExpiresIn: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("eco_points")
	val ecoPoints: Int,

	@field:SerializedName("username")
	val username: String
)
