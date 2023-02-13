package com.alexandnovik.camera.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandnovik.camera.data.repository.EffectsRepository
import com.alexandnovik.camera.domain.entity.Effect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    private val effectsRepository: EffectsRepository
) : ViewModel() {
    fun add() {
        viewModelScope.launch {
            effectsRepository.addEffect(Effect.Glitch)
            delay(2000)
            effectsRepository.addEffect(Effect.Color)
            delay(2000)
            effectsRepository.addEffect(Effect.Palaroid)
            delay(2000)
            effectsRepository.removeEffect(Effect.Color)
            delay(2000)
            effectsRepository.removeEffect(Effect.Palaroid)
            delay(2000)
            effectsRepository.removeEffect(Effect.Glitch)
        }
    }
}