package com.example.ecocrafters.utils

import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

internal object ExceptionMessageGetter {
    private fun HttpException.getHttpErrorBody(): PostApiResponse? {
        val gson = Gson()
        val responseBody = this.response()?.errorBody()?.string()
        return gson.fromJson(responseBody, PostApiResponse::class.java)
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