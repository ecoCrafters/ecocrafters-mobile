package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

	@field:SerializedName("token")
	val token: String
)
