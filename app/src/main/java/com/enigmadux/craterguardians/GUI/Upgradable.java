package com.enigmadux.craterguardians.GUI;

import android.view.MotionEvent;

import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.MathOps;

/** In the character select layout
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Upgradable extends Button{
    //the minimum width of the box
    private static final float BOX_WIDTH = 0.3f;
    //the maximum width of the box
    private static final float BOX_HEIGHT = 0.15f;
    //the height of the font
    private static final float FONT_HEIGHT = 0.1f;

    /**
     *  Used to refer to the xp value
     */
    private final PlayerData playerData;

    //the player object that needs to upgrade, but it does the upgrading for the whole class
    private Player player;

    //the select button that is used to select this character
    private Button selectButton;

    //the renderer that's used so that we can update other components
    private CraterRenderer renderer;
    /** Default Constructor
     *
     * @param player the player class this button is associated with
     * @param x the center x
     * @param y the center y
     * @param selectButton the button used
     */
    public Upgradable(Player player,float x,float y,Button selectButton,PlayerData playerData,CraterRenderer renderer) {
        super("UPGRADE: " + player.getPlayerLevel(),x,y,BOX_WIDTH,BOX_HEIGHT,FONT_HEIGHT, LayoutConsts.CRATER_TEXT_COLOR,false);
        this.selectButton = selectButton;
        this.playerData = playerData;
        this.player = player;
        this.renderer = renderer;
    }

    /** Sees if this button is being selected
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not this button is being selected by the main pointer
     */
    @Override
    public boolean isSelect(MotionEvent e) {
        return PlayerData.getExperience() >= CraterRenderer.UPGRADE_COST && this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
    }

    /**
     *
     */
    public void onRelease(){
        super.onRelease();

        player.setPlayerLevel(player.getPlayerLevel()+1);
        playerData.updateXP(PlayerData.getExperience() - CraterRenderer.UPGRADE_COST);
        String baseText = this.selectButton.getText().split(":")[0] + ":";

        this.selectButton.setText(baseText + player.getPlayerLevel());
        this.renderer.updateUpgradeLayouts();

    }
}
