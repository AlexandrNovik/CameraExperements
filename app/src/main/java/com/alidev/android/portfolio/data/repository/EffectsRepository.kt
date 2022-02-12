package com.alidev.android.portfolio.data.repository

import com.alidev.android.portfolio.data.manager.EffectsManager
import com.alidev.android.portfolio.domain.entity.Effect
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin

interface EffectsRepository {
    fun addEffect(effect: Effect)
    fun removeEffect(effect: Effect)
    class Impl(
        private val effectsManager: EffectsManager = getKoin().get(named("effectsManager"))
    ) : EffectsRepository {
        override fun addEffect(effect: Effect) = effectsManager.add(effect)

        override fun removeEffect(effect: Effect) = effectsManager.remove(effect)
    }
}