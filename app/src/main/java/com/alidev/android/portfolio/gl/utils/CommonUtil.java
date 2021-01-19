package com.alidev.android.portfolio.gl.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.content.Context.ACTIVITY_SERVICE;

public class CommonUtil {

    public static final String TAG = "opengl-demos";

    public static final int BYTES_PER_FLOAT = 4;

    public static boolean checkGLVersion(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        return ci.reqGlEsVersion >= 0x30000;
    }

    public static FloatBuffer getFloatBuffer(float[] array) {
        FloatBuffer buffer = ByteBuffer
                .allocateDirect(array.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer
                .put(array)
                .position(0);

        return buffer;
    }

}
