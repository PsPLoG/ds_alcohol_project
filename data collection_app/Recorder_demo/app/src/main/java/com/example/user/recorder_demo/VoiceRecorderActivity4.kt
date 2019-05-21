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

class VoiceRecorderActivity4 : AppCompatActivity() {

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

        var first_flag  : Int? = 0
        var second_flag  : Int? = 0
        var third_flag  : Int? = 0

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


        //파일위치는 내파일/내장메모리/myrecording.wav
        output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today + "Long_first" + ".wav"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)
    }


    fun start() {
        try {
            //재시작했을때를 위해 한번 더 시도, 첫번째
            output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today + "Long_first" + ".wav"

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

        //24초동안 녹음을 합니다요~ 첫번째꺼
        Run.after(8000) {stop()}

        //첫번글자가 나타나는 타이밍
        Run.after(1000) {light_on()}
        Run.after(7500) {light_off()}

        //두글자가 나타나는 타이밍
        Run.after(9000) {light_on()}
        Run.after(9000) {start2()}
        Run.after(16000) {stop()}
        Run.after(15500) {light_off()}

        //세번글자가 나타나는 타이밍
        Run.after(17000) {light_on()}
        Run.after(17000) {start3()}
        Run.after(24000) {last_stop()}
        Run.after(23500) {light_off()}






    }

    fun stop() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null

    }


    fun start2() {
        //파일위치는 내파일/내장메모리/myrecording.wav
        output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today + "Long_second" + ".wav"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)

        myAudioRecorder!!.prepare()
        myAudioRecorder!!.start()

    }


    fun start3() {
        //파일위치는 내파일/내장메모리/myrecording.wav
        output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  user_today + "Long_third" + ".wav"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)

        myAudioRecorder!!.prepare()
        myAudioRecorder!!.start()

    }

    fun last_stop() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null
        play!!.isEnabled = true


        //NEXT -> SEND로 이름을 바꾸어서 보여줌
        button_voice_next1.text = "SEND"
        next!!.visibility = View.VISIBLE



        Toast.makeText(applicationContext, "Audio recorded successfully", Toast.LENGTH_SHORT).show()

    }


    fun next()
    {
        //다음 어절 녹음
        val resultIntent = Intent(this@VoiceRecorderActivity4, WriteActivity::class.java) // Intent객체 생성방법
        resultIntent.putExtra("In_name", user_name)
        resultIntent.putExtra("In_age", user_age)
        resultIntent.putExtra("In_gender", user_gender)
        resultIntent.putExtra("In_alchol", user_alchol)
        resultIntent.putExtra("In_today", user_today)
        startActivity(resultIntent)
    }


    @Throws(IllegalArgumentException::class, SecurityException::class, IllegalStateException::class, IOException::class)
    fun play() {
        play!!.isEnabled = false
        start!!.isEnabled = true
        val m = MediaPlayer()
        m.setDataSource(output)
        m.prepare()
        m.start()
        Toast.makeText(applicationContext,"Playing", Toast.LENGTH_LONG).show()


    }

    fun light_on()
    {
        text_forth.visibility = View.VISIBLE
    }

    fun light_off()
    {
        text_forth.visibility = View.INVISIBLE
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