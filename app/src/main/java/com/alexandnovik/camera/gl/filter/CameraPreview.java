package com.alexandnovik.camera.gl.filter;

import com.alexandnovik.camera.gl.utils.CommonUtil;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameterf;

public class CameraPreview extends OesFilter {

    public static final float[] textureCoordCameraBack = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    public static final float[] textureCoordCameraFront = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f
    };

    public boolean useFront = true;

    public void setUseFront(boolean useFront) {
        if (this.useFront != useFront) {
            this.useFront = useFront;
            textureCoordBuffer = useFront ? CommonUtil.getFloatBuffer(textureCoordCameraFront)
                    : CommonUtil.getFloatBuffer(textureCoordCameraBack);
        }
    }

    public CameraPreview() {
        super();
    }

    @Override
    public void initBuffer() {
        vertexBuffer = CommonUtil.getFloatBuffer(vertex);
        textureCoordBuffer = useFront ? CommonUtil.getFloatBuffer(textureCoordCameraFront)
                : CommonUtil.getFloatBuffer(textureCoordCameraBack);
    }

}
