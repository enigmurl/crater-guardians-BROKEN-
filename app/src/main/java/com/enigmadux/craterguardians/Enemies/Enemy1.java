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
    private static final float CHARACTER_SPEED = 0.6f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    public static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    public static final int FRAMES_PER_ROTATION = 8;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;
    //a constant that represents the maximum health of Enemy1
    private static final int MAXIMUM_HEALTH = 30;
    //a constant that represents the attack range of this enemy
    private static final float ATTACK_RANGE = 2f;


    /** The radius in openGL terms of this enemy
     *
     */
    public static final float CHARACTER_RADIUS = 0.15f;

    /** Default Constructor
     *
     */
    public Enemy1(int instanceID){
        super(instanceID,NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    @Override
    public void setFrame(float rotation, int frameNum) {
        this.deltaTextureX =  MathOps.getTextureBufferTranslationX(frameNum,framesPerRotation);
        this.deltaTextureY = MathOps.getTextureBufferTranslationY(rotation,numRotationOrientations);
        this.offsetDegrees = MathOps.getOffsetDegrees(rotation,numRotationOrientations);
    }

    public void setRotation(float rotation){

    }

    @Override
    public void attack(float angle) {
        // for now we are having it only 1 attack at a time
        if (this.attacks.size() == 0) {
            this.attacks.add(new Enemy1Attack(this.getDeltaX(), this.getDeltaY(), 5, angle, ATTACK_RANGE, 0.2f, 250));
        }
    }

    @Override
    public float getCharacterSpeed() {
        return Enemy1.CHARACTER_SPEED;
    }



    /** Updates the transform
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
        Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        Matrix.scaleM(blankInstanceInfo,0,2 * Enemy1.CHARACTER_RADIUS,2 * Enemy1.CHARACTER_RADIUS,0);
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

    /** Gets the speed of the enemy
     *
     * @return the spped of this enemy
     */
    @Override
    public float getSpeed() {
        return Enemy1.CHARACTER_SPEED;
    }

    /** Gets the attack range of this enemy
     *
     * @return the attack range of this enemy (how far it can shoot
     */
    @Override
    public float getAttackRange(){
        return ATTACK_RANGE;
    }
}
