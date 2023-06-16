package com.example.ecocrafters.ui.change_password

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ecocrafters.R
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.databinding.ActivityChangePasswordBinding
import com.example.ecocrafters.utils.EditTextValidator
import com.example.ecocrafters.utils.ViewModelFactory
import com.example.ecocrafters.utils.showToast
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityChangePasswordBinding

    private val viewModel: ChangePasswordViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        code = intent?.getStringExtra(CODE_EXTRA) ?: ""

        setSupportActionBar(binding.toolbarChangePassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            btnChangePassword.setOnClickListener(this@ChangePasswordActivity)
            edPasswordChange.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validatePasswordInput(
                    inputPasswordChange,
                    text,
                    getString(R.string.password_anda_kurang_dari_8_karakter)
                )
            })

            edConfirmPasswordChange.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                EditTextValidator.validatePasswordInput(
                    inputConfirmPasswordChange,
                    text,
                    getString(R.string.password_anda_kurang_dari_8_karakter)
                )
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isInputValid(): Boolean {
        binding.apply {
            val isEmpty = edPasswordChange.text.isNullOrBlank()
                    || edConfirmPasswordChange.text.isNullOrBlank()
            val isNotError = edPasswordChange.error == null
                    || edConfirmPasswordChange.error == null
            val isEqual =
                edPasswordChange.text.toString() == edConfirmPasswordChange.text.toString()

            return !isEmpty && isNotError && isEqual
        }
    }

    private fun renderResult(result: ResultOf<CodeResponse>) {
        when (result) {
            ResultOf.Loading -> {
                showLoading(true)
            }

            is ResultOf.Success -> {
                showLoading(false)
                showToast(result.data.message)
                if (result.data.status) {
                    binding.apply {
                        val completeAnim = ivCompleteChangePassword.drawable
                        ivCompleteChangePassword.isVisible = true
                        overlayCheckCode.isVisible = true
                        if (completeAnim is AnimatedVectorDrawable) {
                            Log.d("testanim", "onCreate: instancefound" + completeAnim.toString())
                            val animation = completeAnim as AnimatedVectorDrawable?
                            animation?.start()
                        }
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                finish()
                            }
                        }, 2000)
                    }
                }
            }

            is ResultOf.Error -> {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnChangePassword.isVisible = !isLoading
            pbChangePassword.isVisible = isLoading
        }
    }

    override fun onClick(v: View?) {
        if (isInputValid()) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.changePassword(code, binding.edPasswordChange.text.toString())
                        .collect {
                            renderResult(it)
                        }
                }
            }
        } else {
            showToast(getString(R.string.password_tidak_valid))
        }
    }

    companion object {
        const val CODE_EXTRA = "code"
    }

}