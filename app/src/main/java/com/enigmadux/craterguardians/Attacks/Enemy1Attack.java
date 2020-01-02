package com.enigmadux.craterguardians.Attacks;


import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.GameObjects.Supply;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** this whole subdirectory needs javadoc //TODO
 *
 */
public class Enemy1Attack extends Attack {

    //matrix used to scale the default to the desired
    private final float[] finalMatrix = new float[16];

    private final float[] scalarMatrix = new float[16];
    private final float[] translatorMatrix = new float[16];
    private final float[] rotatorMatrix = new float[16];
    private final float[] rotationScalarMatrix = new float[16];
    private final float[] rotationScalarTranslationMatrix = new float[16];


    private float angleRadians;
    private float length;
    private float width;
    private int damage;

    private List<Object> hits= new ArrayList<>();


    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,-0.5f,1,1);


    /** Default constructor
     * @param x openGL x
     * @param y openGL y
     * @param damage how much damage to deal to enemies;
     * @param angleRadians the angle between the start of the sweep and the positive x axis in radians. Zero would mean that half the sweep is above the x axis, and half below
     * @param length how long the attack is in open gl terms
     * @param width how wide the attack is in open gl terms, because orginally it is point ing in the positive x axis, this is originally the height at 0 radians
     * @param millis how long the attack takes to finish
     * @param initializer the Enemy or player who summoned the attack
     */
    public Enemy1Attack(float x, float y, int damage, float angleRadians, float length,float width, long millis,BaseCharacter initializer){
        super(x,y,0,0,5,millis,initializer);


        this.angleRadians = angleRadians;
        this.damage = damage;
        this.length =length;
        this.width = width;

        Matrix.setIdentityM(scalarMatrix, 0);
        Matrix.scaleM(scalarMatrix, 0, length , width, 1);


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





        Matrix.setIdentityM(rotatorMatrix,0);
        Matrix.setIdentityM(translatorMatrix,0);
        Matrix.setIdentityM(rotationScalarMatrix,0);
        Matrix.setIdentityM(rotationScalarTranslationMatrix,0);

        Matrix.rotateM(rotatorMatrix,0,180f/(float) Math.PI * this.angleRadians,0,0,1);
        Matrix.translateM(translatorMatrix,0,this.x,this.y,0);

        Matrix.multiplyMM(rotationScalarMatrix,0,rotatorMatrix,0,scalarMatrix,0);

        Matrix.multiplyMM(rotationScalarTranslationMatrix,0,translatorMatrix,0, rotationScalarMatrix,0);

        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rotationScalarTranslationMatrix, 0);
        VISUAL_REPRESENTATION.draw(gl, finalMatrix);
    }

    /** Loads the texture for all instances of the attack
     *
     * @param gl access to openGL
     * @param context used to access resources
     */
    public static void loadGLTexture(@NonNull GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(gl,context,R.drawable.kaiser_attack_spritesheet);//todo needs work
    }



    @Override
    public boolean isHit(BaseCharacter character) {
        if (this.hits.contains(character)){
            return false;
        }

        float originalXvalue = length * (float) finishedMillis/millis;
        float originalYValue = width/2f;

        float cos = (float) Math.cos(angleRadians);
        float sin = (float) Math.sin(angleRadians);

        float x1 = this.x + cos * originalXvalue - sin * originalYValue;
        float y1 = this.y + sin * originalXvalue + cos * originalYValue;
        float x2 = this.x + cos * originalXvalue + sin * originalYValue;
        float y2 = this.y + sin * originalXvalue - cos * originalYValue;



        if (character.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(character);
            return true;
        }
        return false;
    }

    @Override
    public boolean isHit(Spawner spawner) {
        return false;//because nothing is done, it doesn't matter whether it's true or false, so we just return true to reduce calculations
        /*if (this.hits.contains(spawner)){
            return false;
        }

        float x1 = this.x + (float) Math.cos(angleRadians + SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;
        float y1 = this.y + (float) Math.sin(angleRadians + SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;
        float x2 = this.x + (float) Math.cos(angleRadians - SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;
        float y2 = this.y + (float) Math.sin(angleRadians - SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;

        if (spawner.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(spawner);
            return true;
        }
        return false; */
    }

    @Override
    public boolean isHit(Supply supply) {
        if (this.hits.contains(supply)){
            return false;
        }
        float originalXvalue = length * (float) finishedMillis/millis;
        float originalYValue = width/2f;

        float cos = (float) Math.cos(angleRadians);
        float sin = (float) Math.sin(angleRadians);

        float x1 = this.x + cos * originalXvalue - sin * originalYValue;
        float y1 = this.y + sin * originalXvalue + cos * originalYValue;
        float x2 = this.x + cos * originalXvalue + sin * originalYValue;
        float y2 = this.y + sin * originalXvalue - cos * originalYValue;

        //(oX + oYi) * (cos + sinI) = oxCos - oYSin + cosOYI + sinIOX
        //(oX - oYi) * (cos + sinI) = oxCos + oYSin - cosOYI + sinIOX

        if (supply.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(supply);
            return true;
        }
        return false;
    }

    @Override
    public void onHitPlayer(Player player) {
        float hypotenuse = (float) Math.hypot(player.getDeltaX() - this.x,player.getDeltaY() - this.y);
        player.damage(player.getAttackDamage(this.damage, MathOps.getAngle((this.x - player.getDeltaX())/hypotenuse,(this.y - player.getDeltaY())/hypotenuse)));
    }
    @Override
    public void onHitEnemy(Enemy enemy) {
        //pass nothing needs to be done
    }
    @Override
    public void onHitSpawner(Spawner spawner) {
        //pass nothing is needed
    }

    @Override
    public void onHitSupply(Supply supply){
        supply.damage(damage);
    }

    @Override
    public void onAttackFinished() {
        //nothing needed to be done
    }
}
