package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreport.data.repo.AuthRepository
import com.example.skyreport.utils.AuthUiState
import android.content.Context
import com.example.skyreport.utils.Resources
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewmodel(
    private val authRepo : AuthRepository = AuthRepository()
) : ViewModel(){

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState : StateFlow<AuthUiState> = _uiState

    fun onGoogleSignInClick(context: Context, idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = authRepo.signInWithGoogle(context, idToken)
                when (result) {
                    is Resources.Success -> {
                        // The user object is available in result.data
                        val firebaseUser = result.data?.user

                        // Update Firestore lastLogin (fetchUserData actually does a set/write)
                        try {
                            authRepo.fetchUserData()
                        } catch (e: Exception) {
                            // Non-critical: sign-in worked even if Firestore update failed
                        }

                        // Set success state with the user object to trigger navigation
                        _uiState.value = AuthUiState.Success(firebaseUser)
                    }
                    is Resources.Error -> {
                        _uiState.value = AuthUiState.Error(result.message ?: "Authentication failed")
                    }
                    is Resources.Loading -> {
                        _uiState.value = AuthUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message.toString())
            }
        }
    }

    fun signout(){
        viewModelScope.launch {
            try {
                authRepo.signOut()
                _uiState.value = AuthUiState.Success(null)
            }catch (e: Exception){
                _uiState.value = AuthUiState.Error(e.message ?: "Sign Out Failed")
            }

        }
    }

    //complete all code
//    fun fatchUserdata(){
//
//    }

    val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit) // Trigger initial load
    }

    fun refreshData2(){
        viewModelScope.launch { _refreshTrigger.emit(Unit) }
    }
}