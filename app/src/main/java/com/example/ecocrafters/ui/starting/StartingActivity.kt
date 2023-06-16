package com.example.ecocrafters.ui.starting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecocrafters.databinding.ActivityStartingBinding
import com.example.ecocrafters.ui.login.LoginActivity
import com.example.ecocrafters.ui.main.MainActivity
import com.example.ecocrafters.ui.register.RegisterActivity
import com.example.ecocrafters.utils.ViewModelFactory
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch

class StartingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityStartingBinding
    private val viewModel: StartingViewModel by viewModels {
        ViewModelFactory
            .getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivitiesIfAvailable(this.application)
        super.onCreate(savedInstanceState)
        binding = ActivityStartingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLoginStarting.setOnClickListener(this@StartingActivity)
            btnRegisterStarting.setOnClickListener(this@StartingActivity)
        }
        lifecycleScope.launch {
            if (viewModel.isLoggedIn()){
                val intent = Intent(this@StartingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onClick(v: View) {
        val intentDestination = when (v.id) {
            binding.btnLoginStarting.id -> {
                LoginActivity::class.java
            }
            else -> {
                RegisterActivity::class.java
            }
        }
        val intent = Intent(this@StartingActivity, intentDestination)
        startActivity(intent)
    }
}