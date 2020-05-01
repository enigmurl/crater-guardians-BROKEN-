package com.enigmadux.craterguardians.guis.postGameLayout;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.VisibilityInducedButton;
import com.enigmadux.craterguardians.gamelib.World;

/** Enters either the game, or the home screen/ level select
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PostGameVisibilityButton extends VisibilityInducedButton {



    /** Backend thread object used to go into the actual game
     *
     */
    private CraterRenderer craterRenderer;

    /** Default constructor
     * @param context any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     * @param objectToHide the object that should be hidden
     * @param objectToShow the object that should be shown when the button is pressed
     * @param isRounded if the corners are rounded
     */
    public PostGameVisibilityButton(Context context, int texturePointer,
                                       float x, float y, float w, float h,
                                       PostGameLayout objectToHide, GUILayout objectToShow,
                                       CraterRenderer craterRenderer,boolean isRounded) {
        super(context, texturePointer, x, y, w, h, objectToHide, objectToShow, isRounded);

        this.craterRenderer = craterRenderer;
    }

    /** Enters the specified layout
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onHardRelease(MotionEvent e) {
        super.onHardRelease(e);


        World backend = this.craterRenderer.getCraterBackendThread().getBackend();
        //this means we are going into the game
        if (this.objectToShow == null){
            this.craterRenderer.getCraterBackendThread().setGamePaused(false);
            backend.resetJoySticks();

        } else {
            this.craterRenderer.getCraterBackendThread().setGamePaused(true);
            //going to level screen or home screen
            backend.killEndGamePausePeriod();
            //backend.getGameScreenLayout().hide();
        }






        return true;
    }
}
