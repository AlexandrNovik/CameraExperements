package com.alidev.android.portfolio

import android.app.Application
import com.alidev.android.portfolio.gl.utils.GLUtil

class App : Application() {
    override fun onCreate() {
        GLUtil.init(applicationContext)
    }
}