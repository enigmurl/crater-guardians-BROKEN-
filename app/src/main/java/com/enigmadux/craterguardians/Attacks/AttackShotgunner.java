package com.enigmadux.craterguardians.Attacks;

import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;
import com.enigmadux.craterguardians.worlds.World;

import java.util.HashSet;

public class AttackShotgunner extends PlayerAttack {
    private static final float DAMAGE = 10;
    private static final float LENGTH = 0.5f;
    private static final float SPEED = 2f;
    private static final float SWEEP = 1.75f;//radians

    private static final float START_R = 0.15f;



    private AttackShotgunner(int instanceID, float x, float y, float angle,int evolveGen) {
        super(instanceID, x, y, 2 * START_R,2 * START_R, angle, SPEED, LENGTH, DAMAGE);
    }

    @Override
    public void update(long dt, World world) {
        super.update(dt, world);
    }



    @Override
    boolean collidesWithEnemy(Enemy e) {
        float r = this.width/2;
        return Math.hypot(e.getDeltaX()-deltaX,e.getDeltaY()-deltaY) < r + e.getRadius();
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        float r = this.width/2;

        return s.collidesWithCircle(deltaX,deltaY,r);
    }

    @Override
    void onHitSpawner(Spawner s, World w) {
        super.onHitSpawner(s, w);
        this.isFinished = true;
    }

    @Override
    void onHitEnemy(Enemy e, World w) {
        super.onHitEnemy(e, w);
        this.isFinished = true;
    }

    public static void spawnBatch(World world, float x, float y, float midAngle, int evolveGen){
        int id1 = world.getPlayerAttacks().createVertexInstance();
        int id2 = world.getPlayerAttacks().createVertexInstance();
        int id3 = world.getPlayerAttacks().createVertexInstance();
        int id4 = world.getPlayerAttacks().createVertexInstance();
        AttackShotgunner a1 = new AttackShotgunner(id1,x,y,midAngle - SWEEP/2,evolveGen);
        AttackShotgunner a2 = new AttackShotgunner(id2,x,y,midAngle - SWEEP/6,evolveGen);
        AttackShotgunner a3 = new AttackShotgunner(id3,x,y,midAngle + SWEEP/6,evolveGen);
        AttackShotgunner a4 = new AttackShotgunner(id4,x,y,midAngle + SWEEP/2,evolveGen);
        world.getPlayerAttacks().addInstance(a1);
        world.getPlayerAttacks().addInstance(a2);
        world.getPlayerAttacks().addInstance(a3);
        world.getPlayerAttacks().addInstance(a4);

    }
}
