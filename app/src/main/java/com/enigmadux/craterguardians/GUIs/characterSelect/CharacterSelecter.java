package com.enigmadux.craterguardians.GUIs.characterSelect;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.values.STRINGS;


/** This actually selects the current character, by telling crater renderer
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CharacterSelecter extends VisibilityInducedButton {

    /** Where we tell the updated player
     */
    private CraterRenderer craterRenderer;

    /** The current player, may differ from the value in the backend
     *
     */
    private Player currentPlayer;

    /**
     * Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     * @param craterRenderer       A Backend object used to change the current player
     * @param isRounded      if the object has rounded corners
     */
    public CharacterSelecter(Context context, int texturePointer, CharacterSelectLayout characterSelectLayout, GUILayout homeScreen,
                             float x, float y, float w, float h,
                             CraterRenderer craterRenderer, boolean isRounded) {
        super(context, texturePointer, x, y, w, h,characterSelectLayout,homeScreen, isRounded);

        this.craterRenderer = craterRenderer;
    }

    /** Makes sure that it won't be pressed if no character is selected
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the player isn't null and touch event is clicked inside the bounding box
     */
    @Override
    public boolean isPressed(MotionEvent e) {
        return this.currentPlayer != null && super.isPressed(e);
    }

    /** Called when user presses the button
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onPress(MotionEvent e) {
        super.onPress(e);
        this.isDown = true;
        return true;
    }

    /** Called when user slides over the button
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onSoftRelease(MotionEvent e) {
        super.onSoftRelease(e);
        this.isDown = false;
        return true;
    }

    /**  Called when user lets go of this button, tells the character select layout to set the current player
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onHardRelease(MotionEvent e) {
        this.isDown = false;

        //will not be called if the current player is null
        this.craterRenderer.getWorld().setPlayer(this.currentPlayer);
        //do super call last because you want to set the current player, then have the home screen update, otherwise the home screen wont update
        return super.onHardRelease(e);
    }




    /** Updates the current buffered player
     *
     * @param newPlayer updates the current buffered player
     */
    public void updateCurrentPlayer(Player newPlayer){
        this.currentPlayer = newPlayer;
        if (this.currentPlayer == null){
            this.setVisibility(false);
            this.updateText(null,0.05f);
        } else {
            this.setVisibility(true);
            this.updateText(STRINGS.CHARACTER_SELECTER_TEXT + newPlayer,0.05f);
        }
    }
}
