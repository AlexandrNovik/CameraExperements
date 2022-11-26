package com.alidev.android.portfolio

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alidev.android.portfolio.data.camera.AppCamera
import com.alidev.android.portfolio.data.manager.EffectsManager
import com.alidev.android.portfolio.domain.entity.Effect
import kotlinx.android.synthetic.main.activity_camera.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named

class CameraActivity : AppCompatActivity() {
    companion object {
        private const val CAMERA_PERMISSION = 10
    }

    private lateinit var camera: AppCamera
    private lateinit var effectsManager: EffectsManager
    private lateinit var gLSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        gLSurfaceView = getKoin().get(named("gLSurfaceView"))
        camera = getKoin().get(named("camera"))
        effectsManager = getKoin().get(named("effectsManager"))
        addGlSurfaceView()
        requestPermissions()
        fabPlus.setOnClickListener {
            effectsManager.add(Effect.Glitch)
            effectsManager.add(Effect.Color)
            effectsManager.add(Effect.Palaroid)
        }
        fabMinus.setOnClickListener {
            effectsManager.remove(Effect.Glitch)
            effectsManager.remove(Effect.Color)
            effectsManager.remove(Effect.Palaroid)
        }
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
        removeGlSurfaceView()
    }

    private fun addGlSurfaceView() {
        gLSurfaceView.parent?.let { (it as ViewGroup).removeView(gLSurfaceView) }
        mainCameraContainer.addView(gLSurfaceView, 0,
            ViewGroup.LayoutParams(mainCameraContainer.layoutParams)
                .apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                })
    }

    private fun removeGlSurfaceView() {
        gLSurfaceView.parent?.let { (it as ViewGroup).removeView(gLSurfaceView) }
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