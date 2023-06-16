package com.example.ecocrafters.data.remote.retrofit

import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.data.remote.response.CreatePostResponse
import com.example.ecocrafters.data.remote.response.DetectImageResponse
import com.example.ecocrafters.data.remote.response.IngredientsResponse
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostDetailResponse
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import com.example.ecocrafters.data.remote.response.RefreshTokenResponse
import com.example.ecocrafters.data.remote.response.SavedPostsResponse
import com.example.ecocrafters.data.remote.response.TagResponse
import com.example.ecocrafters.data.remote.response.UserAboutResponse
import com.example.ecocrafters.data.remote.response.UserCommentResponse
import com.example.ecocrafters.data.remote.response.UserFollowersResponse
import com.example.ecocrafters.data.remote.response.UserFollowingResponse
import com.example.ecocrafters.data.remote.response.UserInfo
import com.example.ecocrafters.data.remote.response.UserPostsResponse
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("logout")
    suspend fun logOut(
        @Header("Authorization") token: String
    ): PostApiResponse

    @GET("refresh-token")
    suspend fun refreshToken(
        @Header("Authorization") token: String
    ): RefreshTokenResponse

    @FormUrlEncoded
    @POST("password/email")
    suspend fun sendChangePasswordCode(
        @Field("email") email: String
    ): PostApiResponse

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

    // User's Feature API

    @GET("users")
    suspend fun searchUser(
        @Header("Authorization") token: String,
        @Query("q") searchTerm: String
    ): List<UserInfo>

    @GET("profile/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String
    ): UserProfileResponse


    @GET("profile/{username}/posts")
    suspend fun getUserPosts(
        @Path("username") username: String
    ): UserPostsResponse

    @GET("profile/{username}/comments")
    suspend fun getUserComments(
        @Path("username") username: String
    ): UserCommentResponse

    @GET("profile/{username}/following")
    suspend fun getUserFollowing(
        @Path("username") username: String
    ): UserFollowingResponse

    @GET("profile/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username") username: String
    ): UserFollowersResponse

    @GET("profile/{username}/about")
    suspend fun getUserAbout(
        @Path("username") username: String
    ): UserAboutResponse

    @Multipart
    @POST("profile?_method=PUT")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Part("full_name") fullName: RequestBody,
        @Part("username") username: RequestBody,
        @Part avatar: MultipartBody.Part? = null
    ): UserProfileResponse

    @GET("check-follow/{id_user}")
    suspend fun checkFollowStatus(
        @Header("Authorization") token: String,
        @Path("id_user") idUser: Int
    ): Boolean

    @FormUrlEncoded
    @POST("follow")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Field("user_id_two") userId: Int
    ): PostApiResponse

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "unfollow",hasBody = true)
    suspend fun unfollowUser(
        @Header("Authorization") token: String,
        @Field("target") userId: Int
    ): PostApiResponse


    // Post API

    @GET("post")
    suspend fun getAllPost(
        @Header("Authorization") token: String
    ): List<PostInfoResponse>

    @GET("post/{q}")
    suspend fun searchPost(
        @Header("Authorization") token: String,
        @Path("q") searchTerm: String
    ): List<PostInfoResponse>

    @GET("post/{slug}/{post_id}")
    suspend fun getPost(
        @Header("Authorization") token: String,
        @Path("slug") slug: String,
        @Path("post_id") postId: Int
    ): PostDetailResponse

    @GET("post/saved")
    suspend fun getSavedPost(
        @Header("Authorization") token: String
    ): SavedPostsResponse

    @POST("post/save/{id_post}")
    suspend fun savePost(
        @Header("Authorization") token: String,
        @Path("id_post") idPost: Int
    ): PostApiResponse
    @POST("post/unsave/{id_post}")
    suspend fun unsavePost(
        @Header("Authorization") token: String,
        @Path("id_post") idPost: Int
    ): PostApiResponse

    @GET("post/check-saved-post/{id_post}")
    suspend fun checkSavedPost(
        @Header("Authorization") token: String,
        @Path("id_post") idPOST: Int
    ): Boolean

    @POST("post/like/{id_post}")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id_post") idPost: Int
    ): PostApiResponse

    @FormUrlEncoded
    @POST("post/comment/{id_post}")
    suspend fun commentPost(
        @Header("Authorization") token: String,
        @Path("id_post") idPost: Int,
        @Field("comment") comment: String
    ): PostApiResponse

    @POST("post/like/comment/{id_comment}")
    suspend fun likeComment(
        @Header("Authorization") token: String,
        @Path("id_comment") idComment: Int
    ): PostApiResponse

    @GET("tag")
    suspend fun getAllTag(
        @Header("Authorization") token: String
    ): List<TagResponse>

    @GET("ingredient")
    suspend fun getAllIngredients(
        @Header("Authorization") token: String
    ): List<IngredientsResponse>

    @Multipart
    @POST("post/create")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part thumbnail: MultipartBody.Part? = null,
        @Part("tag") tag: RequestBody,
        @Part("ingredient") ingredient: RequestBody,
    ): CreatePostResponse

    @Multipart
    @POST("detection/upload")
    suspend fun detectImage(
        @Header("Authorization") token: String,
        @Part thumbnail: MultipartBody.Part? = null
    ): DetectImageResponse
}