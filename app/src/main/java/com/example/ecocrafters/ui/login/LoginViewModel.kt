package com.example.ecocrafters.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.PostApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState: MutableStateFlow<ResultOf<AuthResponse>?> = MutableStateFlow(null)
    val loginState: StateFlow<ResultOf<AuthResponse>?> = _loginState

    private val _passwordRequest: MutableStateFlow<ResultOf<PostApiResponse>?> = MutableStateFlow(null)
    val passwordRequest: StateFlow<ResultOf<PostApiResponse>?> = _passwordRequest

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginUser(email, password).collect {
                _loginState.value = it
            }
        }
    }

    fun sendChangePasswordCodeRequest(email: String) {
        viewModelScope.launch {
            authRepository.sendChangePasswordRequest(email).collect {
                _passwordRequest.value = it
            }
        }
    }
}