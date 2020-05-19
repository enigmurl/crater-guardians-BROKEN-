package com.enigmadux.craterguardians.animations;

import android.util.Log;

import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.util.FloatPoint;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.util.SoundLib;

import java.util.LinkedList;

/** Played when a player evolves
 *
 * @author Manu Bhat
 * @version BETA
 */
public class EvolveAnim extends Animation {


    //the standard dimensions of evolve animations
    public static final float STANDARD_DIMENSIONS = 0.4f;
    /** The amount of frames in the animation
     *
     */
    private static final long ANIMATION_LENGTH = 1000;

    //* the width
    private static final float SHAKE_PERCENT = 0.2f;

    private static final int NUM_POINTS = 23;



    //current place in animation in milliseconds
    private long currentPosition;

    private float orgW;
    private float orgH;

    private LinkedList<FloatPoint> path;

    /** Default constructor
     *
     * @param x center opengl deltX
     * @param y center opengl y
     * @param w opengl width
     * @param h opengl height
     */
    public EvolveAnim(float x, float y, float w, float h){
        //super(x-w/2,y-h/2,w,h);
        super(null,R.drawable.evolve_animation,x,y,w,h);

        SoundLib.playPlayerEvolvingSoundEffect();
        this.orgW = w;
        this.orgH = h;

        this.path = ScreenShake.getRandomShake(w * SHAKE_PERCENT,NUM_POINTS,x,y);

    }


    /** Updates to the currentFrame
     *
     * @param dt milliseconds since last call of update
     */
    @Override
    public void update(World world,long dt) {
        super.update(world,dt);
        if (this.currentPosition == 0){
            world.setEnableInGameUpdating(false);
        }
        int index = Math.min((int) (currentPosition * (NUM_POINTS-1)/ANIMATION_LENGTH),NUM_POINTS - 2) + 1;
        //todod this is inefficient linked List O(N) get
        float denom = (ANIMATION_LENGTH/(float) (NUM_POINTS - 1));
        float x = (currentPosition % denom)/denom * (path.get(index).x - path.get(index-1).x) + path.get(index-1).x;
        float y = (currentPosition % denom)/denom * (path.get(index).y - path.get(index-1).y) + path.get(index-1).y;
        this.setCord(x,y);


        this.currentPosition += dt;
        float t = (float) this.currentPosition/ANIMATION_LENGTH;
        float s = this.getScale(t);

        float color =getColor(t);
        this.setScale(orgW * s,orgH * s);
        this.setShader(1,1 - 3 * color/4,1-3 * color/4,this.getAlpha(t));
    }

    /** Sees whether it's finished or not
     *
     * @return whether or not the animation is finished
     */
    @Override
    public boolean isFinished() {
        return this.currentPosition> EvolveAnim.ANIMATION_LENGTH;
    }

    @Override
    public void finish(World world) {
        super.finish(world);
        world.setEnableInGameUpdating(true);
    }


    private float getScale(float t){
        return t < 0.2f ? -62.5f * (t-0.2f) * (t-0.2f)  + 3.5f : 3.5f * (float) (Math.exp(21*(t-0.75f))+ 1 -Math.exp(0.75 * -21));
    }

    private float getAlpha(float t){
        return 1 - t * t * t * t * t;
    }

    private float getColor(float t){
        float x = (float) Math.cos((12 - 12 *(t-1)) * 2.5 * t);
        return x * x;
    }
}
