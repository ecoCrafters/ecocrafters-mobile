package com.example.ecocrafters.ui.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.CodeResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ChangePasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun changePassword(code: String, password: String): StateFlow<ResultOf<CodeResponse>> {
        return authRepository.changePassword(code, password)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ResultOf.Loading)
    }
}