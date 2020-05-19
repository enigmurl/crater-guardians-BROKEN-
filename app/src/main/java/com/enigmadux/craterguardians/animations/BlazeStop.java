package com.enigmadux.craterguardians.animations;

import com.enigmadux.craterguardians.util.SoundLib;

public class BlazeStop extends TransitionAnim {
    private static final long MILLIS = 120;

    public BlazeStop() {
        HANDLER.postDelayed(this,MILLIS);
    }

    @Override
    public void cancel() {
        SoundLib.stopBlaze();
    }

    @Override
    public void run() {
        SoundLib.stopBlaze();
    }
}
