package com.example.user.alcohol_measurement

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.TextView


class Practice_Activity : AppCompatActivity() {


    //캔버스 핸들러
    internal var pStatus = 0
    private val handler = Handler()
    internal lateinit var tv: TextView
    internal lateinit var mProgress : ProgressBar
    internal lateinit var drawable: Drawable



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity3)


        //캔버스 프로게스바 그리기
        val res = resources
        drawable = res.getDrawable(R.drawable.circular2)
        mProgress = findViewById(R.id.circularProgressbar) as ProgressBar

        mProgress.progress = 100   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress

        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable

//        tv = findViewById(R.id.tv) as TextView

    }

    fun run_progress()
    {
        //초기화
        mProgress.progress = 0   // Main Progress
        mProgress.secondaryProgress = 100 // Secondary Progress
        mProgress.max = 100 // Maximum Progress
        mProgress.progressDrawable = drawable


        Thread(Runnable {
            // TODO Auto-generated method stub
            while (pStatus < 100) {
                pStatus += 1

                handler.post(Runnable {
                    // TODO Auto-generated method stub
                    mProgress.progress = pStatus
                    tv.text = pStatus.toString() + "%"
                })
                try {
                    // Sleep for 200 milliseconds.
                    // Just to display the progress slowly
                    Thread.sleep(240) //thread will take approx 3 seconds to finish
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }).start()

    }
}