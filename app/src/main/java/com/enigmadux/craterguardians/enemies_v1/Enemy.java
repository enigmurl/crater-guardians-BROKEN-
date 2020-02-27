package com.enigmadux.craterguardians.enemies_v1;

import android.opengl.Matrix;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;

import java.util.ArrayList;
import java.util.List;

/** New Enemy
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends CraterCollectionElem implements Character {

    /** The center x in openGL terms
     *
     */
    private float x;
    /** The center y in openGL terms
     *
     */
    private float y;

    /** The radius in openGL terms (half the width, half the height)
     *
     */
    private float r;

    /** This is the health variable, by default if it's over 0 that indicates this character is alive
     *
     */
    protected int health;

    /** All active attacks
     *
     */
    protected List<Attack> activeAttacks;

    /** Default Constructor
     *
     * @param instanceID the id of the instance in reference to the vao it's in (received using VaoCollection.addInstance());
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     * @param r the radius in openGL terms (half the width, half the height)
     */
    public Enemy(int instanceID,float x,float y,float r){
        super(instanceID);

        //assign variables to attributes
        this.x = x;
        this.y = y;
        this.r = r;

        this.activeAttacks = new ArrayList<>();
    }


    /** Damages the player along, that's it for now but maybe in the future
     *
     * @param damage the amount of damage the character must take, a >0 value will decrease the health, <0 will increase, =0 will do nothing
     */
    @Override
    public void damage(int damage) {
        this.health -= damage;
    }


    /** Sees if this enemy is alive
     *
     * @return if the enemy health is above 0
     */
    public boolean isAlive(){
        return this.health > 0;
    }


    /** Sets the filtration of specific channels to the desired values. The most common uses may be shade the character when
     * losing or gaining health
     *
     * @param r the filter of the red channel
     * @param b the filter of the blue channel
     * @param g the filter of the green channel
     * @param a the filter of the alpha channel
     */
    @Override
    public void setShader(float r, float b, float g, float a) {
        super.setShader(r, b, g, a);
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
        Matrix.scaleM(blankInstanceInfo,0,2 * this.r,2 * this.r,0);
    }

    /** Moves the enemy to a specific position
     *
     * @param x how much to translate in the deltX direction
     * @param y how much to translate in the y direction
     */
    public void setTranslate(float x,float y){
        this.deltaX = x;
        this.deltaY = y;
    }


    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param supplies  all alive supplies on the map
     * @param enemyMap A map of where and how the enemy should go
     */
    public void update(long dt, BaseCharacter player, List<Supply> supplies, EnemyMap enemyMap) {
        //todo
    }

    /** Spawn an attack at a particular angle
     *
     * @param angle the angle
     */
    public abstract void attack(float angle);




}
