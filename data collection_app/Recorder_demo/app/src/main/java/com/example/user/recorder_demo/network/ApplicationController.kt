package com.example.user.recorder_demo

import android.app.Application
import com.example.user.recorder_demo.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class  ApplicationController  :  Application()  {
    private  val  baseURL  =  "127.0.0.1:8080/users/save"
    lateinit  var  networkService : NetworkService

    companion  object  {
        lateinit  var  instance :  ApplicationController
    }

    override  fun  onCreate ()  {
        super .onCreate()
        instance  =  this
        //buildNetWork()
    }

    fun  buildNetWork ()  {
        val  retrofit:  Retrofit  =  Retrofit.Builder()
            .baseUrl( baseURL )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        networkService  =  retrofit.create(NetworkService:: class . java )
    }
}
