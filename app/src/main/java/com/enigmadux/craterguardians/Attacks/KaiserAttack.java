package com.enigmadux.craterguardians.Attacks;


import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Characters.Kaiser;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.GameObjects.Supply;

import java.util.ArrayList;
import java.util.List;

import enigmadux2d.core.shapes.TexturedRect;

/** this whole subdirectory needs javadoc //TODO
 *
 */
public class KaiserAttack extends Attack {
    /** The amount of frames in this animation
     *
     */
    private static int NUM_FRAMES = 8;
    /** The width of the fireballs
     *
     */
    private static float FIREBALL_WIDTH = 0.5f;
    /** The sweep of this attack in radians
     *
     */
    public static float SWEEP_ANGLE = 0.5f;


    //matrix used to scale the default to the desired
    private final float[] finalMatrix = new float[16];
    private final float[] translationMatrix = new float[16];
    private final float[] rotationScalarMatrix = new float[16];
    private final float[] rotationScalarTranslationMatrix = new float[16];


    private float length;
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
     * @param attackAngle the angle between the start of the sweep and the positive x axis in radians. Zero would mean that half the sweep is above the x axis, and half below
     * @param length how long the attack is in open gl terms
     * @param millis how long the attack takes to finish
     * @param initializer the Enemy or player who summoned the attack
     */
    public KaiserAttack(float x, float y, int damage, float attackAngle, float length, long millis, BaseCharacter initializer){
        super(x,y,0,0,NUM_FRAMES,millis,initializer, attackAngle);


        this.damage = damage;
        this.length =length;

        float[] scalarMatrix = new float[16];
        Matrix.setIdentityM(scalarMatrix, 0);
        Matrix.scaleM(scalarMatrix, 0, FIREBALL_WIDTH , FIREBALL_WIDTH, 1);

        float[] rotatorMatrix = new float[16];
        Matrix.setIdentityM(rotatorMatrix,0);
        Matrix.setIdentityM(rotationScalarMatrix,0);
        Matrix.setIdentityM(rotationScalarTranslationMatrix,0);

        Matrix.rotateM(rotatorMatrix,0,180f/(float) Math.PI * this.attackAngle,0,0,1);

        Matrix.multiplyMM(rotationScalarMatrix,0, rotatorMatrix,0, scalarMatrix,0);


    }

    @Override
    public void draw(float[] parentMatrix) {
        if (this.isFinished) return;
        float translationX = MathOps.getTextureBufferTranslationX((int) (this.numFrames * this.finishedMillis/this.millis),numFrames);
        //y translation is always 0
        VISUAL_REPRESENTATION.setTextureDelta(translationX,0);
        //Log.d("TEXTURED:","U: " + translation[0] + " V: " + translation[1]);
        float rad = length * (float) finishedMillis/millis;

        float cos = (float) Math.cos(attackAngle);
        float sin = (float) Math.sin(attackAngle);

        float x = this.x + cos * rad;
        float y = this.y + sin * rad;

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,x,y,0);
        Matrix.multiplyMM(rotationScalarTranslationMatrix,0,translationMatrix,0,rotationScalarMatrix,0);

        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rotationScalarTranslationMatrix, 0);
        VISUAL_REPRESENTATION.draw(finalMatrix);
    }

    /** Loads the texture for all instances of the attack
     *
     * @param context used to access resources*/
    public static void loadGLTexture(Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.enemy1_attack_spritesheet);//todo needs work




        //this rotates it so the textures are turned, perhaps in the future we turn the actual sprite sheet instead
        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                1/(float) NUM_FRAMES,1,
                0,1,
                1/(float) NUM_FRAMES,0,
                0,0,
        });
    }


    @Override
    public boolean isHit(BaseCharacter character) {
        if (this.hits.contains(character)){
            return false;
        }

        float x = this.x + (float) Math.cos(this.attackAngle) * length * (float) finishedMillis/millis;
        float y = this.y + (float) Math.sin(this.attackAngle) * length * (float) finishedMillis/millis;
        if (Math.hypot(x - character.getDeltaX(),y - character.getDeltaY()) <  FIREBALL_WIDTH/2 + character.getRadius()){
            this.hits.add(character);
            //ends the attack
            this.finishedMillis = this.millis + 1;
            this.isFinished = true;
            return true;
        }
        return false;
    }
x
    @Override
    public boolean isHit(Spawner spawner) {
        if (this.hits.contains(spawner)){
            return false;
        }

        float x = this.x + (float) Math.cos(this.attackAngle) * length * (float) finishedMillis/millis;
        float y = this.y + (float) Math.sin(this.attackAngle) * length * (float) finishedMillis/millis;
        if (spawner.collidesWithCircle(x,y,FIREBALL_WIDTH/2)){
            this.hits.add(spawner);
            //ends the attack
            this.finishedMillis = this.millis + 1;
            this.isFinished = true;
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
