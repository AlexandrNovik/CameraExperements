package com.alidev.android.portfolio

import android.Manifest
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alidev.android.portfolio.camera.AppCamera
import com.alidev.android.portfolio.gl.render.CameraPreviewRender
import kotlinx.android.synthetic.main.activity_main.*

class CameraActivity : AppCompatActivity(), CameraPreviewRender.OnSurfaceCreatedListener {
    private val camera = AppCamera()
    private val cameraPreviewRender = CameraPreviewRender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glSurfaceView.setEGLContextClientVersion(3)
        glSurfaceView.setRenderer(cameraPreviewRender)

        // TODO: deal with permissions correctly
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 2222)
    }

    override fun onSurfaceCreated(texture: SurfaceTexture) {
        Handler(Looper.getMainLooper()).post {
            camera.openCamera(glSurfaceView, texture, this)
            cameraPreviewRender.setUseFront(true)
            texture.setOnFrameAvailableListener {
                glSurfaceView.requestRender()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.closeCamera()
    }
}