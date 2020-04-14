package com.enigmadux.craterguardians.GUIs.levelSelect;


import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Animations.PopUp;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.FileStreams.LevelData;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.Arrays;

/** When pressed, it goes into the game with the corresponding level number
 *
 * @author Manu Bhat
 * @version BETA
 */
public class LevelSelector extends GUIClickable {
    private static final float MAX_MULT = 3.5f;
    private static final float MIN_MULT = 1f;

    //private static final long DELAY_LEVEL = 48;

    /** Color of when the level is unlocked but not completed (RGBA)
     *
     */
    private static float[] UNLOCKED_SHADER = new float[] {1,1,1,1};

    /** Color of when the level is unlocked and completed (RGBA)
     *
     */
    private static float[] COMPLETED_SHADER = new float[] {0.5f,1,0.5f,1};

    /** Color of when level is not unlocked
     *
     */
    private static float[] LOCKED_SHADER = new float[] {1,0.5f,0.5f,1};

    /** The level select layout that will be hidden after
     *
     */
    private LevelSelectLayout levelSelectLayout;

    /** The In Game layout that will be shown after being pressed
     *
     */
    private GUILayout inGameScreen;

    /** The backend object that we use to get into the game
     *
     */
    private World backend;


    //what we use to get the backend thread
    private CraterRenderer craterRenderer;

    /** The level num that will be played when this is releaesd (if unlocked)
     *
     */
    private int levelNum;

    private PopUp popUpAnimation;
    private float orgOrgW;
    private float orgOrgH;
    private float orgW;
    private float orgH;

    private float startX;

    /**
     * Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     * @param levelSelectLayout the level select layout that will be hidden after
     * @param levelNum       The level num that will be played when this is released (if unlocked)
     */
    public LevelSelector(Context context, int texturePointer, float x, float y, float w, float h,
                         LevelSelectLayout levelSelectLayout, GUILayout inGameScreen,
                         CraterRenderer craterRenderer, int levelNum) {
        super(context, texturePointer, x, y, w, h, false);
        this.startX = x;
        this.levelSelectLayout = levelSelectLayout;
        this.inGameScreen = inGameScreen;
        this.backend = craterRenderer.getCraterBackendThread().getBackend();
        this.craterRenderer = craterRenderer;

        this.levelNum = levelNum;
        this.orgW = this.orgOrgW = this.w;
        this.orgH = this.orgOrgH = this.h;




        this.updateText(""+levelNum,h/5);
        this.textDeltaY = -h/10;

    }

    /** Sees if the level is unlocked AND the touch event intersects
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the button is pressed
     */
    @Override
    public boolean isPressed(MotionEvent e) {
        //-1 because it's offset
        return super.isPressed(e) && LevelData.getUnlockedLevels()[this.levelNum -1];
    }

    /** Called when the button is pressed
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onPress(MotionEvent e) {
        this.isDown = true;
        return true;
    }

    /** Called when the user slides over the button
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onSoftRelease(MotionEvent e) {
        this.isDown = false;
        return true;
    }

    /** Goes into the actual game
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onHardRelease(MotionEvent e) {
        this.isDown = false;

        this.backend.setLevelNum(this.levelNum);
        this.backend.loadLevel();
        this.backend.setState(World.STATE_PREGAME);
        this.craterRenderer.getCraterBackendThread().setPause(false);

        this.levelSelectLayout.setVisibility(false);
        this.inGameScreen.setVisibility(true);


        SoundLib.setStateLobbyMusic(false);
        SoundLib.setStateGameMusic(true);

        return true;
    }


    /** Updates shaders and visibility
     *
     * @param visible whether to be drawn or not
     */
    @Override
    public void setVisibility(boolean visible) {
        super.setVisibility(visible);
        if (visible){
            boolean unlocked = LevelData.getUnlockedLevels()[this.levelNum-1];
            boolean completed = LevelData.getCompletedLevels()[this.levelNum-1];

            if (unlocked && completed){
                this.shader = Arrays.copyOf(LevelSelector.COMPLETED_SHADER,4);
            } else if (unlocked) {
                this.shader = Arrays.copyOf(LevelSelector.UNLOCKED_SHADER,4);
            } else {
                this.shader = Arrays.copyOf(LevelSelector.LOCKED_SHADER,4);
            }
            if (popUpAnimation != null){
                popUpAnimation.cancel();
            }
            //float s = getScale(this.x );
            //this.popUpAnimation = new PopUp(PopUp.DEFAULT_MILLIS,orgW * s,orgH * s,this,this.levelNum * DELAY_LEVEL);

        }
    }

    @Override
    public void setScale(float w, float h) {
        super.setScale(w, h);
        this.scale = w/orgW;
    }

    void setCameraX(float cameraX){

        float orgX = cameraX + startX;
        float x = orgX + (float) (orgX < 0 ? -Math.pow(-orgX,0.75):Math.pow(orgX,0.75));
        float s = getScale(x);

        if (Math.abs(x) - s * w/2 > 1){
            s = 0;
        }
        this.fontScale = s;
        if (x < 0 != this.x < 0 && isVisible){
            SoundLib.playLevelSelectTickSoundEffect();
        }
        this.setTransform(x,y,this.orgOrgW * s ,orgOrgH*s );
        this.orgW = w;
        this.orgH = h;

        if (Math.abs(x) > 0.8f + w/2 && Math.abs(x) <= 1 + w/2) {
            this.shader[3] = (1 - Math.abs(x)) * 5;
            if (this.visibleText != null) {
                this.visibleText.setAlpha((1 - Math.abs(x)) * 5);
                Log.d("Text Shader: ","ID: " + this.visibleText.getShader() + " " + this.visibleText);
            }
        } else {
            this.shader[3] = 1;
            if (this.visibleText != null) {
                this.visibleText.setAlpha(1);
            }
        }
    }

    //given location on screen, gets the scale
    private float getScale(float orgX){
        float t = (1 - Math.abs(orgX));
        return  t * (MAX_MULT - MIN_MULT) + MIN_MULT;
    }

}
