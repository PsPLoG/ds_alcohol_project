package com.example.user.alcohol_measurement

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.result_activity.*
import kotlinx.android.synthetic.main.result_activity2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.animation.AnimationUtils
import android.view.animation.Animation




class ResultActivity : AppCompatActivity() {

    //서버
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    //인텐트 받아올놈들
    var intent_id: String? = null
    var intent_password : String? = null
    var intent_name : String? = null
    var intent_gender : String? = null
    var intent_age : String? = null
    var intent_today : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity2)



        //인텐트 값 받자
        if (intent.hasExtra("In_id") &&
            intent.hasExtra("In_password") &&
            intent.hasExtra("In_name") &&
            intent.hasExtra("In_gender") &&
            intent.hasExtra("In_age") &&
            intent.hasExtra("In_today") ) {

            intent_id = intent.getStringExtra("In_id")
            intent_password = intent.getStringExtra("In_password")
            intent_name = intent.getStringExtra("In_name")
            intent_gender = intent.getStringExtra("In_gender")
            intent_age = intent.getStringExtra("In_age")
            intent_today = intent.getStringExtra("In_today")


            // Toast.makeText(this, user_age + user_alchol + user_gender + user_name, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        //애니메이션
        val alphaAni: Animation

        alphaAni = AnimationUtils.loadAnimation(this, R.anim.transrate)
        inside.setAnimation(alphaAni)

        //버튼클릭
        setOnBtnClickListener()

        //결과 요청
        postGetResultResponse()

    }

    private fun setOnBtnClickListener() {

        //결과창 확인 리스너
        btn_result_act_complete2.setOnClickListener {
            finish()
        }

        //재측정 리스너
        btn_result_act_remesure.setOnClickListener {

            //go_Mesure_Activity()
            setResult(9)
            finish()
        }



    }

    fun showResultText(str : String)
    {
        if(str == "인식 불가")
        {
            txt_result_text.text = "재측정 요망"
        }
        else
        {
            txt_result_text.text = str
        }
    }

    //결과주세요.
    private fun postGetResultResponse() {

        val token = SharedPreferenceController.getAuthorization(this)
        val postResultResponse = networkService.postResultResponse(token)

        postResultResponse.enqueue(object : Callback<GetResultResponse> {
            override fun onFailure(call: Call<GetResultResponse>, t: Throwable) {
                inside.clearAnimation()
                Log.e("TEST :: get_result fail", t.toString())
            }
            override fun onResponse(call: Call<GetResultResponse>, response: Response<GetResultResponse>) {
                inside.clearAnimation()
                if (response.isSuccessful) {
                    Log.i("TEST :: ",response.body()!!.message)
                    Log.i("TEST :: ",response.body()!!.status)

                    //메시지 결과에따라 텍스트에 보여줌
                    showResultText(response.body()!!.message)
                }
            }
        })
    }

}