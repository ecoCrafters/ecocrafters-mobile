package com.example.ecocrafters.ui.account_security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountSecurityViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _passwordRequest: MutableStateFlow<ResultOf<PostApiResponse>?> = MutableStateFlow(null)
    val passwordRequest: StateFlow<ResultOf<PostApiResponse>?> = _passwordRequest
    fun sendChangePasswordRequest(){
        viewModelScope.launch {
            authRepository.sendChangePasswordRequest(null).collect {
                _passwordRequest.value = it
            }
        }
    }
}