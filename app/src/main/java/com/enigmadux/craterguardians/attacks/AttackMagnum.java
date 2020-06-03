package com.enigmadux.craterguardians.attacks;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.util.SoundLib;

import java.util.HashSet;


public class AttackMagnum extends PlayerAttack {
    private static final float[] DAMAGE = new float[] {50,60,70,75,80};
    private static final float[] LENGTH = new float[]{0.7f,0.75f,0.8f,0.85f,0.9f};
    private static final float[] SPEED = new float[] {3f,3.1f,3.2f,3.3f,3.4f};
    private static final float[] SWEEP = new float[] {0.75f,0.77f,0.79f,0.81f,0.73f};//radians

    private static final float START_R = 0.15f;
    private static final float SPAWNER_MULT = 0.2f;



    private int evolveGen;
    private HashSet<CraterCollectionElem> hits;
    private AttackMagnum(int instanceID, float x, float y, float angle, int evolveGen) {
        super(instanceID, x, y, 2 * START_R,2 * START_R, angle, SPEED[evolveGen], LENGTH[evolveGen], DAMAGE[evolveGen]);
        this.evolveGen = evolveGen;
        this.hits = new HashSet<>();
        this.spawnerDamageMult = SPAWNER_MULT;
    }


    @Override
    public void updateInstanceTransform(float[] blankInstanceInfo, float[] uMVPMatrix) {
        super.updateInstanceTransform(blankInstanceInfo, uMVPMatrix);
        Matrix.rotateM(blankInstanceInfo,0,(float) Math.toDegrees(angle),0,0,1);
    }

    @Override
    boolean collidesWithEnemy(Enemy e) {
        float r = this.width/2;
        return ! hits.contains(e) && Math.hypot(e.getDeltaX()-deltaX,e.getDeltaY()-deltaY) < r + e.getRadius();
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        float r = this.width/2;
        return ! hits.contains(s) && s.collidesWithCircle(deltaX,deltaY,r);
    }

    @Override
    void onHitEnemy(Enemy e, World w) {
        super.onHitEnemy(e, w);
        this.hits.add(e);
        this.damage *= 0.5f;
    }

    @Override
    void onHitSpawner(Spawner s, World w) {
        super.onHitSpawner(s, w);
        this.hits.add(s);
        this.damage *= 0.5f;
    }

    public static void spawnBatch(World world, float x, float y, float midAngle, int evolveGen){
        int id1 = world.getPlayerAttacks().createVertexInstance();
        int id2 = world.getPlayerAttacks().createVertexInstance();
        int id3 = world.getPlayerAttacks().createVertexInstance();
        AttackMagnum a1 = new AttackMagnum(id1,x,y,midAngle - SWEEP[evolveGen]/2,evolveGen);
        AttackMagnum a2 = new AttackMagnum(id2,x,y,midAngle + SWEEP[evolveGen]/2,evolveGen);
        AttackMagnum a3 = new AttackMagnum(id3,x,y,midAngle,evolveGen);
        world.getPlayerAttacks().addInstance(a1);
        world.getPlayerAttacks().addInstance(a2);
        world.getPlayerAttacks().addInstance(a3);

        SoundLib.playMagnumShoot();
    }
}
