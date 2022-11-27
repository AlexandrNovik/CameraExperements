package com.alexandnovik.camera.gl.utils;

import android.opengl.Matrix;

public class MatrixUtil {

    public static float[] flip(float[] m, boolean x, boolean y) {
        if (x || y) Matrix.scaleM(m, 0, x ? -1 : 1, y ? -1 : 1, 1);
        return m;
    }
}
