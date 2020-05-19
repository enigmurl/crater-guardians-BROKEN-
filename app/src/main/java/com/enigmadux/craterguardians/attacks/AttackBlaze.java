package com.enigmadux.craterguardians.attacks;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.animations.BlazeStop;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.util.SoundLib;

import java.util.HashSet;

public class AttackBlaze extends PlayerAttack {
    private static final float[] DAMAGE = new float[] {2,3,4,4.5f,5};
    private static final float[] LENGTH = new float[] {1.4f,1.475f,1.55f,1.625f,1.7f};
    private static final float SPEED = 4f;
    private static final float SWEEP = 0.4f;//radiansX

    private static final float[] START_R = new float[] {0.14f,0.1475f,0.155f,0.1625f,0.17f};
    private static final float[] END_R  =  new float[] {0.364f,0.3835f,0.403f,0.4225f,0.442f};

    private HashSet<CraterCollectionElem> hits;


    private int eg;
    private AttackBlaze(int instanceID, float x, float y, float angle, int evolveGen) {
        super(instanceID, x, y, 2 * START_R[evolveGen],2 * START_R[evolveGen], angle, SPEED, LENGTH[evolveGen], DAMAGE[evolveGen]);
        hits = new HashSet<>();
        this.eg = evolveGen;
        this.enableKnockback = false;

        this.spawnerDamageMult = 0.4f;

    }

    @Override
    public void update(long dt, World world) {
        super.update(dt, world);
        float t= curLength/LENGTH[eg];
        this.shader[3] = (1 - t);
    }

    @Override
    void updateMatrix() {
        float r = this.curLength/LENGTH[eg] * (END_R[eg] - START_R[eg]) + START_R[eg];
        Matrix.setIdentityM(scalarTranslationM,0);
        Matrix.translateM(scalarTranslationM,0,this.deltaX,this.deltaY,0);
        Matrix.scaleM(scalarTranslationM,0,r * 2,r * 2,1);
    }



    @Override
    boolean collidesWithEnemy(Enemy e) {
        if (hits.contains(e)){
            return false;
        }
        float r = this.curLength/LENGTH[eg] * (END_R[eg] - START_R[eg]) + START_R[eg];
        return Math.hypot(e.getDeltaX()-deltaX,e.getDeltaY()-deltaY) < r + e.getRadius();
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        if (hits.contains(s)){
            return false;
        }
        float r = this.curLength/LENGTH[eg] * (END_R[eg] - START_R[eg]) + START_R[eg];

        return s.collidesWithCircle(deltaX,deltaY,r);
    }

    @Override
    void onHitSpawner(Spawner s, World w) {
        super.onHitSpawner(s, w);
        hits.add(s);
    }

    @Override
    void onHitEnemy(Enemy e, World w) {
        super.onHitEnemy(e, w);
        hits.add(e);
    }

    @Override
    public void finish(World world) {
        super.finish(world);
    }

    public void cancel(){
    }

    public static void spawnBatch(World world, float x, float y, float midAngle, int evolveGen){
        int id1 = world.getPlayerAttacks().createVertexInstance();
        int id2 = world.getPlayerAttacks().createVertexInstance();
        int id3 = world.getPlayerAttacks().createVertexInstance();
        int id4 = world.getPlayerAttacks().createVertexInstance();
        AttackBlaze a1 = new AttackBlaze(id1,x,y,midAngle - SWEEP/2,evolveGen);
        AttackBlaze a2 = new AttackBlaze(id2,x,y,midAngle - SWEEP/6,evolveGen);
        AttackBlaze a3 = new AttackBlaze(id3,x,y,midAngle + SWEEP/6,evolveGen);
        AttackBlaze a4 = new AttackBlaze(id4,x,y,midAngle + SWEEP/2,evolveGen);
        world.getPlayerAttacks().addInstance(a1);
        world.getPlayerAttacks().addInstance(a2);
        world.getPlayerAttacks().addInstance(a3);
        world.getPlayerAttacks().addInstance(a4);
        SoundLib.playBlazeShoot();
        new BlazeStop();
    }
}
