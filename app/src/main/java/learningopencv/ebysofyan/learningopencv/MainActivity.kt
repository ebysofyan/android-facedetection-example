package learningopencv.ebysofyan.learningopencv

import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener {

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.e(BaseLoaderCallback::class.java.simpleName, "OpenCV loaded successfully")
                    camera_view.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    private val CAMERA_OK = 10

    private var mRgba: Mat? = null
    private var mRgbaF: Mat? = null
    private var mRgbaT: Mat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        camera_view.visibility = SurfaceView.VISIBLE
        camera_view.setCvCameraViewListener(this)

        requestPermissions()

        fab_camera.setOnClickListener {
            Toast.makeText(this, camera_view.matrix.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRgba = Mat(height, width, CvType.CV_8UC4)
        mRgbaF = Mat(height, width, CvType.CV_8UC4)
        mRgbaT = Mat(width, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        mRgba?.release()
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(
                this,
                "Internal OpenCV library not found. Using OpenCV Manager for initialization",
                Toast.LENGTH_LONG
            ).show()
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback)
        } else {
            Toast.makeText(this, "OpenCV library found inside package. Using it!", Toast.LENGTH_LONG).show()
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onPause() {
        super.onPause()
        camera_view.disableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera_view.disableView()
    }

    override fun onCameraFrame(inputFrame: Mat?): Mat? {
        // Rotate mRgba 90 degrees
        return inputFrame
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT > 22) {
            try {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_OK)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
