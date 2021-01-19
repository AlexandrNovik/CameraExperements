package com.alidev.android.portfolio.gl.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.alidev.android.portfolio.gl.filter.CameraFilter;
import com.alidev.android.portfolio.gl.filter.ColorFilter;

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
        void onCreated(SurfaceTexture texture);
    }

    boolean useFront = false;
    float[] matrix = new float[16];

    boolean takingPhoto = false;
    boolean recordingVideo = false;

    SurfaceTexture surfaceTexture;
    int[] cameraTexture = new int[1];

    CameraFilter cameraFilter;
    ColorFilter colorFilter;
    int width, height;

    int[] exportFrame = new int[1];
    int[] exportTexture = new int[1];

    private final OnSurfaceCreatedListener listener;

    public CameraPreviewRender(OnSurfaceCreatedListener listener) {
        this.listener = listener;
        cameraFilter = new CameraFilter();
        colorFilter = new ColorFilter();
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
        listener.onCreated(surfaceTexture);
        cameraFilter.onSurfaceCreated();
        colorFilter.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;

            cameraFilter.onSurfaceChanged(width, height);
            colorFilter.onSurfaceChanged(width, height);

            delFrameBufferAndTexture();
            genFrameBufferAndTexture();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }

        cameraFilter.setTextureId(cameraTexture);
        cameraFilter.onDraw();
        colorFilter.setTextureId(cameraFilter.getOutputTextureId());
        colorFilter.onDraw();
    }

    private void createTexture() {
        glGenTextures(cameraTexture.length, cameraTexture, 0);
    }

    public void delFrameBufferAndTexture() {
        glDeleteFramebuffers(exportFrame.length, exportFrame, 0);
        glDeleteTextures(exportTexture.length, exportTexture, 0);
    }

    public void genFrameBufferAndTexture() {
        glGenFramebuffers(exportFrame.length, exportFrame, 0);

        glGenTextures(exportTexture.length, exportTexture, 0);
        glBindTexture(GL_TEXTURE_2D, exportTexture[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        setTextureParameters();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setTextureParameters() {
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public void bindFrameBufferAndTexture() {
        glBindFramebuffer(GL_FRAMEBUFFER, exportFrame[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, exportTexture[0], 0);
    }

    public void unBindFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

}
