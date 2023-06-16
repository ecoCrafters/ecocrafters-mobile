package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostApiResponse(

	@field:SerializedName("message")
	val message: String
)
