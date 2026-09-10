package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreport.data.repo.AuthRepository
import com.example.skyreport.utils.AuthUiState
import android.content.Context
import android.util.Log
import com.example.skyreport.data.models.Users
import com.example.skyreport.utils.Resources
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewmodel(
    private val authRepo : AuthRepository = AuthRepository()
) : ViewModel(){

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState : StateFlow<AuthUiState> = _uiState

    private val _userprofile = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val userProfile : StateFlow<AuthUiState> = _userprofile

    private val firestore = FirebaseFirestore.getInstance()

    fun onGoogleSignInClick(context: Context, idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = authRepo.signInWithGoogle(context, idToken)
                when (result) {
                    is Resources.Success -> {
                        // The user object is available in result.data
                        val firebaseUser = result.data?.user

                        val usermodel = Users(
                            uid = firebaseUser?.uid ?: "",
                            name = firebaseUser?.displayName?: "",
                            email = firebaseUser?.email?: "",
                            profileImage = firebaseUser?.photoUrl?.toString()
                        )

                        // Update Firestore lastLogin (fetchUserData actually does a set/write)
                        try {
                            authRepo.fetchUserData()
                        } catch (e: Exception) {
                            // Non-critical: sign-in worked even if Firestore update failed
                        }

                        // Set success state with the user object to trigger navigation
                        _uiState.value = AuthUiState.Success(usermodel)
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


    //complete all code
    fun fatchUserdata(userId: String){
        viewModelScope.launch {
            _userprofile.value = AuthUiState.Loading
            try {
                val document = firestore.collection("SKY REPORT")
                    .document(userId)
                    .get()
                    .await()

                        if (document != null && document.exists()) {
                            val user = document.toObject(Users::class.java)
                            if (user != null) {
                                _userprofile.value = AuthUiState.Success(user)
                            } else {
                                _userprofile.value = AuthUiState.Error("Invalid User data")

                            }
                        }else{
                                _userprofile.value = AuthUiState.Error("Profile not found")
                            }

            }catch (e: Exception){
                _userprofile.value = AuthUiState.Error(e.message ?: "Failed to fetch user data")
                Log.d("AuthViewmodel", "Failed fatchUserdata",e)
            }
        }


    }

    val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit) // Trigger initial load
    }

    fun refreshData2(){
        viewModelScope.launch { _refreshTrigger.emit(Unit) }
    }
}