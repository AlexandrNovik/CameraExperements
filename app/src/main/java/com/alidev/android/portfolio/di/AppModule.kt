package com.alidev.android.portfolio.di

import android.app.Application
import com.alidev.android.portfolio.gl.utils.GLUtil
import org.koin.dsl.module

val appModule = fun(application: Application) = module {
    GLUtil.init(application)
}