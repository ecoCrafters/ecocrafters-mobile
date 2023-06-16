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
    private val tokenKey = stringPreferencesKey(TOKEN_KEY)
    private val expiresInKey = intPreferencesKey(EXPIRES_IN_KEY)
    private val tokenCreatedKey = stringPreferencesKey(TOKEN_CREATED_KEY)

    private val emailKey = stringPreferencesKey(EMAIL_KEY)
    private val fullNameKey = stringPreferencesKey(FULL_NAME_KEY)
    private val userNameKey = stringPreferencesKey(USERNAME_KEY)
    private val avatarsKey = stringPreferencesKey(AVATARS_KEY)
    private val ecoPointsKey = intPreferencesKey(ECO_POINTS_KEY)

    fun getAccessToken(): Flow<AccessToken?> {
        return dataStore.data.map { pref ->
            val token = pref[tokenKey]
            val expiresIn = pref[expiresInKey]
            val tokenCreatedString = pref[tokenCreatedKey]
            if (token != null && expiresIn != null && tokenCreatedString != null) {
                AccessToken(token, expiresIn, Instant.parse(tokenCreatedString))
            } else {
                null
            }
        }
    }

    suspend fun saveAccessToken(accessToken: AccessToken) {
        dataStore.edit { pref ->
            pref[tokenKey] = accessToken.token
            pref[expiresInKey] = accessToken.expiresIn
            pref[tokenCreatedKey] = accessToken.TokenCreated.toString()
        }
    }

    suspend fun saveAccessToken(token: String, tokenCreated: Instant){
        dataStore.edit { pref ->
            pref[tokenKey] = token
            pref[tokenCreatedKey] = tokenCreated.toString()
        }
    }

    suspend fun logOut() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

    suspend fun saveMyProfile(myProfile: MyProfile) {
        dataStore.edit { pref ->
            pref[emailKey] = myProfile.email
            pref[fullNameKey] = myProfile.fullName
            pref[userNameKey] = myProfile.username
            pref[avatarsKey] = myProfile.avatars
            pref[ecoPointsKey] = myProfile.ecoPoints
        }
    }

    fun getMyProfile(): Flow<MyProfile?> {
        return dataStore.data.map { pref ->
            val email = pref[emailKey]
            val fullName = pref[fullNameKey]
            val username = pref[userNameKey]
            val avatars = pref[avatarsKey]
            val ecoPoints = pref[ecoPointsKey]
            if (email != null && fullName != null && username != null && avatars != null && ecoPoints != null) {
                MyProfile(email, fullName, username, avatars, ecoPoints)
            } else {
                null
            }
        }
    }

    fun getUsername(): Flow<String?>{
        return dataStore.data.map { pref ->
            pref[userNameKey]
        }
    }
    fun getToken(): Flow<String?>{
        return dataStore.data.map { pref ->
            pref[tokenKey]
        }
    }

    companion object {
        private const val TOKEN_KEY = "user_token"
        private const val EXPIRES_IN_KEY = "expires_in"
        private const val TOKEN_CREATED_KEY = "token_created"
        private const val EMAIL_KEY = "email"

        private const val FULL_NAME_KEY = "full_name"
        private const val USERNAME_KEY = "username"
        private const val AVATARS_KEY = "avatars"
        private const val ECO_POINTS_KEY = "eco_points"

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