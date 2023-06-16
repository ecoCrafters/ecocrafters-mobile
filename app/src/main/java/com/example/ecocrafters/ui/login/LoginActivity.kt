package com.example.ecocrafters.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.AuthResponse
import com.example.ecocrafters.data.remote.response.PostApiResponse
import com.example.ecocrafters.databinding.ActivityLoginBinding
import com.example.ecocrafters.ui.check_code.CheckCodeActivity
import com.example.ecocrafters.ui.main.MainActivity
import com.example.ecocrafters.utils.EditTextValidator
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLoginLogin.setOnClickListener(this@LoginActivity)
            btnBackLogin.setOnClickListener(this@LoginActivity)
            btnChangePasswordLogin.setOnClickListener(this@LoginActivity)
            edEmailLogin.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validateEmailInput(
                    inputEmailLogin, text, getString(R.string.email_tidak_valid)
                )
            })
            edPasswordLogin.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validatePasswordInput(
                    inputPasswordLogin,
                    text,
                    getString(R.string.password_anda_kurang_dari_8_karakter)
                )
            })
        }

        startSubscription()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.loginState.collect {
                renderResultLogin(it)
            }

        }
        lifecycleScope.launch {
            viewModel.passwordRequest.collect {
                renderResultChangePassword(it)
            }
        }
    }


    private fun isEmailValid(): Boolean {
        binding.apply {
            return !(edEmailLogin.text.isNullOrBlank()) && edEmailLogin.error == null
        }
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v.id) {
                btnBackLogin.id -> finish()
                btnLoginLogin.id -> {
                    if (isInputValid()) {
                        val email = edEmailLogin.text.toString()
                        val password = edPasswordLogin.text.toString()
                        lifecycleScope.launch {
                            viewModel.loginUser(email, password)
                        }
                    } else {
                        showToast(getString(R.string.informasi_akun_kosong_atau_tidak_valid))
                    }
                }

                btnChangePasswordLogin.id -> {
                    if (isEmailValid()) {
                        val email = edEmailLogin.text.toString()
                        lifecycleScope.launch {
                            viewModel.sendChangePasswordCodeRequest(email)
                        }
                    } else {
                        showToast(getString(R.string.email_kosong_atau_tidak_valid))
                    }
                }
            }
        }
    }


    private fun showOverlay(isShown: Boolean) {
        binding.apply {
            overlayLogin.isVisible = isShown
            pbChangeLogin.isVisible = isShown
        }
    }

    private fun renderResultChangePassword(result: ResultOf<PostApiResponse>?) {
        when (result) {
            ResultOf.Loading -> showOverlay(true)
            is ResultOf.Success -> {
                showOverlay(false)
                showToast(result.data.message)
                val intent = Intent(this, CheckCodeActivity::class.java)
                startActivity(intent)
            }

            is ResultOf.Error -> {
                showOverlay(false)
                showToast(result.error)
            }

            else -> {}
        }
    }

    private fun renderResultLogin(result: ResultOf<AuthResponse>?) {
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

            else -> {}
        }
    }

    private fun isInputValid(): Boolean {
        binding.apply {
            val isEmpty = edEmailLogin.text.isNullOrBlank() || edPasswordLogin.text.isNullOrBlank()
            val isNotError = edEmailLogin.error == null || edPasswordLogin.error == null
            return !isEmpty && isNotError
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbLogin.isVisible = isLoading
            btnLoginLogin.visibility = if (isLoading) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }
    }
}

