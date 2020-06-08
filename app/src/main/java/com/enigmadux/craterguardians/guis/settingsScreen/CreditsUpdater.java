package com.enigmadux.craterguardians.guis.settingsScreen;

import com.enigmadux.craterguardians.animations.FrameTransitionAnim;

public class CreditsUpdater extends FrameTransitionAnim {
    private static final float SPEED = 0.0001f;//per milli

    private Credits credits;

    private float offsetY;
    public CreditsUpdater(Credits credits) {
        super((long) ((credits.getH() + 2)/SPEED));
        this.credits = credits;

        this.credits.setVisibility(true);
        offsetY = -credits.getH()/2 - 1;
        this.credits.setCord(0,offsetY);
        start();
    }

    @Override
    protected void step() {
        offsetY = -credits.getH()/2 - 1 + SPEED * finishedMillis;

        credits.setCord(0,offsetY);

    }


    @Override
    protected void finish() {
        super.finish();
        this.credits.setVisibility(false);
    }

    @Override
    public void cancel() {
        super.cancel();
        this.credits.setVisibility(false);
    }
}
