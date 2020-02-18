package com.enigmadux.craterguardians.GUILib;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.MathOps;

import enigmadux2d.core.quadRendering.QuadTexture;

/** Any class that wants to be a clickable must extend this class.
 *
 *
 *
 * COMMON DEBUGS:
 * make sure the clickable is viewable (isVisible = true), this is crucial
 * isDown must be handled by the sub class, as in when it's pressed it must become true, todo might want to change this in future
 *
 */
public abstract class GUIClickable extends QuadTexture implements VisibilitySwitch {

    /**
     * The scale factor when it's pushed down
     */
    private static final float BUTTON_DOWN_SCALEFACTOR = 0.8f;


    //if this should be drawn or not, if it's not visisble it also shouldn't be clicked
    protected boolean isVisible;
    //if this is being pressed
    protected boolean isDown;

    /**
     * This is always initialized to scale it as if the button is being pressed down.
     * Only use this variables if the button is being down
     */
    private final float[] scalarMatrix = new float[16];

    /**
     * This is where intermediate dumping of the the matrices is put into
     */
    private final float[] finalMatrix = new float[16];


    /**
     * Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     */
    protected GUIClickable(Context context, int texturePointer, float x, float y, float w, float h) {
        super(context, texturePointer, x, y, w * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH, h);
        Matrix.setIdentityM(this.scalarMatrix, 0);
        Matrix.scaleM(this.scalarMatrix, 0, GUIClickable.BUTTON_DOWN_SCALEFACTOR, GUIClickable.BUTTON_DOWN_SCALEFACTOR, 0);

    }

    /** Default Constructor, most likely will only work in a GL THREAD
     *
     * @param texturePointer an OPEN GL texture pointer, this is different from R.drawable.*, as menntioned in first constructor
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     */
    protected GUIClickable(int texturePointer,float x,float y,float w,float h) {
        super(texturePointer, x, y, w * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH, h);
        Matrix.setIdentityM(this.scalarMatrix, 0);
        Matrix.scaleM(this.scalarMatrix, 0, GUIClickable.BUTTON_DOWN_SCALEFACTOR, GUIClickable.BUTTON_DOWN_SCALEFACTOR, 0);
    }


    /**
     * See if the touch event intersects the bounding box of this, it may also return false if this button is disabled
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event intersects this bounding box
     */
    public boolean isPressed(MotionEvent e) {
        float x = MathOps.getOpenGLX(e.getX());
        float y = MathOps.getOpenGLY(e.getY());

        return (this.isVisible &&
                x > this.x - this.w / 2 &&
                x < this.x + this.w / 2 &&
                y > this.y - this.h / 2 &&
                y < this.y + this.h / 2);

    }

    /**
     * When the object is pressed
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event was used, as in this object, or a sub component was affected by the event
     */
    public abstract boolean onPress(MotionEvent e);

    /**
     * When it's let go directly
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event was used, as in this object, or a sub component was affected by the event
     */
    public abstract boolean onHardRelease(MotionEvent e);

    /**
     * When it was being pressed, but the finger was moved off
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return if the touch event was used, as in this object, or a sub component was affected by the event
     */
    public abstract boolean onSoftRelease(MotionEvent e);

    /**
     * If this is being pressed right now
     *
     * @return If this is being pressed right now
     */
    public boolean isDown() {
        return this.isDown;
    }

    /**
     * If this is visible, and being drawn to the screen
     *
     * @return If this is being drawn right now
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Sets whether this should be drawn or not
     *
     * @param visible whether to be drawn or not
     */
    public void setVisibility(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Dumps the output matrix for rendering
     *
     * @param dumpMatrix where the output matrix will be placed
     * @param mvpMatrix  the input matrix 4 by 4
     */
    @Override
    public void dumpOutputMatrix(float[] dumpMatrix, float[] mvpMatrix) {
        if (this.isDown) {
            super.dumpOutputMatrix(this.finalMatrix, mvpMatrix);
            Matrix.multiplyMM(dumpMatrix, 0, this.finalMatrix, 0, this.scalarMatrix, 0);
        } else {
            super.dumpOutputMatrix(dumpMatrix, mvpMatrix);
        }
    }

    /** Handles touch events
     *
     * @param e  motion event object that describes the touch event
     * @return whether or not the touch event has been handled
     */
    public boolean onTouch(MotionEvent e) {
        if (this.isPressed(e)) {
            if (this.isDown() && e.getActionMasked() == MotionEvent.ACTION_UP) {
                this.onHardRelease(e);
            } else if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                this.onPress(e);
            }
            return true;
        } else if (this.isDown()) {
            this.onSoftRelease(e);
            return true;
        }
        return false;
    }
}
