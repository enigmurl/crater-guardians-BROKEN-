package com.enigmadux.craterguardians.attacks;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.HashSet;

public class AttackFlamethrower extends PlayerAttack {
    private static final float[] DAMAGE = new float[] {3,4,5,5.5f,6};
    private static final float[] LENGTH = new float[] {1f,1.125f,1.25f,1.375f,1.5f};
    private static final float SPEED = 4f;
    private static final float SWEEP = 0.4f;//radians

    private static final float[] START_R = new float[] {0.1f,0.1125f,0.125f,0.1375f,0.15f};
    private static final float[] END_R  =  new float[] {0.26f,0.3f,0.325f,0.3575f,0.39f};

    private HashSet<CraterCollectionElem> hits;


    private int eg;
    private AttackFlamethrower(int instanceID, float x, float y, float angle,int evolveGen) {
        super(instanceID, x, y, 2 * START_R[evolveGen],2 * START_R[evolveGen], angle, SPEED, LENGTH[evolveGen], DAMAGE[evolveGen]);
        hits = new HashSet<>();
        this.eg = evolveGen;
        this.enableKnockback = false;
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

    public static void spawnBatch(World world, float x, float y, float midAngle, int evolveGen){
        int id1 = world.getPlayerAttacks().createVertexInstance();
        int id2 = world.getPlayerAttacks().createVertexInstance();
        int id3 = world.getPlayerAttacks().createVertexInstance();
        int id4 = world.getPlayerAttacks().createVertexInstance();
        AttackFlamethrower a1 = new AttackFlamethrower(id1,x,y,midAngle - SWEEP/2,evolveGen);
        AttackFlamethrower a2 = new AttackFlamethrower(id2,x,y,midAngle - SWEEP/6,evolveGen);
        AttackFlamethrower a3 = new AttackFlamethrower(id3,x,y,midAngle + SWEEP/6,evolveGen);
        AttackFlamethrower a4 = new AttackFlamethrower(id4,x,y,midAngle + SWEEP/2,evolveGen);
        world.getPlayerAttacks().addInstance(a1);
        world.getPlayerAttacks().addInstance(a2);
        world.getPlayerAttacks().addInstance(a3);
        world.getPlayerAttacks().addInstance(a4);

    }
}
