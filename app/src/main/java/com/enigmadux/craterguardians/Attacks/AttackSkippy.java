package com.enigmadux.craterguardians.Attacks;

import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.Animations.RippleAnim;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.worlds.World;

public class AttackSkippy extends PlayerAttack {
    /*Note because the speed reduces over time, we treat the curLength as a sort of "t"*/
    private static final long MILLIS = 1100;
    private static final float LENGTH = 1.5f;
    private static final float RADIUS = 0.1f;
    private static final float DAMAGE = 10;
    //amount in turns after each hop;
    private static final float TURN_RATE = -0.75f;


    private static final float MIN_SCALE = 0.5f;
    private static final long[] HITS = new long[] {200,435,600,841,928,957,986,1040,1075};

    private boolean[] completed = new boolean[HITS.length];


    public AttackSkippy(int instanceID, float x, float y, float angle, int evolveGen) {
        super(instanceID, x, y,RADIUS * 2,RADIUS * 2, angle, LENGTH/MILLIS*1000, LENGTH, DAMAGE);

    }

    @Override
    public void update(long dt, World world){
        this.setFrame();
        this.ellapsedMillis += dt;



        if (! this.isFinished)
            this.isFinished = this.ellapsedMillis >= MILLIS;

        boolean attacking = false;
        long minDiff = ellapsedMillis;
        long tillNext = HITS[0] - ellapsedMillis;
        for (int i = 0;i<completed.length;i++){
            if (ellapsedMillis >= HITS[i]){
                if (! completed[i]){
                    attacking = true;
                    completed[i] = true;
                    this.angle += TURN_RATE;
                    world.getAnims().add(new RippleAnim(deltaX,deltaY,this.width/2));
                }
                minDiff = ellapsedMillis - HITS[i];
                if (i == completed.length -1){
                    tillNext = 0;
                } else {
                    tillNext = HITS[i + 1] - ellapsedMillis;
                }

            } else {

                break;
            }
        }

        float t = ((float) (ellapsedMillis)/MILLIS);


        //found the coordinate of previous thing, now need
        float den =  Math.max(1,minDiff + tillNext);
        float subT = (float) (minDiff)/(den);


        float len = (-(t -1) * (t-1) + 1) * LENGTH;

        this.deltaX += (float) Math.cos(angle) * (len - curLength);
        this.deltaY += (float) Math.sin(angle) * (len - curLength);

        this.curLength = len;


        if (! this.isFinished){
            if (attacking) this.collisionCheck(world);
        } else {
            this.finish(world);
            return;
        }

        float additionalScale =MIN_SCALE + Math.min(1-subT,subT) * (1-MIN_SCALE) * 2;
        Matrix.setIdentityM(scalarTranslationM,0);
        Matrix.translateM(scalarTranslationM,0,this.deltaX,this.deltaY,0);
        Matrix.scaleM(scalarTranslationM,0,additionalScale * this.width,additionalScale * this.height,1);
    }


    @Override
    boolean collidesWithEnemy(Enemy e){

        return Math.hypot(deltaX - e.getDeltaX(),deltaY-e.getDeltaY()) < e.getRadius() + RADIUS;
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        return s.collidesWithCircle(deltaX,deltaY,RADIUS);
    }
}
