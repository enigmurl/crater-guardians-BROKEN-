package com.enigmadux.craterguardians.Enemies;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.Attacks.Enemy2Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.Plateau;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Supply;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** The second type of enemy. todo javadoc
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy2 extends Enemy {
    //a constant that represents how fast the character is, right now there aren't any particular units which needs to change (see todo)
    private static final float CHARACTER_SPEED = 2.5f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 16;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;
    //a constant that represents the maximum health of Enemy2
    private static final int MAXIMUM_HEALTH = 20;



    /** The radius in openGL terms of this enemy
     *
     */
    public static final float CHARACTER_RADIUS = 0.3f;

    //visual is shared by all objects as they all have the same sprite
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-Enemy2.CHARACTER_RADIUS,-Enemy2.CHARACTER_RADIUS,Enemy2.CHARACTER_RADIUS*2,Enemy2.CHARACTER_RADIUS*2);


    //translates the Character according to delta x and delta y
    private float[] translationMatrix = new float[16];

    //parent matrix * translation matrix
    private float[] finalMatrix = new float[16];

    /** Default Constructor
     *
     */
    public Enemy2(){
        super(NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(@NonNull GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(gl,context,R.drawable.enemy1_sprite_sheet);//todo

    }

    @Override
    public void setFrame(float rotation, int frameNum) {
        //VISUAL_REPRESENTATION.loadTextureBuffer(MathOps.getTextureBuffer(rotation,frameNum,framesPerRotation,numRotationOrientations));
        this.offsetDegrees = MathOps.getOffsetDegrees(rotation,numRotationOrientations);
    }

    @Override
    public void attack(float angle) {
        //only 1 alive attack for enemies
        this.attacks[0] = new Enemy2Attack(this.getDeltaX(),this.getDeltaY(),25, angle,0.5f + Enemy2.CHARACTER_RADIUS * 2,0.2f,1000,this);
    }

    @Override
    public float getCharacterSpeed() {
        return Enemy2.CHARACTER_SPEED;
    }


    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param supplies  all alive supplies on the map
     * @param enemyMap A map of where and how the enemy should go
     */
    @Override
    public void update(long dt, BaseCharacter player, Supply[] supplies, EnemyMap enemyMap) {
        if (this.attacks[0] == null){
            this.canMove = true;
        } else {
            this.canMove = this.attacks[0].isFinished();
        }

        super.update(dt, player,supplies,enemyMap);


    }


    /** Draws the enemy, and all sub components
     *
     * @param gl used to access openGL
     * @param parentMatrix used to translate from model to world space
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        super.draw(gl,parentMatrix);
        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.getDeltaX(),this.getDeltaY(),0);

        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationMatrix,0);
        VISUAL_REPRESENTATION.draw(gl,finalMatrix);
    }

    /** This tells the maximum health of any character; what to initialize the health to
     *
     * @return the maximum health of the character
     */
    @Override
    public int getMaxHealth() {
        return Enemy2.MAXIMUM_HEALTH;
    }

    /** Gets the radius of this enemy
     *
     * @return the radius of this enemy
     */
    @Override
    public float getRadius() {
        return Enemy2.CHARACTER_RADIUS;
    }

}
