package com.example.ecocrafters.utils

import com.example.ecocrafters.data.remote.response.PostResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

internal object ExceptionMessageGetter {
    fun HttpException.getHttpErrorBody(): PostResponse? {
        val gson = Gson()
        val responseBody = this.response()?.errorBody()?.string()
        return gson.fromJson(responseBody, PostResponse::class.java)
    }

    fun Throwable.getErrorMessage(): String {
        when (this) {
            is IOException -> return "Network Error"
            is HttpException -> {
                this.apply {
                    val response = getHttpErrorBody()
                    return response?.message ?: "${code()}: ${message()}"
                }
            }
            else -> return "Error: $message"
        }
    }
}