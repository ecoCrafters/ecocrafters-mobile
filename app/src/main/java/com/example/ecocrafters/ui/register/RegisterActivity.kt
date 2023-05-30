package com.example.ecocrafters.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.databinding.ActivityRegisterBinding
import com.example.ecocrafters.ui.main.MainActivity
import com.example.ecocrafters.utils.EditTextValidator
import com.example.ecocrafters.utils.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBackRegister.setOnClickListener(this@RegisterActivity)
            btnRegisterRegister.setOnClickListener(this@RegisterActivity)
            edEmailRegister.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validateEmailInput(
                    inputEmailRegister,
                    text,
                    getString(R.string.email_tidak_valid)
                )
            })
            edPasswordRegister.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validatePasswordInput(
                    inputPasswordRegister,
                    text,
                    getString(R.string.password_anda_kurang_dari_8_karakter)
                )
            })
            edUsernameRegister.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validateUsernameInput(
                    inputUsernameRegister,
                    text,
                    getString(R.string.username_anda_tidak_valid)
                )
            })
            edNameRegister.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validateFullNameInput(
                    inputNameRegister,
                    text,
                    getString(R.string.karakter_kurang)
                )
            })
        }
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v.id) {
                btnBackRegister.id -> finish()
                btnRegisterRegister.id -> {
                    if (isInputValid()) {
                        val fullName = edNameRegister.text.toString()
                        val username = edUsernameRegister.text.toString()
                        val email = edEmailRegister.text.toString()
                        val password = edPasswordRegister.text.toString()

                        lifecycleScope.launch {
                            viewModel.registerUser(fullName, username, email, password)
                                .collectLatest {
                                    renderResultOf(it)
                                }
                        }
                    } else {
                        showToast(getString(R.string.informasi_akun_kosong_atau_tidak_valid))
                    }
                }
            }
        }
    }

    private fun renderResultOf(result: ResultOf<AuthResponse>?) {
        when (result) {
            ResultOf.Loading -> {
                showLoading(true)
            }
            is ResultOf.Success -> {
                showLoading(false)
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(mainIntent)
                finish()
            }

            is ResultOf.Error -> {
                showLoading(false)
                showToast(result.error)
            }
            else -> {
                showLoading(false)
            }
        }
    }

    private fun isInputValid(): Boolean {
        binding.apply {
            val isEmpty = edNameRegister.text.isNullOrBlank()
                    || edUsernameRegister.text.isNullOrBlank()
                    || edEmailRegister.text.isNullOrBlank()
                    || edPasswordRegister.text.isNullOrBlank()
            val isNotError = edNameRegister.error == null
                    || edUsernameRegister.error == null
                    || edEmailRegister.error == null
                    || edPasswordRegister.error == null
            return !isEmpty && isNotError
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbRegister.isVisible = isLoading
            btnRegisterRegister.visibility = if (isLoading){
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}