package com.enigmadux.craterguardians.GUI;

import android.content.Context;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;


import enigmadux2d.core.shapes.TexturedRect;

public class HomeButton extends Button {
    //the backend object used to quit the current game
    private final CraterBackend backend;
    //the renderer object to load the home screen
    private final CraterRenderer craterRenderer;
    /** Default Constructor
     * @param x the center x
     * @param y the center y
     * @param diameter the height of the scaled image,the width is calculated through this by multiplying to make it a square
     * @param backend the backend object
     * @param renderer the renderer object
     */
    public HomeButton(float x, float y, float diameter, CraterBackend backend, CraterRenderer renderer){
        super(new TexturedRect(x -diameter * LayoutConsts.SCREEN_HEIGHT/(2*LayoutConsts.SCREEN_WIDTH),y-diameter/2,diameter * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH,diameter));
        this.backend = backend;
        this.craterRenderer = renderer;

    }


    @Override
    public boolean isSelect(MotionEvent e) {
        return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
    }

    @Override
    public void onRelease() {
        super.onRelease();

        craterRenderer.exitGame();

        backend.setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);
        backend.killEndGamePausePeriod();

        SoundLib.setStateLobbyMusic(true);
        SoundLib.setStateVictoryMusic(false);
        SoundLib.setStateLossMusic(false);
        SoundLib.setStateGameMusic(false);
    }



}
