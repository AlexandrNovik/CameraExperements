package com.alidev.android.portfolio

import android.Manifest
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alidev.android.portfolio.camera.AppCamera
import com.alidev.android.portfolio.gl_2.render.CameraPreviewRender
import com.alidev.android.portfolio.gl_2.utils.GLUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CameraPreviewRender.OnSurfaceCreatedListener {
    private val camera = AppCamera()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GLUtil.init(this)
        glSurfaceView.setEGLContextClientVersion(3)
        val cameraPreviewRender = CameraPreviewRender(this)
        glSurfaceView.setRenderer(cameraPreviewRender)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 2222)
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }

    override fun onCreated(texture: SurfaceTexture) {
        Handler(Looper.getMainLooper()).post {
            camera.openCamera(glSurfaceView, texture, this)
        }
    }
}