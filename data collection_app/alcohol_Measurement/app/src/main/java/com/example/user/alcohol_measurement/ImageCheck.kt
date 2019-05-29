package com.example.user.alcohol_measurement

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


/**
 * Created by Administrator on 2017-08-07.
 */

class ImageCheck(private val context: Context) {

    private var dialogListener : MyDialogListener? = null

    var user_image : ImageView? = null
    var continue_Button : Button? = null
    var reMeasure_Button : Button? = null

    lateinit var bmImg: Bitmap


    fun setDialogListener(dialogListener: MyDialogListener)
    {
        this.dialogListener = dialogListener
    }


    fun downloadFile(fileUrl: String) {
        var myFileUrl: URL? = null
        try {
            myFileUrl = URL(fileUrl)
        } catch (e: MalformedURLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        try {
            val conn = myFileUrl!!.openConnection() as HttpURLConnection
            conn.setDoInput(true)
            conn.connect()
            val length = conn.getContentLength()
            val inputStream : InputStream = conn.getInputStream()

            bmImg = BitmapFactory.decodeStream(inputStream)
            user_image!!.setImageBitmap(bmImg)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    // 호출할 다이얼로그 함수를 정의한다.
    fun callFunction(image_url : String?) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        val dlg = Dialog(context)

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.imagecaputre_check)

        // 커스텀 다이얼로그를 노출한다.
        dlg.show()



        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        user_image = dlg.findViewById(R.id.img_check_captureimage) as ImageView
        continue_Button = dlg.findViewById(R.id.btn_check_continue) as Button
        reMeasure_Button = dlg.findViewById(R.id.btn_check_reMesure) as Button

        //파일가져오기
        if (image_url != null) {
            downloadFile(image_url)
        }

        continue_Button!!.setOnClickListener {
            Toast.makeText(context, "계속을 눌렀습니다.", Toast.LENGTH_SHORT).show()
            dialogListener!!.onContinueClicked("continue")
            // 커스텀 다이얼로그를 종료한다.
            dlg.dismiss()
        }
        reMeasure_Button!!.setOnClickListener {
            Toast.makeText(context, "재측정을 눌렀습니다.", Toast.LENGTH_SHORT).show()
            dialogListener!!.onContinueClicked("re")
            // 커스텀 다이얼로그를 종료한다.
            dlg.dismiss()
        }


    }
}