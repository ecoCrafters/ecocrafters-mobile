package com.example.ecocrafters.ui.search_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchUserViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _searchUserState: MutableStateFlow<ResultOf<List<UserInfo>>?> =
        MutableStateFlow(null)
    val searchUserState: StateFlow<ResultOf<List<UserInfo>>?> = _searchUserState

    fun updateSearchUser(searchTerm: String){
        viewModelScope.launch {
            userRepository.searchUser(searchTerm).collect{
                _searchUserState.value = it
            }
        }
    }
}