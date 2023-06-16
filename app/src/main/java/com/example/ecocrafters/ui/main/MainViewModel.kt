package com.example.ecocrafters.ui.main

import androidx.lifecycle.ViewModel
import com.example.ecocrafters.data.AuthRepository

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {
    suspend fun refreshToken(): Boolean {
            return authRepository.refreshToken()
    }
}