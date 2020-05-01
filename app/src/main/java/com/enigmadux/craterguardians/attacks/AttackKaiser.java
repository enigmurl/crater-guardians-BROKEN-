package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.enemies.Enemy;


public class AttackKaiser extends PlayerAttack {
    private static final float[] RADIUS = new float[] {0.15f,0.16f,0.17f,0.18f,0.19f};

    private static final int[] DAMAGE = new int[] {17,22,24,26,28};
    private static final float SPEED = 4f;
    private static final float[] LENGTH = new float[] {1.75f,1.85f,1.95f,2.05f,2.15f};


    private int evolveGen;
    /**
     * Default Constructor
     *
     * @param instanceID The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x start x
     * @param y start y
     * @param angle      RADIANS
     */
    public AttackKaiser(int instanceID, float x, float y, float angle,int evolveGen) {
        super(instanceID, x, y,RADIUS[evolveGen] * 2,RADIUS[evolveGen] * 2, angle, SPEED, LENGTH[evolveGen],AttackKaiser.DAMAGE[evolveGen]);
        this.evolveGen = evolveGen;
    }


    @Override
    boolean collidesWithEnemy(Enemy e) {
        return Math.hypot(this.deltaX - e.getDeltaX(),this.deltaY - e.getDeltaY()) < e.getRadius() + AttackKaiser.RADIUS[evolveGen];
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        return s.collidesWithCircle(this.deltaX,this.deltaY,RADIUS[evolveGen]);
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
