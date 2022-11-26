package com.alidev.android.portfolio.data.manager

import com.alidev.android.portfolio.domain.entity.Effect
import com.alidev.android.portfolio.domain.entity.Effect.*
import com.alidev.android.portfolio.gl.filter.ColorFilter
import com.alidev.android.portfolio.gl.filter.Filter
import com.alidev.android.portfolio.gl.filter.GlitchFilter
import com.alidev.android.portfolio.gl.filter.PalaroidFilter
import java.util.*

interface EffectsManager {
    fun add(effect: Effect)
    fun remove(effect: Effect)
}

interface EffectsController {
    val haveToApplyEffects: Boolean
    fun onSurfaceChanged(width: Int, height: Int)
    fun onSurfaceCreated()
    fun applyFilters(baseTexture: IntArray)
}

class EffectsDataManager : EffectsManager, EffectsController {
    override val haveToApplyEffects: Boolean
        get() = applyEffects.isNotEmpty()

    private val effectsMap: Map<Effect, Filter> =
        mapOf(
            Palaroid to PalaroidFilter(),
            Color to ColorFilter(),
            Glitch to GlitchFilter(),
        )

    private val applyEffects = mutableSetOf<Filter>()

    override fun add(effect: Effect) {
        effectsMap[effect]?.let(applyEffects::add)
    }

    override fun remove(effect: Effect) {
        effectsMap[effect]?.let(applyEffects::remove)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        effectsMap.values.forEach { it.onSurfaceChanged(width, height) }
    }

    override fun onSurfaceCreated() {
        effectsMap.values.forEach { it.onSurfaceCreated() }
    }

    override fun applyFilters(baseTexture: IntArray) {
        if (haveToApplyEffects) {
            apply(baseTexture)
        }
    }

    private fun apply(baseId: IntArray) {
        var index = 0
        val list: LinkedList<IntArray> = LinkedList()
        for (filter in applyEffects) {
            index++
            filter.setTextureId(if (list.isEmpty()) baseId else list.last)
            filter.onDraw(index != applyEffects.size)
            list.addLast(filter.outputTextureId)
        }
    }
}