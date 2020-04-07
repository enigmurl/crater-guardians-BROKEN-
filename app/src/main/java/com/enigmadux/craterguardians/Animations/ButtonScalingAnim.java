package com.enigmadux.craterguardians.Animations;

import android.util.Log;

import com.enigmadux.craterguardians.GUILib.GUIClickable;


public class ButtonScalingAnim extends FrameTransitionAnim {

    //the default amount of time a knockback takes
    //note it is not actually "default" per se but more a reccomended value, it will not be automaticlaly implemented
    public static final long DEFAULT_MILLIS = 100;

    //the component that needs to be hidden
    private GUIClickable quadTexture;

    private float startS;
    private float endS;


    /** Default constructor
     *
     * @param millis how long to delay the un shading of the component
     *
     */
    public ButtonScalingAnim(GUIClickable quadTexture, long millis,long delay, float startScale, float endScale){
        super(millis);
        this.quadTexture = quadTexture;

        this.startS = startScale;
        this.endS = endScale;
        start(delay);
    }


    @Override
    void finish() {
        super.finish();
        this.quadTexture.setScale(endS);
    }

    @Override
    void step() {
        float s = ((float) millisLeft/totalMillis) * (startS - endS) + endS;
        this.quadTexture.setScale(s);
    }




}
