package com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.animations.TransitionAnim;
import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.guis.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadRenderer;

public class TutorialWrapper extends TransitionAnim {
    private static final int NUM_HELPERS = 3;

    private static final long[] DELAYS = new long[] {World.PRE_GAME_MILLIES,7000,2000};

    private boolean isVisible;
    private ArrayList<TutorialPauseHelper> tutorialPauseHelpers = new ArrayList<>();

    private int count = 0;
    private boolean[] called = new boolean[NUM_HELPERS];

    //just need the world to make sure we're in the right state
    private World world;

    private InGameScreen inGameScreen;
    public TutorialWrapper(Context context, CraterBackendThread craterBackendThread, InGameScreen inGameScreen){
        tutorialPauseHelpers.add(new JoystickHelper(context,craterBackendThread));
        tutorialPauseHelpers.add(new GameObjectHelper(context,craterBackendThread));
        tutorialPauseHelpers.add(new TelemetryHelper(context,craterBackendThread));

        this.inGameScreen = inGameScreen;
    }


    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (! isVisible) return;
        for (int i = 0,size = tutorialPauseHelpers.size();i  < size;i++){
            tutorialPauseHelpers.get(i).render(uMVPMatrix,renderer,textRenderer);
        }
    }
    public boolean onTouch(MotionEvent e){
        for (int i = tutorialPauseHelpers.size()-1;i>=0;i--){
            if (tutorialPauseHelpers.get(i).onTouch(e)) return true;
        }
        return false;
    }



    public void setVisiblility(boolean visiblility) {
        this.isVisible = visiblility;
        if (this.isVisible){
            HANDLER.postDelayed(this,DELAYS[count]);
            called[0] = true;
        }
    }

    @Override
    public void run() {
        if (world != null && world.getCurrentGameState() != World.STATE_INGAME){
            return;
        }

        this.tutorialPauseHelpers.get(count).setVisibility(true);
        count++;
        this.inGameScreen.resetJoySticks();
    }

    public void update(World world,long dt){
        if (world.getCurrentGameState() != World.STATE_INGAME){
            return;
        }
        this.world = world;
        if (count > 0 && ! this.tutorialPauseHelpers.get(count - 1).isVisible()){
            switch (count){
                case 1://explain supplies, plateaus, and toxic lakes
                    if (! called[1]){
                        HANDLER.postDelayed(this,DELAYS[count]);
                        called[1] = true;
                    }
                    break;
                case 2://explain health bar, and other stuff, as well as evolve button
                    if (! called[2] && world.getPlayer().getEvolveCharge() >= 1){
                        HANDLER.postDelayed(this,DELAYS[count]);
                        called[2] = true;
                    }
                    break;


            }
        }
    }
}
