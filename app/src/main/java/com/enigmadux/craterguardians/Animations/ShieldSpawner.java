package com.enigmadux.craterguardians.Animations;

import com.enigmadux.craterguardians.GameObjects.Shield;

public class ShieldSpawner extends FrameTransitionAnim {
    public static final long DEFAULT_MILLIS = 250;
    private Shield s;
    private float orgR;


    public ShieldSpawner(long millis, Shield s) {
        super(millis);
        this.s = s;
        this.orgR = s.getRadius();
        s.setRadius(0);
        start();
    }


    @Override
    void step() {
        s.setRadius(orgR * finishedMillis/totalMillis);
    }

    @Override
    void finish() {
        super.finish();
        s.setRadius(orgR);
    }

    @Override
    public void cancel() {
        super.cancel();
        s.setRadius(orgR);
    }
}
