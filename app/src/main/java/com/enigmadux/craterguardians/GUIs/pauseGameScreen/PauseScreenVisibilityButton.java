package com.enigmadux.craterguardians.GUIs.pauseGameScreen;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Animations.Countdown;
import com.enigmadux.craterguardians.Animations.ZoomFadeIn;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.Text;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.VisibilitySwitch;
import com.enigmadux.craterguardians.worlds.World;

/** Enters either the game, or the home screen/ level select
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PauseScreenVisibilityButton extends VisibilityInducedButton {



    /** Backend thread object used to go into the actual game
     *
     */
    private CraterRenderer craterRenderer;

    private Countdown countdown;

    /** Default constructor
     * @param context any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     * @param objectToHide the object that should be hidden
     * @param objectToShow the object that should be shown when the button is pressed
     * @param isRounded whether or not it has rounded corners
     */
    public PauseScreenVisibilityButton(Context context, int texturePointer,
                                       float x, float y, float w, float h,
                                       PauseGameLayout objectToHide, VisibilitySwitch objectToShow,
                                       CraterRenderer craterRenderer, boolean isRounded) {
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

        World backend = this.craterRenderer.getWorld();
        //this means we are going into the game
        if (this.objectToShow instanceof Text){
            backend.resetJoySticks();
            this.countdown = new Countdown((Text)this.objectToShow,3,this.craterRenderer.getCraterBackendThread());
            // this needs to be done later

        } else {
            this.craterRenderer.getCraterBackendThread().setPause(true);
            //going to level screen or home screen
            backend.killEndGamePausePeriod();
            backend.setState(World.STATE_GUI);
        }




        return true;
    }

    @Override
    public void setVisibility(boolean visible) {
        super.setVisibility(visible);
        if (visible && this.countdown != null){
            this.countdown.cancel();
        }
    }
}
