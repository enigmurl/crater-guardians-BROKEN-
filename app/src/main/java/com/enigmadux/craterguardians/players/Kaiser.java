package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enigmadux.craterguardians.attacks.AttackKaiser;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;

import enigmadux2d.core.quadRendering.QuadTexture;

public class Kaiser extends Player {
    private static final int NUM_GENS = 5;
    private static final int[] EVOLVE_DAMAGE = new int[] {2800,6000,10000,15000,25000};

    private static final float[] SPEED = new float[] {1,1.05f,1.1f,1.15f,1.2f};
    private static final float RADIUS = 0.1f;
    private static final int[] MAX_HEALTH = new int[] {150,175,200,225,250};

    //attack details
    private static final long MILLIS_BETWEEN_ATTACKS = 100;
    private static final long RELOADING_MILLIS = 750;
    private static final int MAX_ATTACKS = 20;

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
    public Kaiser(float x, float y) {
        super(x, y, RADIUS,MILLIS_BETWEEN_ATTACKS);
    }


    public Kaiser(){
        this(0,0);
    }


    @Override
    public void spawn() {
        super.spawn();
        if (e1 != null && e2 != null && e3 != null && e4 != null && e5 != null) {
            this.updateSprite();
        }
    }

    @Override
    protected void addRotatableEntities(Context context) {
        this.e1 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e1,0,0,1,1);
        this.e2 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e2,0,0,1,1);
        this.e3 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e3,0,0,1,1);
        this.e4 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e4,0,0,1,1);
        this.e5 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e5,0,0,1,1);
    }

    //angle is in radians
    @Override
    public void attack(World world,float angle) {
        super.attack(world,angle);
        int id = world.getPlayerAttacks().createVertexInstance();
        AttackKaiser a = new AttackKaiser(id,this.getDeltaX(),this.getDeltaY(),angle,this.evolveGen);
        world.getPlayerAttacks().addInstance(a);
    }

    @Override
    public int getMaxHealth() {
        return MAX_HEALTH[evolveGen];
    }

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
        switch (this.evolveGen) {
            case 0:
                e1.setShader(r, b, g, a);
                break;
            case 1:
                e2.setShader(r, b, g, a);
                break;
            case 2:
                e3.setShader(r, b, g, a);
                break;
            case 3:
                e4.setShader(r, b, g, a);
                break;
            case 4:
                e5.setShader(r, b, g, a);
                break;
        }
    }

    @Override
    public int getAttackSpritesheetPointer(){
        return AttackKaiser.getAttackSheetPointer();
    }

    @Override
    public float getCharacterSpeed() {
        return ((this.activeLakes.size() > 0) ? Player.TOXIC_LAKE_SLOWNESS * SPEED[evolveGen] : SPEED[evolveGen]);
    }

    @Override
    public float getDamageForEvolve() {
        return EVOLVE_DAMAGE[this.evolveGen];
    }

    @Override
    public int getNumGens() {
        return Math.min(NUM_GENS,2 + PLAYER_LEVEL/10);
    }

    @NonNull
    @Override
    public String toString() {
        return "Kaiser";
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
}
