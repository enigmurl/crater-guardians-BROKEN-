package com.enigmadux.craterguardians.enemies;

import com.enigmadux.craterguardians.animations.ShootAnimation;
import com.enigmadux.craterguardians.attacks.AttackEnemy1;
import com.enigmadux.craterguardians.attacks.AttackEnemy2;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;

/** Long Ranger
 *
 */
public class Enemy2 extends Enemy {

    private static final float RADIUS = 0.15f;
    //milliseconds between attacks
    private static final long[] ATTACK_RATE = new long[] {1000,975,950,925};
    private static final float[] ATTACK_LEN = AttackEnemy2.LENGTH;
    private static final float SCRAMBLE_DIST = 2;

    private static final int[] HEALTHS = new int[] {40,60,100,150};
    private static final float[] SPEEDS = new float[]{0.95f,0.975f,1f,1.025f};



    public Enemy2(int instanceID, float x, float y, boolean isBlue,int strength) {
        super(instanceID, x, y, RADIUS, ATTACK_LEN[strength], isBlue,ATTACK_RATE[strength],strength);
        this.minDist = Math.min(ATTACK_LEN[strength]-0.05f,GUN_LENGTH * this.height + this.getRadius() * 2.5f);
    }

    @Override
    public int getMaxHealth() {
        return HEALTHS[strength];
    }

    @Override
    public void attack(World world, float angle) {
        super.attack(world,angle);

        float gunTipX = GUN_LENGTH * this.height + AttackEnemy2.RADIUS[strength]+this.getRadius();
        //don't need h/2 because its in the middle
        float gunTipY = GUN_OFFSET_Y * this.height;
        float x = (float) (gunTipX * Math.cos(angle) - Math.sin(angle) * gunTipY);
        float y = (float) (gunTipX * Math.sin(angle) + Math.cos(angle) * gunTipY);
        synchronized (World.enemyAttackLock) {
            int id = world.getEnemyAttacks().createVertexInstance();
            AttackEnemy2 a = new AttackEnemy2(id,this.getDeltaX() + x,this.getDeltaY() + y,angle,strength);
            world.getEnemyAttacks().addInstance(a);
        }
    }

    @Override
    public float getCharacterSpeed() {
        return SPEEDS[strength];
    }

    @Override
    boolean isScrambling(World world) {
        return MathOps.sqrDist(deltaX - world.getPlayer().getDeltaX(),deltaY - world.getPlayer().getDeltaY()) < SCRAMBLE_DIST * SCRAMBLE_DIST;
    }

    @Override
    float getPlayerVsSupplyBias() {
        return 1.3f;
    }
}
