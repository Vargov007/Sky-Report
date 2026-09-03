package com.example.skyreport.activities

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.TransitionManager
import com.example.skyreport.R
import com.example.skyreport.databinding.ActivityWeatherPageBinding

class WeatherPage : BaseActivity() {

    private lateinit var binding: ActivityWeatherPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWeatherPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.locationBtn.setOnClickListener {
//            observeSearch()
//        }
//        binding.clearSearchBtn.setOnClickListener {
//            observeClear()
//        }

        textBgColor()
    }



























//    fun observeClear() {
//        TransitionManager.beginDelayedTransition(binding.headerContainer)
//        binding.searchText.text.clear()
//        binding.locationBtn.visibility = View.VISIBLE
//        binding.searchBar.visibility = View.GONE
//    }


//    fun observeSearch() {
//        TransitionManager.beginDelayedTransition((binding.headerContainer))
//        binding.locationBtn.visibility = View.GONE
//        binding.searchBar.visibility = View.VISIBLE
//        binding.searchText.requestFocus()
//
//    }


    private fun textBgColor() {
        val textview = findViewById<TextView>(R.id.tempt)

// Ensure the TextView has been laid out so we can get its exact height
        textview.post {
            val paint = textview.paint
            val height = textview.height.toFloat() // Get height instead of width

            // Create the linear gradient shader (Top to Bottom)
            val textShader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height, // Start and end points of the gradient.
                    intArrayOf(
                        Color.parseColor("#FFFFFF"), // Start color
                        Color.parseColor("#89DBCFCF"),
                    ),
                    null,
                    Shader.TileMode.CLAMP,
                )
            // Assign the shader to the TextView's paint object
            paint.shader = textShader
            textview.invalidate() // Redraw the TextView with the vertical gradient
        }
    }
}
