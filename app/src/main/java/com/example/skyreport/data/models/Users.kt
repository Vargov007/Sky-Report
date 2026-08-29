package com.example.skyreport.data.models

data class Users(
    private val uid: String = "",
    private val name: String? = "",
    private val email : String? = "",
    private val password : String = "",
    private val profileImage : String? = "",
    private val  createdAt : Long = System.currentTimeMillis(),
)
