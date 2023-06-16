package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetectImageResponse(

	@field:SerializedName("label")
	val label: List<Int>,

	@field:SerializedName("posts")
	val posts: List<PostInfoResponse>,

	@field:SerializedName("status")
	val status: Int
)
