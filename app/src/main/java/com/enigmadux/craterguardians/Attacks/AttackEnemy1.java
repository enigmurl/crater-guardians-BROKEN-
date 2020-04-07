package com.enigmadux.craterguardians.Attacks;

import com.enigmadux.craterguardians.GameObjects.Shield;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.players.Player;

public class AttackEnemy1 extends EnemyAttack {
    private static final float RADIUS = 0.2f;
    public static final float LENGTH = 2f;
    //per second
    private static final float SPEED = 1f;

    private static final int DAMAGE = 2;


    /**
     * Default Constructor
     *
     * @param instanceID     The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x
     * @param y
     * @param angle          RADIANS
     */
    public AttackEnemy1(int instanceID, float x, float y, float angle) {
        super(instanceID, x, y, RADIUS * 2, RADIUS * 2, angle, SPEED, LENGTH, DAMAGE);
    }

    @Override
    boolean collidesWithPlayer(Player p) {
        return Math.hypot(p.getDeltaX() - this.deltaX,p.getDeltaY() - this.deltaY) < RADIUS + p.getRadius();
    }

    @Override
    boolean collidesWithSupply(Supply s) {
        return Math.hypot(s.getDeltaX() - deltaX,s.getDeltaY() - deltaY) < s.getWidth()/2 + RADIUS;
    }

    @Override
    boolean collidesWithShield(Shield s) {
        return s.intersectsCircle(deltaX,deltaY,RADIUS);
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
