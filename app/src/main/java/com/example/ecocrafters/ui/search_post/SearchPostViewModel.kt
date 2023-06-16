package com.example.ecocrafters.ui.search_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.data.remote.response.PostInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchPostViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _searchPostState: MutableStateFlow<ResultOf<List<PostInfoResponse>>?> =
        MutableStateFlow(null)
    val searchPostState: StateFlow<ResultOf<List<PostInfoResponse>>?> = _searchPostState

    private val _likePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val likePostState: StateFlow<ResultOf<PostApiResponse>?> = _likePostState

    private val _commentPostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val commentPostState: StateFlow<ResultOf<PostApiResponse>?> = _commentPostState

    private val _savePostState: MutableStateFlow<ResultOf<PostApiResponse>?> =
        MutableStateFlow(null)
    val savePostState: StateFlow<ResultOf<PostApiResponse>?> = _savePostState


    fun updateSearchUser(searchTerm: String){
        viewModelScope.launch {
            userRepository.searchPost(searchTerm).collect{
                _searchPostState.value = it
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
}