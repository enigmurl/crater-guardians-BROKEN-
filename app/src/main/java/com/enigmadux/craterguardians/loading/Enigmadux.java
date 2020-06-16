package com.enigmadux.craterguardians.loading;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.R;

public class Enigmadux extends LoadingRenderable {
    private static final float ASPECT_RATIO = 512/128f;


    public Enigmadux(Context context) {
        super(context, R.drawable.enigmadux);
        Matrix.translateM(instanceTransform,0,0,-0.75f,0);

        final float scale = 0.9f;

        Matrix.scaleM(instanceTransform,0,scale,scale/ASPECT_RATIO,1);
    }


    @Override
    public float getClipR() {
        return Float.MAX_VALUE;
    }
}
