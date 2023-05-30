package com.example.ecocrafters.data

sealed class ResultOf<out R> private constructor() {
    object Loading : ResultOf<Nothing>()
    data class Success<out T>(val data: T) : ResultOf<T>()
    data class Error(val error: String) : ResultOf<Nothing>()
}
