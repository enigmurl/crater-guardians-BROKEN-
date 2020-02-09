package com.enigmadux.craterguardians.Spawners;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.Enemies.Enemy2;
import com.enigmadux.craterguardians.R;

import java.util.ArrayList;
import java.util.List;

import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.shapes.TexturedRect;

/** Spawns enemy 1
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy2Spawner extends Spawner {

    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];


    /** Constructor used for waves, this is in future will be the only one
     * @param instanceId the instanceID with respects to the vao this is in
     * @param x the open gl coordinate of the spawner, left most edge deltX coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the spawner, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the spawner (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the spawner (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param numSpawns for each spawn location specified by times: how many enemies are spawning
     * @param times the milliseconds at which a spawn will happen
     * @param totalWaveTime the total milli seconds of a wave, this means that after the sai amount of milliseconds, the cycle will repeat
     * @param decayTime the total amount of time it takes to decay
     * @param health the health of the spawner
     */
    public Enemy2Spawner(int instanceId,float x, float y, float w, float h, short[] numSpawns, long[] times, long totalWaveTime, long decayTime, int health){
        super(instanceId,x,y,w,h,numSpawns,times,totalWaveTime,decayTime,health);
        //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.deltaX + w/2,this.deltaY + h/2,0);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,w,h,0);

        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);

    }




    /** Returns a list of enemies
     *
     * @param numEnemies the amount of enemies in the wave
     * @return a list of enemies
     */
    @Override
    public List<Enemy> spawnEnemies(int numEnemies, VaoCollection enemiesCollection) {
        //TODO, may want to pass in the arraylist rather than creating new arrays
        List<Enemy> enemies = new ArrayList<>();
        for (int i = 0;i<numEnemies;i++){
            int instanceID = enemiesCollection.addInstance();
            enemies.add(new Enemy2(instanceID));
            enemies.get(i).setTranslate(this.deltaX +this.width/2,this.deltaY+this.height/2);
        }
        return enemies;
    }


    /** Do not directly call this method, only super class should directly call
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     */
    @Override
    public void updateInstanceTransform(float[] blankInstanceInfo, float[] uMVPMatrix) {
        Matrix.multiplyMM(blankInstanceInfo,0,uMVPMatrix,0,translationScalarMatrix,0);
    }


}
