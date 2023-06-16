package com.example.ecocrafters.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel()  {

    private val _userState: MutableStateFlow<ResultOf<UserProfileResponse>?> =
        MutableStateFlow(null)
    val userState: StateFlow<ResultOf<UserProfileResponse>?> = _userState

    private val _followState: MutableStateFlow<ResultOf<Boolean>?> =
        MutableStateFlow(null)
    val followState: StateFlow<ResultOf<Boolean>?> = _followState

    private val _followActionState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val followActionState: StateFlow<ResultOf<PostApiResponse>?> = _followActionState

    fun updateUserProfile(username: String) {
        viewModelScope.launch {
            userRepository.getUserProfile(username).collect {
                _userState.value = it
            }
        }
    }

    fun updateFollowStatus(idUser: Int) {
        viewModelScope.launch {
            userRepository.checkUserFollowStatus(idUser).collect {
                _followState.value = it
            }
        }
    }

    fun followUser(idUser: Int){
        viewModelScope.launch {
            userRepository.followUser(idUser).collect{
                _followActionState.value = it
            }
        }
    }

    fun unfollowUser(idUser: Int){
        viewModelScope.launch {
            userRepository.unfollowUser(idUser).collect{
                _followActionState.value = it
            }
        }
    }
}