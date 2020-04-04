package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.Attacks.AttackKaiser;
import com.enigmadux.craterguardians.Attacks.AttackTutorialPlayer;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.worlds.World;

import enigmadux2d.core.quadRendering.QuadTexture;

/** Not in any of the character lists, because this is just examplle player
 * Very similar to kaiser however
 */

public class TutorialPlayer extends Player {
    private static final int NUM_GENS = 2;
    private static final int[] EVOLVE_DAMAGE = new int[] {40,100};
    private static final int DAMAGE_FOR_CHARGE_UP = 100;

    private static final float SPEED = 1;
    private static final float RADIUS = 0.1f;
    private static final int MAX_HEALTH = 20000000;

    //attack details
    private static final long MILLIS_BETWEEN_ATTACKS = 300;
    private static final long RELOADING_MILLIS = 2000;
    private static final int MAX_ATTACKS = 30;

    private static int PLAYER_LEVEL = 0;



    public static QuadTexture visualRep;

    /**
     * Default Constructor
     *
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     */
    public TutorialPlayer(float x, float y) {
        super(x, y, RADIUS,MILLIS_BETWEEN_ATTACKS);
    }


    public TutorialPlayer(){
        this(0,0);
    }

    public static void loadGLTexture(Context context){
        visualRep = new QuadTexture(context, R.drawable.kaiser_sprite_sheet_e1,0,0,1,1);
    }

    @Override
    protected void addRotatableEntities(Context context) {
        this.rotatableEntities.add(visualRep);
    }

    //angle is in radians
    @Override
    public void attack(World world,float angle) {
        int id = world.getPlayerAttacks().createVertexInstance();
        AttackTutorialPlayer a = new AttackTutorialPlayer(id,this.getDeltaX(),this.getDeltaY(),angle,this.attackChargeUp);
        world.getPlayerAttacks().addInstance(a);
    }

    @Override
    public int getAttackSpritesheetPointer(){
        return AttackTutorialPlayer.getAttackSheetPointer();
    }



    @Override
    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    //these stuff won't really be used
    @Override
    public int getPlayerIcon() {
        return R.drawable.kaiser_info;
    }

    @Override
    public int getPlayerInfo() {
        return R.drawable.kaiser_info;
    }

    @Override
    public int getPlayerLevel(){
        return PLAYER_LEVEL;
    }

    @Override
    public void setPlayerLevel(int playerLevel){
        PLAYER_LEVEL = playerLevel;
    }

    @Override
    public long getReloadingTime() {
        return RELOADING_MILLIS;
    }

    @Override
    public int getMaxAttacks() {
        return MAX_ATTACKS;
    }

    @Override
    public void setShader(float r, float b, float g, float a) {
        //technically it only affects the static, but theres only one so the shader will be affect that only
        //TODO make more general solution
        visualRep.setShader(r, b, g, a);
    }



    @Override
    public float getCharacterSpeed() {
        return (this.activeLakes.size() > 0) ? Player.TOXIC_LAKE_SLOWNESS * SPEED : SPEED;
    }

    @Override
    public float getDamageForEvolve() {
        return EVOLVE_DAMAGE[this.evolveGen];
    }

    @Override
    public float getDamageForFullChargeUp() {
        return DAMAGE_FOR_CHARGE_UP;
    }

    @Override
    public int getNumGens() {
        return NUM_GENS;
    }

}
