package com.example.user.alcohol_measurement


import android.content.Context
import com.example.user.alcohol_measurement.domain.Account

object SharedPreferenceController{
    //토큰을 저장하자
    private val USER_NAME = "MYKEY"
    private val myAuth = "myAuth"

    //id , password, name, gender, age 서버에 계속 요청하기는 좀그러니까 여기서 저장하자
    private val myAuthed_intent_id = "myAuthed_intent_id"
    private val myAuthed_intent_password = "myAuthed_intent_password"
    private val myAuthed_intent_name = "myAuthed_intent_name"
    private val myAuthed_intent_gender = "myAuthed_intent_gender"
    private val myAuthed_intent_age = "myAuthed_intent_age"
    private val myAuthed_intent_email = "myAuthed_intent_email"
    private val myAuthed_intent_phone = "myAuthed_intent_phone"




    fun setAuthored_intent(context: Context, id : String, pass : String, name : String, gender : String, age : String, email : String, phone : String){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE ) //현재 내 기기에서만 볼수 있는 데이터
        val editor = pref.edit()
        editor.putString(myAuthed_intent_id, id)
        editor.putString(myAuthed_intent_password, pass)
        editor.putString(myAuthed_intent_name, name)
        editor.putString(myAuthed_intent_gender, gender)
        editor.putString(myAuthed_intent_age, age)
        editor.putString(myAuthed_intent_email, email)
        editor.putString(myAuthed_intent_phone, phone)

        editor.commit()
    }

    fun getAuthored_intent(context: Context) : Account {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE ) //현재 내 기기에서만 볼수 있는 데이터
        var account: Account

        var id : String = pref.getString(myAuthed_intent_id, "")
        var password : String = pref.getString(myAuthed_intent_password, "")
        var name : String = pref.getString(myAuthed_intent_name, "")
        var gender : String = pref.getString(myAuthed_intent_gender, "")
        var age : String = pref.getString(myAuthed_intent_age, "")
        var email : String = pref.getString(myAuthed_intent_email, "")
        var phone : String = pref.getString(myAuthed_intent_phone, "")

        account = Account(id,password,name,gender,age,email, phone)

        return account

    }




    fun setAuthorization(context: Context, authorization : String){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE ) //현재 내 기기에서만 볼수 있는 데이터
        val editor = pref.edit()
        editor.putString(myAuth, authorization)
        editor.commit()
    }

    fun getAuthorization(context: Context) : String {
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE ) //현재 내 기기에서만 볼수 있는 데이터
        return pref.getString(myAuth, "") }

    //데이터 전부 삭제
    fun clearSPC(context: Context){
        val pref = context.getSharedPreferences(USER_NAME, Context.MODE_PRIVATE ) //현재 내 기기에서만 볼수 있는 데이터
        val editor = pref.edit()
        editor.clear()
        editor.commit()
    }
}

