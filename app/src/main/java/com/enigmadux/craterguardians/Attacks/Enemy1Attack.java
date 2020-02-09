package com.enigmadux.craterguardians.Attacks;


import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.BaseCharacter;
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
public class Enemy1Attack extends Attack {

    /** The amount of frames in this animation
     *
     */
    private static int NUM_FRAMES = 8;

    //matrix used to scale the default to the desired
    private final float[] finalMatrix = new float[16];
    private final float[] translationMatrix = new float[16];
    private final float[] rotationScalarMatrix = new float[16];
    private final float[] rotationScalarTranslationMatrix = new float[16];


    private float length;
    private float width;
    private int damage;

    private List<Object> hits= new ArrayList<>();


    /** The actual visual component is shared between all instances to save memory
     *
     */
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,-0.5f,1,1);


    /** Default constructor
     * @param x openGL deltX
     * @param y openGL y
     * @param damage how much damage to deal to enemies;
     * @param attackAngle the angle between the start of the sweep and the positive deltX axis in radians. Zero would mean that half the sweep is above the deltX axis, and half below
     * @param length how long the attack is in open gl terms
     * @param width how wide the attack is in open gl terms, because orginally it is point ing in the positive deltX axis, this is originally the height at 0 radians
     * @param millis how long the attack takes to finish
     */
    public Enemy1Attack(float x, float y, int damage, float attackAngle, float length,float width, long millis){
        super(x,y,0,0,NUM_FRAMES,millis,null, attackAngle);


        this.damage = damage;
        this.length =length;
        this.width = width;

        float[] scalarMatrix = new float[16];
        Matrix.setIdentityM(scalarMatrix, 0);
        Matrix.scaleM(scalarMatrix, 0, width , width, 1);

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

        float originalXvalue = length * (float) finishedMillis/millis;
        float originalYValue = width/2f;

        float cos = (float) Math.cos(attackAngle);
        float sin = (float) Math.sin(attackAngle);

        float x1 = this.x + cos * originalXvalue - sin * originalYValue;
        float y1 = this.y + sin * originalXvalue + cos * originalYValue;
        float x2 = this.x + cos * originalXvalue + sin * originalYValue;
        float y2 = this.y + sin * originalXvalue - cos * originalYValue;



        if (character.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(character);
            //ends the attack
            this.finishedMillis = this.millis + 1;
            this.isFinished = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isHit(Spawner spawner) {
        return false;//because nothing is done, it doesn't matter whether it's true or false, so we just return true to reduce calculations
    }

    @Override
    public boolean isHit(Enemy enemy) {
        return false;
    }

    @Override
    public boolean isHit(Supply supply) {
        if (this.hits.contains(supply)){
            return false;
        }
        float originalXvalue = length * (float) finishedMillis/millis;
        float originalYValue = width/2f;

        float cos = (float) Math.cos(attackAngle);
        float sin = (float) Math.sin(attackAngle);

        float x1 = this.x + cos * originalXvalue - sin * originalYValue;
        float y1 = this.y + sin * originalXvalue + cos * originalYValue;
        float x2 = this.x + cos * originalXvalue + sin * originalYValue;
        float y2 = this.y + sin * originalXvalue - cos * originalYValue;

        //(oX + oYi) * (cos + sinI) = oxCos - oYSin + cosOYI + sinIOX
        //(oX - oYi) * (cos + sinI) = oxCos + oYSin - cosOYI + sinIOX

        if (supply.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(supply);
            //ends the attack
            this.finishedMillis = this.millis + 1;
            this.isFinished = true;
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
