package com.example.ecocrafters.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object EditTextValidator {
    fun validateEmailInput(textInput: TextInputLayout, text: CharSequence?, errorMessage: String) {
        val isNotValid = Patterns.EMAIL_ADDRESS.matcher(text ?: "").matches().not()
        if (text.isNullOrBlank()) {
            textInput.isErrorEnabled = false
        } else if (isNotValid) {
            textInput.error = errorMessage
        } else {
            textInput.isErrorEnabled = false
        }
    }

    fun validateFullNameInput(textInput: TextInputLayout, text: CharSequence?, errorMessage: String) {
        val isLessThanThree = text?.length in 1..2
         if (isLessThanThree) {
             textInput.error = errorMessage
        } else {
             textInput.isErrorEnabled = false
        }
    }

    fun validateUsernameInput(textInput: TextInputLayout, text: CharSequence?, errorMessage: String) {
        val unicodeMatcher = """[^\u0000-\u02BF]""".toRegex()
        val containsUnicode = unicodeMatcher.containsMatchIn(text.toString())
        val containsSpace = text.toString().contains(' ')
        val characterCount = text.toString().length !in 4..32
        if (containsSpace || containsUnicode || characterCount) {
            textInput.error = errorMessage
        } else {
            textInput.isErrorEnabled = false
        }
    }

    fun validatePasswordInput(textInput: TextInputLayout, text: CharSequence?, errorMessage: String) {
        val isLessThanEight = text?.length in 1..7
        if (isLessThanEight) {
            textInput.error = errorMessage
        } else {
            textInput.isErrorEnabled = false
        }
    }
}