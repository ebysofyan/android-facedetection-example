package learningopencv.ebysofyan.learningopencv

import android.app.Application
import android.util.Log
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.OpenCVLoader.initAsync

class App : Application() {
    companion object {
        const val TAG = "StarterApplication"
    }

    override fun onCreate() {
        super.onCreate()
        initOpenCV()
    }

    private fun initOpenCV() {
        val wasEngineInitialized = OpenCVLoader.initDebug()
        if (wasEngineInitialized) {
            Log.e(TAG, "The OpenCV was successfully initialized in debug mode using .so libs.")
        } else {
            initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, object : LoaderCallbackInterface {
                override fun onManagerConnected(status: Int) {
                    when (status) {
                        LoaderCallbackInterface.SUCCESS -> Log.e(TAG, "OpenCV successfully started.")
                        LoaderCallbackInterface.INIT_FAILED -> Log.e(TAG, "Failed to start OpenCV.")
                        LoaderCallbackInterface.MARKET_ERROR -> Log.e(
                            TAG,
                            "Google Play Store could not be invoked. Please check if you have the Google Play Store app installed and try again."
                        )
                        LoaderCallbackInterface.INSTALL_CANCELED -> Log.e(
                            TAG,
                            "OpenCV installation has been cancelled by the user."
                        )
                        LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION -> Log.e(
                            TAG,
                            "This version of OpenCV Manager is incompatible. Possibly, a service update is required."
                        )
                    }
                }

                override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface?) {
                    Log.e(TAG, "OpenCV Manager successfully installed from Google Play.")
                }
            })
        }
    }
}