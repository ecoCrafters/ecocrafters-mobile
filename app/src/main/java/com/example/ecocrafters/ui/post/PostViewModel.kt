package com.example.ecocrafters.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _postState: MutableStateFlow<ResultOf<PostDetailResponse>?> =
        MutableStateFlow(null)
    val postState: StateFlow<ResultOf<PostDetailResponse>?> = _postState

    private val _likeCommentState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val likeCommentState: StateFlow<ResultOf<PostApiResponse>?> = _likeCommentState

    private val _likePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val likePostState: StateFlow<ResultOf<PostApiResponse>?> = _likePostState

    private val _commentPostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val commentPostState: StateFlow<ResultOf<PostApiResponse>?> = _commentPostState

    private val _savePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val savePostState: StateFlow<ResultOf<PostApiResponse>?> = _savePostState

    private val _savedState: MutableStateFlow<ResultOf<Boolean>?> =
        MutableStateFlow(null)
    val savedState: StateFlow<ResultOf<Boolean>?> = _savedState

    fun updatePost(slug: String, postId: Int) {
        viewModelScope.launch {
            userRepository.getPost(slug, postId).collect {
                _postState.value = it
            }
        }
    }

    fun updateLikeComment(idComment: Int) {
        viewModelScope.launch {
            userRepository.likeComment(idComment).collect {
                _likeCommentState.value = it
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

    fun updateSavePost(idPost: Int){
        viewModelScope.launch {
            userRepository.savePost(idPost).collect{
                _savePostState.value = it
            }
        }
    }

    fun updateUnsavePost(idPost: Int){
        viewModelScope.launch {
            userRepository.unsavePost(idPost).collect{
                _savePostState.value = it
            }
        }
    }

    fun checkSavedPost(idPost:Int){
        viewModelScope.launch {
            userRepository.checkSavedPost(idPost).collect{
                _savedState.value = it
            }
        }
    }
}