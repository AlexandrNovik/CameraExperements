package com.alexandnovik.camera.gl.filter;

import android.opengl.GLES11Ext;

import com.alexandnovik.camera.R;
import com.alexandnovik.camera.gl.utils.GLUtil;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;

public class OesFilter extends FrameBufferedFilter {

    public OesFilter() {
        super();
    }

    @Override
    public int initProgram() {
        return GLUtil.createAndLinkProgram(R.raw.texture_vertex_shader, R.raw.texture_oes_fragtment_shader);
    }

    @Override
    public void bindTexture() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId()[0]);
        glUniform1i(hTexture, 0);
    }
}
