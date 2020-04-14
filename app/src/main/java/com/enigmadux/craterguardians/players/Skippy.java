package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enigmadux.craterguardians.Attacks.AttackKaiser;
import com.enigmadux.craterguardians.Attacks.AttackSkippy;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.worlds.World;

import enigmadux2d.core.quadRendering.QuadTexture;

public class Skippy extends Player {
    private static final int NUM_GENS = 2;
    private static final int[] EVOLVE_DAMAGE = new int[] {40,100};

    private static final float SPEED = 1f;
    private static final float RADIUS = 0.1f;
    private static final long MILLIS_BETWEEN_ATTACKS = 300;

    private static final long RELOADING_MILLIS = 1000;
    private static final int MAX_ATTACKS = 10;

    private static int PLAYER_LEVEL = 0;

    private QuadTexture e1;
    private QuadTexture e2;
    /**
     * Default Constructor
     *
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     */
    public Skippy(float x, float y) {
        super(x, y, RADIUS,MILLIS_BETWEEN_ATTACKS);
    }

    public Skippy(){
        this(0,0);
    }


    @Override
    public void spawn() {
        super.spawn();
        if (e1 != null && e2 != null) {
            this.updateSprite();
        }
    }

    @Override
    public void attack(World world, float angle) {
        Log.d("SKIPPY","ATTACKING @ "  + angle);
        int id = world.getPlayerAttacks().createVertexInstance();
        AttackSkippy a = new AttackSkippy(id,this.getDeltaX(),this.getDeltaY(),angle,this.evolveGen);
        world.getPlayerAttacks().addInstance(a);
    }

    @Override
    protected void addRotatableEntities(Context context) {
        //this.rotatableEntities.add(visualRep);
        this.e1 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e1,0,0,1,1);
        this.e2 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e2,0,0,1,1);
        this.rotatableEntities.add(e1);
    }


    @Override
    public int getMaxHealth() {
        return 200;
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
        return R.drawable.skippy_attack_spritesheet;
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
    @Override
    public void evolve(World world) {
        super.evolve(world);
        this.updateSprite();
    }

    private void updateSprite(){
        switch (this.evolveGen){
            case 0:
                this.rotatableEntities.add(e1);
                break;
            case 1:
                this.rotatableEntities.remove(e1);
                this.rotatableEntities.add(e2);
                break;
        }
    }


    @NonNull
    @Override
    public String toString() {
        return "Skippy";
    }
}
