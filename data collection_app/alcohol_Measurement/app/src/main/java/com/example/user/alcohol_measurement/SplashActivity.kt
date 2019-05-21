package com.example.user.alcohol_measurement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    var SPLASH_TIME : Long = 3000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        Run.after(SPLASH_TIME) {startActivity(Intent(this, MainActivity::class.java))}
        Run.after(SPLASH_TIME) { finish() }
        }
    }


    class Run {
        companion object {
            fun after(delay: Long, process: () -> Unit) {
                Handler().postDelayed({
                    process()
                }, delay)
            }
        }
    }
