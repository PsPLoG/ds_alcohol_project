package com.example.user.alcohol_measurement


import android.Manifest
import android.app.Activity
import android.content.CursorLoader
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.addinfo_activity.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AddinformationActivity : AppCompatActivity() {
    val REQUEST_CODE_SELECT_IMAGE: Int = 1004
    val My_READ_STORAGE_REQUEST_CODE = 7777
    var imageURI : String? = null

    var PT : String? = "PT"
    var PT_I : String? = "PT"
    var gender_number : String? = null

    //인텐트 받자
    var intent_id: String? = null
    var intent_password : String? = null
    var intent_name : String? = null
    var intent_gender : String? = null
    var intent_age : String? = null
    var intent_today : String? = null


    //파일
    private var mImage: MultipartBody.Part? = null
    private var mVoice1: MultipartBody.Part? = null
    private var mVoice2: MultipartBody.Part? = null
    private var mVoice3: MultipartBody.Part? = null


    private var data : MultipartBody.Part? = null



    // lazy를 안쓰고 인스턴스 상수를 생성한다면 아래와 같은 상수 선언과 같습니다!
    // val networkService : NetworkService = ApplicationController.instance.networkService
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.addinfo_activity2)
        setOnBtnClickListener()

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
          //  Toast.makeText(this, intent_id + intent_password + intent_name + intent_gender + intent_age + intent_today, Toast.LENGTH_SHORT).show()

        }

        //경로
        if(intent_gender == "남자")
            gender_number = "0"
        else if (intent_gender == "여자")
            gender_number = "2"
        PT = "a" + "_" + gender_number + "_" + intent_name + "_" + intent_age + "_" + intent_gender + "_" +  intent_today
        PT_I = intent_name + "_" + intent_age + "_" + intent_gender + "_" +  intent_today

    }



    private fun setOnBtnClickListener() {

        //0)녹음하기 리스너
        btn_add_act_go_voice.setOnClickListener {
            //바로 앨범을 열지 않고,
            //권한 허용을 확인 한 뒤 앨범을 열도록 하는 메소드를 호출합니다.
            RecordVoice()
        }


        //1)앨범 열기 버튼 리스너
        btn_add_act_show_album.setOnClickListener {
            //바로 앨범을 열지 않고,
            //권한 허용을 확인 한 뒤 앨범을 열도록 하는 메소드를 호출합니다.
            requestReadExternalStoragePermission()
        }

        //2)사진찍으러가기 리스너
        btn_add_act_go_capture.setOnClickListener {
            //게시물 쓰기 서버 통신 관련 메소드를 호출합니다!
            CaputureImage()
        }

        //3)상세정보 입력완료 리스너
        btn_add_act_complete.setOnClickListener {
            //게시물 쓰기 서버 통신 관련 메소드를 호출합니다!
            getAddInfoResponse()
        }

        //4)취소 리스너
        btn_add_act_cancel.setOnClickListener {
            //게시물 쓰기 서버 통신 관련 메소드를 호출합니다!
            finish()
        }

    }

    private fun CaputureImage(){
        val capture_intent = go_Addinfo_Image()
        startActivityForResult(capture_intent, 0)
    }

    private fun RecordVoice(){
        val record_intent = go_Addinfo_Voice()
        startActivityForResult(record_intent, 1)
    }

    fun go_Addinfo_Image() : Intent
    {
        val capture_intent = Intent(this, ImageCaptureActivity::class.java)

        capture_intent.putExtra("In_id", intent_id)
        capture_intent.putExtra("In_password", intent_password)
        capture_intent.putExtra("In_name", intent_name)
        capture_intent.putExtra("In_gender", intent_gender)
        capture_intent.putExtra("In_age", intent_age)
        capture_intent.putExtra("In_today", intent_today)

        return capture_intent
    }
    fun go_Addinfo_Voice() : Intent
    {
        val voice_intent = Intent(this, VoiceRecorderActivity::class.java)

        voice_intent.putExtra("In_id", intent_id)
        voice_intent.putExtra("In_password", intent_password)
        voice_intent.putExtra("In_name", intent_name)
        voice_intent.putExtra("In_gender", intent_gender)
        voice_intent.putExtra("In_age", intent_age)
        voice_intent.putExtra("In_today", intent_today)

        return voice_intent
    }





    //앨범을 여는 메소드입니다!
    //앨범에서 사진을 선택한 결과를 받기위해 startActivityForResult를 통해 앨범 엑티비티를 열어요!
    private fun showAlbum(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    //startActivityForResult를 통해 실행한 엑티비티에 대한 callback을 처리하는 메소드입니다!
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        //카메라 앨범에서 콜백 받은것.
        //REQUEST_CODE_SELECT_IMAGE를 통해 앨범에서 보낸 요청에 대한 Callback인지를 체크!!!
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            //앨범 사진 선택에 대한 Callback이 RESULT_OK인지 체크!!
            if (resultCode == Activity.RESULT_OK) {
                //data.data에는 앨범에서 선택한 사진의 Uri가 들어있습니다!! 그러니까 제대로 선택됐는지 null인지 아닌지를 체크!!!
                if (data != null) {
                    val selectedImageUri: Uri = data.data
                    //Uri를 getRealPathFromURI라는 메소드를 통해 절대 경로를 알아내고, 인스턴스 변수인 imageURI에 String으로 넣어줍니다!
                    imageURI = getRealPathFromURI(selectedImageUri)
                    //Glide를 통해 imageView에 우리가 선택한 이미지를 띄워 줍시다!(무엇을 선택했는지는 알아야 좋겠죠?!)
                    Glide.with(this@AddinformationActivity)
                        .load(selectedImageUri)
                        .thumbnail(0.1f)
                        .into(iv_write_act_choice_image)
                }
            }
        }

        //만약 캡쳐해서 온놈이라면
        if(resultCode == 1)
        {
            Glide.with(this@AddinformationActivity)
                .load(Environment.getExternalStorageDirectory().toString() + "/" + PT_I+ ".jpg")
                .thumbnail(0.1f)
                .into(iv_write_act_choice_image)
        }

        //녹음해온놈이라면
        if(resultCode == 2)
        {
           iv_write_act_voice.text = "음성 파일 : " + Environment.getExternalStorageDirectory().toString() + "/" + PT + "Long_first/second/third" + ".wav"
        }
    }

    //Uri에 대한 절대 경로를 리턴하는 메소드입니다! 굳이 코드를 해석하려고 하지말고,

    //앱잼때 코드를 복붙을 통해 재사용해주세요!!

    fun getRealPathFromURI(content : Uri) : String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader : CursorLoader = CursorLoader(this, content, proj, null, null, null)
        val cursor : Cursor = loader.loadInBackground()
        val column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_idx)
        cursor.close()
        return result
    }

    //이 메소드는 외부저장소(앨범과 같은)에 접근 관련해 권한 요청을 하는 로직을 메소드로 만든 것입니다!
    private fun requestReadExternalStoragePermission(){
        //첫번째 if문을 통해 과거에 이미 권한 메시지에 대한 OK를 했는지 아닌지에 대해 물어봅니다!
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                //사용자에게 권한을 왜 허용해야되는지에 메시지를 주기 위한 대한 로직을 추가하려면 이 블락에서 하면됩니다!!
                //하지만 우리는 그냥 비워놓습니다!! 딱히 줄말 없으면 비워놔도 무관해요!!! 굳이 뭐 안넣어도됩니다!
            } else {
                //아래 코드는 권한을 요청하는 메시지를 띄우는 기능을 합니다! 요청에 대한 결과는 callback으로 onRequestPermissionsResult 메소드에서 받습니다!!!
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), My_READ_STORAGE_REQUEST_CODE)
            }
        } else {
            //첫번째 if문의 else로써, 기존에 이미 권한 메시지를 통해 권한을 허용했다면 아래와 같은 곧바로 앨범을 여는 메소드를 호출해주면됩니다!!
            showAlbum()
        }
    }
    //외부저장소(앨범과 같은)에 접근 관련 요청에 대해 OK를 했는지 거부했는지를 callback으로 받는 메소드입니다!
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //onActivityResult와 같은 개념입니다. requestCode로 어떤 권한에 대한 callback인지를 체크합니다.
        if (requestCode == My_READ_STORAGE_REQUEST_CODE){
            //결과에 대해 허용을 눌렀는지 체크하는 조건문이구요!
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //이곳은 외부저장소 접근을 허용했을 때에 대한 로직을 쓰시면됩니다. 우리는 앨범을 여는 메소드를 호출해주면되겠죠?
                showAlbum()
            } else {
                //이곳은 외부저장소 접근 거부를 했을때에 대한 로직을 넣어주시면 됩니다.
                finish()
            }
        }
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


    //사진, 보이스 추가
    private fun getAddInfoResponse() {

          val token = SharedPreferenceController.getAuthorization(this)

        //만약 앨범에서 선택했으면
            // imageURI는 앨범에서 선택한 이미지에 대한 절대 경로가 담겨있는 인스턴스 변수입니다.
            if (imageURI!=null){
                val file : File = File(imageURI)
                val requestfile : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                data = MultipartBody.Part.createFormData("imageFile", file.name, requestfile)
            }
            else
            {
                data = make_MultiPartBody(
                    Environment.getExternalStorageDirectory().absolutePath + "/" + PT_I + ".jpg"
                    , "imageFile"
                )
            }
        mVoice1 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + PT +  "Long_first" + ".wav"
            , "voiceFile1"
        )
        mVoice2 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + PT + "Long_second" + ".wav"
            , "voiceFile2"
        )
        mVoice3 = make_MultiPartBody(Environment.getExternalStorageDirectory().absolutePath + "/" + PT +   "Long_third" + ".wav"
            , "voiceFile3"
        )


        val postAddInfoResponse = networkService.postAddInfoResponse(token, mVoice1,mVoice2,mVoice3, data)

        postAddInfoResponse.enqueue(object : Callback<PostAddInfoResponse> {
            override fun onFailure(call: Call<PostAddInfoResponse>, t: Throwable) {
                Log.e("TEST :: Add fail", t.toString())
            }
            override fun onResponse(call: Call<PostAddInfoResponse>, response: Response<PostAddInfoResponse>) {
                    if (response.isSuccessful) {
                        Log.i("TEST :: ",response.body()!!.message)
                        Log.i("TEST :: ",response.body()!!.status)
                        toast(response.body()!!.message)
                        //BoardActivity로 결과 보내기
                        setResult(Activity.RESULT_OK)
                        startActivity<BoardActivity>()
                        finish()
                    }
                }
            })
        }
    }
