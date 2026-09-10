package com.example.skyreport.data.models

data class Users(
     val uid: String = "",
     val name: String? = "",
     val email : String? = "",
     val password : String = "",
     val profileImage : String? = "",
     val  createdAt : Any? = null
)
