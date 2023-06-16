package com.example.ecocrafters.ui.check_code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.CodeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckCodeViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _checkCodeState: MutableStateFlow<ResultOf<CodeResponse>?> =
        MutableStateFlow(null)
    val checkCodeState: StateFlow<ResultOf<CodeResponse>?> = _checkCodeState

    fun checkChangePasswordCode(code: String){
        viewModelScope.launch {
            authRepository.checkChangePasswordCode(code).collect {
                _checkCodeState.value = it
            }
        }
    }
}