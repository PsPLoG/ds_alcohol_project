package com.example.user.alcohol_measurement


import android.Manifest
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.imagecapture.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class ImageCaptureActivity : AppCompatActivity() {
    private var takePictureButton: Button? = null
    private var textureView: TextureView? = null
    private var cameraId: String? = null
    protected var cameraDevice: CameraDevice? = null
    protected lateinit var cameraCaptureSessions: CameraCaptureSession
     protected lateinit var captureRequestBuilder: CaptureRequest.Builder
    private var imageDimension: Size? = null
   private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    //인텐트 받아올놈들
    var intent_id: String? = null
    var intent_password : String? = null
    var intent_name : String? = null
    var intent_gender : String? = null
    var intent_age : String? = null
    var intent_today : String? = null

    var PT : String? = null


    internal var textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
              openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened")
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice!!.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imagecapture)

        //인텐트 값 받자
        if (intent.hasExtra("In_id") &&
            intent.hasExtra("In_password") &&
            intent.hasExtra("In_name") &&
            intent.hasExtra("In_gender") &&
            intent.hasExtra("In_age") &&
            intent.hasExtra("In_today") ) {

            intent_id = intent.getStringExtra("In_id")
            intent_password = intent.getStringExtra("In_password")
            intent_name = intent.getStringExtra("In_name")
            intent_gender = intent.getStringExtra("In_gender")
            intent_age = intent.getStringExtra("In_age")
            intent_today = intent.getStringExtra("In_today")


            // Toast.makeText(this, user_age + user_alchol + user_gender + user_name, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "온전한 정보가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        //경로
        PT = intent_name + "_" + intent_age + "_" + intent_gender + "_" +  intent_today



        textureView = findViewById(R.id.texture) as TextureView
        assert(textureView != null)
        textureView!!.surfaceTextureListener = textureListener
        takePictureButton = findViewById<View>(R.id.btn_takepicture) as Button
        assert(takePictureButton != null)
        takePictureButton!!.setOnClickListener { takePicture() }


        //버튼, 바로 텍스트 바꾸고 바로 보여줌
        val Next_button = findViewById(R.id.button_next2) as Button
        Next_button.visibility = View.VISIBLE
        Next_button.text = "종료"

        //다음으로
        Next_button.setOnClickListener {
            finish()
            /*val resultIntent = Intent(this@ImageCaptureActivity, AddinformationActivity::class.java) // Intent객체 생성방법
            resultIntent.putExtra("In_name", user_name)
            resultIntent.putExtra("In_age", user_age)
            resultIntent.putExtra("In_gender", user_gender)
            resultIntent.putExtra("In_alchol", user_alchol)
            resultIntent.putExtra("In_today", user_today)
            startActivity(resultIntent)*/
        }

    }

    protected fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    protected fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    protected fun takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null")
            return
        }
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
            var jpegSizes: Array<Size>? = null
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.getOutputSizes(
                    ImageFormat.JPEG
                )
            }
            var width = 640
            var height = 480
            if (jpegSizes != null && 0 < jpegSizes.size) {
                width = jpegSizes[0].width
                height = jpegSizes[0].height
            }
            val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            val outputSurfaces = ArrayList<Surface>(2)

            outputSurfaces.add(reader.surface)
            outputSurfaces.add(Surface(textureView!!.surfaceTexture))
            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(reader.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            // Orientation
            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))



//         val file = File(Environment.getExternalStorageDirectory().toString() + "/" + user_name + "_" + user_age + "_" + user_gender + "_" + user_alchol + "_" +
  //                  user_today + ".jpg")
            val file = File(Environment.getExternalStorageDirectory().toString() + "/" + PT+ ".jpg")


                    val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader) {
                    var image: Image? = null
                    try {
                        image = reader.acquireLatestImage()
                        val buffer = image!!.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer.get(bytes)
                        save(bytes)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        image?.close()
                    }
                }


                //여기서 저장. 근데 안됨;
                @Throws(IOException::class)
                private fun save(bytes: ByteArray) {
                    var output: OutputStream? = null
                    try {
                        output = FileOutputStream(file)
                        output.write(bytes)
                         applicationContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory().toString())))
                    } catch(e : Exception )
                    {
                        e.printStackTrace()
                    }finally {
                        output?.close()
                    }
                }
            }
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)
            val captureListener = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

            //캡쳐 성공후
            object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    setResult(1) //1이면 성공이라는 뜻이니까
                    Toast.makeText(this@ImageCaptureActivity, "Saved:$file", Toast.LENGTH_SHORT).show()
                    createCameraPreview()
                }
            }
            cameraDevice!!.createCaptureSession(outputSurfaces, @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }

                }

                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    protected fun createCameraPreview() {
        try {
            val texture = textureView!!.surfaceTexture!!
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice!!.createCaptureSession(Arrays.asList(surface), @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession
                    updatePreview()
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    Toast.makeText(this@ImageCaptureActivity, "Configuration change", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }


    //카메라 열기( 여기서 아이디, 카메라앞에껀지 뒤에껀지해서 넣네요 1이 셀카) + 권한없으면 획득
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openCamera() {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        Log.e(TAG, "is camera open")
        try {
            cameraId = 1.toString() // 셀카 카메라
            val characteristics = manager.getCameraCharacteristics(cameraId!!)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                ActivityCompat.requestPermissions(
                    this@ImageCaptureActivity,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CAMERA_PERMISSION
                )
                return
            }
            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        Log.e(TAG, "openCamera X")
    }



    protected fun updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return")
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(
                    this@ImageCaptureActivity,
                    "Sorry!!!, you can't use this app without granting permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        startBackgroundThread()
        if (textureView!!.isAvailable) {
            openCamera()
        } else {
            textureView!!.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        Log.e(TAG, "onPause")
        //closeCamera();
        stopBackgroundThread()
        super.onPause()
    }

    companion object {
        private val TAG = "AndroidCameraApi"
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 270)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180,90)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        private val REQUEST_CAMERA_PERMISSION = 200
    }


}
