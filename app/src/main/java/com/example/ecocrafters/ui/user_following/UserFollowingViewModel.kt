package com.example.ecocrafters.ui.user_following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.UserFollowingResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserFollowingViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userFollowingState: MutableStateFlow<ResultOf<UserFollowingResponse>?> =
        MutableStateFlow(null)
    val userFollowingState: StateFlow<ResultOf<UserFollowingResponse>?> = _userFollowingState

    fun updateUserFollowing(username: String){
        viewModelScope.launch {
            userRepository.getUserFollowing(username).collect{
                _userFollowingState.value = it
            }
        }
    }
}