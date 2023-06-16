package com.example.ecocrafters.ui.create_one

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.UserRepository
import com.example.ecocrafters.data.remote.response.CreatePostResponse
import com.example.ecocrafters.data.remote.response.IngredientsResponse
import com.example.ecocrafters.data.remote.response.TagResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreatePostViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _tagListState: MutableStateFlow<ResultOf<List<TagResponse>>?> =
        MutableStateFlow(null)
    val tagListState: StateFlow<ResultOf<List<TagResponse>>?> = _tagListState

    private val _ingredientsListState: MutableStateFlow<ResultOf<List<IngredientsResponse>>?> =
        MutableStateFlow(null)
    val ingredientsListState: StateFlow<ResultOf<List<IngredientsResponse>>?> =
        _ingredientsListState

    private val _titleState: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val titleState: StateFlow<String?> = _titleState

    private val _fileState: MutableStateFlow<File?> =
        MutableStateFlow(null)
    val fileState: StateFlow<File?> = _fileState

    private val _isBackCameraState: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    val isBackCameraState: StateFlow<Boolean> = _isBackCameraState

    private val _postTagState: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    val postTagState: StateFlow<List<String>> = _postTagState

    private val _postIngredientsState: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())
    val postIngredientsState: StateFlow<List<String>> = _postIngredientsState

    private val _contentState: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val contentState: StateFlow<String?> = _contentState

    private val _createPostState: MutableStateFlow<ResultOf<CreatePostResponse>?> =
        MutableStateFlow(null)
    val createPostState: StateFlow<ResultOf<CreatePostResponse>?> = _createPostState

    fun updateAllTag() {
        viewModelScope.launch {
            userRepository.getAllTag().collect {
                _tagListState.value = it
            }
        }
    }

    fun updateAllIngredients() {
        viewModelScope.launch {
            userRepository.getAllIngredients().collect {
                _ingredientsListState.value = it
            }
        }
    }

    fun setTitle(title: String) {
        _titleState.value = title
    }

    fun setFile(file: File?) {
        _fileState.value = file
    }

    fun setBackCamera(isBackCamera: Boolean) {
        _isBackCameraState.value = isBackCamera
    }

    fun addPostTag(tag: String) {
        _postTagState.update {
            _postTagState.value + listOf(tag)
        }
    }

    fun removePostTag(tag: String) {
        _postTagState.update {
            _postTagState.value.toMutableList().apply { remove(tag.trim()) }
        }
    }

    fun addPostIngredients(ingredient: String) {
        _postIngredientsState.update {
            _postIngredientsState.value + listOf(ingredient)
        }
    }

    fun removePostIngredients(ingredient: String) {
        _postIngredientsState.update {
            _postIngredientsState.value.toMutableList().apply { remove(ingredient.trim()) }
        }
    }

    fun updateContent(content: String?) {
        _contentState.value = content
    }

    fun createPost() {
        val imageFile = fileState.value
        val imageMultipart: MultipartBody.Part? =
            if (imageFile != null) {
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "thumbnail",
                    imageFile.name,
                    requestImageFile
                )
            } else {
                null
            }
        val title = titleState.value ?: "Kosong"
        val titleBody = title.toRequestBody("text/plain".toMediaType())

        val content = contentState.value ?: "Kosong"
        val contentBody = content.toRequestBody("text/plain".toMediaType())


        val tag = if (postTagState.value.isEmpty()) {
            ""
        } else {
            postTagState.value.reduce { oldValue, newValue ->
                "$oldValue,$newValue"
            }
        }
        val tagBody = tag.toRequestBody("text/plain".toMediaType())

        val ingredient = if (postIngredientsState.value.isEmpty()) {
            ""
        } else {
            postIngredientsState.value.reduce { oldValue, newValue ->
                "$oldValue,$newValue"
            }
        }
        val ingredientBody = ingredient.toRequestBody("text/plain".toMediaType())

        viewModelScope.launch {
            userRepository.createPost(
                titleBody,
                contentBody,
                imageMultipart,
                tagBody,
                ingredientBody
            ).collect {
                _createPostState.value = it
            }
        }
    }

    fun resetViewModel(){
        _createPostState.value = null
        _titleState.value = null
        _fileState.value = null
        _isBackCameraState.value = true
        _postTagState.value = mutableListOf()
        _postIngredientsState.value = listOf()
        _contentState.value = null
    }
}