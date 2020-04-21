package com.enigmadux.craterguardians.animations;

import android.util.Log;

import enigmadux2d.core.quadRendering.QuadTexture;

public class PopUp extends FrameTransitionAnim {
    public static final long DEFAULT_MILLIS = 450;
    private QuadTexture quadTexture;

    private float orgW;
    private float orgH;
    public PopUp(long millis, float orgW, float orgH, QuadTexture quadTexture, long delay) {
        super(millis);
        this.quadTexture = quadTexture;
        this.orgW = orgW;
        this.orgH = orgH;
        quadTexture.setScale(0,0);
        start(delay);
        Log.d("PopUP","Org w: "+ orgW + " org H: " + orgH + " millis: " + millis + " delay: " + delay);
    }

    @Override
    void step() {
        float scale =this.getScale(Math.min(finishedMillis,totalMillis)/(float) totalMillis);
        this.quadTexture.setScale(scale * orgW,scale * orgH);

    }

    @Override
    void finish() {
        super.finish();
        this.quadTexture.setScale(orgW,orgH);
        Log.d("PopUP","Finished w: " + quadTexture.getW() + " H: " + quadTexture.getH());

    }

    //runs the animation (0 < t  <1)
    private float getScale(float t){
        //quadratic
        return -1.93452380952f * t * t + 2.93452380952f * t;
    }
}
