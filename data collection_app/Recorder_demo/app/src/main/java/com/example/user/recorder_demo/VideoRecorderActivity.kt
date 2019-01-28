package com.example.user.recorder_demo

import java.io.IOException

import android.app.ActionBar
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Camera
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


//control.xml 이랑 이놈 액티비티가 필요
class VideoRecorderActivity : Activity(), SurfaceHolder.Callback {

    internal var camera: Camera? = null
    internal lateinit var surfaceView: SurfaceView
    internal lateinit var surfaceHolder: SurfaceHolder
    internal var previewing = false
    internal var controlInflater: LayoutInflater? = null

    //인텐트 받아올놈들
    var user_name : String? = null
    var user_age : String? = null
    var user_gender : String? = null
    var user_alchol : String? = null

    //현재시간
    var formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    var cal = Calendar.getInstance()
    var today: String? = formatter.format(cal.getTime())


    //녹화용
    internal var player: MediaPlayer? = null
    internal var recorder: MediaRecorder? = null


    //사각형 위치
    private val RectLeft: Float = 0.toFloat()
    private val RectTop: Float = 0.toFloat()
    private val RectRight: Float = 0.toFloat()
    private val RectBottom: Float = 0.toFloat()
    internal var deviceHeight: Int = 0
    internal var deviceWidth: Int = 0

    internal var stringPath = "/sdcard/samplevideo.3gp"

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.videorecorder)

        //인텐트 값 받자
        if (intent.hasExtra("In_name") &&
            intent.hasExtra("In_age") &&
            intent.hasExtra("In_gender") &&
            intent.hasExtra("In_alchol") ) {

            user_name = intent.getStringExtra("In_name")
            user_age = intent.getStringExtra("In_age")
            user_gender = intent.getStringExtra("In_gender")
            user_alchol = intent.getStringExtra("In_alchol")

        } else {
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        //녹화용
        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) {
        } else {
            EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().absolutePath
        }


        //서페이스
        window.setFormat(PixelFormat.UNKNOWN)
        surfaceView = findViewById(R.id.surfaceview) as SurfaceView
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this as SurfaceHolder.Callback)
        // 캡쳐방지
        // surfaceView.setSecure(true);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT)
        surfaceView.setZOrderMediaOverlay(true)
        /*
        deviceWidth=getScreenWidth();
        deviceHeight=getScreenHeight();
*/


        /*
        //투명 서페이스
        transparentView = (SurfaceView)findViewById(R.id.transparentView2);
        holderTransparent = transparentView.getHolder();
        holderTransparent.addCallback((SurfaceHolder.Callback) this);
        holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
        transparentView.setZOrderMediaOverlay(true);
        //getting the device heigth and width
        deviceWidth=getScreenWidth();
        deviceHeight=getScreenHeight();
*/
        //surface에 버튼 추가 (Control.xml 레이아웃 추가)
        controlInflater = LayoutInflater.from(baseContext)
        val viewControl = controlInflater!!.inflate(R.layout.control, null)
        val layoutParamsControl = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        val layoutParamsControl2 = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        this.addContentView(viewControl, layoutParamsControl)


        //시작과 동시에 애니메이션
        val Start_button = findViewById(R.id.button_circle_animation) as Button
        val Stop_Next_button = findViewById(R.id.button_next) as Button
        val Next_button = findViewById(R.id.button_next2) as Button

        Start_button.setOnClickListener {
            //애니메이션 부분
            val icm = findViewById(R.id.image_circle_moving) as ImageView
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.moving)
            icm.startAnimation(anim)
            Start_button.visibility = View.INVISIBLE
            Stop_Next_button.visibility = View.VISIBLE

            //촬영시작부분
            try {
                if (recorder == null) {
                    recorder = MediaRecorder()
                }




                recorder!!.setCamera(camera)
                camera!!.unlock();
                recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
                recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                recorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)

                filename = createFilename()
                recorder!!.setOutputFile(filename)

                recorder!!.setPreviewDisplay(surfaceHolder.surface)
                recorder!!.prepare()
                recorder!!.start()

            } catch (ex: Exception) {
                ex.printStackTrace()
                recorder!!.release()
                recorder = null
            }
        }

        Stop_Next_button.setOnClickListener(View.OnClickListener {
            if (recorder == null)
                return@OnClickListener

            recorder!!.stop()
            recorder!!.reset()
            recorder!!.release()
            recorder = null

            //버튼
            Stop_Next_button.visibility = View.INVISIBLE
            Next_button.visibility = View.VISIBLE

            val values = ContentValues(10)

            values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo")
            values.put(MediaStore.Audio.Media.ALBUM, "Video Album")
            values.put(MediaStore.Audio.Media.ARTIST, "Mike")
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Video")
            values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Audio.Media.DATA, filename)

            val videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            if (videoUri == null) {
                Log.d("SampleVideoRecorder", "Video insert failed.")
                return@OnClickListener
            }

            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri))

        })


        //다음으로
        Next_button.setOnClickListener {
            val resultIntent = Intent(this@VideoRecorderActivity, VoiceRecorderActivity::class.java) // Intent객체 생성방법
            resultIntent.putExtra("In_name", user_name)
            resultIntent.putExtra("In_age", user_age)
            resultIntent.putExtra("In_gender", user_gender)
            resultIntent.putExtra("In_alchol", user_alchol)
            startActivity(resultIntent)
        }
    }

    private fun createFilename(): String {
        return Environment.getExternalStorageDirectory().absolutePath + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +
            today + ".mp4"
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
        if (previewing) {
            camera!!.stopPreview()
            previewing = false
        }

        if (camera != null) {
            try {

                camera!!.setDisplayOrientation(90)
                camera!!.setPreviewDisplay(surfaceHolder)
                camera!!.startPreview()
                previewing = true

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera = Camera.open(findFrontCamera())
        camera!!.setDisplayOrientation(90)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera!!.stopPreview()
        camera!!.release() //for release a camera
        camera = null
        previewing = false
    }

    fun findFrontCamera() : Int
    {
        var cameraId = -1
        var numCamera = Camera.getNumberOfCameras()

        for (i in 0..numCamera) {
            var cmInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cmInfo)
            if (cmInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId
    }


    companion object {
        private var EXTERNAL_STORAGE_PATH: String? = ""
        private val RECORDED_FILE = "video_recorded"
        private var fileIndex = 0
        private var filename = ""
    }


}