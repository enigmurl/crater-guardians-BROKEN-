package com.enigmadux.craterguardians.Animations;

import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;

/** Used to play animations, such as death animations, todo looping animations
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Animation extends EnigmaduxComponent {
    /** Default constructor
     *
     * @param x left edge x openGL
     * @param y bottom edge y openGL
     * @param w width openGL
     * @param h height openGL
     */
    public Animation(float x,float y,float w,float h){
        super(x,y,w,h);
    }

    /** Draws the component onto the screen, the frame choosing is done in the update call
     *
     * @param gl           the GL10 object used to access openGL
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public abstract void draw(GL10 gl,float[] parentMatrix);

    /** Updates the animation to the current frame, for some animations it may also translate or other transformations
     *
     * @param dt milliseconds since last call of update
     */
    public abstract void update(long dt);

    /** Whether or not the animation is finished, if it is, it can be deleted from memory
     *
     * @return whether or not the animation is complete
     */
    public abstract boolean isFinished();

    /** Animations don't need to handle touch events
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not the touch event can be disposed of
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
