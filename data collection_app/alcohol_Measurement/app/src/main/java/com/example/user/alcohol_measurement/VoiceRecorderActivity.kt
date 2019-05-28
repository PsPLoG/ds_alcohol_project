package com.example.user.alcohol_measurement

import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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

class VoiceRecorderActivity : AppCompatActivity() {

    //네트워크 서비스
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    //서버로 보내자
    var PT : String? = null
    var gender_number : String? = null

    //초기화 및 허가
    private var myAudioRecorder: MediaRecorder? = null
    private var output: String? = null
    private var start: Button? = null
    private var play: Button? = null
    private var next: Button? = null

    //인텐트 받아올놈들
    var intent_id: String? = null
    var intent_password : String? = null
    var intent_name : String? = null
    var intent_gender : String? = null
    var intent_age : String? = null
    var intent_today : String? = null


    //캔버스 핸들러
    internal var pStatus = 0
    private val handler = Handler()
    internal lateinit var tv: TextView
    internal lateinit var mProgress : ProgressBar
    internal lateinit var drawable: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voicerecorder)

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

        //경로
        if(intent_gender == "남자")
            gender_number = "0"
        else if (intent_gender == "여자")
            gender_number = "2"
        PT = "a" + "_" + gender_number + "_" + intent_name + "_" + intent_age + "_" + intent_gender + "_" +  intent_today



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
        output = Environment.getExternalStorageDirectory().absolutePath + "/"+ PT + "Long_first" + ".wav"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)


        //캔버스 프로게스바 그리기
        val res = resources
        drawable = res.getDrawable(R.drawable.circular)
        mProgress = findViewById(R.id.circularProgressbar) as ProgressBar

        mProgress.progress = 0   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress
        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable

        tv = findViewById(R.id.tv) as TextView
    }


    fun start() {
        try {
            //재시작했을때를 위해 한번 더 시도, 첫번째
            output = Environment.getExternalStorageDirectory().absolutePath + "/" +PT+ "Long_first" + ".wav"
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

        //캔버스그리는거 같이돌리자, 프로그레서바
        run_progress()

        //텍스트 바꾸자 녹음중으로!
        txt_recorder_3.text = "녹음중"

        start!!.isEnabled = false
        Toast.makeText(applicationContext, "Recording started", Toast.LENGTH_SHORT).show()


        /*
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
        Run.after(23500) {light_off()}*/


        Run.after(1000) {light_on()}
        Run.after(8000) {last_stop()}
        Run.after(8100) {next()}
        Run.after(7500) {light_off()}


/*

        //글자다시 바꾸자
        Run.after(24000) {
            txt_recorder_3.text = "글자가 나타나면 위의 글자를\n 한번 씩 끊어서 \n총 3회 또박또박 읽어주세요."
        }
*/

        //글자다시 바꾸자
                Run.after(8000) {
                    txt_recorder_3.text = "글자가 나타나면 위의 글자를\n 한번 씩 끊어서 \n총 3회 또박또박 읽어주세요."
                }





    }

    fun stop() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null

    }

/*

    fun start2() {
        //파일위치는 내파일/내장메모리/myrecording.wav
        output = Environment.getExternalStorageDirectory().absolutePath + "/" + PT+ "Long_second" + ".wav"
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
        output = Environment.getExternalStorageDirectory().absolutePath + "/"+ PT + "Long_third" + ".wav"
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder!!.setOutputFile(output)

        myAudioRecorder!!.prepare()
        myAudioRecorder!!.start()

    }
*/

    fun last_stop() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null
        play!!.isEnabled = true

/*

        //NEXT -> SEND로 이름을 바꾸어서 보여줌
        button_voice_next1.text = "BACK"
        next!!.visibility = View.VISIBLE
*/



        Toast.makeText(applicationContext, "Audio recorded successfully", Toast.LENGTH_SHORT).show()

    }


    fun next()
    {
        setResult(2)
        finish()
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

    fun run_progress()
    {
        //초기화
        mProgress.progress = 0   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress
        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable


        Thread(Runnable {
            // TODO Auto-generated method stub
            while (pStatus < 100) {
                pStatus += 1

                handler.post(Runnable {
                    // TODO Auto-generated method stub
                    mProgress.progress = pStatus
                    tv.text = pStatus.toString() + "%"
                })
                try {
                    Thread.sleep(80)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }).start()

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
