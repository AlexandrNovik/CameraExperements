package com.alexandnovik.camera.di

import com.alexandnovik.camera.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(named("effectsRepository"))) }
}
