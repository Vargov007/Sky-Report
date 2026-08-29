package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreport.data.repo.AuthRepository
import com.example.skyreport.utils.AuthUiState
import android.content.Context
import com.example.skyreport.utils.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewmodel(
    private val authRepo : AuthRepository = AuthRepository()
) : ViewModel(){

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState : StateFlow<AuthUiState> = _uiState

    fun onGoogleSignInClick(context: Context, idToken : String){
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = authRepo.signInWithGoogle(context, idToken)
                _uiState.value =
                    when(result){
                        is Resources.Success -> AuthUiState.Success(result.data?.user)
                        is Resources.Error -> AuthUiState.Error(result.message.toString())
                        is Resources.Loading -> AuthUiState.Loading
                    }
            }catch (e : Exception){
                _uiState.value = AuthUiState.Error(e.message.toString())
            }
            }


    }
}