package com.example.ecocrafters.data.remote.response

import com.google.gson.annotations.SerializedName
data class UserInfo(

    @field:SerializedName("full_name")
    val fullName: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("avatar")
    val avatar: String,

    @field:SerializedName("username")
    val username: String
)
