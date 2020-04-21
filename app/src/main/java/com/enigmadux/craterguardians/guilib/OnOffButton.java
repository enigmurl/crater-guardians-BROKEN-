package com.enigmadux.craterguardians.guilib;

import android.content.Context;
import android.view.MotionEvent;

/** A button that switches color based on it's state
 *
 * @author Manu Bhat
 * @version BETA
 */
public class OnOffButton extends GUIClickable {
    //shader of off buttons in form of r g b a
    private static final float[] OFF_SHADER = new float[] {1.0f,0.5f,0.5f,1};
    //shader of on buttons in form of r g b a
    private static final float[] ON_SHADER = new float[] {0.5f,1.0f,0.5f,1};


    /** if the button is currently on
     *
     */
    private boolean isOn = false;

    /** Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     */
    public OnOffButton(Context context,int texturePointer,float x,float y,float w,float h){
        super(context, texturePointer, x, y, w, h, false);
        this.shader = OnOffButton.OFF_SHADER;
    }
    /** Handles when the user slides off the button
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onSoftRelease(MotionEvent e) {
        this.isDown = false;

        return true;
    }

    /** Handles when the user presses this button
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onPress(MotionEvent e) {
        this.isDown = true;
        return true;
    }

    /** When the user lets go of the button
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time
     */
    @Override
    public boolean onHardRelease(MotionEvent e) {
        this.isDown = false;


        this.setState(! this.isOn);

        return true;
    }

    /** Sets the state of this button, as well as change the color
     *
     * @param onOrOff whether the new state is on or off (on = true)
     */
    public void setState(boolean onOrOff){
        this.isOn = onOrOff;
        this.shader = (this.isOn) ? OnOffButton.ON_SHADER : OnOffButton.OFF_SHADER;
    }

    /** If this button is currently activated
     *
     * @return if this button is turned on
     */
    public boolean isOn() {
        return this.isOn;
    }
}
