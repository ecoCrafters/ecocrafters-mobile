package com.example.ecocrafters.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.AuthRepository
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.PostApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MoreViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _logOutState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val logOutState: StateFlow<ResultOf<PostApiResponse>?> = _logOutState

    private val _myProfileState: MutableStateFlow<ResultOf<MyProfile?>> =
        MutableStateFlow(ResultOf.Success(null))
    val myProfileState: StateFlow<ResultOf<MyProfile?>> = _myProfileState

    fun logOut() {
        viewModelScope.launch {
            authRepository.logOut().collect {
                _logOutState.value = it
            }
        }
    }

    fun updateMyProfile(){
        viewModelScope.launch {
            authRepository.getMyProfile().collect{
                _myProfileState.value = it
            }
        }
    }
}