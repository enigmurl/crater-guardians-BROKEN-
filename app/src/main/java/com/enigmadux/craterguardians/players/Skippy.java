package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enigmadux.craterguardians.attacks.AttackSkippy;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;

import enigmadux2d.core.quadRendering.QuadTexture;

public class Skippy extends Player {
    private static final int NUM_GENS = 3;
    private static final int[] EVOLVE_DAMAGE = new int[] {1400,2000,3000,3500,4000};

    private static final float[] SPEED = new float[] {1f,1.1f,1.2f,1.3f,1.4f,1.5f};
    private static final float RADIUS = 0.1f;
    private static final long MILLIS_BETWEEN_ATTACKS = 200;

    private static final long RELOADING_MILLIS = 500;
    private static final int[] MAX_ATTACKS = new int[] {10,12,15,20,25};
    private static final int[] HEALTHS = new int[] {200,225,250,275,300};

    private static int PLAYER_LEVEL = 0;

    private QuadTexture e1;
    private QuadTexture e2;
    private QuadTexture e3;
    private QuadTexture e4;
    private QuadTexture e5;
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
        super.attack(world,angle);
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
        this.e3 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e3,0,0,1,1);
        this.e4 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e4,0,0,1,1);
        this.e5 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e5,0,0,1,1);
    }


    @Override
    public int getMaxHealth() {
        return HEALTHS[this.evolveGen];
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
        return R.drawable.skippy_attack_spritesheet;
    }

    @Override
    public void translateFromPos(float dX, float dY) {
        this.deltaX += dX;
        this.deltaY += dY;
    }
    @Override
    public float getCharacterSpeed() {
        return (this.activeLakes.size() > 0) ? Player.TOXIC_LAKE_SLOWNESS * SPEED[evolveGen] : SPEED[evolveGen];
    }
    @Override
    public float getDamageForEvolve() {
        return EVOLVE_DAMAGE[this.evolveGen];
    }

    @Override
    public int getNumGens() {
        return Math.min(NUM_GENS,2 + PLAYER_LEVEL/10);
    }


    @Override
    public long getReloadingTime() {
        return RELOADING_MILLIS;
    }

    @Override
    public int getMaxAttacks() {
        return MAX_ATTACKS[this.evolveGen];
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
            case 2:
                this.rotatableEntities.remove(e2);
                this.rotatableEntities.add(e3);
                break;
            case 3:
                this.rotatableEntities.remove(e3);
                this.rotatableEntities.add(e4);
                break;
            case 4:
                this.rotatableEntities.remove(e4);
                this.rotatableEntities.add(e5);
                break;
        }
    }


    @NonNull
    @Override
    public String toString() {
        return "Skippy";
    }
}
