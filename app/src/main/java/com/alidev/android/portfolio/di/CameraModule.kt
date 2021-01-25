package com.alidev.android.portfolio.di

import android.opengl.GLSurfaceView
import com.alidev.android.portfolio.camera.AppCamera
import com.alidev.android.portfolio.gl.render.CameraPreviewRender
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cameraModule = module {

    single(named("gLSurfaceView")) {
        GLSurfaceView(get()).apply {
            setEGLContextClientVersion(3)
        }
    }

    single(named("camera")) {
        val glView = get(named("gLSurfaceView")) as GLSurfaceView
        AppCamera().apply {
            CameraPreviewRender {
                texture = it.apply {
                    setOnFrameAvailableListener { glView.requestRender() }
                }
            }
                .apply {
                    setUseFront(true)
                    glView.setRenderer(this)
                }
        }
    }
}