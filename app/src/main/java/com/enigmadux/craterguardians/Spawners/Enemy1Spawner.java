package com.enigmadux.craterguardians.Spawners;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.Enemies.Enemy1;
import com.enigmadux.craterguardians.R;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Spawns enemy 1
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy1Spawner extends Spawner {
    //parentMatrix*translationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];



    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,0,1,1);

    /** Default Constructor
     *
     * @param x the open gl coordinate of the spawner, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the spawner, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the spawner (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param millisPerSpawn the milli seconds between spawns
     * @param h the height of the spawner (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param health the amount of damage this can take before dieing
     */
    public Enemy1Spawner(float x, float y, float w, float h, long millisPerSpawn,int health) {
        super(x, y, w, h,millisPerSpawn,health);
        //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.x,this.y,0);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,w,h,0);

        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);

    }

    /** Constructor used for waves, this is in future will be the only one TODO actually implement this
     *
     * @param x the open gl coordinate of the spawner, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the spawner, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the spawner (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the spawner (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param numSpawns for each spawn location specified by times: how many enemies are spawning
     * @param times the milliseconds at which a spawn will happen
     * @param totalWaveTime the total milli seconds of a wave, this means that after the sai amount of milliseconds, the cycle will repeat
     * @param health the health of the spawner
     */
    public Enemy1Spawner(float x,float y,float w,float h,short[] numSpawns,long[] times,long totalWaveTime,int health){
        super(x,y,w,h,0,health);
    }



    /** Draws the VISUAL_REPRESENTATION customized for this spawner
     *
     * @param gl access to openGL
     * @param parentMatrix describes how to change from model to world coordinates
     */
    @Override
    public void draw(GL10 gl,float[] parentMatrix){
        super.draw(gl,parentMatrix);
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);
        VISUAL_REPRESENTATION.draw(gl,finalMatrix);

    }

    /** Creates an enemy
     *
     * @return always returns type of enemy1, as this is an enemy1 spawner
     */
    public Enemy instantiateSingleEnemy() {
        Enemy1 e = new Enemy1();
        e.setTranslate(this.x+this.w/2,this.y+this.h/2);
        return e;
    }

    /** Loads the texture
     *
     * @param gl used to tell openGL what the new texture is
     * @param context used to access resources
     */
    public static void loadGLTexture(@NonNull GL10 gl,Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(gl,context, R.drawable.enemy1_spawner);
    }


}

