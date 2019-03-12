package com.example.user.sever_test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.user.sever_test.network.ApplicationController
import com.example.user.sever_test.network.NetworkService
import com.example.user.sever_test.post.PostSignUpResponse
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import org.jetbrains.anko.toast
import org.json.JSONObject

class  SignUpActivity  :  AppCompatActivity() {

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setOnBtnClickListener()
    }

    private fun setOnBtnClickListener() {
        btn_sign_up_act_complete.setOnClickListener { getSignUpResponse() }
        btn_sign_up_act_close.setOnClickListener {
            finish()
        }
    }

    private fun getSignUpResponse() {
        //EditText 에  있는  값  받기
        val input_name: String = et_sign_up_act_name.text.toString()
        val input_pw: String = et_sign_up_act_pw.text.toString()
        val input_email: String = et_sign_up_act_email.text.toString()
        val input_part: String = et_sign_up_act_part.text.toString()

        //Json  형식의  객체  만들기
        var jsonObject = JSONObject()
        jsonObject.put("name", input_name)
        jsonObject.put("email", input_email)
        jsonObject.put("password", input_pw)
        jsonObject.put("part", input_part)

        //Gson  라이브러리의  Json  Parser 을  통해  객체를  Json 으로 !
        val gsonObject = JsonParser().parse(jsonObject.toString()) as JsonObject
        val postSignUpResponse: Call<PostSignUpResponse> =
            networkService.postSignUpResponse("application/json", gsonObject)

        postSignUpResponse.enqueue(object : Callback<PostSignUpResponse> {
            override fun onFailure(call: Call<PostSignUpResponse>, t: Throwable) {
                Log.i("TEST::", "회원가입실패")
                Log.e("sign  up  fail", t.toString())
            }

            // 통신  성공  시  수행되는  메소드
            override fun onResponse(call: Call<PostSignUpResponse>, response: Response<PostSignUpResponse>) {

                if (response.isSuccessful) {
                    Log.i("TEST::", "회원가입성공")
                    toast(response.body()!!.message)
                    finish()
                }
            }
        })
    }
}