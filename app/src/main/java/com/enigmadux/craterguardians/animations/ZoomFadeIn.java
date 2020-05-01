package com.enigmadux.craterguardians.animations;


import enigmadux2d.core.quadRendering.QuadTexture;

//fades in and out, zooms in (like a cubic
public class ZoomFadeIn extends FrameTransitionAnim {
    public static final long DEFAULT_MILLIS = 2000;


    private QuadTexture original;
    private float orgW;
    private float orgH;

    public ZoomFadeIn(QuadTexture original,long millis){
        super(millis);
        this.original = original;
        this.orgH = original.getH();
        this.orgW = original.getW();
        this.inGameAnim = true;
        original.setScale(0,0);
        original.setVisibility(true);
        start();
    }



    @Override
    void step() {
        float t = 1 - (float)(millisLeft)/totalMillis;
        float scale = getScale(t);
        original.setScale(orgW * scale,orgH * scale);
        original.setAlpha(getAlpha(t));
    }

    @Override
    void finish() {
        super.finish();
        original.setVisibility(false);
        original.setScale(orgW,orgH);
    }

    private float getScale(float t){
        if (t < 0.2843f){
            return  -12.2956965062f* t * t + 6.9910031489f * t;
        } else if (t < 0.7157f){
            return 0.146612740142f * t * t - 0.0317997977755f * t + 0.99689180990f;
        } else {
            return 20.5677464298f * t * t - 28.7890978753f * t + 11.115830721f;
        }
    }
    private float getAlpha(float t){
        if (t < 0.2843f){
            return 3.517f * t;
        } else if (t < 0.7157f){
            return 1;
        } else {
            return -3.517f * t + 3.517f;
        }
    }


    @Override
    public void cancel() {
        super.cancel();
        finish();
    }
}
