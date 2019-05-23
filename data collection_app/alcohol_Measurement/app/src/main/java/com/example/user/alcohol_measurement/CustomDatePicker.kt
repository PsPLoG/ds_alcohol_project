package com.example.user.alcohol_measurement

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import java.util.*

class CustomDatePicker(private val context: Context) {

    // 호출할 다이얼로그 함수를 정의한다.
    fun callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        val dlg = Dialog(context)

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.date_spinner)

        // 커스텀 다이얼로그를 노출한다.
        dlg.show()

        val textView = dlg.findViewById(R.id.datePicker_text) as TextView
        val datePicker = dlg.findViewById<DatePicker>(R.id.datePicker)

        val today = Calendar.getInstance()

        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
        {
            view, year, monthOfYear, dayOfMonth ->
            val month = monthOfYear + 1
            val msg = "Selected Date is $dayOfMonth/$month/$year"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

            if (textView != null) {
                textView.text = msg
            }
        }
            // 커스텀 다이얼로그를 종료한다.
            dlg.dismiss()
        }

    }



