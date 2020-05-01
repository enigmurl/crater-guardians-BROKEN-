package com.enigmadux.craterguardians.attacks;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.animations.RippleAnim;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.World;

public class AttackSkippy extends PlayerAttack {
    /*Note because the speed reduces over time, we treat the curLength as a sort of "t"*/
    private static final long MILLIS = 750;
    private static final float[] LENGTH = new float[] {1.7f,1.8f,1.9f,2f,2.1f};
    private static final float[] RADIUS = new float[] {0.1f,0.12f,0.14f,0.16f,0.18f};
    private static final float[] DAMAGE = new float[] {6,6.33f,6.666f,7,7.333f};
    //amount in turns after each hop;
    private static final float TURN_RATE = -0.75f;


    private static final float MIN_SCALE = 0.5f;
    private static final long[] HITS = new long[] {136,296,409,573,632,652,672,708,732};

    private boolean[] completed = new boolean[HITS.length];


    private int evolveGen;
    public AttackSkippy(int instanceID, float x, float y, float angle, int evolveGen) {
        super(instanceID, x, y,RADIUS[evolveGen] * 2,RADIUS[evolveGen] * 2, angle, LENGTH[evolveGen]/MILLIS*1000, LENGTH[evolveGen], DAMAGE[evolveGen]);
        this.evolveGen = evolveGen;
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


        float len = (-(t -1) * (t-1) + 1) * LENGTH[evolveGen];

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

        return Math.hypot(deltaX - e.getDeltaX(),deltaY-e.getDeltaY()) < e.getRadius() + RADIUS[evolveGen];
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        return s.collidesWithCircle(deltaX,deltaY,RADIUS[evolveGen]);
    }
}
