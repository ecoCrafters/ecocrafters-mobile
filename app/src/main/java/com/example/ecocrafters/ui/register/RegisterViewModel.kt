package com.example.ecocrafters.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.AuthResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String
    ): StateFlow<ResultOf<AuthResponse>?> {
        return authRepository.registerUser(fullName, username, email, password).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )
    }
}