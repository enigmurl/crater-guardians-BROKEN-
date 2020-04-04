package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enigmadux.craterguardians.Attacks.AttackTutorialPlayer;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.worlds.World;

import enigmadux2d.core.quadRendering.QuadTexture;

public class Ryze extends Player {
    private static final int NUM_GENS = 2;
    private static final int[] EVOLVE_DAMAGE = new int[] {40,100};
    private static final int DAMAGE_FOR_CHARGE_UP = 40;

    private static final float SPEED = 1f;
    private static final float RADIUS = 0.1f;
    private static final long MILLIS_BETWEEN_ATTACKS = 300;

    private static final long RELOADING_MILLIS = 2000;
    private static final int MAX_ATTACKS = 30;

    private static int PLAYER_LEVEL = 0;

    private static QuadTexture visualRep;
    /**
     * Default Constructor
     *
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     */
    public Ryze(float x, float y) {
        super(x, y, RADIUS,MILLIS_BETWEEN_ATTACKS);
    }

    public Ryze(){
        this(0,0);
    }

    @Override
    public void attack(World world, float angle) {
        Log.d("RYZE","ATTACKING @ "  + angle);

    }

    @Override
    protected void addRotatableEntities(Context context) {
        //this.rotatableEntities.add(visualRep);
    }


    @Override
    public int getMaxHealth() {
        return 10;
    }
    @Override
    public int getPlayerIcon() {
        return R.drawable.ryze_info;
    }


    @Override
    public int getPlayerInfo() {
        return R.drawable.ryze_info;
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
    public void setShader(float r, float b, float g, float a) {

    }

    @Override
    public int getAttackSpritesheetPointer(){
        return R.drawable.kaiser_attack_spritesheet;
    }

    @Override
    public void translateFromPos(float dX, float dY) {
        this.deltaX += dX;
        this.deltaY += dY;
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

    @Override
    public long getReloadingTime() {
        return RELOADING_MILLIS;
    }

    @Override
    public int getMaxAttacks() {
        return MAX_ATTACKS;
    }



    @NonNull
    @Override
    public String toString() {
        return "Ryze";
    }
}
