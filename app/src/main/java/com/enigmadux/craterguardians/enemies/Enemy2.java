package com.enigmadux.craterguardians.enemies;

import com.enigmadux.craterguardians.attacks.AttackEnemy2;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;

/** Long Ranger
 *
 */
public class Enemy2 extends Enemy {

    private static final float RADIUS = 0.15f;
    //milliseconds between attacks
    private static final long ATTACK_RATE = 1000;
    private static final float ATTACK_LEN = AttackEnemy2.LENGTH;
    private static final float SCRAMBLE_DIST = 2;

    private static final int[] HEALTHS = new int[] {40,60,100,150};



    public Enemy2(int instanceID, float x, float y, boolean isBlue,int strength) {
        super(instanceID, x, y, RADIUS, ATTACK_LEN, isBlue,ATTACK_RATE,strength);
        this.minDist = ATTACK_LEN/5;
    }

    @Override
    public int getMaxHealth() {
        return HEALTHS[strength];
    }

    @Override
    public void attack(World world, float angle) {
        super.attack(world,angle);
        int id = world.getEnemyAttacks().createVertexInstance();
        AttackEnemy2 a = new AttackEnemy2(id,this.getDeltaX(),this.getDeltaY(),angle);
        world.getEnemyAttacks().addInstance(a);
    }

    @Override
    public float getCharacterSpeed() {
        return 1f;
    }

    @Override
    boolean isScrambling(World world) {
        return this.getHealth() < this.getMaxHealth()  || MathOps.sqrDist(deltaX - world.getPlayer().getDeltaX(),deltaY - world.getPlayer().getDeltaY()) < SCRAMBLE_DIST * SCRAMBLE_DIST;
    }

    @Override
    float getPlayerVsSupplyBias() {
        return 5;
    }
}