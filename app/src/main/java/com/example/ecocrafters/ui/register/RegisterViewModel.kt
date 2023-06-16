package com.example.ecocrafters.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState: MutableStateFlow<ResultOf<AuthResponse>?> = MutableStateFlow(null)
    val registerState: StateFlow<ResultOf<AuthResponse>?> = _registerState

    fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String
    ){
        viewModelScope.launch {
            authRepository.registerUser(fullName, username, email, password).collect {
                _registerState.value = it
            }
        }
    }
}