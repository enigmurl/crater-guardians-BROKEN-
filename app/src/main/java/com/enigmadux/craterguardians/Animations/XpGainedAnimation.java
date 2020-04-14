package com.enigmadux.craterguardians.Animations;

import com.enigmadux.craterguardians.GUILib.Text;
import com.enigmadux.craterguardians.util.SoundLib;

public class XpGainedAnimation extends FrameTransitionAnim {
    public static final long DEFAULT_MILLIS = 2000;

    private static final float EXPANDING_PERCENT = 0.2f;
    private static final float DEPLATION_PERCENT = 0.1f;
    private Text xpDisplay;
    private int xp;
    private float maxSize;
    public XpGainedAnimation(long millis, Text xpDisplay,int xp) {
        super(millis);
        this.xpDisplay = xpDisplay;
        this.xp = xp;
        this.maxSize = xpDisplay.getFontSize();
        xpDisplay.setVisibility(true);
        SoundLib.playXpCounterSoundEffect();
        start();
    }

    @Override
    void step() {
        long expandingMillis = (long) (EXPANDING_PERCENT * totalMillis);
        float size = maxSize * getScale((float) finishedMillis/totalMillis);
        if (finishedMillis < expandingMillis){
            this.xpDisplay.updateText(" + XP",size);
        } else if (finishedMillis < totalMillis - (DEPLATION_PERCENT) * totalMillis){
            //normalizing to fit the expanidng percenr
            int currentAmount = (int) ((finishedMillis - expandingMillis) * xp / (totalMillis - expandingMillis - (long) ((DEPLATION_PERCENT) * totalMillis)));
            this.xpDisplay.updateText(" + " + currentAmount + " XP",size);
        } else {
            this.xpDisplay.updateText(" + " + xp + " XP",size);
        }


    }

    @Override
    void finish() {
        super.finish();
        float size = maxSize;
        this.xpDisplay.updateText(" + " + xp + " XP",size);
    }

    //t is finished/total
    private float getScale(float t){
        if (t < EXPANDING_PERCENT){
            return (float) Math.log10(100 * t + 1);
        } else if (t > 1 - DEPLATION_PERCENT){
            return -9.96165969896f * t * t  + 15.9272961077f * t - 4.96563640878f;
        }
        return 0.130864958914f * (t - 0.2f) + 1.32221929473f;
    }
    //in degrees, this would cause for some additional stuff I don't really want to do right now
//    private float getRotation(float t){
//
//    }
}
