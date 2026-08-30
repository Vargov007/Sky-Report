package com.example.skyreport.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.skyreport.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private  var  binding : ActivityMainBinding ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding?.root)

        if (Firebase.auth.currentUser != null){
            setupWeatherUi()
        }
    }

    private fun setupWeatherUi() {

        binding?.navHostFragment?.isVisible = false

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}