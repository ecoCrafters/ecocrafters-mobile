package com.example.ecocrafters.data

import android.util.Log
import com.example.ecocrafters.data.local.datastore.AccessToken
import com.example.ecocrafters.data.local.datastore.UserPreferences
import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.data.remote.response.PostResponse
import com.example.ecocrafters.data.remote.retrofit.ApiService
import com.example.ecocrafters.utils.ExceptionMessageGetter.getErrorMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
            Log.d("this", "Working")
            val response = apiService.registerUser(fullName, username, email, password)
            AccessToken(
                response.email,
                response.token,
                response.tokenExpiresIn,
                Instant.now()
            ).let {
                userPreferences.saveToken(it)
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
            AccessToken(
                response.email,
                response.token,
                response.tokenExpiresIn,
                Instant.now()
            ).let {
                userPreferences.saveToken(it)
            }
            emit(ResultOf.Success(response))
        } catch (throwable: Throwable) {
            emit(ResultOf.Error(throwable.getErrorMessage()))
        }
    }

    fun sendChangePasswordRequest(email: String?): Flow<ResultOf<PostResponse>> = flow {
        emit(ResultOf.Loading)
        val newEmail = email ?: userPreferences.getToken().first()?.email
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