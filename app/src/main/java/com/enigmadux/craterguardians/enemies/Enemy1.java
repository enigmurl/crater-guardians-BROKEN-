package com.enigmadux.craterguardians.enemies;


import com.enigmadux.craterguardians.attacks.AttackEnemy1;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;

/** Melee range
 *
 */
public class Enemy1 extends Enemy {


    private static final float RADIUS = 0.2f;
    //milliseconds between attacks
    private static final long ATTACK_RATE = 2000;

    private static final float PLAYER_BIAS = 50;

    private static final float MIN_DIST = 0.4f;
    private static final float SCRAMBLE_DIST = 1f;

    private static final int[] HEALTHS = new int[] {50,100,175,250};


    /**
     * Default Constructor
     *
     * @param instanceID the id of the instance in reference to the vao it's in (received using VaoCollection.addInstance());
     * @param x          the center x in openGL terms
     * @param y          the center y in openGL terms
     * @param isBlue    if its blue
     */
    public Enemy1(int instanceID, float x, float y, boolean isBlue,int strength) {
        super(instanceID, x, y, RADIUS, AttackEnemy1.LENGTH, isBlue,ATTACK_RATE,strength);
        this.minDist = MIN_DIST;
    }


    public float getCharacterSpeed(){
        return 0.65f;
    }

    @Override
    public int getMaxHealth() {
        return HEALTHS[strength];
    }


    @Override
    public void attack(World world,float angle){
        super.attack(world,angle);
        AttackEnemy1.spawnBatch(world,deltaX,deltaY,angle);
    }

    @Override
    boolean canMove(World world) {
        return super.canMove(world);
    }

    @Override
    float getPlayerVsSupplyBias() {
        return PLAYER_BIAS;
    }

    @Override
    boolean isScrambling(World world) {
        return this.getHealth() * 2 < this.getMaxHealth() || MathOps.sqrDist(deltaX - world.getPlayer().getDeltaX(),deltaY - world.getPlayer().getDeltaY()) < SCRAMBLE_DIST * SCRAMBLE_DIST;
    }
}