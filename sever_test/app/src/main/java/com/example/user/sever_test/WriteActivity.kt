package com.example.user.sever_test

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import com.example.user.sever_test.network.ApplicationController
import com.example.user.sever_test.network.NetworkService
import kotlinx.android.synthetic.main.activity_write.*
import okhttp3.MultipartBody

class WriteActivity : AppCompatActivity() {
    val REQUEST_CODE_SELECT_IMAGE: Int = 1004
    private var mImage: MultipartBody.Part? = null
    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (R.layout.activity_write)
        setOnBtnClickListener()
    }

    private fun setOnBtnClickListener() {
        btn_write_act_show_album.setOnClickListener {
            //앨범 여는 로직
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
        btn_write_act_complete.setOnClickListener {
            //getWriteBoardRespon()
        }
    }


}