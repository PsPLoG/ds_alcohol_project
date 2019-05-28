package com.example.user.alcohol_measurement

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main3.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var TAG: String = "에러 종류 = "
    var FLAG: Boolean = false

    //
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    //현재시간
    var formatter = SimpleDateFormat("yyyy-MM-dd_HH:mm")
    var cal = Calendar.getInstance()
    var today: String? = formatter.format(cal.getTime())

    //인텐트 넣어줄 놈들
    var intent_id: String? = null
    var intent_pw: String? = null
    var intent_name: String? = null
    var intent_gender: String? = null
    var intent_age: String? = null
    var intent_today: String? = null
    var intent_birth: String? = null
    var intent_email: String? = null
    var intent_phone: String? = null

    //권한 얻기위한 변수
    private var permissionToRecordAccepted = false
    private var permissionToWriteAccepted = false
    private var permissionToCameraAccepted = false
    private var permissionToReadAccepted = false
    private var permissionToInternetAccepted = false

    //허가 배열
    private val permissions = arrayOf(
            "android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.INTERNET"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        /* //영훈이가해달라했떤거
         getAResponse()*/

        //처음에 권한얻어오려고 확인
        val requestCode = 200
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }

        setOnBtnClickListener() //자동 로그인, 토큰값과 아이디값,
        if (SharedPreferenceController.getAuthorization(this).isNotEmpty()) {
            //makeIntentAutoLogin()
            startActivity(makeIntentAutoLogin())
        }
    }

    //권한 획득
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                permissionToCameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                permissionToReadAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED
                permissionToInternetAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED
            }
        }


        //권한 획득 여부 -> 로그로 확인
        if (!permissionToRecordAccepted) {
            Log.i(TAG, "리코드 허가 에러")
            super@MainActivity.finish()
        }
        if (!permissionToWriteAccepted) {
            Log.i(TAG, "쓰기 허가 에러")
            super@MainActivity.finish()

        }
        if (!permissionToCameraAccepted) {
            Log.i(TAG, "카메라 허가 에러")
            super@MainActivity.finish()

        }
        if (!permissionToReadAccepted) {
            Log.i(TAG, "읽기 허가 에러")
            super@MainActivity.finish()

        }
        if (!permissionToInternetAccepted) {
            Log.i(TAG, "인터넷 허가 에러")
            super@MainActivity.finish()

        }

    }


    private fun setOnBtnClickListener() {
        button_main_login.setOnClickListener {

            //tempLoginResponse()
            //startActivity<AddinformationActivity>()
            getLoginResponse() //임시로
        }
        button_main_goregister.setOnClickListener {
            startActivity<RegisterFormActivity>()
        }


    }

    private fun tempLoginResponse() {
        val resultIntent = makeIntent()
        startActivity(resultIntent)

    }

    private fun calcurate_age_from_birth(today: String?, birth: String?): String? {
        val s: Int = today!!.substring(0, 4).toInt()
        val s2: Int = birth!!.substring(0, 4).toInt()
        //val s2 : Int = 1995

        val result: String = (s - s2 + 1).toString()

        //Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        return result
    }

    //6개 id, pw, name, gender, age ,today
    private fun makeIntentAutoLogin(): Intent {
        val resultIntent = Intent(this, BoardActivity::class.java) // Intent객체 생성방법

        var account: com.example.user.alcohol_measurement.domain.Account = SharedPreferenceController.getAuthored_intent(this)

        resultIntent.putExtra("In_id", account.id)
        resultIntent.putExtra("In_password", account.pass)
        resultIntent.putExtra("In_name", account.name)
        resultIntent.putExtra("In_gender", account.gender)
        resultIntent.putExtra("In_age", account.age)
        resultIntent.putExtra("In_today", today)
        resultIntent.putExtra("In_email", account.email)
        resultIntent.putExtra("In_phone", account.phone)

        //Toast.makeText(this, account.id + account.pass + account.name + account.gender + account.age + today, Toast.LENGTH_SHORT).show()

        return resultIntent
        //  startActivity(resultIntent)
    }

    //6개 id, pw, name, gender, age ,today
    private fun makeIntent(): Intent {
        val resultIntent = Intent(this, BoardActivity::class.java) // Intent객체 생성방법

        intent_age = calcurate_age_from_birth(today, intent_birth)

        resultIntent.putExtra("In_id", intent_id)
        resultIntent.putExtra("In_password", editText_main_password.text)
        resultIntent.putExtra("In_name", intent_name)
        resultIntent.putExtra("In_gender", intent_gender)
        resultIntent.putExtra("In_age", intent_age)
        resultIntent.putExtra("In_today", today)
        resultIntent.putExtra("In_email", intent_email)
        resultIntent.putExtra("In_phone", intent_phone)


        return resultIntent
        //  startActivity(resultIntent)
    }


    private fun getLoginResponse() {
        if (editText_main_id.text.toString().isNotEmpty() && editText_main_password.text.toString().isNotEmpty()) {
            val input_id = editText_main_id.text.toString()
            val input_pw = editText_main_password.text.toString()
            val jsonObject: JSONObject = JSONObject()

            //로그인 json형식 만들기
            jsonObject.put("id", input_id)
            jsonObject.put("password", input_pw)

            val gsonObject: JsonObject = JsonParser().parse(jsonObject.toString()) as JsonObject
            val postLogInResponse = networkService.postLoginResponse("application/json", gsonObject)

            postLogInResponse.enqueue(object : Callback<PostLogInResponse> {
                override fun onFailure(call: Call<PostLogInResponse>, t: Throwable) {
                    Log.e("TEST :: Login fail", t.toString())
                }

                override fun onResponse(call: Call<PostLogInResponse>, response: Response<PostLogInResponse>) {
                    if (response.isSuccessful) {
                        Log.i("TEST :: ", response.body()!!.message)
                        Log.i("TEST :: ", response.body()!!.status)
                        if (response.body()!!.message == "로그인 성공") {
                            //로그가 위에있을때 로그인실패시 튕김.
                            Log.i("TEST :: ", response.body()!!.data.name)
                            Log.i("TEST :: ", response.body()!!.data.gender)
                            Log.i("TEST :: ", response.body()!!.data.birth)


                            val token = response.body()!!.data.token
                            //저번 시간에 배웠던 SharedPreference에 토큰을 저장!
                            SharedPreferenceController.setAuthorization(this@MainActivity, token)
                            //toast(SharedPreferenceController.getAuthorization(this@MainActivity))

                            intent_id = editText_main_id.text.toString()
                            intent_name = response.body()!!.data.name
                            intent_gender = response.body()!!.data.gender
                            intent_birth = response.body()!!.data.birth
                            intent_email = response.body()!!.data.email
                            intent_phone = response.body()!!.data.phone
                            intent_age = calcurate_age_from_birth(today, intent_birth)

                            //인텐트도 저장할겁니다 내부 저장소에
                            SharedPreferenceController.setAuthored_intent(this@MainActivity,
                                    editText_main_id.text.toString(), editText_main_password.text.toString(),
                                    intent_name!!, intent_gender!!, intent_age!!, intent_email!!, intent_phone!!)

                            var account: com.example.user.alcohol_measurement.domain.Account = (SharedPreferenceController.getAuthored_intent(this@MainActivity))

                            //toast(account.age + "-" + account.gender + " " + account.id + " " + account.name + " " + account.age + " " + account.pass + " " + account.email
                            //        + " " + account.phone)
                            startActivity(makeIntent())
                        } else {
                            Toast.makeText(this@MainActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        } else {
            Toast.makeText(this, "빈칸을 채워주세요", Toast.LENGTH_SHORT).show()
        }


    }


    /*
    private fun make_MultiPartBody(path : String, Sever_in_name : String) : MultipartBody.Part?
    {
        val file = File(path)
        val body = RequestBody.create(
            MediaType.parse("multipart/form-data"), file)
        val multipartBody = MultipartBody.Part.createFormData(
            Sever_in_name,
            file.name,
            body
        )

        return multipartBody
    }


    //영훈이가 보내달라고한 형태 버릴것!
    private fun getAResponse() {


        var mImage : MultipartBody.Part? = make_MultiPartBody(
            Environment.getExternalStorageDirectory().absolutePath + "/" + "PT" + ".jpg"
            , "video"
        )
            val postAResponse = networkService.postAResponse(mImage)

            postAResponse.enqueue(object : Callback<PostAResponse> {
                override fun onFailure(call: Call<PostAResponse>, t: Throwable) {

                    Log.i("TEST :: send fail", t.toString())

                }

                override fun onResponse(call: Call<PostAResponse>, response: Response<PostAResponse>) {
                    if (response.isSuccessful) {

                        Log.i("TEST :: ","dd")
                      //  Log.i("TEST :: ",response.body()!!.message)
                        //Log.i("TEST :: ",response.body()!!.status)

                    }
                }
            })
        }*/
}



