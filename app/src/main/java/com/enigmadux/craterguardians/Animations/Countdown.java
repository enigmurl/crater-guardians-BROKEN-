package com.enigmadux.craterguardians.Animations;

import android.util.Log;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.GUILib.Text;
import com.enigmadux.craterguardians.GUILib.TextRenderable;

import java.util.logging.Handler;

import enigmadux2d.core.gameObjects.CollectionElem;

/** Pretty much hardcoded to countdown after resuming th egame
 *
 */
public class Countdown extends TransitionAnim {
    private Text text;
    private int secondsLeft;
    private CraterBackendThread craterBackendThread;
    public Countdown(Text text, int seconds, CraterBackendThread craterBackendThread){
        text.setVisibility(true);
        this.text = text;
        this.text.updateText("" + seconds,this.text.getFontSize());
        this.secondsLeft = seconds;
        this.craterBackendThread = craterBackendThread;
        HANDLER.postDelayed(this,1000);
    }

    @Override
    public void run() {
        secondsLeft--;
        if (secondsLeft <= 0){
            this.text.setVisibility(false);
            this.craterBackendThread.setPause(false);
            return;
        }
        this.text.updateText("" + secondsLeft,this.text.getFontSize());
        HANDLER.postDelayed(this,1000);

    }
}
