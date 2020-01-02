package com.enigmadux.craterguardians.GUI;

import android.content.Context;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.GUI.InGameTextbox;
import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.R;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Displays hitPoints of characters and spawners
 *
 * @author Manu Bhat
 * @version BETA
 */
public class ProgressBar extends EnigmaduxComponent  {
    /** The bar that actually displays the hitPoints. The texture is shared by all instances, and tailored for each one
     *
     */
    private static final TexturedRect BAR_VISUAL = new TexturedRect(0,0,1f,1f);
    /** Displays the holder of the Bar, just for aesthetics
     *
     */
    private static final TexturedRect BAR_HOLDER = new TexturedRect(0,0,1f,1f);

    //the width of the bar in openGL terms
    private final float w;
    //the height of the bar in openGL terms
    private final float h;

    //the maximum hitPoints the bar contains
    private int maxHitPoints;
    //the current hitPoints the bar contains
    private int currentHitPoints;


    //final Matrix = parentMatrix * translationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //translates the hitPoints bar so it aligns with the enemy/spawner
    private final float[] translationScalarMatrix = new float[16];
    //translates the components
    private final float[] translationMatrix = new float[16];
    //scales the designated components
    private final float[] scalarMatrix = new float[16];

    //the text that represents the hitpoints
    private InGameTextbox textbox;


    /** Default Constructor
     * @param hitPoints the maximum hitPoints the bar contains
     * @param w the width of the bar in openGL terms
     * @param h the height of the bar in openGL terms
     * @param drawText whether or not to draw the text
     * @param inGame whether the bar is bounded to the screen or the game map (true = gameMap, false = screen)
     */
    public ProgressBar(int hitPoints, float w, float h, boolean drawText, boolean inGame){
        super(0,0,w,h);
        this.maxHitPoints = hitPoints;
        this.w = w;
        this.h = h;

        if (drawText){
            textbox = new InGameTextbox("-1",this.w/2,this.h*3/2 ,this.h, LayoutConsts.CRATER_TEXT_COLOR, inGame);
        }
    }

    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(GL10 gl, Context context) {
        BAR_HOLDER.loadGLTexture(gl, context, R.drawable.hitpoints_bar_holder);
        BAR_VISUAL.loadGLTexture(gl, context, R.drawable.hitpoints_bar);
    }

    /** Called whenever the bar needs to be moved or the hitPoints has changed todo: split this into to functions 1 for translating 1 for hitpoints
     *
     * @param currentHitPoints the amount of damage the character can take before dieing should be less than max hitPoints, it will clip to 0, but not below max
     * @param deltaX how much to translate the left edge from 0
     * @param deltaY how much to translate the bottom edge from 0
     */
    public void update(int currentHitPoints,float deltaX,float deltaY){
        this.currentHitPoints = Math.max(0,currentHitPoints);

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,deltaX,deltaY,0);

    }

    /** Draws the holder and the actual hitPoints bar
     *
     * @param gl access to openGL
     * @param parentMatrix describes how to alter the bar from model to world space
     */
    public void draw(GL10 gl,float[] parentMatrix){
        if (! this.visible){
            return;
        }
        //Log.d("PROGRESSBAR",)
        //todo this is probably expensive to create a bitmap each time, maybe do it only when the score is updated, this whole method can be optimized
        if (this.textbox != null && (! this.textbox.getText().equals(String.valueOf(this.currentHitPoints)) || ! this.textbox.isTextureLoaded())){
            this.textbox.setText(String.valueOf(currentHitPoints));
        }

        BAR_VISUAL.setShader(1 - (float) currentHitPoints/maxHitPoints, (float) currentHitPoints/maxHitPoints,0,1);

        //it needs to be scaled vertically but horizontally just according to dimensions not hitpoints
        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,this.w,this.h,0);
        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);


        BAR_HOLDER.draw(gl,finalMatrix);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,(float) currentHitPoints/maxHitPoints * w,h,1);
        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);
        BAR_VISUAL.draw(gl,finalMatrix);

        if (this.textbox != null) {
            Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, translationMatrix, 0);
            this.textbox.draw(gl, finalMatrix);
        }


    }

    /** Sets the maximum hitPoints
     *
     * @param maxHitPoints the maximum hitPoints the bar contains
     */
    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    /** gets the current hit points
     *
     * @return the current hit points of the bar
     */
    public int getCurrentHitPoints() {
        return this.currentHitPoints;
    }

    /** Used to implememt the method
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not the touch event can be disposed
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
