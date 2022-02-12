package com.alidev.android.portfolio.gl.render;

import static android.opengl.GLES20.glGenTextures;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.alidev.android.portfolio.data.manager.EffectsManager;
import com.alidev.android.portfolio.gl.filter.CameraPreview;
import com.alidev.android.portfolio.gl.utils.MatrixUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraPreviewRender implements GLSurfaceView.Renderer {
    public interface OnSurfaceCreatedListener {
        void onSurfaceCreated(SurfaceTexture texture);
    }

    private boolean useFront = false;
    private float[] matrix = new float[16];

    private SurfaceTexture surfaceTexture;
    private final int[] cameraTexture = new int[1];

    private int width, height;

    private final CameraPreview cameraPreview;
    private final OnSurfaceCreatedListener listener;
    private final EffectsManager effectsManager;

    public CameraPreviewRender(EffectsManager manager,
                               OnSurfaceCreatedListener listener) {
        this.listener = listener;
        this.effectsManager = manager;
        cameraPreview = new CameraPreview();
    }

    public void setUseFront(boolean useFront) {
        if (this.useFront != useFront) {
            this.useFront = useFront;
            cameraPreview.setUseFront(useFront);
            matrix = MatrixUtil.flip(matrix, true, false);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        createTexture();
        surfaceTexture = new SurfaceTexture(cameraTexture[0]);
        listener.onSurfaceCreated(surfaceTexture);
        cameraPreview.onSurfaceCreated();
        effectsManager.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;

            cameraPreview.onSurfaceChanged(width, height);
            effectsManager.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        cameraPreview.setTextureId(cameraTexture);
        cameraPreview.onDraw(effectsManager.getHaveToApplyEffects());
        effectsManager.applyFilters(cameraPreview.getOutputTextureId());
    }

    private void createTexture() {
        glGenTextures(cameraTexture.length, cameraTexture, 0);
    }

}
