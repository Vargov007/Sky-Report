package com.example.skyreport.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.example.skyreport.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : BaseActivity() {

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
        startActivity(Intent(this, WeatherPage::class.java))
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}