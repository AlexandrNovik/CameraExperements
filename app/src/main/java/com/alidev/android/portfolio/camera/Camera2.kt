package com.alidev.android.portfolio.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.opengl.GLSurfaceView
import android.view.Surface
import android.view.View

class Camera2 {
    var session: CameraCaptureSession? = null
    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null
    private var surfaceTexture: SurfaceTexture? = null

    @SuppressLint("MissingPermission")
    fun openCamera(view: View, surfaceTexture: SurfaceTexture, context: Context) {
        if (cameraDevice != null) return

        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        cameraManager?.openCamera("0", object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                val surface = Surface(surfaceTexture)
                surfaceTexture?.setDefaultBufferSize(view.width, view.height)
                val req = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                req.addTarget(surface)
                surfaceTexture.setOnFrameAvailableListener {
                    if (view is GLSurfaceView) view.requestRender()
                }
                camera.createCaptureSession(
                    listOf(surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            req.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            req.set(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON
                            )
                            req.set(
                                CaptureRequest.CONTROL_AE_ANTIBANDING_MODE,
                                CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO
                            )
                            req.set(
                                CaptureRequest.CONTROL_AE_ANTIBANDING_MODE,
                                CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO
                            )
                            session.setRepeatingRequest(req.build(), null, null)
                            this@Camera2.session = session
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            error("onConfigure Failed")
                        }
                    },
                    null
                )
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
                error("camera open failed")
            }
        }, null)
    }

    fun closeCamera() {
        session?.close()
        session = null
        cameraDevice?.close()
        cameraDevice = null
        surfaceTexture = null
    }
}