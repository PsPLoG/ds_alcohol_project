package com.example.user.recorder_demo

data class PostSendFileResponse(
    val status : String,
    val message : String
)

data class SendData(
    val token : String
)