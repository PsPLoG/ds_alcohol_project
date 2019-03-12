package com.example.user.sever_test.post

data class PostLogInResponse (
    val status : Int,
    val message : String,
    val data : LoginData
)

data class LoginData(
    val token : String
)