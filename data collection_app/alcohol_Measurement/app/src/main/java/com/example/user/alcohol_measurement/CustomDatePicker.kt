package com.example.user.alcohol_measurement

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
 * Created by Administrator on 2017-08-07.
 */

class CustomDatePicker(private val context: Context) {

    // 호출할 다이얼로그 함수를 정의한다.
    fun callFunction() : String? {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        val dlg = Dialog(context)

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        //dlg.setContentView(R.id)

        // 커스텀 다이얼로그를 노출한다.
        dlg.show()
        /*okButton.setOnClickListener {
            Toast.makeText(context, "확인을 눌렀습니다.", Toast.LENGTH_SHORT).show()

            // 커스텀 다이얼로그를 종료한다.
            dlg.dismiss()
        }*/

        return null
    }
}