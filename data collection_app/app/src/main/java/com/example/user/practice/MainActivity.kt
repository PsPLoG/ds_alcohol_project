package com.example.user.practice

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //오류인지 봅시다
    private var TAG = "Error_Check"

    //권한 얻기위한 변수
    private var permissionToRecordAccepted = false
    private var permissionToWriteAccepted = false
    private var permissionToCameraAccepted = false
    private var permissionToReadAccepted = false
    private var permissionToCamera2Accepted = false
    private var permissionToMountAccepted = false
   // private var permissionToHardwareCameraAccepted = false



    private val permissions = arrayOf(
        "android.permission.RECORD_AUDIO",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.CAMERA",
        "android.permission.READ_EXTERNAL_STORAGE"
        //"android.hardware.camera2" ,
        //"android.permission.MOUNT_UNMOUNT"
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //처음에 권한얻어오려고 확인
        val requestCode = 200
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }


        ///초기화, 설정
        val spinner_age_String = arrayOf("20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41"
            ,"42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67"
            ,"68","69","70")
        val spinner_gender_String = arrayOf("남자", "여자")
        val spinner_alchol_String = arrayOf("정상" ,"취기_하" , "취기_중", "취기_상")





        //배열을 스피너에 연결
        spinner_age.adapter =  ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,spinner_age_String)
        spinner_gender.adapter =  ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,spinner_gender_String)
        spinner_alchol.adapter =  ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,spinner_alchol_String)

        /////클릭시
        spinner_age.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(this@MainActivity, spinner_age_String[position], Toast.LENGTH_LONG)
            }
        }
        spinner_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(this@MainActivity, spinner_gender_String[position], Toast.LENGTH_LONG)
            }
        }

        spinner_alchol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Toast.makeText(this@MainActivity, spinner_alchol_String[position], Toast.LENGTH_LONG)
            }
        }
        /////




        ///버튼 클릭시 넘겨주는 파트, 동시에 값도 넘겨줌,  이름 입력이 그대로 이름 입력이면 이름입력해달라고할거임
        input_information_button.setOnClickListener {
            if(editText_name.text.toString() == "이름입력")
            {
                Toast.makeText(this@MainActivity, "이름을 입력해주세요.", Toast.LENGTH_LONG).show()
            }
            else {
                val resultIntent = Intent(this@MainActivity, ImageCaptureActivity::class.java) // Intent객체 생성방법

                resultIntent.putExtra("In_name", editText_name.text.toString())
                resultIntent.putExtra("In_age", spinner_age.selectedItem.toString())
                resultIntent.putExtra("In_gender", spinner_gender.selectedItem.toString())
                resultIntent.putExtra("In_alchol", spinner_alchol.selectedItem.toString())
                startActivity(resultIntent)
            }
        }
    }

    //권한 획득
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            200 -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                permissionToCameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                permissionToReadAccepted  = grantResults[3] == PackageManager.PERMISSION_GRANTED
               // permissionToCamera2Accepted = grantResults[4] == PackageManager.PERMISSION_GRANTED
               // permissionToMountAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED
                //permissionToHardwareCameraAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED

            }
        }


        //권한 획득 여부 -> 로그로 확인
        if (!permissionToRecordAccepted)
        {
            Log.i(TAG, "리코드 허가 에러")
            super@MainActivity.finish()
        }
        if (!permissionToWriteAccepted)
        {
            Log.i(TAG, "쓰기 허가 에러")
            super@MainActivity.finish()
        }
        if (!permissionToCameraAccepted)
        {
            Log.i(TAG, "카메라 허가 에러")
            super@MainActivity.finish()
        }
        if (!permissionToReadAccepted )
        {
            Log.i(TAG, "읽기 허가 에러")
            super@MainActivity.finish()
        }
        /*if (!permissionToCamera2Accepted)
        {
            Log.i(TAG, "카메라2 허가 에러")
            super@MainActivity.finish()
        }*/
       /* if (!permissionToMountAccepted)
        {
            Log.i(TAG, "마운트 허가 에러")
            super@MainActivity.finish()
        }*/
        //if (!permissionToHardwareCameraAccepted) super@VoiceRecorderActivity_sample.finish()

    }


}
