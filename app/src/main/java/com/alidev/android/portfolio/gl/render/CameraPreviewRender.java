package com.alidev.android.portfolio.gl.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.alidev.android.portfolio.gl.filter.CameraFilter;
import com.alidev.android.portfolio.gl.filter.ColorFilter;
import com.alidev.android.portfolio.gl.filter.GlitchFilter;
import com.alidev.android.portfolio.gl.filter.PalaroidFilter;
import com.alidev.android.portfolio.gl.utils.MatrixUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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

public class CameraPreviewRender implements GLSurfaceView.Renderer {
    public interface OnSurfaceCreatedListener {
        void onSurfaceCreated(SurfaceTexture texture);
    }

    boolean useFront = false;
    float[] matrix = new float[16];

    boolean takingPhoto = false;
    boolean recordingVideo = false;

    SurfaceTexture surfaceTexture;
    int[] cameraTexture = new int[1];

    CameraFilter cameraFilter;
    ColorFilter colorFilter;
    PalaroidFilter palaroidFilter;
    GlitchFilter glitchFilter;
    int width, height;

    private final OnSurfaceCreatedListener listener;

    public CameraPreviewRender(OnSurfaceCreatedListener listener) {
        this.listener = listener;
        cameraFilter = new CameraFilter();
        colorFilter = new ColorFilter();
        palaroidFilter = new PalaroidFilter();
        glitchFilter = new GlitchFilter();
    }

    public void setUseFront(boolean useFront) {
        if (this.useFront != useFront) {
            this.useFront = useFront;
            cameraFilter.setUseFront(useFront);
            matrix = MatrixUtil.flip(matrix, true, false);
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public boolean isTakingPhoto() {
        return takingPhoto;
    }

    public void setTakingPhoto(boolean takingPhoto) {
        this.takingPhoto = takingPhoto;
    }

    public boolean isRecordingVideo() {
        return recordingVideo;
    }

    public void setRecordingVideo(boolean recordingVideo) {
        this.recordingVideo = recordingVideo;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        createTexture();
        surfaceTexture = new SurfaceTexture(cameraTexture[0]);
        listener.onSurfaceCreated(surfaceTexture);
        cameraFilter.onSurfaceCreated();
        colorFilter.onSurfaceCreated();
        palaroidFilter.onSurfaceCreated();
        glitchFilter.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;

            cameraFilter.onSurfaceChanged(width, height);
            colorFilter.onSurfaceChanged(width, height);
            palaroidFilter.onSurfaceChanged(width, height);
            glitchFilter.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }

        cameraFilter.setTextureId(cameraTexture);
        cameraFilter.onDraw(true);

        colorFilter.setTextureId(cameraFilter.getOutputTextureId());
        colorFilter.onDraw(true);

        palaroidFilter.setTextureId(colorFilter.getOutputTextureId());
        palaroidFilter.onDraw(true);

        glitchFilter.setTextureId(palaroidFilter.getOutputTextureId());
        glitchFilter.onDraw(false);
    }

    private void createTexture() {
        glGenTextures(cameraTexture.length, cameraTexture, 0);
    }

}
