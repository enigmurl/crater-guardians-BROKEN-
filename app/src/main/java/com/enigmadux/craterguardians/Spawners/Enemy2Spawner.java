package com.enigmadux.craterguardians.Spawners;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.Enemies.Enemy2;

import java.util.ArrayList;
import java.util.List;

import enigmadux2d.core.gameObjects.VaoCollection;

/** Spawns enemy 1
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy2Spawner extends Spawner {

    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];


    public Enemy2Spawner(int instanceId, float x, float y, float w, float h, int orangeEndHealth, int blue1EndHealth, int maxHealth, long blue1, long orange, long blue2, short[] numBlueSpawns, long[] blueSpawnJuice, short[] numOrangeSpawns, long[] orangeSpawnJuice) {
        super(instanceId, x, y, w, h, orangeEndHealth, blue1EndHealth, maxHealth, blue1, orange, blue2, numBlueSpawns, blueSpawnJuice, numOrangeSpawns, orangeSpawnJuice);
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



    @Override
    public List<Enemy> spawnBlueEnemies(int numEnemies, VaoCollection enemiesCollection) {
        //TODO, may want to pass in the arraylist rather than creating new arrays
        List<Enemy> enemies = new ArrayList<>();
        for (int i = 0;i<numEnemies;i++){
            int instanceID = enemiesCollection.addInstance();
            enemies.add(new Enemy2(instanceID,false));
            enemies.get(i).setTranslate(this.deltaX +this.width/2,this.deltaY+this.height/2);
        }
        return enemies;
    }

    @Override
    public List<Enemy> spawnOrangeEnemies(int numEnemies, VaoCollection enemiesCollection) {
        //TODO, may want to pass in the arraylist rather than creating new arrays
        List<Enemy> enemies = new ArrayList<>();
        for (int i = 0;i<numEnemies;i++){
            int instanceID = enemiesCollection.addInstance();
            enemies.add(new Enemy2(instanceID,true));
            enemies.get(i).setTranslate(this.deltaX +this.width/2,this.deltaY+this.height/2);
        }
        return enemies;
    }
}
