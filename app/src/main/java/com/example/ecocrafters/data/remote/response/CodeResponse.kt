package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class CodeResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)
