package com.alidev.android.portfolio

import android.app.Application
import com.alidev.android.portfolio.di.appModule
import com.alidev.android.portfolio.di.cameraModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)

            modules(
                listOf(
                    appModule(this@App),
                    cameraModule
                )
            )
        }
    }
}