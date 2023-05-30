package com.example.ecocrafters.ui.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.data.remote.response.PostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CheckCodeViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _checkCodeState: MutableStateFlow<ResultOf<CodeResponse>?> =
        MutableStateFlow(null)
    val checkCodeState: StateFlow<ResultOf<CodeResponse>?> = _checkCodeState

    fun checkChangePasswordCode(code: String){
        viewModelScope.launch {
            authRepository.checkChangePasswordCode(code).collectLatest {
                _checkCodeState.value = it
            }
        }
    }
}