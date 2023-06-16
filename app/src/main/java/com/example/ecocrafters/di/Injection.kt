package com.example.ecocrafters.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.local.datastore.UserPreferences
import com.example.ecocrafters.data.remote.retrofit.ApiConfig

object Injection {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getService()
        val userPreferences = UserPreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, userPreferences)
    }

    fun provideUserRepository(context: Context): UserRepository{
        val apiService = ApiConfig.getService()
        val userPreferences = UserPreferences.getInstance(context.dataStore)
        return UserRepository(apiService, userPreferences)
    }
}