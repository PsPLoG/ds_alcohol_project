package com.example.user.alcohol_measurement


data class PostSendFileResponse(
    val status : String,
    val message : String
)

data class SendData(
    val token : String
)