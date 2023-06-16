package com.example.ecocrafters.ui.user_follower

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.UserFollowersResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserFollowerViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userFollowerState: MutableStateFlow<ResultOf<UserFollowersResponse>?> =
        MutableStateFlow(null)
    val userFollowerState: StateFlow<ResultOf<UserFollowersResponse>?> = _userFollowerState

    fun updateUserFollower(username: String){
        viewModelScope.launch {
            userRepository.getUserFollowers(username).collect{
                _userFollowerState.value = it
            }
        }
    }
}