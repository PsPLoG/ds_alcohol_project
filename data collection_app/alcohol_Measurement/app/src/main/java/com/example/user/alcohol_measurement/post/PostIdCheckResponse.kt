package com.example.user.alcohol_measurement

data class PostIdCheckResponse (
    val status : String,
    val message : String,
    val data : SendID
)

data class SendID(
    val ID : String
)