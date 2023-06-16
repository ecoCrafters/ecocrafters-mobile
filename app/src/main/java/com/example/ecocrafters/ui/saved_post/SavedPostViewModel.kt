package com.example.ecocrafters.ui.saved_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.SavedPostsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavedPostViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _savedPostState: MutableStateFlow<ResultOf<SavedPostsResponse>?> =
        MutableStateFlow(null)
    val savedPostState: StateFlow<ResultOf<SavedPostsResponse>?> = _savedPostState

    private val _likePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val likePostState: StateFlow<ResultOf<PostApiResponse>?> = _likePostState

    private val _commentPostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val commentPostState: StateFlow<ResultOf<PostApiResponse>?> = _commentPostState

    private val _unsavePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val unsavePostState: StateFlow<ResultOf<PostApiResponse>?> = _unsavePostState

    fun updateSavedPost(){
        viewModelScope.launch {
            userRepository.getSavedPost().collect{
                _savedPostState.value = it
            }
        }
    }

    fun updateLikePost(idPost: Int){
        viewModelScope.launch {
            userRepository.likePost(idPost).collect{
                _likePostState.value = it
            }
        }
    }

    fun updateCommentPost(idPost: Int,comment: String) {
        viewModelScope.launch {
            userRepository.commentPost(idPost, comment).collect {
                _commentPostState.value = it
            }
        }
    }

    fun updateUnsavePost(idPost: Int){
        viewModelScope.launch {
            userRepository.unsavePost(idPost).collect{
                _unsavePostState.value = it
            }
        }
    }
}