package com.enigmadux.craterguardians.GUIs.levelSelect;


import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.SoundLib;
import com.enigmadux.craterguardians.values.STRINGS;

/** When pressed, it goes into the game with the corresponding level number
 *
 * @author Manu Bhat
 * @version BETA
 */
public class LevelSelector extends GUIClickable {

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
    private CraterBackend backend;

    /** that backend object that will be used to resume the game
     *
     */
    private CraterBackendThread backendThread;

    /** The level num that will be played when this is releaesd (if unlocked)
     *
     */
    private int levelNum;

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
     * @param backendThread        that backend object that will be used to resume the game
     * @param levelNum       The level num that will be played when this is released (if unlocked)
     */
    public LevelSelector(Context context, int texturePointer, float x, float y, float w, float h,
                         LevelSelectLayout levelSelectLayout, GUILayout inGameScreen,
                         CraterBackendThread backendThread, int levelNum) {
        super(context, texturePointer, x, y, w, h, false);

        this.levelSelectLayout = levelSelectLayout;
        this.inGameScreen = inGameScreen;
        this.backend = backendThread.getBackend();
        this.backendThread = backendThread;

        this.levelNum = levelNum;

        this.updateText(STRINGS.LEVEL_BUTTON_BASE_TEXT + levelNum,0.05f);
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
        this.backend.setCurrentGameState(CraterBackend.GAME_STATE_INGAME);
        this.backendThread.setPause(false);

        this.levelSelectLayout.setVisibility(false);
        this.inGameScreen.setVisibility(true);


        SoundLib.setStateLobbyMusic(false);
        SoundLib.setStateGameMusic(true);

        return true;
    }
}
