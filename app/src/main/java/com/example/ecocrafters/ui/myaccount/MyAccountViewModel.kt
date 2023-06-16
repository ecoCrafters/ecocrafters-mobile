package com.example.ecocrafters.ui.myaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyAccountViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _myAccountState: MutableStateFlow<ResultOf<UserProfileResponse>?> =
        MutableStateFlow(null)
    val myAccountState: StateFlow<ResultOf<UserProfileResponse>?> = _myAccountState

    fun updateMyAccountProfile() {
        viewModelScope.launch {
            userRepository.getMyAccountProfile().collect {
                _myAccountState.value = it
            }
        }
    }

    suspend fun saveMyProfile(myProfile: MyProfile){
        userRepository.saveMyProfile(myProfile)
    }
}