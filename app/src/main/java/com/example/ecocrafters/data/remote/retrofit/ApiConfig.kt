package com.example.ecocrafters.data.remote.retrofit

import com.example.ecocrafters.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {

    var BASE_URL = "https://ecocrafters-api.et.r.appspot.com/api/"

    fun getService(): ApiService {
        val client = OkHttpClient.Builder().let {
            if (BuildConfig.DEBUG) {
                it.addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
            } else {
                it
            }
        }.build()

        val retrofit = Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(client)
        }.build()

        return retrofit.create(ApiService::class.java)
    }
}