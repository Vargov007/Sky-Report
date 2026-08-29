package com.example.skyreport.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.skyreport.databinding.ActivityMainBinding
import com.example.skyreport.ui.viewmodels.AuthViewmodel
import com.example.skyreport.utils.AuthUiState
import com.example.skyreport.BuildConfig
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityMainBinding
    private val viewModel : AuthViewmodel by viewModels()
    private val webClientID = BuildConfig.WEB_CLIENT_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.button.setOnClickListener {
            viewModel.onGoogleSignInClick(this, webClientID)
        }

        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect {state ->
                    when(state){
                        is AuthUiState.Idle ->{
                            binding.progressbar.visibility = View.GONE
                            binding.button.isEnabled = true
                        }

                        is AuthUiState.Loading -> {
                            binding.progressbar.visibility = View.VISIBLE
                            binding.button.isEnabled = false
                        }

                        is AuthUiState.Success -> {
                            binding.progressbar.visibility = View.GONE
                            binding.button.isEnabled = true



                            Toast.makeText(this@MainActivity, "Welcome, ${state.user?.displayName}!",
                                Toast.LENGTH_LONG).show()
                        }

                        is AuthUiState.Error -> {
                            binding.progressbar.visibility = View.GONE
                            binding.button.isEnabled = true

                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }
}