package com.example.user.recorder_demo

data class PostLogInResponse (
    val status : Int,
    val message : String,
    val data : LoginData
)

data class LoginData(
    val token : String
)