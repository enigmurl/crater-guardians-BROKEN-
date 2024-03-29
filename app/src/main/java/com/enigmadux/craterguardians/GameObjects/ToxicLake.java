package com.enigmadux.craterguardians.GameObjects;

import android.content.Context;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Animations.ToxicBubble;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private static final float TOXIC_BUBBLE_CHANCE = 0.03f;
    /** The smallest a bubble can be
     *
     */
    private static final float TOXIC_BUBBLE_MIN_RADIUS = 0.03f;
    /** The largest a bubble can be
     *
     */
    private static final float TOXIC_BUBBLE_MAX_RADIUS = 0.1f;
    /** THe shortest milliseconds an bubble popping can be
     *
     */
    private static final long TOXIC_BUBBLE_MIN_ANIMLEN = 1200;
    /** THe longest milliseconds an bubble popping can be
     *
     */
    private static final long TOXIC_BUBBLE_MAX_ANIMLEN = 2000;


    //center x of the lake
    private float x;
    //center y of the lake
    private float y;
    //radius of the image (width/2)
    private float radius;

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
    private ArrayList<ToxicBubble> toxicBubbles = new ArrayList<>();

    /** Default Constructor
     *  @param x the openGL x coordinate
     * @param y the openGL y coordinate
     * @param radius the radius of the image
     */
    public ToxicLake(float x, float y, float radius){
        super(x,y,2*radius,2*radius);
        this.x = x;
        this.y = y;
        //super(x-r,y-r,2*r,2*r);
        this.radius = radius;

        //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.x,this.y,0);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,2*radius,2*radius,0);

        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);
    }

    /** Draws the enemy, and all sub components
     *
     * @param parentMatrix used to translate from model to world space
     */
    public void draw(float[] parentMatrix){
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);
        VISUAL_REPRESENTATION.draw(finalMatrix);
        //for (int i = 0, size = this.toxicBubbles.size();i < size; i++) {
        //    toxicBubbles.get(i).draw(gl, parentMatrix);
        //}
    }




    /** Loads the texture
     *
     * @param context context used to load resources, and non null context should work
     */
    public static void loadGLTexture(Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.toxic_lake_texture);
    }

    /** Tries to attack the enemies and the bots
     *
     * @param dt milliseconds since last call
     * @param player the player it attempts to damage
     * @param enemyList all enemies it attempts to damage
     */
    public void update(long dt, Player player,List<Enemy> enemyList){
        //randomly add a toxic bubbles
        if (Math.random() < ToxicLake.TOXIC_BUBBLE_CHANCE){
            double r = Math.min(this.radius, Math.random() * (ToxicLake.TOXIC_BUBBLE_MAX_RADIUS - TOXIC_BUBBLE_MIN_RADIUS) + TOXIC_BUBBLE_MIN_RADIUS);
            double magnitude = Math.random() * (this.w/2 - r);
            double angle = Math.random() * 2 * Math.PI;

            double x = magnitude * Math.cos(angle) + this.x;
            double y = magnitude * Math.sin(angle) + this.y;

            long animLength = (long) (Math.random() * (TOXIC_BUBBLE_MAX_ANIMLEN - TOXIC_BUBBLE_MIN_ANIMLEN) + TOXIC_BUBBLE_MIN_ANIMLEN);

            this.toxicBubbles.add(new ToxicBubble((float) x,(float) y,(float) r*2,(float) r*2,animLength));

        }

        //update the mini bubbles
        Iterator<ToxicBubble> toxicBubbleIterator = this.toxicBubbles.iterator();
        while (toxicBubbleIterator.hasNext()){
            ToxicBubble toxicBubble = toxicBubbleIterator.next();
            if (toxicBubble.isFinished()){
                toxicBubbleIterator.remove();
            }
            toxicBubble.update(dt);
        }


        currentMillis += dt;
        if (currentMillis > MILLIS_BETWEEN_DAMAGE) {
            currentMillis = 0;
        }

        if (currentMillis == 0) {
            for (Enemy enemy : enemyList) {
                if (enemy == null) continue;
                if (Math.hypot(enemy.getDeltaX() - this.x, enemy.getDeltaY() - this.y) < this.radius + enemy.getWidth() / 2) {
                    enemy.damage(DAMAGE);
                }
            }
        }

        if (Math.hypot(player.getDeltaX() - this.x,player.getDeltaY() - this.y) < this.radius + player.getWidth()/2){
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
