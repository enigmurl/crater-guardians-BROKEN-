package com.enigmadux.craterguardians.Animations;


import enigmadux2d.core.quadRendering.QuadTexture;

public class ExpansionAnim extends FrameTransitionAnim {
    //the default amount of time a knockback takes
    //note it is not actually "default" per se but more a reccomended value, it will not be automaticlaly implemented
    public static final long DEFAULT_MILLIS = 250;


    //the component that needs to be hidden
    private QuadTexture quadTexture;

    //the amount to move in the deltX direction
    private float maxWidth;
    //the amount to move in the y direction
    private float maxHeight;
    private float minWidth;
    private float minHeight;



    /** Default constructor
     *
     * @param millis how long to delay the un shading of the component
     *
     */
    public ExpansionAnim(QuadTexture quadTexture, long millis, float startW, float startH){
        super(millis);
        this.quadTexture = quadTexture;

        this.maxWidth = quadTexture.getW();
        this.maxHeight = quadTexture.getH();
        this.minWidth  = startW;
        this.minHeight = startH;
        quadTexture.setScale(0,0);
        start(16);
    }

    @Override
    void step() {
        float wid = ((float) millisLeft/totalMillis) * (minWidth - maxWidth) + maxWidth;
        float hgt = ((float) millisLeft/totalMillis) * (minHeight - maxHeight) + maxHeight;

        this.quadTexture.setScale(wid,hgt);
    }

    @Override
    void finish() {
        super.finish();
        this.quadTexture.setScale(maxWidth,maxHeight);
    }

    @Override
    public void cancel() {
        super.cancel();
        this.finish();
    }
}
