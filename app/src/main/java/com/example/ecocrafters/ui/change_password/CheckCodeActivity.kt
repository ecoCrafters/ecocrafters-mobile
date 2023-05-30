package com.example.ecocrafters.ui.change_password

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.ecocrafters.data.ResultOf
import com.example.ecocrafters.data.remote.response.CodeResponse
import com.example.ecocrafters.databinding.ActivityCheckCodeBinding
import com.example.ecocrafters.ui.change_password.ChangePasswordActivity.Companion.CODE_EXTRA
import com.example.ecocrafters.utils.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CheckCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckCodeBinding

    private val viewModel: CheckCodeViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCheckCode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            btnCheckCode.setOnClickListener {
                viewModel.checkChangePasswordCode(pinConfirmCheck.text.toString())
            }
        }

        startSubscription()
    }

    private fun startSubscription() {
        lifecycleScope.launch {
            viewModel.checkCodeState.collectLatest {
                renderResultOf(it)
            }
        }

    }

    private fun renderResultOf(result: ResultOf<CodeResponse>?) {
        when (result) {
            ResultOf.Loading -> {
                showLoading(true)
            }
            is ResultOf.Success -> {
                showLoading(false)
                showToast(result.data.message)
                if (result.data.status) {
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    val code = binding.pinConfirmCheck.text.toString()
                    intent.putExtra(CODE_EXTRA, code)
                    startActivity(intent)
                    finish()
                }
            }

            is ResultOf.Error -> {
                showLoading(false)
                showToast(result.error)
            }

            else -> {
                // Do Nothing
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            ovelayCheckCode.isVisible = !isLoading
            pbCheckCode.isVisible = !isLoading
            pinConfirmCheck.isEnabled = !isLoading
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}