package com.alexandnovik.camera.gl.filter

import android.opengl.GLES20
import com.alexandnovik.camera.R
import com.alexandnovik.camera.gl.utils.GLUtil

internal class PalaroidFilter : FrameBufferedFilter() {
    private var iTime: Int = 0
    private var time = 0.0f

    override fun initProgram(): Int {
        return GLUtil.createAndLinkProgram(
            R.raw.texture_vertex_shader,
            R.raw.texture_fragment_palaroid
        )
    }

    override fun initAttribLocations() {
        super.initAttribLocations()
        iTime = GLES20.glGetUniformLocation(program, "iTime")
    }

    override fun setExtend() {
        super.setExtend()
        GLES20.glUniform1f(iTime, time)
        time += 0.1f
    }
}