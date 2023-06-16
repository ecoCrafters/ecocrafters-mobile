package com.example.ecocrafters.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _postListState: MutableStateFlow<ResultOf<List<PostInfoResponse>>?> =
        MutableStateFlow(null)
    val postListState: StateFlow<ResultOf<List<PostInfoResponse>>?> = _postListState

    private val _myProfileState: MutableStateFlow<ResultOf<MyProfile?>> =
        MutableStateFlow(ResultOf.Success(null))
    val myProfileState: StateFlow<ResultOf<MyProfile?>> = _myProfileState

    private val _likePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val likePostState: StateFlow<ResultOf<PostApiResponse>?> = _likePostState

    private val _commentPostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val commentPostState: StateFlow<ResultOf<PostApiResponse>?> = _commentPostState

    private val _savePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val savePostState: StateFlow<ResultOf<PostApiResponse>?> = _savePostState


    fun getAllPost() {
        viewModelScope.launch {
            userRepository.getAllPost().collect {
                _postListState.value = it
            }
        }
    }

    fun updateMyProfile() {
        viewModelScope.launch {
            userRepository.getMyProfile().collect {
                _myProfileState.value = it
            }
        }
    }

    fun updateLikePost(idPost: Int) {
        viewModelScope.launch {
            userRepository.likePost(idPost).collect {
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

    fun updateSavePost(idPost: Int) {
        viewModelScope.launch {
            userRepository.savePost(idPost).collect {
                _savePostState.value = it
            }
        }
    }
}