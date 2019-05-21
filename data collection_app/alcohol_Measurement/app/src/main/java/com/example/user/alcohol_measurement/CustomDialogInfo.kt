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

class CustomDialogInfo(private val context: Context) {

    // 호출할 다이얼로그 함수를 정의한다.
    fun callFunction(name_input : String?, id_input: String?, email_input: String?, gender_input: String?, phone_input: String?) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        val dlg = Dialog(context)

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog_myinfo)

        // 커스텀 다이얼로그를 노출한다.
        dlg.show()

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        val name =  dlg.findViewById(R.id.txt_info_name) as TextView
        val id = dlg.findViewById(R.id.txt_info_id) as TextView
        val email = dlg.findViewById(R.id.txt_info_email) as TextView
        val gender = dlg.findViewById(R.id.txt_info_gender) as TextView
        val phone = dlg.findViewById(R.id.txt_info_phone) as TextView
        val okButton = dlg.findViewById(R.id.btn_info_okay) as Button

        //텍스트 설정
        name.text = name_input
        id.text = id_input
        email.text = email_input
        gender.text = gender_input
        phone.text = phone_input

        okButton.setOnClickListener {
            Toast.makeText(context, "확인을 눌렀습니다.", Toast.LENGTH_SHORT).show()

            // 커스텀 다이얼로그를 종료한다.
            dlg.dismiss()
        }

    }
}