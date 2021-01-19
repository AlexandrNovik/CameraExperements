package com.alidev.android.portfolio.gl.filter

import android.opengl.GLES20
import com.alidev.android.portfolio.R
import com.alidev.android.portfolio.gl.utils.GLUtil

class ColorFilter : BaseFilter() {
    private var hColorFlag = 0
    private var hTextureLUT = 0
    private var LUTTextureId = 0
    override fun onSurfaceCreated() {
        super.onSurfaceCreated()
        LUTTextureId = GLUtil.loadTextureFromRes(R.drawable.amatorka)
    }

    override fun initProgram(): Int {
        return GLUtil.createAndLinkProgram(
            R.raw.texture_vertex_shader,
            R.raw.texture_color_fragtment_shader
        )
    }

    override fun initAttribLocations() {
        super.initAttribLocations()
        hColorFlag = GLES20.glGetUniformLocation(program, UNIFORM_COLOR_FLAG)
        hTextureLUT = GLES20.glGetUniformLocation(program, UNIFORM_TEXTURE_LUT)
    }

    override fun setExtend() {
        super.setExtend()
        GLES20.glUniform1i(hColorFlag, COLOR_FLAG)
    }

    override fun bindTexture() {
        super.bindTexture()
        if (COLOR_FLAG == COLOR_FLAG_USE_LUT) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, LUTTextureId)
            GLES20.glUniform1i(hTextureLUT, 1)
        }
    }

    companion object {
        const val UNIFORM_COLOR_FLAG = "colorFlag"
        const val UNIFORM_TEXTURE_LUT = "textureLUT"
        var COLOR_FLAG = 0
        var COLOR_FLAG_USE_LUT = 6
    }
}