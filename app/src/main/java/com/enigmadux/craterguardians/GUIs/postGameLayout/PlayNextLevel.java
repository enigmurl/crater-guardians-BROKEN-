package com.enigmadux.craterguardians.GUIs.postGameLayout;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.SoundLib;

/** Plays either this level, or the next level if the player won
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PlayNextLevel extends GUIClickable {


    /** The backend object that we use to get into the game
     *
     */
    private CraterBackend backend;

    /** The post game layout that will be hidden when this is released
     *
     */
    private PostGameLayout postGameLayout;

    /**
     * Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     */
    public PlayNextLevel(Context context, int texturePointer, float x, float y, float w, float h,
                         CraterBackend backend, PostGameLayout postGameLayout) {
        super(context, texturePointer, x, y, w, h, false);
        this.backend = backend;

        this.postGameLayout = postGameLayout;

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

        backend.loadLevel();
        backend.setCurrentGameState(CraterBackend.GAME_STATE_INGAME);

        SoundLib.setStateGameMusic(true);
        SoundLib.setStateLossMusic(false);
        SoundLib.setStateVictoryMusic(false);

        //give it time to do atleast 1 frame
        try {
            Thread.sleep(32);
            this.postGameLayout.setVisibility(false);
        } catch (InterruptedException ex){
            //pass
        }
        return true;
    }
}
