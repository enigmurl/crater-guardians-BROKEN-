package com.enigmadux.craterguardians.GUIs.testingGui;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

/** Tests the GUIClickable abstract class
 *
 * @author Manu Bhat
 * @version BETA
 */
public class TesterButton extends GUIClickable {


    private float x,y,w,h;


    private QuadTexture quadTexture;

    private final float[] scalarMatrix = new float[16];
    private final float[] translMatrix = new float[16];

    private final float[] tsM = new float[16];

    private float[] finalMatrix = new float[16];
    /** Default Constructor
     *
     * @param x center x
     * @param y center y
     * @param w width
     * @param h height
     */
    public TesterButton(float x, float y, float w, float h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;


        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,0.8f,0.8f,0);

        Matrix.setIdentityM(translMatrix,0);
        Matrix.translateM(translMatrix,0, this.w * 0.2f,this.h*0.2f,0);

        Matrix.multiplyMM(tsM,0,translMatrix,0,scalarMatrix,0);

    }




    public void loadGLTexture(Context context){
        this.quadTexture = new QuadTexture(context, R.drawable.button_background,x,y,w,h);

    }

    /** When it's pressed
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true
     */
    @Override
    public boolean isPressed(MotionEvent e) {
        //Log.d("Button:","is pressed");
        return (this.isVisible &&
                MathOps.getOpenGLX(e.getRawX()) > this.x - this.w/2 &&
                MathOps.getOpenGLX(e.getRawX()) < this.x + this.w/2 &&
                MathOps.getOpenGLY(e.getRawY()) > this.y - this.h/2 &&
                MathOps.getOpenGLY(e.getRawY()) < this.y + this.h/2);
    }

    @Override
    public boolean onHardRelease(MotionEvent e) {
        Log.d("Button:","hard release");

        this.isDown = false;
        return false;
    }

    @Override
    public boolean onPress(MotionEvent e) {
        Log.d("Button:","press ");

        this.isDown = true;
        return false;
    }


    @Override
    public boolean onSoftRelease(MotionEvent e) {
        Log.d("Button:","soft release");

        this.isDown = false;
        return false;
    }

    @Override
    public void render(float[] uMVPMatrix, QuadRenderer renderer) {
        if (! this.isVisible) return;
        if (this.isDown){
            Matrix.multiplyMM(finalMatrix,0,uMVPMatrix,0,this.tsM,0);

            renderer.renderQuad(this.quadTexture,finalMatrix);
        } else {
            renderer.renderQuad(this.quadTexture,uMVPMatrix);

        }
    }
}
