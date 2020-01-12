package com.enigmadux.craterguardians.AngleAimers;

/** Angle aimer
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class AngleAimer {
    /**the angle between the start of the sweep and the positive x axis in degrees. Zero would mean that half the sweep is above the x axis, and half below */
    protected float midAngle;

    /** whether or not its going to be drawn to the screen, the actual functionality must be implemented by child class */
    protected boolean visible = true;

    /**the x position of the origin (where the aimer rotates around)*/
    protected float x;
    /**the y position of the origin (where the aimer rotates around)*/
    protected float y;

    /** Default Constructor
     *
     * @param midAngle the angle between the start of the sweep and the positive x axis in radians. Zero would mean that half the sweep is above the x axis, and half below private float midAngle;
     * @param x the x position of the origin (where the aimer rotates around)
     * @param y the y position of the origin (where the aimer rotates around)
     */
    public AngleAimer(float midAngle,float x,float y){
        this.midAngle = midAngle;
        this.x = x;
        this.y = y;
    }

    /** Draws the angle aimer
     *
     * @param parentMatrix describes how to transform it from model to world coordinates
     */
    public abstract void draw(float[] parentMatrix);

    /** Translates the aimer as to match up with the player at all times
     *
     * @param x the x position of the origin (where the aimer rotates around)
     * @param y the y position of the origin (where the aimer rotates around)
     */
    public void setPosition(float x,float y){
        this.x = x;
        this.y =y;
    }

    /** sets the mid angle
     *
     * @param angle the angle between the start of the sweep and the positive x axis in degrees. Zero would mean that half the sweep is above the x axis, and half below
     */
    public void setMidAngle(float angle){
        this.midAngle = angle;
    }

    /** Tells the aimer to be drawn to the screen
     *
     */
    public void show() {
        this.visible = true;
    }

    /** Tells the aimer to NOT be drawn to the screen
     *
     */
    public void hide(){
        this.visible = false;
    }

    /** returns whether or not its being drawn to screen
     *
     * @return whether or not it's being drawn to screen
     */
    public boolean isVisible() {
        return visible;
    }
}
