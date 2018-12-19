package learningopencv.ebysofyan.learningopencv

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream


class FaceDetectionActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener {

    private var openCvCameraView: CameraBridgeViewBase? = null
    private var cascadeClassifier: CascadeClassifier? = null
    private var grayscaleImage: Mat? = null
    private var absoluteFaceSize: Double = 0.0

    private val mLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> initializeOpenCVDependencies()
                else -> super.onManagerConnected(status)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openCvCameraView = JavaCameraView(this, -1)
        setContentView(openCvCameraView)
        openCvCameraView?.setCvCameraViewListener(this)
    }

    private fun initializeOpenCVDependencies() {

        // Copy the resource into a temp file so OpenCV can load it
        val rawFile = resources.openRawResource(resources.getIdentifier("lbpcascade_frontalface", "raw", packageName))
        val cascadeDir = getDir("cascade", Context.MODE_PRIVATE)
        val mCascadeFile = File(cascadeDir, "lbpcascade_frontalface.xml")
        val os = FileOutputStream(mCascadeFile)

        val buffer = ByteArray(4096)
        var bytesRead: Int = rawFile.read(buffer)
        while (bytesRead != -1) {
            os.write(buffer, 0, bytesRead)

            bytesRead = rawFile.read(buffer)
        }
        rawFile.close()
        os.close()

        // Load the cascade classifier
        cascadeClassifier = CascadeClassifier(mCascadeFile.absolutePath)
        if (cascadeClassifier?.empty() == true) {
            Log.e("CascadeClassifier", "Failed to load cascade classifier")
        } else {
            Log.i("CascadeClassifier", "Loaded cascade classifier from " + mCascadeFile.absolutePath)
        }

        // And we are ready to go
        openCvCameraView?.enableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        grayscaleImage = Mat(height, width, CvType.CV_8UC4)

        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (height * 0.2)
    }

    override fun onCameraViewStopped() {
    }

    override fun onCameraFrame(inputFrame: Mat?): Mat? {
        // Create a grayscale image
        Imgproc.cvtColor(inputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB)

        val faces = MatOfRect()

        // Use the classifier to detect faces
        cascadeClassifier?.detectMultiScale(
            grayscaleImage, faces, 1.1, 2, 2,
            Size(absoluteFaceSize, absoluteFaceSize), Size()
        )

        // If there are any faces found, draw a rectangle around it
        val facesArray = faces.toArray()
        for (i in facesArray.indices)
            Imgproc.rectangle(inputFrame, facesArray[i].tl(), facesArray[i].br(), Scalar(0.0, 255.0, 0.0, 255.0), 3)

        return inputFrame
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
}