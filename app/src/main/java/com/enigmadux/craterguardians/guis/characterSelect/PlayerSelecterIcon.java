package com.enigmadux.craterguardians.guis.characterSelect;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.values.STRINGS;

/** This class offers a way where when pressed it tells the character select layout to buffer the player
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PlayerSelecterIcon extends GUIClickable {
    private static final float FONT_SIZE = 0.04f;
    private static final float TOP_MARGIN = 0.025f;

    /** The player that will be selected
     *
     */
    private Player mPlayer;

    /** The layout that will receive updated player messages
     *
     */
    private CharacterSelectLayout characterSelectLayout;

    /**
     * Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     * @param player         the player that is represented by this button; that is the player will be selected upon clicking
     * @param characterSelectLayout the layout that will receive messages about the updated player
     */
    public PlayerSelecterIcon(Context context, int texturePointer,
                              float x, float y, float w, float h,
                              Player player, CharacterSelectLayout characterSelectLayout) {
        super(context, texturePointer, x, y, w, h, false);

        //assign attributes
        this.mPlayer = player;
        this.characterSelectLayout = characterSelectLayout;

        this.textDeltaY = this.h/2 - FONT_SIZE - TOP_MARGIN;

        this.update();
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

        this.characterSelectLayout.updateCurrentPlayer(this.mPlayer);
        return true;
    }

    public void update(){
        String level = mPlayer.getPlayerLevel() / 10 + "." + mPlayer.getPlayerLevel() % 10;
        this.updateText(STRINGS.CHARACTER_UPGRADER_TEXT + level,FONT_SIZE);
    }
}
