package com.example.skyreport.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.skyreport.BuildConfig
import com.example.skyreport.databinding.FragmentSignInBinding
import com.example.skyreport.ui.viewmodels.AuthViewmodel
import com.example.skyreport.utils.AuthUiState
import kotlinx.coroutines.launch
import kotlin.getValue

class SignIn : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: AuthViewmodel by viewModels()
    private val webClientID = BuildConfig.WEB_CLIENT_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
//        return inflater.inflate(R.layout.fragment_sign_in, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setUpClickListeners()
        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AuthUiState.Idle -> {
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

                            Toast
                                .makeText(
                                    requireContext(),
                                    "Welcome, ${state.user?.displayName}!",
                                    Toast.LENGTH_LONG,
                                ).show()
                        }

                        is AuthUiState.Error -> {
                            binding.progressbar.visibility = View.GONE
                            binding.button.isEnabled = true

                            Toast
                                .makeText(requireContext(), state.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun setUpClickListeners() {
        binding.button.setOnClickListener {
            viewModel.onGoogleSignInClick(requireActivity(), webClientID)
        }

        binding.button2.setOnClickListener {
            viewModel.signout()
        }
    }
}
