package com.enigmadux.craterguardians.GUIs.characterSelect;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.values.STRINGS;


/** This actually selects the current character, by telling crater renderer
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CharacterSelecter extends GUIClickable {

    /** Where we tell the updated player
     */
    private CraterRenderer renderer;

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
     * @param renderer       A Backend object used to change the current player
     * @param isRounded      if the object has rounded corners
     */
    public CharacterSelecter(Context context, int texturePointer,
                          float x, float y, float w, float h,
                             CraterRenderer renderer,boolean isRounded) {
        super(context, texturePointer, x, y, w, h, isRounded);

        this.renderer = renderer;
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
        this.renderer.setPlayer(this.currentPlayer);
        return true;
    }




    /** Updates the current buffered player
     *
     * @param newPlayer updates the current buffered player
     */
    public void updateCurrentPlayer(Player newPlayer){
        this.currentPlayer = newPlayer;
        this.updateText(STRINGS.CHARACTER_SELECTER_TEXT + newPlayer,0.1f);
    }
}
