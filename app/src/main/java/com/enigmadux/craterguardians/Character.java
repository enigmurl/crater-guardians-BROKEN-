package com.enigmadux.craterguardians;

/** The interface for characters; players and enemies
 *
 * @author Manu Bhat
 * @version BETA
 */
public interface Character {

    /**
     * All Character classes must be able to be damaged
     *
     * @param damage the amount of damage the character must take, a >0 value will decrease the health, <0 will increase, =0 will do nothing
     *               though depending on the sub classes implementation, this may vary
     */
    void damage(int damage);


    /**
     * Characters should be able to be channel filtered, the most common use is turning slightly red on damage, but perhaps
     * others as well
     *
     * @param r the filter of the red channel
     * @param b the filter of the blue channel
     * @param g the filter of the green channel
     * @param a the filter of the alpha channel
     */
    void setShader(float r, float b, float g, float a);

    float getDeltaX();

    float getDeltaY();

    float getRadius();

    float getCharacterSpeed();


    void translateFromPos(float dX,float dY);

    void setTranslate(float dX,float dY);



}




