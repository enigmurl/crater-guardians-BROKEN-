package com.enigmadux.craterguardians.Animations;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.R;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Played when player or enemy dies
 *
 * TODO convert this to 1 texture textured rect
 *
 * @author Manu Bhat
 * @version BETA
 */
public class ToxicBubble extends Animation {

    /** The amount of frames in the animation
     *
     */
    private static final int NUM_FRAMES = 5;



    //visual is shared by all objects as they all have the same sprite
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(0,0,1,1,ToxicBubble.NUM_FRAMES);


    //the length of the animation in milliseconds
    private long animationLength;

    //parentMatrix * translationScalarMatrix
    private final float[] finalMatrix = new float[16];
    //translates and scales appropriately
    private final float[] translationScalarMatrix = new float[16];
    //current place in animation in milliseconds
    private long currentPosition;

    /** Default constructor
     *
     * @param x center opengl x
     * @param y center opengl y
     * @param w opengl width
     * @param h opengl height
     * @param animationLength  the length of the animation in milliseconds
     */
    public ToxicBubble(float x,float y,float w,float h,long animationLength){
        super(x-w/2,y-h/2,w,h);

        this.animationLength = animationLength;

        Matrix.setIdentityM(translationScalarMatrix,0);
        Matrix.translateM(translationScalarMatrix,0,x-w/2,y-h/2,0);
        Matrix.scaleM(translationScalarMatrix,0,w,h,1);
    }


    /** Binds the sprite sheet to the quad
     *
     * @param gl10 access to openGL
     * @param context any nonnull context
     */
    public static void loadGLTexture(GL10 gl10, Context context){
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.bubbleanim_frame_0,0);
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.bubbleanim_frame_1,1);
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.bubbleanim_frame_2,2);
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.bubbleanim_frame_3,3);
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.bubbleanim_frame_4,4);
        //this is once we made it sprite sheet based
//        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
//                0,1,
//                0,0,
//                1/(float) NUM_FRAMES,1,
//                1/(float) NUM_FRAMES,0
//        });
    }

    /** draws the current frame
     *
     * @param gl           the GL10 object used to access openGL
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.translationScalarMatrix,0);

        int frameNum = Math.min(ToxicBubble.NUM_FRAMES-1, (int) (this.currentPosition* ToxicBubble.NUM_FRAMES/this.animationLength));
        VISUAL_REPRESENTATION.draw(this.finalMatrix,frameNum);
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
        return this.currentPosition>this.animationLength;
    }
}
