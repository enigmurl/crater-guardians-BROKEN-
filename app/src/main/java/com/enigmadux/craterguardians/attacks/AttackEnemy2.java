package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.gameobjects.Shield;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.players.Player;

public class AttackEnemy2 extends EnemyAttack {
    public static final float[] RADIUS =  new float[] {0.1f,0.1025f,0.115f,0.1175f};
    public static final float[] LENGTH = new float[] {2,2.2f,2.4f,2.6f};
    //per second
    private static final float[] SPEED = new float[] {2.5f,2.55f,2.6f,2.65f};

    private static final int[] DAMAGE = new int[] {6,8,11,15};


    private int strength;
    /**
     * Default Constructor
     *
     * @param instanceID     The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x
     * @param y
     * @param angle          RADIANS
     */
    public AttackEnemy2(int instanceID, float x, float y, float angle,int strength) {
        super(instanceID, x, y, RADIUS[strength] * 2, RADIUS[strength] * 2, angle, SPEED[strength], LENGTH[strength], DAMAGE[strength]);
        this.strength = strength;
    }

    @Override
    boolean collidesWithPlayer(Player p) {
        return Math.hypot(p.getDeltaX() - this.deltaX,p.getDeltaY() - this.deltaY) < RADIUS[strength] + p.getRadius();
    }

    @Override
    boolean collidesWithSupply(Supply s) {
        return Math.hypot(s.getDeltaX() - deltaX,s.getDeltaY() - deltaY) < s.getWidth()/2 + RADIUS[strength];
    }

    @Override
    boolean collidesWithShield(Shield s) {
        return s.intersectsCircle(deltaX,deltaY,RADIUS[strength]);
    }

    @Override
    void onHitPlayer(Player player) {
        super.onHitPlayer(player);
        this.isFinished = true;
    }

    @Override
    void onHitSupply(Supply s) {
        super.onHitSupply(s);
        this.isFinished = true;
    }
}
