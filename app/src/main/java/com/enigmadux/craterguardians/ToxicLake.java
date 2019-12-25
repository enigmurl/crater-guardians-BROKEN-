package com.enigmadux.craterguardians;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Animations.ToxicBubble;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Hurts and damages players, bots are immune to it though for now
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class ToxicLake extends EnigmaduxComponent {
    /** How much damage it does at once
     *
     */
    private static final int DAMAGE  = 4;
    /** The damage is discrete, not continuous so this says how many milliseconds before asserting a new damage
     *
     */
    private static final long MILLIS_BETWEEN_DAMAGE = 1000L;

    /** On any given frame the chance that a bubble spawns
     *
     */
    private static final float TOXIC_BUBBLE_CHANCE = 0.07f;
    /** The smallest a bubble can be
     *
     */
    private static final float TOXIC_BUBBLE_MIN_RADIUS = 0.03f;
    /** The largest a bubble can be
     *
     */
    private static final float TOXIC_BUBBLE_MAX_RADIUS = 0.2f;
    /** THe shortest milliseconds an bubble popping can be
     *
     */
    private static final long TOXIC_BUBBLE_MIN_ANIMLEN = 200;
    /** THe longest milliseconds an bubble popping can be
     *
     */
    private static final long TOXIC_BUBBLE_MAX_ANIMLEN = 200;


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

    //the bubbles on the toxic lake, should be an array list later on
    private ArrayList<ToxicBubble> toxicBubbles;

    /** Default Constructor
     *
     * @param x the openGL x coordinate
     * @param y the openGL y coordinate
     * @param width xRadius of the image (radius * 2)
     * @param height yRadius of the image (height * 2)
     */
    public ToxicLake(float x,float y,float width,float height){
        super(x,y,width,height);
        this.x = x;
        this.y = y;
        //super(x-r,y-r,2*r,2*r);
        this.xRadius = width/2;
        this.yRadius = height/2;

        this.toxicBubbles = new ArrayList<ToxicBubble>();

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
        for (ToxicBubble tb:this.toxicBubbles) {
            tb.draw(gl, parentMatrix);
        }
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
        //randomly add a toxic bubles
        if (Math.random() < ToxicLake.TOXIC_BUBBLE_CHANCE){
            double r = Math.min(this.xRadius, Math.random() * (ToxicLake.TOXIC_BUBBLE_MAX_RADIUS - TOXIC_BUBBLE_MIN_RADIUS) + TOXIC_BUBBLE_MIN_RADIUS);
            double magnitude = Math.random() * (this.w/2 - r);
            double angle = Math.random() * 2 * Math.PI;

            double x = magnitude * Math.cos(angle) + this.x;
            double y = magnitude * Math.sin(angle) + this.y;

            long animLength = (long) (Math.random() * (TOXIC_BUBBLE_MAX_ANIMLEN - TOXIC_BUBBLE_MIN_ANIMLEN) + TOXIC_BUBBLE_MIN_ANIMLEN);

            toxicBubbles.add(new ToxicBubble((float) x,(float) y,(float) r*2,(float) r*2,animLength));

        }

        //update the mini bubbles
        Iterator<ToxicBubble> iterator = this.toxicBubbles.iterator();
        while (iterator.hasNext()){
            ToxicBubble tb = iterator.next();
            tb.update(dt);
            if (tb.isFinished()){
                iterator.remove();
            }
        }


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

    /** Used to meet implementation requirements, functionally useless
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return FALSE
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
