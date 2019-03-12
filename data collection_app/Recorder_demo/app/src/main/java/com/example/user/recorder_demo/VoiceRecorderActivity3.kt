package com.example.user.recorder_demo

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.voicerecorder.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class VoiceRecorderActivity3 : AppCompatActivity() {

    //네트워크 서비스
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    //초기화 및 허가
    private var myAudioRecorder: MediaRecorder? = null
    private var output: String? = null
    private var start: Button? = null
    private var play: Button? = null
    private var next: Button? = null

    //인텐트 받아올놈들
    var user_name : String? = null
    var user_age : String? = null
    var user_gender : String? = null
    var user_alchol : String? = null
    var user_today : String? = null



    //시간 핸들러







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voicerecorder)

        //인텐트 값 받자
        if (intent.hasExtra("In_name") &&
            intent.hasExtra("In_age") &&
            intent.hasExtra("In_gender") &&
            intent.hasExtra("In_alchol") ) {

            user_name = intent.getStringExtra("In_name")
            user_age = intent.getStringExtra("In_age")
            user_gender = intent.getStringExtra("In_gender")
            user_alchol = intent.getStringExtra("In_alchol")
            user_today = intent.getStringExtra("In_today")



              // Toast.makeText(this, user_age + user_alchol + user_gender + user_name, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }


        //준비준비
        start = findViewById<View>(R.id.button_start1) as Button
        play = findViewById<View>(R.id.button_play1) as Button
        next = findViewById<View>(R.id.button_voice_next1) as Button


        button_start1.setOnClickListener {
            start()
        }
        button_play1.setOnClickListener {
            try{
                play()
            }
            catch(e: IOException){
                Log.i("IOException", "Error in play")
            }
        }
        button_voice_next1.setOnClickListener{
            next()
        }
        play!!.isEnabled = false


        //파일위치는 내파일/내장메모리/myrecording.3gp
        output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today + "third" + ".3gp"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)
    }


    fun start() {
        try {
            //재시작했을때를 위해 한번 더 시도
            output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today + "third" + ".3gp"
            myAudioRecorder = MediaRecorder()
            myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            myAudioRecorder!!.setOutputFile(output)

            myAudioRecorder!!.prepare()
            myAudioRecorder!!.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        start!!.isEnabled = false
        Toast.makeText(applicationContext, "Recording started", Toast.LENGTH_SHORT).show()

        //8초동안 녹음을 합니다요~
        // 1~2.5, 3.5~5.0, 6.0~ 7.5... 한번 녹음 할때마다 1.5초씩!
        Run.after(8000) {stop()}

        //글자가 나타나는 타이밍
        Run.after(1000) {light_on()}
        Run.after(2500) {light_off()}
        Run.after(3500) {light_on()}
        Run.after(5000) {light_off()}
        Run.after(6000) {light_on()}
        Run.after(7500) {light_off()}



    }

    fun stop() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null
        play!!.isEnabled = true


        //NEXT -> SEND로 이름을 바꾸어서 보여줌
        button_voice_next1.text = "SEND"
        next!!.visibility = View.VISIBLE



        Toast.makeText(applicationContext, "Audio recorded successfully", Toast.LENGTH_SHORT).show()

    }
    //VoiceRecorderActivity1과 2와 메소드는 같지만 서버로 보내는 작업으로 나타낼것임


    fun next()
    {
        //다음 어절 녹음
        val resultIntent = Intent(this@VoiceRecorderActivity3, WriteActivity::class.java) // Intent객체 생성방법
        resultIntent.putExtra("In_name", user_name)
        resultIntent.putExtra("In_age", user_age)
        resultIntent.putExtra("In_gender", user_gender)
        resultIntent.putExtra("In_alchol", user_alchol)
        resultIntent.putExtra("In_today", user_today)
        startActivity(resultIntent)
    }

    //서버로 보내는 작업
/*
    private fun getSendFileResponse() {
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
    */

    @Throws(IllegalArgumentException::class, SecurityException::class, IllegalStateException::class, IOException::class)
    fun play() {
        play!!.isEnabled = false
        start!!.isEnabled = true
        val m = MediaPlayer()
        m.setDataSource(output)
        m.prepare()
        m.start()
        Toast.makeText(applicationContext,"Playing", Toast.LENGTH_LONG).show()




        //Toast.makeText(applicationContext, Environment.getExternalStorageDirectory().absolutePath, Toast.LENGTH_SHORT).show()
        //mHandler.postDelayed(delayed_task(), m.duration.toLong()+100) // 끝난후방샐
        /*  Timer().schedule(10){
              stop!!.isEnabled = false
              play!!.isEnabled = true
          }
      */
    }

    fun light_on()
    {
        text_third.visibility = View.VISIBLE
    }

    fun light_off()
    {
        text_third.visibility = View.INVISIBLE
    }




    //시간 핸들러
    class Run {
        companion object {
            fun after(delay: Long, process: () -> Unit) {
                Handler().postDelayed({
                    process()
                }, delay)
            }
        }
    }




}
