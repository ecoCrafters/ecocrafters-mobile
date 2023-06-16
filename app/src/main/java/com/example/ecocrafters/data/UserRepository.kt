package com.example.ecocrafters.data

import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.local.datastore.UserPreferences
import com.example.ecocrafters.data.remote.response.CreatePostResponse
import com.example.ecocrafters.data.remote.response.DetectImageResponse
import com.example.ecocrafters.data.remote.response.IngredientsResponse
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostDetailResponse
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import com.example.ecocrafters.data.remote.response.SavedPostsResponse
import com.example.ecocrafters.data.remote.response.TagResponse
import com.example.ecocrafters.data.remote.response.UserAboutResponse
import com.example.ecocrafters.data.remote.response.UserCommentResponse
import com.example.ecocrafters.data.remote.response.UserFollowersResponse
import com.example.ecocrafters.data.remote.response.UserFollowingResponse
import com.example.ecocrafters.data.remote.response.UserInfo
import com.example.ecocrafters.data.remote.response.UserPostsResponse
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import com.example.ecocrafters.data.remote.retrofit.ApiService
import com.example.ecocrafters.utils.ExceptionMessageGetter.getErrorMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    fun searchUser(searchTerm: String): Flow<ResultOf<List<UserInfo>>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.searchUser("Bearer $token", searchTerm)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getMyAccountProfile(): Flow<ResultOf<UserProfileResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val username = userPreferences.getUsername().first()
            if (username == null) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.getUserProfile(username)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getUserProfile(username: String): Flow<ResultOf<UserProfileResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.getUserProfile(username)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getUserPosts(username: String): Flow<ResultOf<UserPostsResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.getUserPosts(username)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getUserComments(username: String): Flow<ResultOf<UserCommentResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.getUserComments(username)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getUserFollowers(username: String): Flow<ResultOf<UserFollowersResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.getUserFollowers(username)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getUserFollowing(username: String): Flow<ResultOf<UserFollowingResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.getUserFollowing(username)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getUserAbout(username: String): Flow<ResultOf<UserAboutResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.getUserAbout(username)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun checkUserFollowStatus(idUser: Int): Flow<ResultOf<Boolean>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.checkFollowStatus("Bearer $token", idUser)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun followUser(idUser: Int): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.followUser("Bearer $token", idUser)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun unfollowUser(idUser: Int): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.unfollowUser("Bearer $token", idUser)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun editMyAccountProfile(
        fullName: RequestBody,
        username: RequestBody,
        avatar: MultipartBody.Part? = null
    ): Flow<ResultOf<UserProfileResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response =
                    apiService.updateUserProfile("Bearer $token", fullName, username, avatar)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getMyProfile(): Flow<ResultOf<MyProfile?>> = flow {
        emit(ResultOf.Loading)
        try {
            userPreferences.getMyProfile().collect {
                emit(ResultOf.Success(it))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    suspend fun saveMyProfile(myProfile: MyProfile) {
        userPreferences.saveMyProfile(myProfile)
    }

    // Post Feature
    fun getAllPost(): Flow<ResultOf<List<PostInfoResponse>>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.getAllPost("Bearer $token")
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun searchPost(searchTerm: String): Flow<ResultOf<List<PostInfoResponse>>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.searchPost("Bearer $token", searchTerm)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun getPost(slug: String, postId: Int): Flow<ResultOf<PostDetailResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.getPost("Bearer $token", slug, postId)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }


    fun getSavedPost(): Flow<ResultOf<SavedPostsResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.getSavedPost("Bearer $token")
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun createPost(
        title: RequestBody,
        content: RequestBody,
        thumbnail: MultipartBody.Part? = null,
        tag: RequestBody,
        ingredient: RequestBody
    ): Flow<ResultOf<CreatePostResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.createPost(
                    "Bearer $token",
                    title,
                    content,
                    thumbnail,
                    tag,
                    ingredient
                )
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun likePost(idPost: Int): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.likePost("Bearer $token", idPost)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun commentPost(idPost: Int, comment: String): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.commentPost("Bearer $token", idPost, comment)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun likeComment(idComment: Int): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.likeComment("Bearer $token", idComment)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun savePost(idPost: Int): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.savePost("Bearer $token", idPost)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    // Tag Feature
    fun getAllTag(): Flow<ResultOf<List<TagResponse>>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.getAllTag("Bearer $token")
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    // Ingredients Feature
    fun getAllIngredients(): Flow<ResultOf<List<IngredientsResponse>>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.getAllIngredients("Bearer $token")
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }


    fun detectImage(image: MultipartBody.Part):Flow<ResultOf<DetectImageResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val token = userPreferences.getToken().first()
            if (token.isNullOrEmpty()) {
                throw IllegalStateException("The user is not logged in yet")
            } else {
                val response = apiService.detectImage("Bearer $token", image)
                emit(ResultOf.Success(response))
            }
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(apiService, userPreferences)
        }.also { instance = it }
    }
}