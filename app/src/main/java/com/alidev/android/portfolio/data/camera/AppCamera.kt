package com.alidev.android.portfolio.data.camera

import android.graphics.SurfaceTexture
import android.util.DisplayMetrics
import android.util.Size
import android.view.Surface
import android.view.View
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.alidev.android.portfolio.utils.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AppCamera {
    var texture: SurfaceTexture? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private var preview: Preview? = null
    private var camera: Camera? = null

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun openCamera(view: View, owner: LifecycleOwner) {
        val context = view.context
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { view.display.getRealMetrics(it) }
        Logger.d("@@@ Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        texture?.setDefaultBufferSize(view.height, view.width)

        val rotation = view.display.rotation
        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val resolution = Size(view.height, view.width)

            preview = Preview.Builder()
                .setTargetResolution(resolution)
                .setTargetRotation(rotation)
                .build()

            // Must unbind the use-cases before rebinding them
            cameraProvider.unbindAll()

            try {
                camera =
                    cameraProvider.bindToLifecycle(owner, cameraSelector, preview)

                preview?.setSurfaceProvider {
                    it.provideSurface(Surface(texture), cameraExecutor) {}
                }
            } catch (e: Exception) {
                Logger.e("[CAMERA] Binding failed", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun closeCamera() {
        cameraExecutor.shutdown()
    }
}