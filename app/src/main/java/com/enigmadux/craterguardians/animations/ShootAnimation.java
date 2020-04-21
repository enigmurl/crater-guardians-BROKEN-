package com.enigmadux.craterguardians.animations;

import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.R;

/** Played when a player evolves
 *
 * @author Manu Bhat
 * @version BETA
 */
public class ShootAnimation extends Animation {


    //the standard dimensions of evolve animations
    public static final float STANDARD_DIMENSIONS = 0.2f;
    /** The amount of frames in the animation
     *
     */
    private static final int NUM_FRAMES = 5;


    /** The amount of millis in the animation
     *
     */
    private static final long ANIMATION_LENGTH = 100;



    //current place in animation in milliseconds
    private long currentPosition;

    /** Default constructor
     *
     * @param x center opengl deltX
     * @param y center opengl y
     * @param w opengl width
     * @param h opengl height
     */
    public ShootAnimation(float x, float y, float w, float h){
        //super(x-w/2,y-h/2,w,h);
        super(null,R.drawable.death_animation,x,y,w,h);

        this.textureW = 1f/NUM_FRAMES;
    }


    /** Updates to the currentFrame
     *
     * @param dt milliseconds since last call of update
     */
    @Override
    public void update(World world,long dt) {
        super.update(world,dt);

        this.currentPosition += dt;

        this.textureDeltaX = MathOps.getTextureBufferTranslationX((int) (this.currentPosition* ShootAnimation.NUM_FRAMES/ ShootAnimation.ANIMATION_LENGTH), ShootAnimation.NUM_FRAMES);
    }

    /** Sees whether it's finished or not
     *
     * @return whether or not the animation is finished
     */
    @Override
    public boolean isFinished() {
        return this.currentPosition> ShootAnimation.ANIMATION_LENGTH;
    }

    @Override
    public void finish(World world) {
        super.finish(world);
    }
}
