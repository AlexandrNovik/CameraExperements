package com.alexandnovik.camera.gl.filter

import android.opengl.GLES20
import com.alexandnovik.camera.R
import com.alexandnovik.camera.gl.utils.GLUtil

internal class GlitchFilter : FrameBufferedFilter() {
    private var time = 0.0f
    private var iTime: Int = 0
    private val skipTimeout = 10
    private var skipped = 10

    override fun initProgram(): Int {
        return GLUtil.createAndLinkProgram(
            R.raw.texture_vertex_shader,
            R.raw.texture_fragment_glitch
        )
    }

    override fun initAttribLocations() {
        super.initAttribLocations()
        iTime = GLES20.glGetUniformLocation(program, "iTime")
    }

    override fun setExtend() {
        super.setExtend()
        if (skipTimeout == skipped) {
            skipped = 0
            if (time >= 100) time = 0f
            GLES20.glUniform1f(iTime, time++)
        }
        skipped++
    }
}