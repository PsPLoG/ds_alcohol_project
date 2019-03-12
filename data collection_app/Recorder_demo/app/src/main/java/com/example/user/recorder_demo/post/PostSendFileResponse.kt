package com.example.user.recorder_demo

data class PostSendFileResponse(
    val name : String,
    val gender : String,
    val age : Int,
    val status : String,
    val message : String
)

data class SendData(
    val token : String
)