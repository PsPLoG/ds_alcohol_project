package com.example.user.alcohol_measurement

data class PostLogInResponse (
    val status : String,
    val message : String,
    val data : LoginData
)
data class LoginData(
    val token : String,
    val name : String,
    val gender : String,
    val birth : String,
    val phone : String,
    val email : String
)