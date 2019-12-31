package com.enigmadux.craterguardians.Attacks;


import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Characters.Ryze;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.Supply;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** this whole subdirectory needs javadoc //TODO
 *
 */
public class RyzeAttack extends Attack {

    //matrix used to scale the default to the desired
    private final float[] finalMatrix = new float[16];

    private final float[] leftAttackM = new float[16];
    private final float[] middleAttackM = new float[16];
    private final float[] rightAttackM = new float[16];


    private float angleRadians;
    private float mainLength;
    private float mainWidth;
    private float sideLength;
    private float sideWidth;
    private int damage;


    private boolean mainAttackFinished = false;
    private boolean rightAttackFinished = false;
    private boolean leftAttackFinished = false;



    public static final float SWEEP_ANGLE = 1.5f;

    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,-0.5f,1,1);


    /** Default constructor
     * @param x openGL x
     * @param y openGL y
     * @param damage how much damage to deal to enemies;
     * @param angleRadians the angle between the start of the sweep and the positive x axis in radians. Zero would mean that half the sweep is above the x axis, and half below
     * @param mainLength how long the middle attack is in open gl terms (radius)
     * @param mainWidth how wide the middle attack is in open gl terms (radius)
     * @param sideLength how long the side attacks are in open gl terms
     * @param sideWidth how wide the side attacks are in open gl terms
     * @param millis how long the attack takes to finish
     * @param initializer the Enemy or player who summoned the attack
     */
    public RyzeAttack(float x, float y, int damage, float angleRadians, float mainLength, float mainWidth, float sideLength, float sideWidth, long millis, Ryze initializer){
        super(x,y,0,0,5,millis,initializer);


        this.angleRadians = angleRadians;
        this.damage = damage;
        this.mainLength = mainLength;
        this.mainWidth = mainWidth;
        this.sideLength = sideLength;
        this.sideWidth = sideWidth;

        Matrix.setIdentityM(leftAttackM,0);
        Matrix.translateM(leftAttackM,0,this.x,this.y,0);
        Matrix.rotateM(leftAttackM,0,180f/(float) Math.PI * (RyzeAttack.SWEEP_ANGLE/2 + angleRadians),0,0,1);
        Matrix.scaleM(leftAttackM,0,sideLength,sideWidth,0);


        Matrix.setIdentityM(rightAttackM,0);
        Matrix.translateM(rightAttackM,0,this.x,this.y,0);
        Matrix.rotateM(rightAttackM,0,180f/(float) Math.PI * (-RyzeAttack.SWEEP_ANGLE/2 + angleRadians),0,0,1);
        Matrix.scaleM(rightAttackM,0,sideLength,sideWidth,0);

        Matrix.setIdentityM(middleAttackM,0);
        Matrix.translateM(middleAttackM,0,this.x,this.y,0);
        Matrix.rotateM(middleAttackM,0,180f/(float) Math.PI * ( angleRadians),0,0,1);
        Matrix.scaleM(middleAttackM,0,mainLength,mainWidth,0);

    }

    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        float x1 = ((int) (this.finishedMillis/(this.millis/this.numFrames)) * 1f/this.numFrames) %1 ;
        float x2 = (((int) (this.finishedMillis/(this.millis/this.numFrames)) * 1f/this.numFrames) %1 + 1f/numFrames);

        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                x1,1,
                x2,1,
                x1,0,
                x2,0

        });

        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, leftAttackM, 0);
        VISUAL_REPRESENTATION.draw(gl, finalMatrix);


        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rightAttackM, 0);
        VISUAL_REPRESENTATION.draw(gl, finalMatrix);

        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, middleAttackM, 0);
        VISUAL_REPRESENTATION.draw(gl, finalMatrix);

    }

    /** Loads the texture for all instances of the attack
     *
     * @param gl access to openGL
     * @param context used to access resources
     */
    public static void loadGLTexture(@NonNull GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(gl,context,R.drawable.kaiser_attack_spritesheet);
    }


    //todo can be optimized
    @Override
    public boolean isHit(BaseCharacter character) {
        if (this.hits.contains(character)){
            return false;
        }
        if (! mainAttackFinished) {
            float originalXvalue = mainLength * (float) finishedMillis / millis;
            float originalYValue = mainWidth / 2f;

            float cos = (float) Math.cos(angleRadians);
            float sin = (float) Math.sin(angleRadians);

            float x1 = this.x + cos * originalXvalue - sin * originalYValue;
            float y1 = this.y + sin * originalXvalue + cos * originalYValue;
            float x2 = this.x + cos * originalXvalue + sin * originalYValue;
            float y2 = this.y + sin * originalXvalue - cos * originalYValue;
            if (character.collidesWithLine(x1, y1, x2, y2)) {
                this.hits.add(character);
                mainAttackFinished = true;
                return true;
            }
        }
        if (! leftAttackFinished){
            float originalXvalue = sideLength * (float) finishedMillis / millis;
            float originalYValue = sideWidth / 2f;

            float cos = (float) Math.cos(angleRadians + SWEEP_ANGLE/2);
            float sin = (float) Math.sin(angleRadians + SWEEP_ANGLE/2);

            float x1 = this.x + cos * originalXvalue - sin * originalYValue;
            float y1 = this.y + sin * originalXvalue + cos * originalYValue;
            float x2 = this.x + cos * originalXvalue + sin * originalYValue;
            float y2 = this.y + sin * originalXvalue - cos * originalYValue;
            if (character.collidesWithLine(x1, y1, x2, y2)) {
                this.hits.add(character);
                leftAttackFinished = true;
                return true;
            }
        }
        if (! rightAttackFinished){
            float originalXvalue = sideLength * (float) finishedMillis / millis;
            float originalYValue = sideWidth / 2f;

            float cos = (float) Math.cos(angleRadians - SWEEP_ANGLE/2);
            float sin = (float) Math.sin(angleRadians - SWEEP_ANGLE/2);

            float x1 = this.x + cos * originalXvalue - sin * originalYValue;
            float y1 = this.y + sin * originalXvalue + cos * originalYValue;
            float x2 = this.x + cos * originalXvalue + sin * originalYValue;
            float y2 = this.y + sin * originalXvalue - cos * originalYValue;
            if (character.collidesWithLine(x1, y1, x2, y2)) {
                this.hits.add(character);
                rightAttackFinished = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isHit(Spawner spawner) {
        if (this.hits.contains(spawner)){
            return false;
        }

        if (! mainAttackFinished) {
            float originalXvalue = mainLength * (float) finishedMillis / millis;
            float originalYValue = mainWidth / 2f;

            float cos = (float) Math.cos(angleRadians);
            float sin = (float) Math.sin(angleRadians);

            float x1 = this.x + cos * originalXvalue - sin * originalYValue;
            float y1 = this.y + sin * originalXvalue + cos * originalYValue;
            float x2 = this.x + cos * originalXvalue + sin * originalYValue;
            float y2 = this.y + sin * originalXvalue - cos * originalYValue;
            if (spawner.collidesWithLine(x1, y1, x2, y2)) {
                this.hits.add(spawner);
                mainAttackFinished = true;
                return true;
            }
        }
        if (! leftAttackFinished){
            float originalXvalue = sideLength * (float) finishedMillis / millis;
            float originalYValue = sideWidth / 2f;

            float cos = (float) Math.cos(angleRadians + SWEEP_ANGLE/2);
            float sin = (float) Math.sin(angleRadians + SWEEP_ANGLE/2);

            float x1 = this.x + cos * originalXvalue - sin * originalYValue;
            float y1 = this.y + sin * originalXvalue + cos * originalYValue;
            float x2 = this.x + cos * originalXvalue + sin * originalYValue;
            float y2 = this.y + sin * originalXvalue - cos * originalYValue;
            if (spawner.collidesWithLine(x1, y1, x2, y2)) {
                this.hits.add(spawner);
                leftAttackFinished = true;
                return true;
            }
        }
        if (! rightAttackFinished){
            float originalXvalue = sideLength * (float) finishedMillis / millis;
            float originalYValue = sideWidth / 2f;

            float cos = (float) Math.cos(angleRadians - SWEEP_ANGLE/2);
            float sin = (float) Math.sin(angleRadians - SWEEP_ANGLE/2);

            float x1 = this.x + cos * originalXvalue - sin * originalYValue;
            float y1 = this.y + sin * originalXvalue + cos * originalYValue;
            float x2 = this.x + cos * originalXvalue + sin * originalYValue;
            float y2 = this.y + sin * originalXvalue - cos * originalYValue;
            if (spawner.collidesWithLine(x1, y1, x2, y2)) {
                this.hits.add(spawner);
                rightAttackFinished = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isHit(Supply supply) {
        return false;//doesn't matter if it's hit so don't waste calculation time on it
    }

    @Override
    public void onHitSpawner(Spawner spawner) {
        spawner.damage(this.damage);
    }
    @Override
    public void onHitEnemy(Enemy enemy) {
        enemy.damage(this.damage);
        ((Ryze) initializer).gainEvolveCharge(this.damage);
    }


    @Override
    public void onAttackFinished() {
        this.leftAttackFinished = true;
        this.rightAttackFinished = true;
        this.mainAttackFinished = true;
        if (hits.size() == 0) {
            ((Ryze) initializer).failedAttack();
        }
    }

    @Override
    public void onHitSupply(Supply supply) {
        //pass nothing needs to be done
    }

    @Override
    public void onHitPlayer(Player player) {
        //nothing needs to be done
    }

}