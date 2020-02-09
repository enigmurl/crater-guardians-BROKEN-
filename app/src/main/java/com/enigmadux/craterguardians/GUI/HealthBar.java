package com.enigmadux.craterguardians.GUI;

import android.content.Context;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Draw a bunch of hearts to represent the health of an enemy
 *
 * @author Manu Bhat
 * @version BETA
 */
public class HealthBar  extends EnigmaduxComponent {

    //The visual representation of a heart that is
    private static TexturedRect VISUAL_HEART_REP = new TexturedRect(0,-0.5f,1,1);

    //matrices used to to scale
    private float[] baseScaleM = new float[16];
    //parentMatrix * baseScaleM
    private float[] finalMatrix = new float[16];



    //the amount of hearts to display
    private int numHearts;
    //the maximum amount of hearts to display
    private int maxHearts;

    //the amount to travel in the deltX direction from anchor point
    private float deltaX;
    //the amount to travel in the y direciton from anchor point
    private float deltaY;


    /** Default Constructor
     *
     * @param x center X of the health bar
     * @param y center Y of the health bar
     * @param w the width of the health bar
     * @param numHearts the amount of hearts to display
     */
    public HealthBar(float x,float y,float w,int numHearts){
        super(x,y,w,w * LayoutConsts.SCREEN_WIDTH/(numHearts * LayoutConsts.SCREEN_HEIGHT));

        this.numHearts = numHearts;
        this.maxHearts = numHearts;

        Matrix.setIdentityM(baseScaleM,0);

        //start at the very left
        Matrix.scaleM(baseScaleM,0,this.w/this.numHearts,this.h,0);
    }

    /** Sets the translation from the anchor point
     *
     * @param x the amount to move in the deltX direction from the anchor point
     * @param y the amount to move in the y direction from the anchor point
     */
    public void setTranslate(float x, float y){
        this.deltaX = x;
        this.deltaY = y;
    }

    /** Loads the textures of this and this' sub components
     *
     * @param context context used to load resources
     */
    public static void loadGLTexture(Context context){
        VISUAL_HEART_REP.loadGLTexture(context, R.drawable.health_icon);
    }


    /** Resets this health bar to full health
     *
     *//*
    public void reset(){
        this.numHearts = this.maxHearts;
    }*/

    /** Updates this health bar to the specified number of hearts
     *
     * @param currentHearts the current amount of hearts
     */
    public void updateHealth(int currentHearts){
        this.numHearts = currentHearts;
    }


    /** Draws the hearts
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        if (true) return;
        VISUAL_HEART_REP.prepareDraw(0);
        for (int i = 0;i<this.numHearts;i++){
            Matrix.translateM(finalMatrix,0,parentMatrix,0,deltaX + x + i * this.w/this.maxHearts,this.deltaY + y,0);
            Matrix.scaleM(finalMatrix,0,this.w/this.maxHearts,this.h,0);

            VISUAL_HEART_REP.intermediateDraw(this.finalMatrix);
        }
        VISUAL_HEART_REP.endDraw();
    }

    /** Always return false, nothing should happen on a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return FALSE
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}

