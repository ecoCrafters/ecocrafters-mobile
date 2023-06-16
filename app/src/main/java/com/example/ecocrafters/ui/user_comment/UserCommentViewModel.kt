package com.example.ecocrafters.ui.user_comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.UserCommentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserCommentViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userCommentState: MutableStateFlow<ResultOf<UserCommentResponse>?> =
        MutableStateFlow(null)
    val userCommentState: StateFlow<ResultOf<UserCommentResponse>?> = _userCommentState

    fun updateUserComment(username: String){
        viewModelScope.launch {
            userRepository.getUserComments(username).collect{
                _userCommentState.value = it
            }
        }
    }
}