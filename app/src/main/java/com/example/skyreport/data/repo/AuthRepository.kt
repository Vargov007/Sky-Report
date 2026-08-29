package com.example.skyreport.data.repo

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.skyreport.utils.Resources
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository (
    private val auth : FirebaseAuth =  FirebaseAuth.getInstance(),
    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
){


    suspend fun signInWithGoogle(context: Context, idToken: String) : Resources<AuthResult> {
//        try {
//            val credential = GoogleAuthProvider.getCredential(idToken,null)
//            val authResult = auth.signInWithCredential(credential).await()
//
//            authResult.user?.let { firebaseUser ->
//                if (authResult.additionalUserInfo?.isNewUser == true){
//                    val user = Users(
//                        uid = firebaseUser.uid,
//                        name = firebaseUser.displayName,
//                        email = firebaseUser.email,
//                        profileImage = firebaseUser.photoUrl?.toString(),
//                    )
//
//                    saveUserToFirestore(user)
//                    Resources.success(user)
//
//                }else{
//                    Resources.success(getUserFromFirestore(firebaseUser.uid))
//                }
//            }?: Resources.error("User not found")
//
//        }catch (e : Exception){
//            Resources.error(e.message.toString())
//        }
//
//        fun saveUserToFirestore(users: Users){
//            firestore.c
//        }

        return try {
            val cradentialManeger = CredentialManager.create(context)

            // Configure Google ID Option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(idToken)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Launch Google Sign-In prompt
            val result = cradentialManeger.getCredential(context,request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(idToken,null)
                val authResult = auth.signInWithCredential(authCredential).await()

                authResult.user?.let {user ->
                    saveUserToFirestore(user)
                }

                Resources.Success(authResult)
            }else{
                Resources.Error("Invalid credential type")
            }
            }catch (e : Exception){
                Resources.Error(e.message.toString())
            }


        }

    private suspend fun saveUserToFirestore(user: FirebaseUser) {
        val usermap = hashMapOf(
            "uid" to user.uid,
            "name" to (user.displayName?: ""),
            "email" to (user.email?: ""),
            "profileImage" to (user.photoUrl?.toString() ?: ""),
            "createdAt" to FieldValue.serverTimestamp()

        )
        firestore.collection("SKY REPORT")
            .document(user.uid)
            .set(usermap, SetOptions.merge())
            .await()
    }

}