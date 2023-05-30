package com.example.ecocrafters.ui.account_security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.PostResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AccountSecurityViewModel(private val authRepository: AuthRepository): ViewModel() {

    fun sendChangePasswordRequest(): StateFlow<ResultOf<PostResponse>>{
        return authRepository.sendChangePasswordRequest(null).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ResultOf.Loading
        )
    }
}