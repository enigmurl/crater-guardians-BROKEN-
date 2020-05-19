package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.gameobjects.Shield;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.gamelib.World;

public class AttackEnemy1 extends EnemyAttack {
    public static final float[] RADIUS = new float[] {0.15f,0.1505f,0.151f,0.15075f};
    public static final float[] LENGTH = new float[] {0.75f,0.8f,0.85f,0.9f};
    //per second
    private static final float[] SPEED = new float[] {1,1.05f,1.1f,1.15f};

    private static final int[] DAMAGE = new int[]{11,16,21,26};

    private static final float SWEEP = 0.2f;


    private int strength;
    /**
     * Default Constructor
     *
     * @param instanceID     The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x
     * @param y
     * @param angle          RADIANS
     */
    private AttackEnemy1(int instanceID, float x, float y, float angle,int strength) {
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

    //radians
    public static void spawnBatch(World world,float x,float y,float midAngle,int strength){
        int id1 = world.getEnemyAttacks().createVertexInstance();
        int id2 = world.getEnemyAttacks().createVertexInstance();
        world.getEnemyAttacks().addInstance(new AttackEnemy1(id1,x,y,midAngle - SWEEP/2,strength));
        world.getEnemyAttacks().addInstance(new AttackEnemy1(id2,x,y,midAngle + SWEEP/2,strength));

    }
}
