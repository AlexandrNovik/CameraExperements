package com.alidev.android.portfolio.di

import android.opengl.GLSurfaceView
import com.alidev.android.portfolio.data.camera.AppCamera
import com.alidev.android.portfolio.data.manager.EffectsManager
import com.alidev.android.portfolio.data.repository.EffectsRepository
import com.alidev.android.portfolio.gl.render.CameraPreviewRender
import org.koin.core.definition.BeanDefinition
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cameraModule = module {

    single(named("gLSurfaceView")) {
        GLSurfaceView(get()).apply {
            setEGLContextClientVersion(3)
        }
    }

    val effectsManager: BeanDefinition<EffectsManager> = single(named("effectsManager")) {
        EffectsManager.Impl()
    }

    val effectsRepository: BeanDefinition<EffectsRepository> = single(named("effectsRepository")) {
        EffectsRepository.Impl()
    }

    single(named("camera")) {
        val glView = get(named("gLSurfaceView")) as GLSurfaceView
        val effectsManager = get(named("effectsManager")) as EffectsManager
        AppCamera().apply {
            CameraPreviewRender(effectsManager) { surfaceTexture ->
                texture = surfaceTexture.apply {
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