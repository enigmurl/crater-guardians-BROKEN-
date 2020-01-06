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
public class TriRectAimer extends AngleAimer {


    /**
     * Hold the texture
     */
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0, -0.5f, 1, 1);


    //matrix used to map the default square to the desired shape
    private final float[] finalMatrix = new float[16];

    private final float[] leftAttackM = new float[16];
    private final float[] middleAttackM = new float[16];
    private final float[] rightAttackM = new float[16];


    //dimensions of the middle attack rect and the side attack rects
    private float mainLength;
    private float mainWidth;
    private float sideLength;
    private float sideWidth;

    //the sweep from the left attack to the right attack in degrees
    private float sweepAngle;

    /**
     * Default Constructor
     *
     * @param x               the openGL x of the tip of the triangle
     * @param y               the openGL y of the tip of the triangle
     * @param sweepAngle      the angle between the left and ride sides of the triangle in radians
     * @param angleDegrees        the angle between the start of the sweep and the positive x axis in degrees. Zero would mean that half the sweep is above the x axis, and half below
     * @param mainLength how long the middle attack is in open gl terms (radius)
     * @param mainWidth how wide the middle attack is in open gl terms (radius)
     * @param sideLength how long the side attacks are in open gl terms
     * @param sideWidth how wide the side attacks are in open gl terms
     */
    public TriRectAimer(float x, float y, float sweepAngle, float angleDegrees,float mainLength,float mainWidth,float sideLength,float sideWidth) {
        super(angleDegrees,x,y);
        this.sweepAngle = (float) (180/(Math.PI) * (sweepAngle));

        this.mainLength = mainLength;
        this.mainWidth = mainWidth;
        this.sideLength = sideLength;
        this.sideWidth = sideWidth;


    }

    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        if (this.visible) {
            //rotationScalarTranslationMatrix = scalarMatrix.clone();
            //todo optimization instead of multiplying it out use the Matrix.rotateM, Matrix.translateM, Matrix.scaleM, it seems to go reverse
            Matrix.setIdentityM(leftAttackM,0);
            Matrix.translateM(leftAttackM,0,this.x,this.y,0);
            Matrix.rotateM(leftAttackM,0,this.sweepAngle/2 + this.midAngle,0,0,1);
            Matrix.scaleM(leftAttackM,0,sideLength,sideWidth,0);

            Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, leftAttackM, 0);
            VISUAL_REPRESENTATION.draw(gl, finalMatrix);

            Matrix.setIdentityM(rightAttackM,0);
            Matrix.translateM(rightAttackM,0,this.x,this.y,0);
            Matrix.rotateM(rightAttackM,0,-this.sweepAngle/2 + this.midAngle,0,0,1);
            Matrix.scaleM(rightAttackM,0,sideLength,sideWidth,0);

            Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, rightAttackM, 0);
            VISUAL_REPRESENTATION.draw(gl, finalMatrix);

            Matrix.setIdentityM(middleAttackM,0);
            Matrix.translateM(middleAttackM,0,this.x,this.y,0);
            Matrix.rotateM(middleAttackM,0,midAngle,0,0,1);
            Matrix.scaleM(middleAttackM,0,mainLength,mainWidth,0);

            Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, middleAttackM, 0);
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
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.tri_rect_angle_aimer);
    }


}
