package com.alexandnovik.camera.di

import android.app.Application
import android.opengl.GLSurfaceView
import com.alexandnovik.camera.data.camera.AppCamera
import com.alexandnovik.camera.data.manager.EffectsController
import com.alexandnovik.camera.data.manager.EffectsDataManager
import com.alexandnovik.camera.data.manager.EffectsManager
import com.alexandnovik.camera.data.repository.EffectsRepository
import com.alexandnovik.camera.gl.render.CameraPreviewRender
import com.alexandnovik.camera.gl.utils.GLUtil
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.KoinDefinition
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cameraModule = fun(application: Application) = module {
    GLUtil.init(application)

    single(named("gLSurfaceView")) {
        GLSurfaceView(androidContext()).apply {
            setEGLContextClientVersion(3)
        }
    }

    val effectsDataManager: KoinDefinition<EffectsDataManager> = single(named("effectsDataManager")) {
            EffectsDataManager()
        }

    val effectsManager: KoinDefinition<EffectsManager> = single(named("effectsManager")) {
        get(named("effectsDataManager")) as EffectsDataManager
    }

    val effectsController: KoinDefinition<EffectsController> = single(named("effectsController")) {
        get(named("effectsDataManager")) as EffectsDataManager
    }

    val effectsRepository: KoinDefinition<EffectsRepository> = single(named("effectsRepository")) {
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
            }.apply {
                setUseFront(true)
                glView.setRenderer(this)
            }
        }
    }
}