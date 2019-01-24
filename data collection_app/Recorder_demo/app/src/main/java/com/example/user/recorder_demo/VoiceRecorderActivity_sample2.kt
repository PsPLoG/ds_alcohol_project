/*package com.example.user.recorder_demo

import android.app.Activity
import android.hardware.Camera
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.VideoView
import kotlinx.android.synthetic.main.videorecorder_sample2.*

class VoiceRecorderActivity_sample2 : Activity(), SurfaceHolder.Callback {

    static final int REQUEST_VIDEO_CAPTURE = 1;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videorec.setOnClickListener {

        }

        public void dispatchTakeVideoIntent(){
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if(takeVideoIntent.resolveActivity(getPackageManager()) != null)
            {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }

        @Override
        protected void onAcitivityResult(int requestCode, int resultCode, Intent data)
        {
            if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK)
            {
                Uri videoUri = intent.getData();
                VideoView.setVideoUri(videoUri);
            }
        }


        }


}*/