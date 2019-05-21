package com.example.user.alcohol_measurement


import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.registerform_activity.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class RegisterFormActivity : AppCompatActivity() {
    val REQUEST_CODE_SELECT_IMAGE: Int = 1004
    val My_READ_STORAGE_REQUEST_CODE = 7777
    var imageURI : String? = null
    var id_flag : Boolean = false

    //인텐트 받아올놈들
    var user_ID : String? = null
    var user_password : String? = null
    var user_name : String? = null
    var user_birth : String? = null
    var user_phone : String? = null
    var user_email : String? = null

    //
    private var mImage: MultipartBody.Part? = null
    private var mLongVoice1: MultipartBody.Part? = null
    private var mLongVoice2: MultipartBody.Part? = null
    private var mLongVoice3: MultipartBody.Part? = null



    //
    var gender_text : String = ""

    // lazy를 안쓰고 인스턴스 상수를 생성한다면 아래와 같은 상수 선언과 같습니다!
    // val networkService : NetworkService = ApplicationController.instance.networkService
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupform_activity)
        setOnBtnClickListener()

    }



    private fun setOnBtnClickListener() {

        //회원가입 취소 버튼 리스너
        btn_write_act_cancel.setOnClickListener {
            finish()
        }

        //회원가입 완료 버튼 리스너
        btn_write_act_complete.setOnClickListener {
            //게시물 쓰기 서버 통신 관련 메소드를 호출합니다!
            getSignUpResponse()
        }

        et_write_act_btn1.setOnClickListener {
            Gender_Checked()
        }

        et_write_act_btn2.setOnClickListener {
            Gender_Checked()
        }
        btn_write_act_check.setOnClickListener{
            ID_Checked()
        }



    }

    private fun ID_Checked() {

        if (et_write_act_id.text.toString() != "") {
            getBoardListResponse()
        }
        else {
            Toast.makeText(this@RegisterFormActivity, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Gender_Checked() {

        var option1 : RadioButton = findViewById(R.id.et_write_act_btn1) // option1체크박스 // 선언
        var option2 : RadioButton = findViewById(R.id.et_write_act_btn2) // option1체크박스 // 선언

        if (option1.isChecked) { // option1 이 체크되었다면
            gender_text = "남자" }
        if (option2.isChecked) {
            gender_text = "여자"
        }
        Toast.makeText(this@RegisterFormActivity, gender_text , Toast.LENGTH_SHORT).show()
    }


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

    private fun getSignUpResponse() {

        user_ID =  et_write_act_id.text.toString()
        user_birth = et_write_act_birth.text.toString()
        user_email = et_write_act_email.text.toString()
        user_name =et_write_act_name.text.toString()
        user_password = et_write_act_pass.text.toString()
        user_phone =et_write_act_phone.text.toString()


        val token = SharedPreferenceController.getAuthorization(this)
        val ID = RequestBody.create(MediaType.parse("text/plain"), user_ID.toString())
        val password = RequestBody.create(MediaType.parse("text/plain"), user_password.toString())
        val name = RequestBody.create(MediaType.parse("text/plain"), user_name.toString())
        var gender = RequestBody.create(MediaType.parse("text/plain"), gender_text.toString())
        val birth = RequestBody.create(MediaType.parse("text/plain"), user_birth.toString())
        val phone = RequestBody.create(MediaType.parse("text/plain"), user_phone.toString())
        val email = RequestBody.create(MediaType.parse("text/plain"), user_email.toString())


        mLongVoice1 = make_MultiPartBody(
            Environment.getExternalStorageDirectory().absolutePath + "/" + "PT" + ".jpg"
            , "voiceFile1"
        )
        mLongVoice2 = make_MultiPartBody(
            Environment.getExternalStorageDirectory().absolutePath + "/" + "PT" + ".jpg"
            , "voiceFile2"
        )
        mLongVoice3 = make_MultiPartBody(
            Environment.getExternalStorageDirectory().absolutePath + "/" + "PT" + ".jpg"
            , "voiceFile3"
        )
        mImage = make_MultiPartBody(
            Environment.getExternalStorageDirectory().absolutePath + "/" + "PT" + ".jpg"
            , "imageFile"
        )

        //중복체크 안하거나 중복이면
        if(!id_flag)
        {
          Toast.makeText(this, "아이디 중복 혹은 중복 체크 해주세요.", Toast.LENGTH_SHORT).show()
        }//전부 넣었는가?
       else if (user_ID!!.isNotEmpty() &&
            user_password!!.isNotEmpty() &&
            user_phone!!.isNotEmpty() &&
            user_email!!.isNotEmpty() &&
            user_name!!.isNotEmpty() &&
            gender_text.isNotEmpty() &&
            user_birth!!.isNotEmpty()
        )
        {

            val postSignUpResponse : Call<PostSignUpResponse> = networkService.postSignUpResponse(
                token,
                ID,
                password,
                name,
                gender,
                birth,
                phone,
                email,
                null,
                null,
                null,
                null
            )

            postSignUpResponse.enqueue(object : Callback<PostSignUpResponse> {
                override fun onFailure(call: Call<PostSignUpResponse>, t: Throwable) {
                    Log.e("send fail", t.toString())
                }
                override fun onResponse(call: Call<PostSignUpResponse>, response: Response<PostSignUpResponse>) {
                    if (response.isSuccessful) {
                        //toast(response.body()!!.message)
                        //    text_send.text = "response.body()!!.message + " - " + response.body()!!.status;

                        Log.i("TEST",response.body()!!.message)
                        Log.i("TEST",response.body()!!.status)
                        //toast( response.body()!!.message + " - " + response.body()!!.status)

                        //상태 반환하고 종료
                        // setResult(response.body()!!.status.toInt())
                        finish ()
                    }
                }
            })
        }
        else
        {
            Toast.makeText(this@RegisterFormActivity, "입력란을 모두 채워주세요.", Toast.LENGTH_SHORT).show()
        }

    }



    private fun getBoardListResponse(){
        user_ID =  et_write_act_id.text.toString()

        val getBoardListResponse = networkService.getBoardListResponse("application/json", user_ID!!)
        Log.i("TEST",user_ID)
        getBoardListResponse.enqueue(object : Callback<GetBoardListResponse>{
            override fun onFailure(call: Call<GetBoardListResponse>, t: Throwable) {
                Log.e("board list fail", t.toString()) }
        override fun onResponse(call: Call<GetBoardListResponse>, response: Response<GetBoardListResponse>) {
            if (response. isSuccessful ){
                Log.i("TEST",response.body()!!.message)
                Log.i("TEST",response.body()!!.status.toString())

                //중복이 안되면
                if(response.body()!!.message == "아이디 사용 가능")
                {
                    //참이여야 회원가입가능
                    toast("아이디 사용 가능")
                    id_flag = true
                }
                //중복이면
                else
                {
                    toast("아이디 중복")
                }

            }
        }
    })
    }
}