package com.enigmadux.craterguardians.GameObjects;

import android.content.Context;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.GUI.ProgressBar;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** This is what the robots are trying to steal
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Supply extends EnigmaduxComponent {

    //visual is shared by all objects as they all have the same sprite,
    private static final TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-0.5f,-0.5f,1,1);

    //the center x position in openGL terms
    private float dx;
    //the center y position in openGL terms
    private float dy;
    //the radius in openGL terms
    private float r;
    //the amount of damage it can take dieing
    private int health;
    //visually display the heatlth
    private ProgressBar healthDisplay;

    //matrices
    //final matrix = parentMatrix*translationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //scalar matrix scales it according to the radius
    private final float[] translationScalarMatrix = new float[16];

    /**  Default constructor
     *
     * @param x the center x position in openGL terms
     * @param y the center y position in openGL terms
     * @param r the radius in openGL terms
     * @param health the amount of damage it can take dieing
     */
    public Supply(float x,float y,float r,int health){
        super(x-r,y-r,2*r,2*r);

        this.dx = x;
        this.dy = y;
        this.r = r;
        this.health = health;

        this.healthDisplay = new ProgressBar(health,this.r,r/5, true, true);
        this.healthDisplay.update(this.health,this.dx-this.r/2,this.dy + r );

        Matrix.setIdentityM(translationScalarMatrix,0);
        Matrix.translateM(translationScalarMatrix,0,this.dx,this.dy,0);
        Matrix.scaleM(translationScalarMatrix,0,2*r,2*r,1);
    }

    /** Loads the texture of the sprite
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(GL10 gl, Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.supply_top_view);
    }


    /** Draws the enemy, and all sub components
     *
     * @param parentMatrix used to translate from model to world space
     */
    public void draw(float[] parentMatrix){
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);
        VISUAL_REPRESENTATION.draw(finalMatrix);
        this.healthDisplay.draw(parentMatrix);

    }


    /** Sees if the supply has been killed
     *
     * @return whether it has more than 0 health or not
     */
    public boolean isAlive(){
        if (health > 0){
            return true;
        } else {
            SoundLib.playSupplyDeathSoundEffect();
            return false;
        }
    }

    /** When an enemy attack hits the supply, this method is called as to decrease the health (or increase if damage is negative)
     *
     * @param damage the amount to decrease the health by
     */
    public void damage(int damage){
        this.health -= damage;
        this.healthDisplay.update(this.health,this.dx-this.r/2,this.dy + r );
    }

    /** Sees if a line intersects this hitbox
     *
     * @param x0 p1 x
     * @param y0 p1 y
     * @param x1 p2 x
     * @param y1 p2 y
     * @return whether or not the line intersects this hitbox.
     */
    public boolean collidesWithLine(float x0,float y0,float x1,float y1){
        return MathOps.segmentIntersectsCircle(this.dx,this.dy,this.r,x0,y0,x1,y1);
    }

    /** Gets the x value
     *
     * @return the center x value in openGL terms
     */
    public float getX() {
        return this.dx;
    }

    /** Gets the y value
     *
     * @return the center y value in openGL terms
     */
    public float getY() {
        return this.dy;
    }

    /** used to implement the method, has no purpose
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return false all the time
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
