package com.example.user.alcohol_measurement


data class PostCheckImageFileResopnse(
    val status : String,
    val message : String,
    val data : ReceiveData
)

data class ReceiveData(
    val preImage : String
)