package com.example.skyreport.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.skyreport.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.navigationBarColor = Color.TRANSPARENT
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            window.isNavigationBarContrastEnforced = false


            setupImmarsiveNavigationBar()
        }
    }

    private fun setupImmarsiveNavigationBar() {
        val windowInsetsController = WindowCompat.getInsetsController(window,window.decorView)

        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}