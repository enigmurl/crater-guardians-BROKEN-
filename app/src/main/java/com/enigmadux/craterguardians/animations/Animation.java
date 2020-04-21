package com.enigmadux.craterguardians.animations;

import android.content.Context;

import com.enigmadux.craterguardians.gamelib.World;

import enigmadux2d.core.quadRendering.QuadTexture;

/** Used to play animations, such as death animations, todo looping animations/ use textures right
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Animation extends QuadTexture {
    /** Default constructor
     *
     * @param x left edge deltX openGL
     * @param y bottom edge y openGL
     * @param w width openGL
     * @param h height openGL
     */
    public Animation(Context context,int texturePointer,float x, float y, float w, float h){
        super(context,texturePointer,x,y,w,h);
    }

    /** Updates the animation to the current frame, for some animations it may also translate or other transformations
     *
     * @param dt milliseconds since last call of update
     */
    public void update(World world,long dt){
        if (this.isFinished()){
            this.finish(world);
        }
    }

    /** Whether or not the animation is finished, if it is, it can be deleted from memory
     *
     * @return whether or not the animation is complete
     */
    public abstract boolean isFinished();

    public void finish(World world){
        world.getAnims().remove(this);
    }
}
