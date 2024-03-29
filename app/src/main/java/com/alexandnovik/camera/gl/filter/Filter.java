package com.alexandnovik.camera.gl.filter;

import com.alexandnovik.camera.R;
import com.alexandnovik.camera.gl.utils.CommonUtil;
import com.alexandnovik.camera.gl.utils.GLUtil;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class Filter {
    public static final String VERTEX_ATTRIB_POSITION = "a_Position";
    public static final int VERTEX_ATTRIB_POSITION_SIZE = 3;
    public static final String VERTEX_ATTRIB_TEXTURE_POSITION = "a_texCoord";
    public static final int VERTEX_ATTRIB_TEXTURE_POSITION_SIZE = 2;
    public static final String UNIFORM_TEXTURE = "s_texture";
    public static final String UNIFORM_MATRIX = "u_matrix";

    public static final float[] vertex = {
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, -1f, 0.0f,
            1f, 1f, 0.0f
    };

    public static final float[] textureCoord = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    public float[] matrix = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public FloatBuffer vertexBuffer;
    public FloatBuffer textureCoordBuffer;

    public int[] textureId;
    public int program;
    public int hVertex, hMatrix, hTextureCoord, hTexture;

    public int width, height;

    public float[] getMatrix() {
        return matrix;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public Filter() {
        initBuffer();
    }

    public void initBuffer() {
        vertexBuffer = CommonUtil.getFloatBuffer(vertex);
        textureCoordBuffer = CommonUtil.getFloatBuffer(textureCoord);
    }

    public int[] getTextureId() {
        return textureId;
    }

    public void setTextureId(int[] textureId) {
        this.textureId = textureId;
    }

    public int[] getOutputTextureId() {
        return null;
    }

    public void onSurfaceCreated() {
        program = initProgram();
        initAttribLocations();
    }

    public void onSurfaceChanged(int width, int height){
        this.width = width;
        this.height = height;
    }

    public void onDraw(boolean shouldBuffer) {
        setViewPort();
        useProgram();
        setExtend();
        bindTexture();
        enableVertexAttribs();
        clear();
        draw();
        disableVertexAttribs();
    }

    public int initProgram() {
        return GLUtil.createAndLinkProgram(R.raw.texture_vertex_shader, R.raw.texture_fragtment_shader);
    }

    public void initAttribLocations() {
        hVertex = glGetAttribLocation(program, VERTEX_ATTRIB_POSITION);
        hMatrix = glGetUniformLocation(program, UNIFORM_MATRIX);
        hTextureCoord = glGetAttribLocation(program, VERTEX_ATTRIB_TEXTURE_POSITION);
        hTexture = glGetUniformLocation(program, UNIFORM_TEXTURE);
    }

    public void setViewPort() {
        glViewport(0, 0, width, height);
    }

    public void clear() {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void useProgram() {
        glUseProgram(program);
    }

    public void setExtend() {
        glUniformMatrix4fv(hMatrix, 1, false, getMatrix(), 0);
    }

    public void bindTexture() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, getTextureId()[0]);
        glUniform1i(hTexture, 0);
    }

    public void enableVertexAttribs() {
        glEnableVertexAttribArray(hVertex);
        glEnableVertexAttribArray(hTextureCoord);
        glVertexAttribPointer(hVertex,
                VERTEX_ATTRIB_POSITION_SIZE,
                GL_FLOAT,
                false,
                0,
                vertexBuffer);

        glVertexAttribPointer(hTextureCoord,
                VERTEX_ATTRIB_TEXTURE_POSITION_SIZE,
                GL_FLOAT,
                false,
                0,
                textureCoordBuffer);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
    }

    public void disableVertexAttribs() {
        glDisableVertexAttribArray(hVertex);
        glDisableVertexAttribArray(hTextureCoord);
    }
}
