package com.example.user.recorder_demo

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {
    /*@POST("/users")
    fun postSignUpResponse(
        @Header("Content-Type") content_type : String,
        @Body() body : JsonObject
    ) : Call<PostSignUpResponse>
*/
    @POST("/login")
    fun postLoginResponse(
        @Header("Content-Type") content_type : String,
        @Body() body : JsonObject
    ) : Call<PostLogInResponse>

    //게시판 글쓰기
    @Multipart
    @POST("/contents")
    fun postWriteBoardResponse(
        @Header("Authorization") token : String,
        @Part("title") title : RequestBody,
        @Part("contents") contents : RequestBody,
        @Part photo : MultipartBody.Part?
        ) : Call<PostWriteBoardResponse>

    //파일 보내기
    @Multipart
    @POST("/users/save")
    fun postSendFileResponse(
        @Header("Authorization") token : String,
        @Part("name") name : RequestBody,
        @Part("gender") gender : RequestBody,
        @Part("age") age : Int,
        @Part("status") status : RequestBody,

        @Part voicefile1 : MultipartBody.Part?,
        @Part voicefile2 : MultipartBody.Part?,
        @Part voicefile3 : MultipartBody.Part?,
        @Part voicefile4 : MultipartBody.Part?,
        @Part voicefile5 : MultipartBody.Part?,
        @Part voicefile6 : MultipartBody.Part?,
        @Part videofile : MultipartBody.Part?
    ) : Call<PostSendFileResponse>

    //회원가입
    @Multipart
    @POST("/users/saveUser")
    fun postSignUpResponse(
        @Header("Authorization") token : String,
        @Part("ID") ID : RequestBody,
        @Part("password") password : RequestBody,
        @Part("name") name : RequestBody,
        @Part("gender") gender : RequestBody,
        @Part("birth") birth : RequestBody,
        @Part("phone") phone : RequestBody,
        @Part("email") email : RequestBody,

        @Part voiceFile1 : MultipartBody.Part?,
    @Part voiceFile2 : MultipartBody.Part?,
    @Part voiceFile3 : MultipartBody.Part?,
    @Part imageFile : MultipartBody.Part?
    ) : Call<PostSignUpResponse>


}