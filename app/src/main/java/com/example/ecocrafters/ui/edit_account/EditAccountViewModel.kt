package com.example.ecocrafters.ui.edit_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.local.datastore.MyProfile
import com.example.ecocrafters.data.remote.response.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EditAccountViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _editAccountState: MutableStateFlow<ResultOf<UserProfileResponse>?> =
        MutableStateFlow(null)
    val editAccountState: StateFlow<ResultOf<UserProfileResponse>?> = _editAccountState

    suspend fun saveAccountProfile(
        email: String,
        fullName: String,
        username: String,
        avatar: String,
        ecoPoints: Int
    ) {
        userRepository.saveMyProfile(
            MyProfile(
                email, fullName, username, avatar, ecoPoints
            )
        )
    }

    fun editAccountProfile(
        fullName: RequestBody,
        username: RequestBody,
        avatar: MultipartBody.Part? = null
    ) {
        viewModelScope.launch {
            userRepository.editMyAccountProfile(fullName, username, avatar).collect {
                _editAccountState.value = it
            }
        }
    }
}