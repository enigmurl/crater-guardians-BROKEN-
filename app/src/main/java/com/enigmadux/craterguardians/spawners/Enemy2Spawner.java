package com.enigmadux.craterguardians.spawners;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.enemies.Enemy2;
import com.enigmadux.craterguardians.gamelib.CraterCollection;

/** Spawns enemy 1
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy2Spawner extends Spawner {

    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];



    public Enemy2Spawner(Context context,int instanceId, float x, float y, float w, float h, int orangeEndHealth, int blue1EndHealth, int maxHealth, long blue1, long orange, long blue2, short[] numBlueSpawns, long[] blueSpawnJuice, short[] numOrangeSpawns, long[] orangeSpawnJuice,int strength) {
        super(context,instanceId, x, y, w, h, orangeEndHealth, blue1EndHealth, maxHealth, blue1, orange, blue2, numBlueSpawns, blueSpawnJuice, numOrangeSpawns, orangeSpawnJuice,strength);  //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.deltaX ,this.deltaY,0);

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
    public void spawnBlueEnemies(int numEnemies, CraterCollection<Enemy> blueEnemies) {
        for (int i = 0;i<numEnemies;i++){
            float cos = (numEnemies == 1) ? 0 : (float) Math.cos(Math.PI * 2* i / numEnemies);
            float sin = (numEnemies == 1) ? 0 : (float) Math.sin(Math.PI * 2 * i / numEnemies);
            float x = this.deltaX + (this.width/2 * (cos));
            float y = this.deltaY + (this.height/2 * (sin));
            int id = blueEnemies.getVertexData().addInstance();
            blueEnemies.getInstanceData().add(new Enemy2(id,x,y,true,strength));
        }
    }

    @Override
    public void spawnOrangeEnemies(int numEnemies, CraterCollection<Enemy> orangeEnemies) {
        for (int i = 0;i<numEnemies;i++){
            float cos = (numEnemies == 1) ? 0 : (float) Math.cos(Math.PI * 2* i / numEnemies);
            float sin = (numEnemies == 1) ? 0 : (float) Math.sin(Math.PI * 2 * i / numEnemies);
            float x = this.deltaX + (this.width/2 * (cos));
            float y = this.deltaY + (this.height/2 * (sin));
            int id = orangeEnemies.getVertexData().addInstance();
            orangeEnemies.getInstanceData().add(new Enemy2(id,x,y,false,strength));
        }
    }
}