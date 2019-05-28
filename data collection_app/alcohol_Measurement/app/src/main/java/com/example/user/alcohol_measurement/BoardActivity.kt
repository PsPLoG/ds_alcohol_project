package com.example.user.alcohol_measurement

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.user.alcohol_measurement.SharedPreferenceController.clearSPC
import kotlinx.android.synthetic.main.board_activity3.*
import kotlinx.android.synthetic.main.result_activity2.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity : AppCompatActivity() {
    var intent_id: String? = null
    var intent_password: String? = null
    var intent_name: String? = null
    var intent_gender: String? = null
    var intent_age: String? = null
    var intent_today: String? = null

    var intent_email: String? = null
    var intent_phone: String? = null

    //캔버스 핸들러
    internal var pStatus = 0
    private val handler = Handler()
    internal lateinit var tv: TextView
    internal lateinit var mProgress: ProgressBar
    internal lateinit var drawable: Drawable

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity3)
        setOnBtnClickListener() //자동 로그인

        //초기값 검사(파일있냐?)
//        val alphaAni: Animation
//
//        alphaAni = AnimationUtils.loadAnimation(this, R.anim.transrate)
//        imageView7.setAnimation(alphaAni)
        getInitCheckResponse()

        //인텐트 값 받자
        if (intent.hasExtra("In_id") &&
                intent.hasExtra("In_password") &&
                intent.hasExtra("In_name") &&
                intent.hasExtra("In_gender") &&
                intent.hasExtra("In_age") &&
                intent.hasExtra("In_today") &&
                intent.hasExtra("In_email") &&
                intent.hasExtra("In_phone")
        ) {
            intent_id = intent.getStringExtra("In_id")
            intent_password = intent.getStringExtra("In_password")
            intent_name = intent.getStringExtra("In_name")
            intent_gender = intent.getStringExtra("In_gender")
            intent_age = intent.getStringExtra("In_age")
            intent_today = intent.getStringExtra("In_today")
            intent_email = intent.getStringExtra("In_email")
            intent_phone = intent.getStringExtra("In_phone")

            //Toast.makeText(this, intent_id + intent_password + intent_name + intent_gender + intent_age + intent_today, Toast.LENGTH_SHORT).show()
        } else {
            //  Toast.makeText(this, intent_id + intent_password + intent_name + intent_gender + intent_age + intent_today, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        //txt_board_Username창 바꾸기
        txt_board_userName.text = intent_name

        // configureBottomNavigation()

        //캔버스 프로게스바 그리기
        val res = resources
        drawable = res.getDrawable(R.drawable.circular2)
        mProgress = findViewById(R.id.circularProgressbar) as ProgressBar

        mProgress.progress = 100   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress

        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable

    }


    private fun setOnBtnClickListener() {
        //측정 시 자 악!
        btn_board_start.setOnClickListener {
            go_Mesure_Activity()
        }

        //로그아웃
        btn_board_logout.setOnClickListener {
            id_logout()
        }

        //내정보
        btn_board_myInfo.setOnClickListener {
            show_info()
        }

        //주의사항
        btn_board_warning.setOnClickListener {
            show_warning()
        }

        //뒤로가기
        btn_board_cancel.setOnClickListener {
            finish()
        }


    }

    fun make_Intent(intent: Intent) {
        intent.putExtra("In_id", intent_id)
        intent.putExtra("In_password", intent_password)
        intent.putExtra("In_name", intent_name)
        intent.putExtra("In_gender", intent_gender)
        intent.putExtra("In_age", intent_age)
        intent.putExtra("In_today", intent_today)
        intent.putExtra("In_email", intent_email)
        intent.putExtra("In_phone", intent_phone)
    }

    fun go_Mesure_Activity() {
        val capture_intent = Intent(this, ImageCapture_MeasureActivity::class.java)
        make_Intent(capture_intent)
        startActivityForResult(capture_intent, 5)
    }

    fun go_Addinfo_Activity(): Intent {
        val capture_intent = Intent(this, AddinformationActivity::class.java)
        make_Intent(capture_intent)
        return capture_intent
    }

    //로그아웃, 다이얼로그 대화상자
    fun id_logout() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("메시지 상자")
        builder.setMessage("로그아웃 하시겠습니까?")
        builder.setPositiveButton("아니요") { dialog, id ->
            //아무것도안해요
        }
        builder.setNegativeButton("예") { dialog, id ->
            startActivity<MainActivity>()
            clearSPC(this)
            finish()
        }
        builder.show()
    }

    fun show_warning() {

        val customDialogWarning = CustomDialogWarning(this@BoardActivity)

        // 커스텀 다이얼로그를 호출한다.
        // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
        customDialogWarning.callFunction()
    }

    fun show_info() {
        val customDialogInfo = CustomDialogInfo(this@BoardActivity)
        // 커스텀 다이얼로그를 호출한다.
        // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
        customDialogInfo.callFunction(intent_name, intent_id, intent_email, intent_gender, intent_phone)
    }


    //startActivityForResult를 통해 실행한 엑티비티에 대한 callback을 처리하는 메소드입니다!
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //캡쳐순서
        if (resultCode == 5) {
            Log.i("TEST", "보이스로!")
            val record_intent = Intent(this, VoiceRecorder_MeasureActivity::class.java)
            make_Intent(record_intent)
            startActivityForResult(record_intent, 5)
        }
        //음성 측정하고 넘어왔는데 에러!
        if (resultCode == 6) {
            Log.i("TEST", "result code =6, 에러 발생!")
            Toast.makeText(this, "DB에러, 재측정 요망", Toast.LENGTH_SHORT).show()

            /*
            val record_intent = Intent(this, ResultActivity::class.java)
            make_Intent(record_intent)
            startActivityForResult(record_intent, 6)*/
        }
        //음성 측정하고 넘어온 녀석
        if (resultCode == 7) {
            Log.i("TEST", "결과창으로!")
            val record_intent = Intent(this, ResultActivity::class.java)
            make_Intent(record_intent)
            startActivityForResult(record_intent, 7)
        }
        //결과창에서 재측정 요청시
        if (resultCode == 9) {
            Log.i("TEST", "이미지로!")
            go_Mesure_Activity()
        }
    }


    private fun getInitCheckResponse() {

        val token = SharedPreferenceController.getAuthorization(this)
        val getInitCheckResponse = networkService.getInitCheckResponse(token)


        getInitCheckResponse.enqueue(object : Callback<GetInitCheckResponse> {
            override fun onFailure(call: Call<GetInitCheckResponse>, t: Throwable) {
                Log.e("TEST :: init_check fail", t.toString())
            }

            override fun onResponse(call: Call<GetInitCheckResponse>, response: Response<GetInitCheckResponse>) {
                if (response.isSuccessful) {

                    Log.i("TEST", response.body()!!.message)
                    Log.i("TEST", response.body()!!.status.toString())

                    //초기화파일없으면..
//                    if (response.body()!!.message == "파일이 없습니다.") {
//                        startActivity(go_Addinfo_Activity())
//                        finish()
//                    }

                }
            }
        })
    }


/*


    //결과 출력 -> 비음주(0), 음주(1)
    fun print_out_result()
    {
        var builder : AlertDialog.Builder  = AlertDialog.Builder(this)
        builder.setTitle("음주 측정 결과")
        builder.setMessage("비음주로 측정되셨습니다.")
        builder.setNegativeButton("확인") {dialog, id ->

        }
        builder.show()
    }
*/


}
