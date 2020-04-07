package com.enigmadux.craterguardians.GUIs.characterSelect;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.OnOffButton;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.values.STRINGS;


/** This actually selects the current character, by telling crater renderer
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CharacterUpgrader extends GUIClickable {
    //shader of off buttons in form of r g b a
    private static final float[] OFF_SHADER = new float[] {1.0f,0.5f,0.5f,1};
    //shader of on buttons in form of r g b a
    private static final float[] ON_SHADER = new float[] {0.5f,1.0f,0.5f,1};




    /** The current player, may differ from the value in the backend
     *
     */
    private Player currentPlayer;

    private PlayerData playerData;

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
     * @param isRounded      if the object has rounded corners
     */
    public CharacterUpgrader(Context context, int texturePointer, PlayerData playerData, CharacterSelectLayout characterSelectLayout,
                             float x, float y, float w, float h,boolean isRounded) {
        super(context, texturePointer, x, y, w, h, isRounded);
        this.playerData = playerData;
        this.characterSelectLayout = characterSelectLayout;
    }

    /** Makes sure that it won't be pressed if no character is selected
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the player isn't null and touch event is clicked inside the bounding box
     */
    @Override
    public boolean isPressed(MotionEvent e) {
        return this.currentPlayer != null && currentPlayer.getPlayerLevel() < Player.UPGRADE_COSTS.length && PlayerData.getExperience() >= Player.UPGRADE_COSTS[currentPlayer.getPlayerLevel()] && super.isPressed(e);
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
        this.currentPlayer.setPlayerLevel(this.currentPlayer.getPlayerLevel() + 1);
        if (currentPlayer.getPlayerLevel() < Player.UPGRADE_COSTS.length) {
            this.updateText(STRINGS.CHARACTER_UPGRADER_SUFFIX_TEXT + Player.UPGRADE_COSTS[currentPlayer.getPlayerLevel()], 0.05f);
            this.playerData.updateXP(PlayerData.getExperience() - Player.UPGRADE_COSTS[currentPlayer.getPlayerLevel()]);
        } else {
            this.updateText("MAX LEVEL",0.05f);
        }
        this.characterSelectLayout.updatePlayerIcons();
        this.updateShader();
        return true;
    }




    /** Updates the current buffered player
     *
     * @param newPlayer updates the current buffered player
     */
    void updateCurrentPlayer(com.enigmadux.craterguardians.players.Player newPlayer){
        this.currentPlayer = newPlayer;
        if (this.currentPlayer == null){
            this.setVisibility(false);
            this.updateText(null,0.1f);
        } else {
            this.setVisibility(true);
            if (currentPlayer.getPlayerLevel() < Player.UPGRADE_COSTS.length) {
                this.updateText(STRINGS.CHARACTER_UPGRADER_SUFFIX_TEXT + Player.UPGRADE_COSTS[currentPlayer.getPlayerLevel()], 0.05f);
            } else {
                this.updateText("MAX LEVEL",0.05f);
            }
            this.updateShader();

        }
    }

    private void updateShader(){

        if (currentPlayer.getPlayerLevel() < Player.UPGRADE_COSTS.length && PlayerData.getExperience() >= Player.UPGRADE_COSTS[currentPlayer.getPlayerLevel()]) {
            this.setShader(ON_SHADER[0], ON_SHADER[1], ON_SHADER[2], ON_SHADER[3]);
        } else {
            this.setShader(OFF_SHADER[0],OFF_SHADER[1],OFF_SHADER[2],OFF_SHADER[3]);
        }
    }
}
