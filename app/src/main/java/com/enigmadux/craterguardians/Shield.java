package com.enigmadux.craterguardians;

import android.content.Context;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** The visual for the shield, which absorbs attacks
 * @author Manu Bhat
 * @version BETA
 *
 */
public class Shield {
    //the visual representation of the shield shared by all objects, it is tailored for each one though
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-0.5f,-0.5f,1,1);

    //final matrix = parentMatrix * rotationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //rotates and scales the shield
    private final float[] rotationScalarMatrix = new float[16];


    /** Default constructor, the sweep is hardcoded to 0.5 degrees radians for now
     *
     * @param width the width in openGL terms
     * @param height the height in openGL terms
     * @param angle the angle to offset in radians
     */
    public Shield(float width,float height,float angle){
        Matrix.setIdentityM(this.rotationScalarMatrix,0);
        Matrix.scaleM(this.rotationScalarMatrix,0,width,height,1);
        Matrix.rotateM(this.rotationScalarMatrix,0,180/(float) Math.PI * angle,0,0,1);
    }

    /** Binds the shield texture to the TexturedRect
     *
     * @param gl a GL10 object used to access openGL
     * @param context Any non null context that is used to access resource
     */
    public static void loadGLTexture(GL10 gl, Context context){
        VISUAL_REPRESENTATION.loadGLTexture(gl,context,R.drawable.visual_shield);
    }

    /** Draws the shield onto the screen
     *
     * @param gl10  a GL10 object used to access openGL
     * @param parentMatrix describes how to change from model coordinates to world coordinates
     */
    public void draw(GL10 gl10,float[] parentMatrix){
        Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.rotationScalarMatrix,0);

        VISUAL_REPRESENTATION.draw(gl10,this.finalMatrix);
    }
}
