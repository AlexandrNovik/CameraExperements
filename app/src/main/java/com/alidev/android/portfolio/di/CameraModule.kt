package com.alidev.android.portfolio.di

import android.opengl.GLSurfaceView
import com.alidev.android.portfolio.camera.AppCamera
import com.alidev.android.portfolio.gl.render.CameraPreviewRender
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cameraModule = module {

    single(named("mainGLSurfaceView")) {
        GLSurfaceView(get()).apply {
            setEGLContextClientVersion(3)
        }
    }

    single(named("mainCamera")) {
        val camera = AppCamera()
        val glView = get(named("mainGLSurfaceView")) as GLSurfaceView
        CameraPreviewRender {
            it.setOnFrameAvailableListener {
                glView.requestRender()
            }
            camera.texture = it

        }
            .apply {
                setUseFront(true)
                glView.setRenderer(this)
            }
        camera
    }

}