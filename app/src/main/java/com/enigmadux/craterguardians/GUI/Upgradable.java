//package com.enigmadux.craterguardians.GUI;
//
//import android.view.MotionEvent;
//
//import com.enigmadux.craterguardians.Characters.Kaiser;
//import com.enigmadux.craterguardians.Characters.Player;
//import com.enigmadux.craterguardians.CraterBackend;
//import com.enigmadux.craterguardians.CraterRenderer;
//import com.enigmadux.craterguardians.LayoutConsts;
//import com.enigmadux.craterguardians.MathOps;
//
///** In the character select layout
// *
// * @author Manu Bhat
// * @version BETA
// */
//public class Upgradable extends Button{
//    //the minimum width of the box
//    private static final float BOX_WIDTH = 0.3f;
//    //the maximum width of the box
//    private static final float BOX_HEIGHT = 0.15f;
//    //the height of the font
//    private static final float FONT_HEIGHT = 0.1f;
//
//
//    //the player object that needs to upgrade, but it does the upgrading for the whole class
//    private Player player;
//
//    /** Default Constructor
//     *
//     * @param player the player class this button is associated with
//     * @param x the x
//     * @param y
//     */
//    public Upgradable(Player player,float x,float y) {
//        super("UPGRADE: " + player.getPlayerLevel(),x,y,BOX_WIDTH,BOX_HEIGHT,FONT_HEIGHT, LayoutConsts.CRATER_TEXT_COLOR,false);
//    }
//
//    @Override
//    public boolean isSelect(MotionEvent e) {
//        return CraterBackend.experience >= CraterRenderer.UPGRADE_COST && this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
//    }
//
//    public void onRelease(){
//        super.onRelease();
//
//        CraterBackend.experience -= CraterRenderer.UPGRADE_COST;
//        player.setPlayerLevel(player.getPlayerLevel()+1);
//        kaiserSelectButton.setText("Kaiser Level: " + Kaiser.PLAYER_LEVEL);
//
//        backend.writePlayerData();
//    }
//}
