package com.example.ecocrafters.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.PostResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LoginViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun loginUser(email: String, password: String): StateFlow<ResultOf<AuthResponse>> {
        return authRepository.loginUser(email, password).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ResultOf.Loading
        )
    }

    fun sendChangePasswordCode(email: String): StateFlow<ResultOf<PostResponse>>{
        return authRepository.sendChangePasswordRequest(email).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ResultOf.Loading
        )
    }
}