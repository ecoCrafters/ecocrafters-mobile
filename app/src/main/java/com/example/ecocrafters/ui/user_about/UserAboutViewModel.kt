package com.example.ecocrafters.ui.user_about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.UserAboutResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserAboutViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userAboutState: MutableStateFlow<ResultOf<UserAboutResponse>?> =
        MutableStateFlow(null)
    val userAboutState: StateFlow<ResultOf<UserAboutResponse>?> = _userAboutState

    fun updateUserAbout(username: String){
        viewModelScope.launch {
            userRepository.getUserAbout(username).collect{
                _userAboutState.value = it
            }
        }
    }
}