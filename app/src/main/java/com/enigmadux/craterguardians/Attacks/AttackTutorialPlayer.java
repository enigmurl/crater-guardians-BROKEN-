package com.enigmadux.craterguardians.Attacks;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.enemies.Enemy;


public class AttackTutorialPlayer extends PlayerAttack {
    private static final float RADIUS = 0.2f;

    private static final int DAMAGE = 10;
    private static final float SPEED = 1f;
    private static final float LENGTH = 1f;





    /**
     * Default Constructor
     *
     * @param instanceID The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x start x
     * @param y start y
     * @param angle      RADIANS
     */
    public AttackTutorialPlayer(int instanceID, float x, float y, float angle) {
        super(instanceID, x, y,RADIUS * 2,RADIUS * 2, angle, SPEED, LENGTH,AttackTutorialPlayer.DAMAGE);
    }


    @Override
    boolean collidesWithEnemy(Enemy e) {
        return Math.hypot(this.deltaX - e.getDeltaX(),this.deltaY - e.getDeltaY()) < e.getRadius() + AttackTutorialPlayer.RADIUS;
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        return s.collidesWithCircle(this.deltaX,this.deltaY,RADIUS);
    }

    @Override
    void onHitEnemy(Enemy e, World w) {
        super.onHitEnemy(e, w);
        this.isFinished = true;
    }

    @Override
    void onHitSpawner(Spawner s, World w) {
        super.onHitSpawner(s, w);
        this.isFinished = true;
    }

    public static int getAttackSheetPointer(){
        return R.drawable.kaiser_attack_spritesheet;
    }

}
