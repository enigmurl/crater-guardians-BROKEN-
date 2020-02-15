package com.enigmadux.craterguardians.GUILib;

import android.view.MotionEvent;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** Any class that wants to be a clickable must implement this class
 *
 * COMMON DEBUGS:
 * make sure the clickable is viewable, this is crucial
 *
 */
public abstract class GUIClickable {

    //if this should be drawn or not, if it's not visisble it also shouldn't be clicked
    protected boolean isVisible;
    //if this is being pressed
    protected boolean isDown;



    /** See if the touch event intersects the bounding box of this, it may also return false if this button is disabled
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event intersects this bounding box
     */
    public abstract boolean isPressed(MotionEvent e);

    /** When the object is pressed
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event was used, as in this object, or a sub component was affected by the event
     */
    public abstract boolean onPress(MotionEvent e);

    /** When it's let go directly
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event was used, as in this object, or a sub component was affected by the event
     */
    public abstract boolean onHardRelease(MotionEvent e);

    /** When it was being pressed, but the finger was moved off
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event was used, as in this object, or a sub component was affected by the event
     */
    public abstract boolean onSoftRelease(MotionEvent e);

    /** If this is being pressed right now
     *
     * @return If this is being pressed right now
     */
    public boolean isDown() {
        return this.isDown;
    }

    /** If this is visible, and being drawn to the screen
     *
     * @return If this is being drawn right now
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /** Sets whether this should be drawn or not
     *
     * @param visible whether to be drawn or not
     */
    public void setVisiblity(boolean visible) {
        this.isVisible = visible;
    }

    /** Render Components given the parent matrix, and the renderer
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     */
    public abstract void render(float[] uMVPMatrix, QuadRenderer renderer);
}
