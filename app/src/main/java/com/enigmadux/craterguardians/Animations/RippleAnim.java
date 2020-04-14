package com.enigmadux.craterguardians.Animations;

import android.content.Context;

import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.quadRendering.QuadTexture;

/** Played when player or enemy dies
 *
 * @author Manu Bhat
 * @version BETA
 */
public class RippleAnim extends Animation {


    /** The amount of millis in the animation
     *
     */
    private static final long ANIMATION_LENGTH = 400;


    //current place in animation in milliseconds
    private long currentPosition;
    private float orgR;

    /** Default constructor
     *
     * @param x center opengl deltX
     * @param y center opengl y
     */
    public RippleAnim(float x,float y,float r){
        //null context bc it isnt needed
        super(null,R.drawable.joystick_background,x,y,0,0);
        this.orgR = r;
    }


    /** Updates to the currentFrame
     *
     * @param dt milliseconds since last call of update
     */
    public void update(World world,long dt) {
        super.update(world,dt);
        this.currentPosition += dt;
        this.setScale(2 * this.orgR * currentPosition/RippleAnim.ANIMATION_LENGTH,2 * this.orgR * currentPosition/RippleAnim.ANIMATION_LENGTH);
        this.setAlpha(1 - (float) currentPosition/RippleAnim.ANIMATION_LENGTH);

    }

    /** Sees whether it's finished or not
     *
     * @return whether or not the animation is finished
     */
    public boolean isFinished() {
        return this.currentPosition>RippleAnim.ANIMATION_LENGTH;
    }
}
