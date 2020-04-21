package com.enigmadux.craterguardians.attacks;

import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.util.MathOps;

public abstract class BaseAttack extends CraterCollectionElem {
    protected static final boolean PLAYER_ATTACK = true;
    protected static final boolean ENEMY_ATTACK = false;


    protected float angle;
    protected float speed;

    protected float maxLength;
    protected float curLength = 0;

    protected boolean isFinished = false;

    private boolean isPlayerAttack;
    final float[] scalarTranslationM = new float[16];

    long ellapsedMillis;
    /**
     * Default Constructor
     *
     * @param instanceID The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param angle RADIANS
     * @param speed openGL coords/ per second
     * @param length max length before dying
     * @param isPlayerAttack if it's player attack or enemy attack
     */
    public BaseAttack(int instanceID,float x,float y,float w,float h,float angle,float speed,float length,boolean isPlayerAttack) {
        super(instanceID);

        this.deltaX = x;
        this.deltaY = y;
        this.width = w;
        this.height = h;

        this.angle = angle;
        this.speed = speed;

        this.maxLength = length;

        this.isPlayerAttack = isPlayerAttack;

    }

    @Override
    public void update(long dt, World world){
        this.setFrame();
        this.curLength += dt * this.speed/1000;
        this.ellapsedMillis += dt;
        if (! this.isFinished)
            this.isFinished = this.curLength >= this.maxLength;

        this.deltaX += dt * Math.cos(this.angle) * speed/1000f;
        this.deltaY += dt * Math.sin(this.angle) * speed/1000f;



        if (! this.isFinished){
            this.collisionCheck(world);
        } else {
            this.finish(world);
            return;
        }

        this.updateMatrix();
    }



    void updateMatrix(){
        Matrix.setIdentityM(scalarTranslationM,0);
        Matrix.translateM(scalarTranslationM,0,this.deltaX,this.deltaY,0);
        Matrix.scaleM(scalarTranslationM,0,this.width,this.height,1);
    }

    public void finish(World world){
        if (this.isPlayerAttack){
            world.getPlayerAttacks().delete(this);
            Log.d("BASE ATTACK","deleted player attack " + this.deltaX + " Y: " + this.deltaY);
        } else {
            world.getEnemyAttacks().delete(this);
        }
    }


    @Override
    public void updateInstanceTransform(float[] blankInstanceInfo, float[] uMVPMatrix) {
        Matrix.multiplyMM(blankInstanceInfo,0,uMVPMatrix,0,this.scalarTranslationM,0);
    }


    //row = rotation, col = frame num
    void setFrame(){
        float framesPerMilli =  getFramesPerSecond()/1000f;
        int frameNum = (int) ((this.ellapsedMillis % (this.getNumFrames()/framesPerMilli)) * framesPerMilli);
        this.deltaTextureX = MathOps.getTextureBufferTranslationX(frameNum,this.getNumFrames());
        this.deltaTextureY = 0;
    }

    public abstract void collisionCheck(World world);


    protected abstract int getNumFrames();

    protected abstract float getFramesPerSecond();

}