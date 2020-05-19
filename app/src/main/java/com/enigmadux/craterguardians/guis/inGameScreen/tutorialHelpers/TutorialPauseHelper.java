package com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.animations.PopUp;
import com.enigmadux.craterguardians.animations.TransitionAnim;
import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.guilib.VisibilitySwitch;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.R;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

public abstract class TutorialPauseHelper extends TransitionAnim implements VisibilitySwitch {
    private static final long FADE_MILLIS = 400;
    private static final long ANIM_VARIATION = 200;


    private long DELAY_MILLIS = 16;


    protected boolean isVisible;
    private CraterBackendThread craterBackendThread;

    private ArrayList<QuadTexture> allRenderables;
    private ArrayList<Text> texts;
    private ArrayList<QuadTexture> scalables;

    private float[] orgWs;
    private float[] orgHs;

    private long minMillis;
    private long elapsedMillis = 0;
    private long fadeInMillis = 0;
    private long animMillis;
    //animMillis < minMillis
    private boolean canceled = false;
    public TutorialPauseHelper(Context context,CraterBackendThread craterBackendThread,long minMillis,long animMillis) {
        this.texts =getTexts(context);
        scalables = getScalables(context);
        allRenderables = new ArrayList<>();
        allRenderables.addAll(scalables);
        QuadTexture background = new QuadTexture(context, R.drawable.tutorial_pause_background,0,0,2,2);
        background.setAlpha(0);
        allRenderables.add(0,background);

        this.craterBackendThread = craterBackendThread;

        this.minMillis = minMillis;
        this.animMillis = animMillis;

        this.orgWs = new float[scalables.size()];
        this.orgHs = new float[scalables.size()];
        for (int i = 0,size = this.scalables.size();i<size;i++) {
            this.orgWs[i] = this.scalables.get(i).getW();
            this.orgHs[i] = this.scalables.get(i).getH();
            this.scalables.get(i).setScale(0,0);
        }

    }

    abstract ArrayList<QuadTexture> getScalables(Context context);
    abstract ArrayList<Text> getTexts(Context context);

    @Override
    public void setVisibility(boolean visibility) {
        this.isVisible = visibility;
        Log.d("Wrapper","Showing");
        if (visibility){
            HANDLER.postDelayed(this,DELAY_MILLIS);
        }
        else {
            for (int i = 0, size = texts.size(); i < size; i++) {
                texts.get(i).setVisibility(visibility);
            }
        }
        craterBackendThread.setGamePaused(visibility);
    }
    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (! this.isVisible) return;
        renderer.renderQuads(allRenderables,uMVPMatrix);
        for (int i = 0,size = texts.size();i<size;i++){
            texts.get(i).renderText(textRenderer,uMVPMatrix);
        }
    }


    @Override
    public void run(){
        if (canceled) return;
        boolean firstTime = fadeInMillis < FADE_MILLIS;
        fadeInMillis += DELAY_MILLIS;
        if (fadeInMillis < FADE_MILLIS){
            //background
            this.allRenderables.get(0).setAlpha((float) fadeInMillis/FADE_MILLIS);
            HANDLER.postDelayed(this,DELAY_MILLIS);
            return;
        } else {
            this.allRenderables.get(0).setAlpha(1);
        }
        for (int i = 0, size = texts.size(); i < size; i++) {
            texts.get(i).setVisibility(true);
        }
        if (firstTime) {
            for (int i = 0, size = this.scalables.size(); i < size; i++) {
                new PopUp(this.animMillis +(long) ((ANIM_VARIATION) * (-0.5f + (float) Math.random())), orgWs[i],orgHs[i],this.scalables.get(i), 0);
            }
        }


        this.elapsedMillis += DELAY_MILLIS;

        HANDLER.postDelayed(this,DELAY_MILLIS);

    }

    @Override
    public void cancel() {
        canceled = true;
    }

    public boolean onTouch(MotionEvent e){
        if (! isVisible) return false;
        if (elapsedMillis > minMillis){
            if (e.getActionMasked() == MotionEvent.ACTION_UP){
                this.setVisibility(false);
            }
        }

        return true;
    }


    @Override
    public boolean isVisible() {
        return isVisible;
    }
}
