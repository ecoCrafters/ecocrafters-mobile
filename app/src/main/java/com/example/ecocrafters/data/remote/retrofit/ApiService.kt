package com.example.ecocrafters.data.remote.retrofit

import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.data.remote.response.PostResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("full_name") fullName: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("password/email")
    suspend fun sendChangePasswordCode(
        @Field("email") email: String
    ): PostResponse

    @FormUrlEncoded
    @POST("password/code/check")
    suspend fun checkChangePasswordCode(
        @Field("code") code: String
    ): CodeResponse

    @FormUrlEncoded
    @POST("password/reset")
    suspend fun changePassword(
        @Field("code") code: String,
        @Field("password") password: String
    ): CodeResponse
}