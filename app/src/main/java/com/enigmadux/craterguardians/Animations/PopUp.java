package com.enigmadux.craterguardians.Animations;

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
    }

    @Override
    void step() {
        float scale =this.getScale(Math.min(finishedMillis,totalMillis)/(float) totalMillis);
        this.quadTexture.setScale(scale * orgW,scale * orgH);
    }

    //runs the animation (0 < t  <1)
    private float getScale(float t){
        //quadratic
        return -1.93452380952f * t * t + 2.93452380952f * t;
    }
}
