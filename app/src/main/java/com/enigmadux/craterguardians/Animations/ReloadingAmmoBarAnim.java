package com.enigmadux.craterguardians.Animations;

import android.util.Log;

import com.enigmadux.craterguardians.GUILib.ProgressBar;
import com.enigmadux.craterguardians.GUILib.Tileable;

public class ReloadingAmmoBarAnim extends FrameTransitionAnim {

    private ProgressBar ammoBar;

    private float rChannel;
    private float gChannel;
    private float bChannel;
    public ReloadingAmmoBarAnim(long millis, ProgressBar ammoBar){
        super(millis);
        this.ammoBar = ammoBar;
        for (int i = 0;i < ammoBar.getRenderables().size();i++) {
            ammoBar.getRenderables().get(i).setAlpha(0);
        }
        Log.d("Animations:","Reloading (new instance)");

        float[] currentShader = ammoBar.getRenderables().get(0).getShader();
        rChannel = currentShader[0];
        gChannel = currentShader[1];
        bChannel = currentShader[2];
        start();
    }

    @Override
    void step() {
        for (int i = 0;i < ammoBar.getRenderables().size();i++) {
            ammoBar.getRenderables().get(i).setAlpha(getAmmoBarAlpha((float) (finishedMillis)/totalMillis));
        }
    }

    @Override
    void finish() {
        super.finish();
        for (int i = 0;i < ammoBar.getRenderables().size();i++) {
            ammoBar.getRenderables().get(i).setShader(rChannel,gChannel,bChannel,(float) (finishedMillis)/totalMillis);
        }
    }

    /** Helper method that helps computes the alpha during the flashing animation: todo make it so values where the reload time is high it does more flashes as to keep each wavelength the same
     *
     * @param t amount of time thats passed by (from 0 to 1)
     * @return the alpha value of the ammo bar (from 0 to 1, 0 being completly transparent)
     */
    private float getAmmoBarAlpha(float t){
        if (t < 1f/4){
            return 8 * t * t;
        }
        if (t > 3f/4){
            return 2 * t - 1;
        }

        return - (float) Math.sin(t * Math.PI * 12)/4 + 0.5f;
    }

    public boolean isFinished(){
        return finishedMillis > totalMillis;
    }
}
