package com.example.user.recorder_demo

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.animationactivity.*
import kotlinx.android.synthetic.main.voicerecorder.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class VoiceRecorderActivity : AppCompatActivity() {
    //초기화 및 허가
    private var myAudioRecorder: MediaRecorder? = null
    private var output: String? = null
    private var start: Button? = null
    private var stop: Button? = null
    private var play: Button? = null

    //인텐트 받아올놈들
    var user_name : String? = null
    var user_age : String? = null
    var user_gender : String? = null
    var user_alchol : String? = null

    //현재시간
    var formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    var cal = Calendar.getInstance()
    var today: String? = formatter.format(cal.getTime())




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



              // Toast.makeText(this, user_age + user_alchol + user_gender + user_name, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }


        //

        start = findViewById<View>(R.id.button1) as Button
        stop = findViewById<View>(R.id.button2) as Button
        play = findViewById<View>(R.id.button3) as Button
        button1.setOnClickListener {
            start()
        }
        button2.setOnClickListener {
            stop()
        }
        button3.setOnClickListener {
            try{
                play()
            }
            catch(e: IOException){
                Log.i("IOException", "Error in play")
            }
        }
        button_send_file.setOnClickListener {
            try{
                send_to_sever()
            }
            catch (e: IOException)
            {
                Log.i("IOException", "Error in play")
            }
        }

        stop!!.isEnabled = false
        play!!.isEnabled = false
        //파일위치는 내파일/내장메모리/myrecording.3gp
        output = Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +  today + ".3gp"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)
    }


    fun start() {
        try {
            myAudioRecorder!!.prepare()
            myAudioRecorder!!.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        start!!.isEnabled = false
        stop!!.isEnabled = true
        Toast.makeText(applicationContext, "Recording started", Toast.LENGTH_SHORT).show()
    }

    fun stop() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null
        stop!!.isEnabled = false
        play!!.isEnabled = true
        button_send_file!!.visibility = View.VISIBLE

        Toast.makeText(applicationContext, "Audio recorded successfully", Toast.LENGTH_SHORT).show()

    }

    @Throws(IllegalArgumentException::class, SecurityException::class, IllegalStateException::class, IOException::class)
    fun play() {
        play!!.isEnabled = false
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

    fun send_to_sever() {
        TODO("서버로 보내는 작업 만들어야함")
    }



}
