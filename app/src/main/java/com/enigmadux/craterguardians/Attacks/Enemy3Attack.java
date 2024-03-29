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
 * This attack is a hammer swing, it only deals damage at the last swing
 *
 *TODO: in the future perhaps make this a spiral like attack which steps up in radius x amount of times
 */
public class Enemy3Attack extends Attack {


    /** The amount of frames in this animation
     *
     */
    private static int NUM_FRAMES = 5;
    //matrix used to scale the default to the desired

    private final float[] finalMatrix = new float[16];

    private final float[] scalarMatrix = new float[16];
    private final float[] translatorMatrix = new float[16];
    private final float[] rotatorMatrix = new float[16];
    private final float[] rotationScalarMatrix = new float[16];
    private final float[] rotationScalarTranslationMatrix = new float[16];


    private float radius;
    private int damage;

    private List<Object> hits= new ArrayList<>();


    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-0.5f,-0.5f,1,1);


    /** Default constructor
     * @param x openGL x
     * @param y openGL y
     * @param damage how much damage to deal to enemies;
     * @param attackAngle this is the angle in radians at which the attacks starts
     * @param radius how long the attack is in open gl terms
     * @param millis how long the attack takes to finish
     * @param initializer the Enemy or player who summoned the attack
     */
    public Enemy3Attack(float x, float y, int damage, float attackAngle, float radius, long millis, BaseCharacter initializer){
        super(x,y,0,0,NUM_FRAMES,millis,initializer, attackAngle);


        this.damage = damage;
        this.radius =radius;

        Matrix.setIdentityM(scalarMatrix, 0);
        Matrix.scaleM(scalarMatrix, 0, radius *2, radius*2, 1);
        Matrix.setIdentityM(rotatorMatrix,0);
        Matrix.setIdentityM(translatorMatrix,0);
        Matrix.setIdentityM(rotationScalarMatrix,0);
        Matrix.setIdentityM(rotationScalarTranslationMatrix,0);

        Matrix.rotateM(rotatorMatrix,0,180f/(float) Math.PI * this.attackAngle,0,0,1);
        Matrix.translateM(translatorMatrix,0,this.x,this.y,0);

        Matrix.multiplyMM(rotationScalarMatrix,0,rotatorMatrix,0,scalarMatrix,0);

        Matrix.multiplyMM(rotationScalarTranslationMatrix,0,translatorMatrix,0, rotationScalarMatrix,0);


    }

    @Override
    public void draw(float[] parentMatrix) {
        float translationX = MathOps.getTextureBufferTranslationX((int) (this.numFrames * this.finishedMillis/this.millis),numFrames);
        //y translation is always 0
        VISUAL_REPRESENTATION.setTextureDelta(translationX,0);



        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rotationScalarTranslationMatrix, 0);
        VISUAL_REPRESENTATION.draw(finalMatrix);
    }

    /** Loads the texture for all instances of the attack
     *
     * @param gl access to openGL
     * @param context used to access resources
     */
    public static void loadGLTexture(@NonNull GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.enemy3_attack_spritesheet);//todo needs work
        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                0,1,
                0,0,
                1/(float) NUM_FRAMES,1,
                1/(float) NUM_FRAMES,0
        });
    }



    @Override
    public boolean isHit(BaseCharacter character) {
        if (this.hits.contains(character) ){
            return false;
        }

        float angleRadians = this.attackAngle + 2 * (float) Math.PI * (float) finishedMillis/millis;

        float cos = (float) Math.cos(angleRadians) * this.radius;
        float sin = (float) Math.sin(angleRadians) * this.radius;

        float x1 = this.x ;
        float y1 = this.y ;
        float x2 = this.x + cos;
        float y2 = this.y + sin;

        if (character.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(character);
            return true;
        }
        return false;
    }

    @Override
    public boolean isHit(Spawner spawner) {
        return false;//because nothing is done, it doesn't matter whether it's true or false, so we just return true to reduce calculations
    }

    @Override
    public boolean isHit(Supply supply) {
        if (this.hits.contains(supply) ){
            return false;
        }
        float angleRadians = this.attackAngle + 2 * (float) Math.PI * (float) finishedMillis/millis;

        float cos = (float) Math.cos(angleRadians) * this.radius;
        float sin = (float) Math.sin(angleRadians) * this.radius;

        float x1 = this.x ;
        float y1 = this.y ;
        float x2 = this.x + cos;
        float y2 = this.y + sin;

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
