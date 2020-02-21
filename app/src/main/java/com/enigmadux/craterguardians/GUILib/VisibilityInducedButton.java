package com.enigmadux.craterguardians.GUILib;

import android.content.Context;
import android.view.MotionEvent;


/** Shows and hides a component when clicked
 *
 *
 */
public class VisibilityInducedButton extends GUIClickable {


    /** The object that will be hidden when the button is pressed
     *
     */
    protected VisibilitySwitch objectToHide;
    /** The object that will be shown when the button is pressed
     *
     */
    protected VisibilitySwitch objectToShow;

    /** Default constructor
     * @param context any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     * @param objectToHide the object that should be hidden
     * @param objectToShow the object that should be shown when the button is presssed
     * @param isRounded if the image has rounded off corners
     */
    public VisibilityInducedButton(Context context, int texturePointer,
                                   float x, float y, float w, float h,
                                   VisibilitySwitch objectToHide, VisibilitySwitch objectToShow, boolean isRounded) {
        super(context, texturePointer, x, y, w, h, isRounded);

        this.objectToHide = objectToHide;
        this.objectToShow = objectToShow;
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
        if (this.objectToShow != null)
            this.objectToShow.setVisibility(true);
        if (this.objectToHide != null)
            this.objectToHide.setVisibility(false);
        return true;
    }

}
