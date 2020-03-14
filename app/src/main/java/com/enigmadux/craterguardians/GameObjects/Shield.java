package com.enigmadux.craterguardians.GameObjects;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

/** Visual for shield
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Shield {

    //the percentage to extend out to
    private static final float SHIELD_EXTRA = 0.4f;



    //sweep in degrees
    private float sweep;
    //mid angle in degrees
    private float midAngle;
    //radius from starting point
    private float radius;


    //the offset x
    private float dX;
    //the offset y
    private float dY;


    //if it's active or not
    private boolean isActive;


    //the visible part
    private QuadTexture quadTexture;

    //the final matrix
    private final float[] finalMatrix = new float[16];
    //intermediate scalar rotational matrix
    private final float[] translationalRotationalMatrix = new float[16];


    /** the shield

    /** Default Constructor
     *
     * @param sweep the sweep in degrees
     * @param radius the radius of the shield
     * @param context anything that can be used to get resources
     */
    public Shield(float sweep, float radius, Context context){
        this.sweep = sweep;
        this.radius = radius;
        float x2 = (float) Math.cos(this.midAngle + this.sweep/2) * radius + this.dX;
        float y2 = (float) Math.sin(this.midAngle + this.sweep/2) * radius + this.dY;
        float x3 = (float) Math.cos(this.midAngle - this.sweep/2) * radius + this.dX;
        float y3 = (float) Math.sin(this.midAngle - this.sweep/2) * radius + this.dY;

        float dist = (float) Math.hypot(x3-x2,y3-y2);



        this.quadTexture = new QuadTexture(context, R.drawable.shield,0,0,this.radius * Shield.SHIELD_EXTRA,dist);
    }

    /** Set the middle pointer angle
     *
     * @param midAngle the middle pointer angle in degrees
     */
    public void setMidAngle(float midAngle) {
        this.midAngle = midAngle;
    }


    /** Sees if a line intersects this
     *
     * @param x0 point 0 x
     * @param y0 point 0 y
     * @param x1 point 1 x
     * @param y1 point 1 y
     * @return sees if it intersects with the shield, (if it's active)
     */
    public boolean intersects(float x0,float y0,float x1,float y1){
        if (! isActive) return false;
        //need to multiply it, and offset it
        float x2 = (float) Math.cos(this.midAngle + this.sweep/2) * radius + this.dX;
        float y2 = (float) Math.sin(this.midAngle + this.sweep/2) * radius + this.dY;
        float x3 = (float) Math.cos(this.midAngle - this.sweep/2) * radius + this.dX;
        float y3 = (float) Math.sin(this.midAngle - this.sweep/2) * radius + this.dY;


        float x4 = (float) Math.cos(this.midAngle + this.sweep/2) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y4 = (float) Math.sin(this.midAngle + this.sweep/2) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;
        float x5 = (float) Math.cos(this.midAngle - this.sweep/2) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y5 = (float) Math.sin(this.midAngle - this.sweep/2) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;

        return  MathOps.lineIntersectsLine(x0,y0,x1,y1,x2,y2,x3,y3) ||
                MathOps.lineIntersectsLine(x0,y0,x1,y1,x4,y4,x5,y5) ||
                MathOps.lineIntersectsLine(x0,y0,x1,y1,x2,y2,x4,y4) ||
                MathOps.lineIntersectsLine(x0,y0,x1,y1,x3,y3,x5,y5);
    }


    /** Sets whether the shield should be on or off
     *
     * @param isOn whether the shield is active or not
     */
    public void setState(boolean isOn){
        this.isActive = isOn;
    }


    /** Draws the shield if it's active
     *
     * @param mvpMatrix the model view projection 4x4 matrix
     * @param quadRenderer anything that can render quads
     */
    public void draw(float[] mvpMatrix, QuadRenderer quadRenderer){
        if (this.isActive){
            Matrix.setIdentityM(this.translationalRotationalMatrix,0);
            Matrix.translateM(this.translationalRotationalMatrix,0,this.dX,this.dY,0);
            Matrix.rotateM(this.translationalRotationalMatrix,0,this.midAngle,0,0,1);
            Matrix.translateM(this.translationalRotationalMatrix,0,this.radius,0,0);


            Matrix.multiplyMM(this.finalMatrix,0,mvpMatrix,0,this.translationalRotationalMatrix,0);
            quadRenderer.renderQuad(this.quadTexture,this.finalMatrix);
        }
    }

    public void setTranslation(float x,float y){
        this.dX = x;
        this.dY = y;
    }
}
