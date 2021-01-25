package com.alidev.android.portfolio

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alidev.android.portfolio.camera.AppCamera
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named

class CameraActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION = 10
    }

    private lateinit var camera: AppCamera
    private lateinit var mainGLSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainGLSurfaceView = getKoin().get(named("mainGLSurfaceView"))
        camera = getKoin().get(named("mainCamera"))
        addTextureView()
        requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != CAMERA_PERMISSION) {
            throw IllegalStateException("Unknown permission request = $requestCode")
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tryOpenCamera()
        } else {
            // TODO: show ui for no permission
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
        removeTexture()
    }

    private fun addTextureView() {
        mainGLSurfaceView.parent?.let { (it as ViewGroup).removeView(mainGLSurfaceView) }
        mainCameraContainer.addView(mainGLSurfaceView, 0,
            ViewGroup.LayoutParams(mainCameraContainer.layoutParams)
                .apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                })
    }

    private fun removeTexture() {
        mainGLSurfaceView.parent?.let { (it as ViewGroup).removeView(mainGLSurfaceView) }
        mainCameraContainer.removeAllViews()
    }

    private fun requestPermissions() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
    }

    private fun tryOpenCamera() {
        Handler(Looper.getMainLooper()).post {
            camera.openCamera(mainCameraContainer, this)
        }
    }
}