package com.enigmadux.craterguardians.Attacks;


import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Characters.Kaiser;
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
public class KaiserE1Attack extends Attack {

    //matrix used to scale the default to the desired
    private final float[] finalMatrix = new float[16];

    private final float[] scalarMatrix = new float[16];
    private final float[] translatorMatrix = new float[16];
    private final float[] rotatorMatrix = new float[16];
    private float[] rotationScalarTranslationMatrix = new float[16];


    private float angleRadians;
    private float length;
    private int damage;


    /** angle in radians, for how wide the sweep is.
     *
     */
    public static final float SWEEP_ANGLE = 1.0f;

    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,0,0,0);

    /** This configures the visual representation to a triangle
     *
     */
    static {

        VISUAL_REPRESENTATION.loadVertexBuffer(new float[]{
                0,0,0,
                1,0.5f,0,
                0,0,0,
                1,-0.5f,0
        });
    }

    /** Default constructor
     * @param x openGL x
     * @param y openGL y
     * @param damage how much damage to deal to enemies;
     * @param angleRadians the angle between the start of the sweep and the positive x axis in radians. Zero would mean that half the sweep is above the x axis, and half below
     * @param length how long the attack is in open gl terms (radius)
     * @param millis how long the attack takes to finish
     * @param initializer the Enemy or player who summoned the attack
     */
    public KaiserE1Attack(float x, float y, int damage, float angleRadians, float length, long millis, Kaiser initializer){
        super(x,y,0,0,5,millis,initializer);


        this.angleRadians = angleRadians;
        this.damage = damage;
        this.length = length;

        Matrix.setIdentityM(scalarMatrix, 0);
        Matrix.scaleM(scalarMatrix, 0, length * (float) Math.cos(SWEEP_ANGLE / 2f), 2 * length * (float) Math.sin(SWEEP_ANGLE / 2f), 1);

    }

    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        float x1 = ((int) (this.finishedMillis/(this.millis/this.numFrames)) * 1f/this.numFrames) %1 ;
        float x2 = (((int) (this.finishedMillis/(this.millis/this.numFrames)) * 1f/this.numFrames) %1 + 1f/numFrames);

        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                x1,1,
                x1,0,
                x2,1,
                x2,0

        });
        Matrix.setIdentityM(rotatorMatrix,0);
        Matrix.setIdentityM(translatorMatrix,0);
        Matrix.setIdentityM(rotationScalarTranslationMatrix,0);

        Matrix.rotateM(rotatorMatrix,0,180f/(float) Math.PI * this.angleRadians,0,0,1);
        Matrix.translateM(translatorMatrix,0,this.x,this.y,0);

        Matrix.multiplyMM(rotationScalarTranslationMatrix,0,rotatorMatrix,0,scalarMatrix,0);
        Matrix.multiplyMM(rotationScalarTranslationMatrix,0,translatorMatrix,0,rotationScalarTranslationMatrix,0);

        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rotationScalarTranslationMatrix, 0);
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



    @Override
    public boolean isHit(BaseCharacter character) {
        if (this.hits.contains(character)){
            return false;
        }
        float x1 = this.x + (float) Math.cos(angleRadians + SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;
        float y1 = this.y + (float) Math.sin(angleRadians + SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;
        float x2 = this.x + (float) Math.cos(angleRadians - SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;
        float y2 = this.y + (float) Math.sin(angleRadians - SWEEP_ANGLE/2) * length * (float) finishedMillis/millis;


        if (character.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(character);
            return true;
        }
        return false;
    }

    @Override
    public boolean isHit(Spawner spawner) {
        if (this.hits.contains(spawner)){
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
        ((Kaiser) initializer).gainEvolveCharge(this.damage);
    }


    @Override
    public void onAttackFinished() {
        if (hits.size() == 0) {
            ((Kaiser) initializer).failedAttack();
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
