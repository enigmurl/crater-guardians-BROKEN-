package com.enigmadux.craterguardians.loading;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.R;

public class Glow extends LoadingRenderable {
    private static final long EXPANSION_TIME = 400;



    private long start;
    private boolean hasStarted = false;

    public Glow(Context context,long start) {
        super(context, R.drawable.loading_glow);

        Matrix.scaleM(instanceTransform,0,2,2,1);
        this.start = start;
    }

    public void start(){
        this.start = System.currentTimeMillis();
        this.hasStarted = true;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    @Override
    public float getClipR() {
        if (! hasStarted){
            return 0;
        }
        long delta = System.currentTimeMillis() - start;



        return Math.min((float) delta/EXPANSION_TIME,5/12f);
    }
}
