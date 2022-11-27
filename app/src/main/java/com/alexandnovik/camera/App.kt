package com.alexandnovik.camera

import android.app.Application
import com.alexandnovik.camera.di.cameraModule
import com.alexandnovik.camera.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                viewModelModule,
                cameraModule(this@App),
            )
            androidContext(this@App)
        }
    }
}