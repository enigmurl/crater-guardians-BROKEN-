package com.enigmadux.craterguardians.gameobjects;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.players.Player;


/** Hurts and damages players, bots are immune to it though for now
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class ToxicLake extends CraterCollectionElem {
    /** How much damage it does at once
     *
     */
    private static final int DAMAGE  = 10;
    /** The damage is discrete, not continuous so this says how many milliseconds before asserting a new damage
     *
     */
    private static final long MILLIS_BETWEEN_DAMAGE = 1000L;



    //radius of the image (width/2)
    private float radius;

    //the amount of millis since the last damage
    private long currentMillis;


    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];

    /** Default Constructor
     *
     * @param instanceID the id of the instance with respect to the Vao it's sitting in
     *  @param x the openGL deltX coordinate
     * @param y the openGL y coordinate
     * @param radius the radius of the image
     */
    public ToxicLake(int instanceID,float x, float y, float radius){
        super(instanceID);
        this.deltaX = x;
        this.deltaY = y;
        //super(deltX-r,y-r,2*r,2*r);
        this.radius = radius;

        //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.deltaX,this.deltaY,0);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,2*radius,2*radius,0);

        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);
    }


    /** Tries to attack the enemies and the bots
     *
     * @param dt milliseconds since last call
     */
    public void update(long dt, World world){

        currentMillis += dt;
        if (currentMillis > MILLIS_BETWEEN_DAMAGE) {
            currentMillis = 0;
        }

        if (currentMillis == 0) {
            for (Enemy enemy : world.getBlueEnemies().getInstanceData()){
                if (enemy == null) continue;
                if (Math.hypot(enemy.getDeltaX() - this.deltaX, enemy.getDeltaY() - this.deltaY) < this.radius + enemy.getRadius()) {
                    enemy.damage(DAMAGE);
                }
            }
            for (Enemy enemy : world.getOrangeEnemies().getInstanceData()){
                if (enemy == null) continue;
                if (Math.hypot(enemy.getDeltaX() - this.deltaX, enemy.getDeltaY() - this.deltaY) < this.radius + enemy.getRadius()) {
                    enemy.damage(DAMAGE);
                }
            }
        }

        Player player = world.getPlayer();
        if (Math.hypot(player.getDeltaX() - this.deltaX,player.getDeltaY() - this.deltaY) < this.radius + player.getRadius()){
            if (currentMillis == 0) {
                player.damage(DAMAGE);
                SoundLib.playToxicLakeTickSoundEffect();
            }
            if (! player.getActiveLakes().contains(this)){
                player.getActiveLakes().add(this);
            }
        } else {
            //checks if it's there internally
            player.getActiveLakes().remove(this);
        }

    }

    /** Updates the matrix info into the instance info. However, OUTSIDE CLASSES SHOULD NOT CALL THIS METHOD. It's only for use by super classes.
     * Instead, call updateInstanceInfo
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
        Matrix.multiplyMM(blankInstanceInfo,0,uMVPMatrix,0,this.translationScalarMatrix,0);
    }


    public boolean intersectsCharacter(Character c) {
        return Math.hypot(c.getDeltaX() - this.deltaX,c.getDeltaY() - this.deltaY) < this.radius + c.getRadius();
    }
}
