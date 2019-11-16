package com.enigmadux.craterguardians;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Hurts and damages players, bots are immune to it though for now
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class ToxicLake {
    /** How much damage it does at once
     *
     */
    private static final int DAMAGE  = 4;
    /** The damage is discrete, not continuous so this says how many milliseconds before asserting a new damage
     *
     */
    private static final long MILLIS_BETWEEN_DAMAGE = 1000L;

    //center x of the lake
    private float x;
    //center y of the lake
    private float y;
    //xRadius of the image (width/2)
    private float xRadius;
    //yRadius of the image (height/2)
    private float yRadius;

    //the amount of millis since the last damage
    private long currentMillis;

    //parentMatrix*translationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];

    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-0.5f,-0.5f,1,1);

    /** Default Constructor
     *
     * @param x the openGL x coordinate
     * @param y the openGL y coordinate
     * @param xRadius xRadius of the image (width/2)
     * @param yRadius yRadius of the image (height/2)
     */
    public ToxicLake(float x,float y,float xRadius,float yRadius){
        this.x = x;
        this.y = y;
        //super(x-r,y-r,2*r,2*r);
        this.xRadius = xRadius;
        this.yRadius = yRadius;

        //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.x,this.y,0);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,2*xRadius,2*yRadius,0);

        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);
    }

    /** Draws the toxic lake onto the screen
     *
     * @param gl used to access openGL
     * @param parentMatrix describes how to transform from model to view
     */
    public void draw(GL10 gl,float[] parentMatrix){
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);
        VISUAL_REPRESENTATION.draw(gl,finalMatrix);
    }

    /** Loads the texture
     *
     * @param gl access to openGL
     * @param context context used to load resources, and non null context should work
     */
    public static void loadGLTexture(@NonNull GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(gl,context,R.drawable.toxic_lake_texture);
    }

    /** Tries to attack the enemies and the bots
     *
     * @param dt milliseconds since last call
     * @param player the player it attempts to damage
     * @param enemyList all enemies it attempts to damage
     */
    public void update(long dt, Player player, List<Enemy> enemyList){
        currentMillis += dt;
        if (currentMillis > MILLIS_BETWEEN_DAMAGE) {
            currentMillis = 0;
        }

        List<float[]> collisions;
        for (Enemy enemy: enemyList){
            collisions = enemy.getCollisionsWithLine(enemy.getDeltaX(),enemy.getDeltaY(),this.x,this.y);
            if (collisions.size() == 0 || Math.pow((collisions.get(0)[0]-this.x),2)/(xRadius*xRadius) + Math.pow((collisions.get(0)[1]-this.y),2)/(yRadius*yRadius) < 1){
                if (currentMillis == 0) {
                    enemy.damage(DAMAGE);
                }
            }
        }
        collisions = player.getCollisionsWithLine(player.getDeltaX(),player.getDeltaY(),this.x,this.y);
        if (collisions.size() == 0 || Math.pow((collisions.get(0)[0]-this.x),2)/(xRadius*xRadius) + Math.pow((collisions.get(0)[1]-this.y),2)/(yRadius*yRadius) < 1){
            if (currentMillis == 0) {
                player.damage(DAMAGE);
                SoundLib.playToxicLakeTickSoundEffect();
            }
            player.addSpeedEffect(0.2f,200);//todo HARDCODED
        }

    }
}
