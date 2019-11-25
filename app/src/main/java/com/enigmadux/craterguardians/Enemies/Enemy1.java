package com.enigmadux.craterguardians.Enemies;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.Attacks.Enemy1Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.Plateau;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Supply;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** The first type of enemy. todo javadoc
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy1 extends Enemy {
    //a constant that represents how fast the character is, right now there aren't any particular units which needs to change (see todo)
    private static final float CHARACTER_SPEED = 5f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 16;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;
    //a constant that represents the maximum health of Enemy1
    private static final int MAXIMUM_HEALTH = 10;


    /** The width in openGL terms of any enemy
     *
     */
    public static final float CHARACTER_WIDTH = 0.3f;
    /** The height in openGL terms of any enemy
     *
     */
    public static final float CHARACTER_HEIGHT = 0.3f;
    //visual is shared by all objects as they all have the same sprite
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-Enemy1.CHARACTER_WIDTH/2,-Enemy1.CHARACTER_HEIGHT/2,Enemy1.CHARACTER_WIDTH,Enemy1.CHARACTER_HEIGHT);


    //translates the Character according to delta x and delta y
    private float[] translationMatrix = new float[16];

    //parent matrix * translation matrix
    private float[] finalMatrix = new float[16];

    /** Default Constructor
     *
     */
    public Enemy1(){
        super(NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(@NonNull GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(gl,context,R.drawable.enemy1_sprite_sheet);

    }

    @Override
    public void setFrame(float rotation, int frameNum) {
        VISUAL_REPRESENTATION.loadTextureBuffer(MathOps.getTextureBuffer(rotation,frameNum,framesPerRotation,numRotationOrientations));
        this.offsetDegrees = MathOps.getOffsetDegrees(rotation,numRotationOrientations);
    }

    @Override
    public void attack(float angle) {
        this.attacks.add(new Enemy1Attack(this.getDeltaX(),this.getDeltaY(),5, angle,0.5f,250,this));
    }

    @Override
    public float getCharacterSpeed() {
        return Enemy1.CHARACTER_SPEED;
    }


    /** Updates the position
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     */
    @Override
    public void update(long dt, BaseCharacter player, List<Supply> supplies, List<Plateau> plateaus){
        super.update(dt, player,supplies,plateaus);


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
        return Enemy1.MAXIMUM_HEALTH;
    }

    /** Gets the width of this enemy
     *
     * @return the width of this enemy
     */
    @Override
    public float getW() {
        return Enemy1.CHARACTER_WIDTH;
    }

    /** Gets the height of this enemy
     *
     * @return the height of this type of enemy
     */
    @Override
    public float getH() {
        return Enemy1.CHARACTER_HEIGHT;
    }
}
