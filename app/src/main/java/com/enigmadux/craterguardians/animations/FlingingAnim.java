package com.enigmadux.craterguardians.animations;


import com.enigmadux.craterguardians.guis.levelSelect.LevelSelectLayout;

public class FlingingAnim extends FrameTransitionAnim {
    //based on velocity;
    private static final float FRICTION = 5f;
    private static final float MIN_VELOCITY = 0.01f;

    private LevelSelectLayout levelSelectLayout;
    private double velocity;
    private double cameraX;


    public FlingingAnim(LevelSelectLayout levelSelectLayout,float velocity) {
        super(10000000L);
        this.levelSelectLayout = levelSelectLayout;
        this.velocity = velocity;
        this.cameraX = levelSelectLayout.getCameraX();
        start();
    }

    @Override
    protected void step() {
        this.velocity *= (1 - FRICTION * DELAY_MILLIS/1000);
        if (Math.abs(this.velocity) < MIN_VELOCITY){
            this.cancel();
        }
        this.cameraX += this.velocity * DELAY_MILLIS/1000;
        levelSelectLayout.setCameraX((float) cameraX);


    }
}
