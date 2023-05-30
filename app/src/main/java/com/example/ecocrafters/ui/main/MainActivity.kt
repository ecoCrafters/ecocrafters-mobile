package com.example.ecocrafters.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecocrafters.R
import com.example.ecocrafters.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_home, R.id.navigation_search, R.id.navigation_create, R.id.navigation_more
//        ).build()
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        binding.bottomNavMain.setupWithNavController(navController)
        NavigationUI.setupWithNavController(binding.bottomNavMain, navController)

    }
}