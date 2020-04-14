package com.enigmadux.craterguardians.Attacks;

import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;
import com.enigmadux.craterguardians.worlds.World;

import java.util.HashSet;

public class AttackFlamethrower extends PlayerAttack {
    private static final float DAMAGE = 10;
    private static final float LENGTH = 1.5f;
    private static final float SPEED = 4f;
    private static final float SWEEP = 0.4f;//radians

    private static final float START_R = 0.15f;
    private static final float END_R  =  0.4f;

    private HashSet<CraterCollectionElem> hits;


    private AttackFlamethrower(int instanceID, float x, float y, float angle,int evolveGen) {
        super(instanceID, x, y, 2 * START_R,2 * START_R, angle, SPEED, LENGTH, DAMAGE);
        hits = new HashSet<>();
    }

    @Override
    public void update(long dt, World world) {
        super.update(dt, world);
        this.shader[3] = (1 - curLength/LENGTH);
    }

    @Override
    void updateMatrix() {
        float r = this.curLength/LENGTH * (END_R - START_R) + START_R;
        Matrix.setIdentityM(scalarTranslationM,0);
        Matrix.translateM(scalarTranslationM,0,this.deltaX,this.deltaY,0);
        Matrix.scaleM(scalarTranslationM,0,r * 2,r * 2,1);
    }



    @Override
    boolean collidesWithEnemy(Enemy e) {
        if (hits.contains(e)){
            return false;
        }
        float r = this.curLength/LENGTH * (END_R - START_R) + START_R;
        return Math.hypot(e.getDeltaX()-deltaX,e.getDeltaY()-deltaY) < r + e.getRadius();
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        if (hits.contains(s)){
            return false;
        }
        float r = this.curLength/LENGTH * (END_R - START_R) + START_R;

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
        AttackFlamethrower a1 = new AttackFlamethrower(id1,x,y,midAngle - SWEEP/2,evolveGen);
        AttackFlamethrower a2 = new AttackFlamethrower(id2,x,y,midAngle,evolveGen);
        AttackFlamethrower a3 = new AttackFlamethrower(id3,x,y,midAngle + SWEEP/2,evolveGen);
        world.getPlayerAttacks().addInstance(a1);
        world.getPlayerAttacks().addInstance(a2);
        world.getPlayerAttacks().addInstance(a3);

    }
}
