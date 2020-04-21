package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.attacks.AttackShotgunner;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;

import enigmadux2d.core.quadRendering.QuadTexture;

public class Shotgunner extends Player {
    private static final int NUM_GENS = 2;
    private static final int[] EVOLVE_DAMAGE = new int[] {40,100};

    private static final float SPEED = 1f;
    private static final float RADIUS = 0.1f;
    private static final long MILLIS_BETWEEN_ATTACKS = 500;

    private static final long RELOADING_MILLIS = 2000;
    private static final int MAX_ATTACKS = 30;

    private static int PLAYER_LEVEL = 0;

    private QuadTexture e1;
    private QuadTexture e2;
    /**
     * Default Constructor
     *
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     */
    public Shotgunner(float x, float y) {
        super(x, y, RADIUS,MILLIS_BETWEEN_ATTACKS);
    }

    public Shotgunner(){
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
        super.attack(world,angle);
        AttackShotgunner.spawnBatch(world,deltaX,deltaY,angle,evolveGen);
    }

    @Override
    protected void addRotatableEntities(Context context) {
        //this.rotatableEntities.add(visualRep);
        this.e1 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e1,0,0,1,1);
        this.e2 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e2,0,0,1,1);
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
        switch (this.evolveGen) {
            case 0:
                e1.setShader(r, b, g, a);
                break;
            case 1:
                e2.setShader(r, b, g, a);
                break;
        }
    }

    @Override
    public int getAttackSpritesheetPointer(){
        return R.drawable.flamethrower_attack_spritesheet;
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
        return "Shotgunner";
    }
}
