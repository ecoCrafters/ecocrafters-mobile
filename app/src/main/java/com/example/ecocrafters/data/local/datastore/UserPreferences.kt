package com.example.ecocrafters.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val emailKey = stringPreferencesKey(EMAIL_KEY)
    private val tokenKey = stringPreferencesKey(TOKEN_KEY)
    private val expiresInKey = intPreferencesKey(EXPIRES_IN_KEY)
    private val tokenCreatedKey = stringPreferencesKey(TOKEN_CREATED_KEY)

    fun getToken(): Flow<AccessToken?> {
        return dataStore.data.map { pref ->
            val email = pref[emailKey]
            val token = pref[tokenKey]
            val expiresIn = pref[expiresInKey]
            val tokenCreatedString = pref[tokenCreatedKey]
            if (email != null && token != null && expiresIn != null && tokenCreatedString != null){
                AccessToken(email ,token, expiresIn, Instant.parse(tokenCreatedString))
            } else {
                null
            }
        }
    }

    suspend fun saveToken(accessToken: AccessToken) {
        dataStore.edit { pref ->
            pref[emailKey] = accessToken.email
            pref[tokenKey] = accessToken.token
            pref[expiresInKey] = accessToken.expiresIn
            pref[tokenCreatedKey] = accessToken.TokenCreated.toString()
        }
    }

    companion object {
        private const val TOKEN_KEY = "user_token"
        private const val EXPIRES_IN_KEY = "expires_in"
        private const val TOKEN_CREATED_KEY = "token_created"
        private const val EMAIL_KEY = "email"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}