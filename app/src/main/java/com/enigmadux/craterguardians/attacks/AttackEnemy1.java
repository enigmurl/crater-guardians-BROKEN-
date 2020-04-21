package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.gameobjects.Shield;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.gamelib.World;

public class AttackEnemy1 extends EnemyAttack {
    private static final float RADIUS = 0.2f;
    public static final float LENGTH = 0.85f;
    //per second
    private static final float SPEED = 1.1f;

    private static final int DAMAGE = 20;

    private static final float SWEEP = 0.2f;


    /**
     * Default Constructor
     *
     * @param instanceID     The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x
     * @param y
     * @param angle          RADIANS
     */
    private AttackEnemy1(int instanceID, float x, float y, float angle) {
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

    //radians
    public static void spawnBatch(World world,float x,float y,float midAngle){
        int id1 = world.getEnemyAttacks().createVertexInstance();
        int id2 = world.getEnemyAttacks().createVertexInstance();
        world.getEnemyAttacks().addInstance(new AttackEnemy1(id1,x,y,midAngle - SWEEP/2));
        world.getEnemyAttacks().addInstance(new AttackEnemy1(id2,x,y,midAngle + SWEEP/2));

    }
}
