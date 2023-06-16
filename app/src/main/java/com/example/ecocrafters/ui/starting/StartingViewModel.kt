package com.example.ecocrafters.ui.starting

import androidx.lifecycle.ViewModel
import com.example.ecocrafters.data.AuthRepository
import kotlinx.coroutines.flow.first

class StartingViewModel(private val authRepository: AuthRepository) : ViewModel() {
    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn().first()
    }
}