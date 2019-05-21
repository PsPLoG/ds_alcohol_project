package com.example.user.alcohol_measurement

data class GetInitCheckResponse(
    val status : Int,
    val message : String,
    val data : InitData
)

data class InitData(
    val token : String
)