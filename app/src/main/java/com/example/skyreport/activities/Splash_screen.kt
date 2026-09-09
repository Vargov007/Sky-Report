package com.example.skyreport.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import com.example.skyreport.R
import com.example.skyreport.databinding.ActivitySplashScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class Splash_screen : BaseActivity() {
    private var binding: ActivitySplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding?.root)

        val headerContainer = binding?.headerContainer
        headerContainer?.alpha = 0f
        headerContainer?.translationY = 80f

        // Run animation
        headerContainer
            ?.animate()
            ?.alpha(1f)
            ?.translationY(0f)
            ?.setDuration(1000)
            ?.setStartDelay(200)
            ?.start()

        // Navigate to WeatherPage Activity[cite: 1]
        Handler(Looper.getMainLooper()).postDelayed({
            setupWeatherUi() // Destroys Splash activity so back button won't return to it
        }, 1000)
    }

    private fun setupWeatherUi() {
        val currentUser = Firebase.auth.currentUser
        val destination =
            if (currentUser != null) {
                WeatherPage::class.java
            } else {
                MainActivity::class.java
            }

        val intent = Intent(this, destination)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, 0)
        finish()


    }
}
