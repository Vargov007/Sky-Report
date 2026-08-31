package com.example.skyreport.activities

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skyreport.R

class WeatherPage : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather_page)

        textBgColor()
    }





























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
