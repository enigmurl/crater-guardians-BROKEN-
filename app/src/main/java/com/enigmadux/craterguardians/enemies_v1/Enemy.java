package com.enigmadux.craterguardians.enemies_v1;

import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;

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

    }


    /** Damages the player along, that's it for now but maybe in the future
     *
     * @param damage the amount of damage the character must take, a >0 value will decrease the health, <0 will increase, =0 will do nothing
     */
    @Override
    public void damage(int damage) {
        this.health -= damage;
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
}
