package com.example.ecocrafters.data

import android.util.Log
import com.example.ecocrafters.data.local.datastore.AccessToken
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.local.datastore.UserPreferences
import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.retrofit.ApiService
import com.example.ecocrafters.utils.ExceptionMessageGetter.getErrorMessage
import com.example.ecocrafters.utils.InstantHelper
import com.example.ecocrafters.utils.isInvalidUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String
    ): Flow<ResultOf<AuthResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.registerUser(fullName, username, email, password)
            val avatarUrl =
                if (response.avatar.isInvalidUrl()) response.avatarUrl else response.avatar
            AccessToken(
                response.token,
                response.tokenExpiresIn,
                Instant.now()
            ).let {
                userPreferences.saveAccessToken(it)
            }
            MyProfile(
                response.email,
                response.fullName,
                response.username,
                avatarUrl,
                response.ecoPoints
            ).let {
                userPreferences.saveMyProfile(it)
            }
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun loginUser(
        email: String,
        password: String
    ): Flow<ResultOf<AuthResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.loginUser(email, password)
            val avatarUrl =
                if (response.avatar.isInvalidUrl()) response.avatarUrl else response.avatar
            AccessToken(
                response.token,
                response.tokenExpiresIn,
                Instant.now()
            ).let {
                userPreferences.saveAccessToken(it)
            }
            MyProfile(
                response.email,
                response.fullName,
                response.username,
                avatarUrl,
                response.ecoPoints
            ).let {
                userPreferences.saveMyProfile(it)
            }
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return userPreferences.getAccessToken().map { token ->
            if (token == null) {
                false
            } else {
                val tokenAgeInSeconds = InstantHelper.toBetweenNowSeconds(token.TokenCreated)
                3600 > tokenAgeInSeconds
            }
        }
    }

    fun logOut(): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val accessToken = userPreferences.getAccessToken().first()
            if (accessToken != null) {
                val response = apiService.logOut("Bearer ${accessToken.token}")
                emit(ResultOf.Success(response))
                userPreferences.logOut()
            }
        } catch (throwable: Throwable) {
            val errorMessage = throwable.getErrorMessage()
            if (errorMessage == "Token Expired") {
                userPreferences.logOut()
            }
            emit(ResultOf.Error(errorMessage))
        }
    }

    suspend fun refreshToken(): Boolean {
        return try {
            val accessToken = userPreferences.getAccessToken().first()
            if (accessToken != null) {
                if (InstantHelper.toBetweenNowSeconds(accessToken.TokenCreated) > 400) {
                    val response = apiService.refreshToken("Bearer ${accessToken.token}")
                    userPreferences.saveAccessToken(response.token, Instant.now())
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (throwable: Throwable) {
            Log.e("AuthRepository.refreshToken", throwable.message.toString())
            false
        }
    }

    fun sendChangePasswordRequest(email: String?): Flow<ResultOf<PostApiResponse>> = flow {
        emit(ResultOf.Loading)
        val newEmail = email ?: userPreferences.getMyProfile().first()?.email
        try {
            val response = apiService.sendChangePasswordCode(newEmail ?: "")
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun checkChangePasswordCode(code: String): Flow<ResultOf<CodeResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.checkChangePasswordCode(code)
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun changePassword(code: String, password: String): Flow<ResultOf<CodeResponse>> = flow {
        emit(ResultOf.Loading)
        try {
            val response = apiService.changePassword(code, password)
            emit(ResultOf.Success(response))
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

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(apiService, userPreferences)
        }.also { instance = it }
    }

}