package com.alidev.android.portfolio.di

import android.opengl.GLSurfaceView
import com.alidev.android.portfolio.data.camera.AppCamera
import com.alidev.android.portfolio.data.manager.EffectsController
import com.alidev.android.portfolio.data.manager.EffectsDataManager
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

    val effectsDataManager: BeanDefinition<EffectsDataManager> =
        single(named("effectsDataManager")) {
            EffectsDataManager()
        }

    val effectsManager: BeanDefinition<EffectsManager> = single(named("effectsManager")) {
        get(named("effectsDataManager")) as EffectsDataManager
    }

    val effectsController: BeanDefinition<EffectsController> = single(named("effectsController")) {
        get(named("effectsDataManager")) as EffectsDataManager
    }

    val effectsRepository: BeanDefinition<EffectsRepository> = single(named("effectsRepository")) {
        EffectsRepository.Impl()
    }

    single(named("camera")) {
        val glView = get(named("gLSurfaceView")) as GLSurfaceView
        val effectsController = get(named("effectsController")) as EffectsController
        AppCamera().apply {
            CameraPreviewRender(effectsController) { surfaceTexture ->
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