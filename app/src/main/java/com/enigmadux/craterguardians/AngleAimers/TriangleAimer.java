package com.enigmadux.craterguardians.AngleAimers;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.R;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Aims in a triangle function
 *
 * @author Manu Bhat
 * @version BETA
 */
public class TriangleAimer extends AngleAimer {
    //the length of two of the side lengths, (the ones connecting to the tip)
    private float length;

    /**
     * Hold the texture
     */
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0, -0.5f, 1, 1);

    //matrix used to scale the default to the desired
    private final float[] finalMatrix = new float[16];

    private final float[] scalarMatrix = new float[16];
    private final float[] translatorMatrix = new float[16];
    private final float[] rotatorMatrix = new float[16];
    private float[] rotationScalarTranslationMatrix = new float[16];


    /**
     * Default Constructor
     *
     * @param x               the openGL x of the tip of the triangle
     * @param y               the openGL y of the tip of the triangle
     * @param sweepAngle      the angle between the left and ride sides of the triangle in radians
     * @param midAngle        the angle between the start of the sweep and the positive x axis in degrees. Zero would mean that half the sweep is above the x axis, and half below
     * @param length          the length of two of the side lengths, (the ones connecting to the tip)
     */
    public TriangleAimer(float x, float y, float sweepAngle, float midAngle, float length) {
        super(midAngle,x,y);

        this.length = length;

        Matrix.setIdentityM(scalarMatrix, 0);
        Matrix.scaleM(scalarMatrix, 0, length * (float) Math.cos(sweepAngle / 2f), 2 * length * (float) Math.sin(sweepAngle / 2f), 1);
    }

    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        if (this.visible) {
            //rotationScalarTranslationMatrix = scalarMatrix.clone();
            //todo optimization instead of multiplying it out use the Matrix.rotateM, Matrix.translateM, Matrix.scaleM, it seems to go reverse
            Matrix.setIdentityM(rotatorMatrix,0);
            Matrix.setIdentityM(translatorMatrix,0);
            Matrix.setIdentityM(rotationScalarTranslationMatrix,0);

            Matrix.rotateM(rotatorMatrix,0,this.midAngle,0,0,1);
            Matrix.translateM(translatorMatrix,0,this.x,this.y,0);

            Matrix.multiplyMM(rotationScalarTranslationMatrix,0,rotatorMatrix,0,scalarMatrix,0);
            Matrix.multiplyMM(rotationScalarTranslationMatrix,0,translatorMatrix,0,rotationScalarTranslationMatrix,0);

            Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rotationScalarTranslationMatrix, 0);
            VISUAL_REPRESENTATION.draw(gl, finalMatrix);
        }
    }

    /**
     * Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.triangle_angle_aimer);
    }


}
