package com.enigmadux.craterguardians.Enemies;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.Attacks.Enemy3Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.GameObjects.Supply;

import java.util.List;

import enigmadux2d.core.shapes.TexturedRect;

/** The second type of enemy. Is a sort of "Boss" todo javado
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy3 extends Enemy {
    //a constant that represents how fast the character is, right now there aren't any particular units which needs to change (see todo)
    private static final float CHARACTER_SPEED = 0.2f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 8;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;
    //a constant that represents the maximum health of Enemy3
    private static final int MAXIMUM_HEALTH = 100;
    //a constant that represents the attack range of this enemy
    private static final float ATTACK_RANGE = 2f;



    /** The radius in openGL terms of this enemy
     *
     */
    public static final float CHARACTER_RADIUS = 0.5f;

    //parent matrix * translation matrix
    private final float[] finalMatrix = new float[16];

    /** Default Constructor
     *
     */
    public Enemy3(){
        super(NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    /** Loads the texture of the sprite sheet
     *
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.enemy1_sprite_sheet);//todo
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
        VISUAL_REPRESENTATION.setTextureDelta(translationX,translationY);
        this.offsetDegrees = MathOps.getOffsetDegrees(rotation,numRotationOrientations);
    }

    @Override
    public void attack(float angle) {
        if (this.attacks.size() == 0) {
            this.attacks.add(new Enemy3Attack(this.getDeltaX(), this.getDeltaY(), 99, angle, 0.5f + Enemy3.CHARACTER_RADIUS * 2, 5000, this));
        }
    }

    @Override
    public float getCharacterSpeed() {
        return Enemy3.CHARACTER_SPEED;
    }


    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param supplies  all alive supplies on the map
     * @param enemyMap A map of where and how the enemy should go
     */
    @Override
    public void update(long dt, BaseCharacter player, List<Supply> supplies, EnemyMap enemyMap) {
        if (this.attacks.size() == 0){
            this.canMove = true;
        } else {
            this.canMove = this.attacks.get(0).isFinished();
        }

        super.update(dt, player,supplies,enemyMap);


    }


    /** Draws the enemy, Sub components are drawn by the superclass draw method
     *
     * @param parentMatrix used to translate from model to world space
     */
    public void drawIntermediate(float[] parentMatrix) {
        //super.draw(gl,parentMatrix);
        //Matrix.setIdentityM(translationMatrix,0);
        //Matrix.translateM(translationMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        Matrix.translateM(finalMatrix,0,parentMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        Matrix.scaleM(finalMatrix,0,Enemy3.CHARACTER_RADIUS,Enemy3.CHARACTER_RADIUS,0);
        Enemy.VISUAL_REPRESENTATION.setShader(this.shader[0],this.shader[1],this.shader[2],this.shader[3]);
        //Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationMatrix,0);
        VISUAL_REPRESENTATION.draw(finalMatrix);
    }
    /** This tells the maximum health of any character; what to initialize the health to
     *
     * @return the maximum health of the character
     */
    @Override
    public int getMaxHealth() {
        return Enemy3.MAXIMUM_HEALTH;
    }

    /** Gets the radius of this enemy
     *
     * @return the radius of this enemy
     */
    @Override
    public float getRadius() {
        return Enemy3.CHARACTER_RADIUS;
    }

    /** Gets the speed of the enemy
     *
     * @return the spped of this enemy
     */
    @Override
    public float getSpeed() {
        return Enemy3.CHARACTER_SPEED;
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
