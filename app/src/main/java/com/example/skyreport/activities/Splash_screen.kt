package com.example.skyreport.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import com.example.skyreport.databinding.ActivitySplashScreenBinding

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
            setupWeatherUi()        // Destroys Splash activity so back button won't return to it
        }, 2000)


    }

    private fun setupWeatherUi() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
