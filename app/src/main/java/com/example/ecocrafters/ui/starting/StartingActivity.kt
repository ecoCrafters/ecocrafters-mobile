package com.example.ecocrafters.ui.starting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ecocrafters.databinding.ActivityStartingBinding
import com.example.ecocrafters.ui.register.RegisterActivity
import com.example.ecocrafters.ui.login.LoginActivity
import com.example.ecocrafters.ui.main.MainActivity
import com.google.android.material.color.DynamicColors

class StartingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityStartingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivitiesIfAvailable(this.application)
        super.onCreate(savedInstanceState)
        binding = ActivityStartingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLoginStarting.setOnClickListener(this@StartingActivity)
            btnRegisterStarting.setOnClickListener(this@StartingActivity)
        }

        // TODO: Delete This If The API is Online
        binding.ivLogo.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intentDestination = when (v.id) {
            binding.btnLoginStarting.id -> {
                LoginActivity::class.java
            }
            // TODO: Delete This If The API is Online
            binding.ivLogo.id -> {
                MainActivity::class.java
            }
            else -> {
                RegisterActivity::class.java
            }
        }
        val intent = Intent(this@StartingActivity, intentDestination)
        startActivity(intent)
    }
}