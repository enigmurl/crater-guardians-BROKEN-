package com.enigmadux.craterguardians.Enemies;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.Attacks.Enemy1Attack;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;

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
    private static final int FRAMES_PER_ROTATION = 8;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;
    //a constant that represents the maximum health of Enemy1
    private static final int MAXIMUM_HEALTH = 10;


    /** The radius in openGL terms of this enemy
     *
     */
    public static final float CHARACTER_RADIUS = 0.15f;

    //visual is shared by all objects as they all have the same sprite
    private static TexturedRect VISUAL_REPRESENTATION1 = new TexturedRect(-Enemy1.CHARACTER_RADIUS,-Enemy1.CHARACTER_RADIUS,Enemy1.CHARACTER_RADIUS*2,Enemy1.CHARACTER_RADIUS*2);

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
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.enemy1_sprite_sheet);

        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                0,1,
                0,(NUM_ROTATION_ORIENTATIONS-1f)/NUM_ROTATION_ORIENTATIONS,
                1/(float) FRAMES_PER_ROTATION,1,
                1/(float) FRAMES_PER_ROTATION,(NUM_ROTATION_ORIENTATIONS-1f)/NUM_ROTATION_ORIENTATIONS,
        });
    }

    @Override
    public void setFrame(float rotation, int frameNum) {
        float translationX = MathOps.getTextureBufferTranslationX(frameNum,framesPerRotation);
        float translationY = MathOps.getTextureBufferTranslationY(rotation,numRotationOrientations);
        Enemy.VISUAL_REPRESENTATION.setTextureDelta(translationX,translationY);
        //VISUAL_REPRESENTATION.loadTextureBuffer(MathOps.getTextureBuffer(rotation,frameNum,framesPerRotation,numRotationOrientations));
        this.offsetDegrees = MathOps.getOffsetDegrees(rotation,numRotationOrientations);
    }

    @Override
    public void attack(float angle) {
        // for now we are having it only 1 attack at a time
        if (this.attacks.size() == 0) {
            this.attacks.add(new Enemy1Attack(this.getDeltaX(), this.getDeltaY(), 5, angle, 0.7f, 0.1f, 250, this));
        }
    }

    @Override
    public float getCharacterSpeed() {
        return Enemy1.CHARACTER_SPEED;
    }



    /** Draws the enemy, and all sub components
     *
     * @param parentMatrix used to translate from model to world space
     */
    public void drawIntermediate(float[] parentMatrix) {
        //super.draw(gl,parentMatrix);
        //Matrix.setIdentityM(translationMatrix,0);
        //Matrix.translateM(translationMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        Matrix.translateM(finalMatrix,0,parentMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        Matrix.scaleM(finalMatrix,0,Enemy1.CHARACTER_RADIUS,Enemy1.CHARACTER_RADIUS,0);
        //Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationMatrix,0);
        Enemy.VISUAL_REPRESENTATION.draw(finalMatrix);
    }

    /** This tells the maximum health of any character; what to initialize the health to
     *
     * @return the maximum health of the character
     */
    @Override
    public int getMaxHealth() {
        return Enemy1.MAXIMUM_HEALTH;
    }

    /** Gets the radius of this enemy
     *
     * @return the radius of this enemy
     */
    @Override
    public float getRadius() {
        return Enemy1.CHARACTER_RADIUS;
    }

}
