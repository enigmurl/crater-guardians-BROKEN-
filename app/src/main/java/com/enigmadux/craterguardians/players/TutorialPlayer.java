package com.enigmadux.craterguardians.players;

import android.content.Context;

import com.enigmadux.craterguardians.attacks.AttackTutorialPlayer;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;

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



    private QuadTexture e1;
    private QuadTexture e2;

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

    @Override
    public void spawn() {
        super.spawn();
        if (e1 != null && e2 != null) {
            this.updateSprite();
        }
    }

    @Override
    protected void addRotatableEntities(Context context) {
        e1 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e1,0,0,1,1);
        e2 = new QuadTexture(context,R.drawable.kaiser_sprite_sheet_e2,0,0,1,1);
    }

    //angle is in radians
    @Override
    public void attack(World world,float angle) {
        super.attack(world,angle);
        float gunTipX = this.getGundx() + this.getGunw()/2;
        //don't need h/2 because its in the middle
        float gunTipY = this.getGundy();
        float x = (float) (gunTipX * Math.cos(angle) - Math.sin(angle) * gunTipY);
        float y = (float) (gunTipX * Math.sin(angle) + Math.cos(angle) * gunTipY);
        synchronized (World.playerAttackLock) {
            int id = world.getPlayerAttacks().createVertexInstance();
            AttackTutorialPlayer a = new AttackTutorialPlayer(id, this.getDeltaX() + x, this.getDeltaY() + y, angle);

            world.getPlayerAttacks().addInstance(a);
        }
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
        if (e1 != null && e2 != null) {
            switch (this.evolveGen) {
                case 0:
                    e1.setShader(r, b, g, a);
                    break;
                case 1:
                    e2.setShader(r, b, g, a);
                    break;
            }
        }
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

    @Override
    public QuadTexture getGun(Context context) {
        return new QuadTexture(context,R.drawable.player_gun,this.getGundx(),this.getGundy(),this.getGunw(),this.getGunh());
    }

    float getGundx(){
        return 7 * this.getGunw()/16 + this.getRadius()/(float) Math.sqrt(2);
    }
    float getGundy(){
        return -this.getRadius()/(float) Math.sqrt(2);
    }
    float getGunw(){
        return this.getRadius() * 4;
    }
    float getGunh(){
        return  getRadius();
    }
}
