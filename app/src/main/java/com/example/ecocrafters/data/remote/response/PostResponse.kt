package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostResponse(

	@field:SerializedName("message")
	val message: String
)
