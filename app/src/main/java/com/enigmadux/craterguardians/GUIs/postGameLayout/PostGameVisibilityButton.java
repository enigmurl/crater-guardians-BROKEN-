package com.enigmadux.craterguardians.GUIs.postGameLayout;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;

/** Enters either the game, or the home screen/ level select
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PostGameVisibilityButton extends VisibilityInducedButton {



    /** Backend thread object used to go into the actual game
     *
     */
    private CraterBackendThread backendThread;

    /** Default constructor
     * @param context any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     * @param objectToHide the object that should be hidden
     * @param objectToShow the object that should be shown when the button is pressed
     * @param backendThread the backend thread
     * @param isRounded if the corners are rounded
     */
    public PostGameVisibilityButton(Context context, int texturePointer,
                                       float x, float y, float w, float h,
                                       PostGameLayout objectToHide, GUILayout objectToShow,
                                       CraterBackendThread backendThread,boolean isRounded) {
        super(context, texturePointer, x, y, w, h, objectToHide, objectToShow, isRounded);

        this.backendThread = backendThread;
    }

    /** Enters the specified layout
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onHardRelease(MotionEvent e) {
        super.onHardRelease(e);


        CraterBackend backend = this.backendThread.getBackend();
        //this means we are going into the game
        if (this.objectToShow == null){
            this.backendThread.setPause(false);
            backend.resetJoySticks();
        } else {
            this.backendThread.setPause(true);
            //going to level screen or home screen
            backend.killEndGamePausePeriod();
            backend.getGameScreenLayout().hide();
        }




        return true;
    }
}
