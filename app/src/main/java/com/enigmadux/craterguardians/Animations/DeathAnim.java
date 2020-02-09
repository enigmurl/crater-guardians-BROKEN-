package com.enigmadux.craterguardians.Animations;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.shapes.TexturedRect;

/** Played when player or enemy dies
 *
 * @author Manu Bhat
 * @version BETA
 */
public class DeathAnim extends Animation {

    /** The amount of frames in the animation
     *
     */
    private static final int NUM_FRAMES = 5;


    /** The amount of millis in the animation
     *
     */
    private static final long ANIMATION_LENGTH = 1000;

    //visual is shared by all objects as they all have the same sprite
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,0,1,1);

    //parentMatrix * translationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //translates and scales appropriately
    private final float[] translationScalarMatrix = new float[16];
    //current place in animation in milliseconds
    private long currentPosition;

    /** Default constructor
     *
     * @param x center opengl deltX
     * @param y center opengl y
     * @param w opengl width
     * @param h opengl height
     */
    public DeathAnim(float x,float y,float w,float h){
        super(x-w/2,y-h/2,w,h);

        Matrix.setIdentityM(translationScalarMatrix,0);
        Matrix.translateM(translationScalarMatrix,0,x-w/2,y-h/2,0);
        Matrix.scaleM(translationScalarMatrix,0,w,h,1);
    }


    /** Binds the sprite sheet to the quad
     *
     * @param context any nonnull context
     */
    public static void loadGLTexture(Context context){
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.death_animation);
        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                0,1,
                0,0,
                1/(float) NUM_FRAMES,1,
                1/(float) NUM_FRAMES,0
        });
    }

    /** draws the current frame
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.translationScalarMatrix,0);

        float translationX = MathOps.getTextureBufferTranslationX((int) (this.currentPosition* DeathAnim.NUM_FRAMES/DeathAnim.ANIMATION_LENGTH), DeathAnim.NUM_FRAMES);
        //y translation is always 0
        VISUAL_REPRESENTATION.setTextureDelta(translationX,0);


        //VISUAL_REPRESENTATION.setTextureDelta(translationY,0);
        VISUAL_REPRESENTATION.draw(this.finalMatrix);

    }

    /** Updates to the currentFrame
     *
     * @param dt milliseconds since last call of update
     */
    @Override
    public void update(long dt) {
        this.currentPosition += dt;
    }

    /** Sees whether it's finished or not
     *
     * @return whether or not the animation is finished
     */
    @Override
    public boolean isFinished() {
        return this.currentPosition>DeathAnim.ANIMATION_LENGTH;
    }
}
