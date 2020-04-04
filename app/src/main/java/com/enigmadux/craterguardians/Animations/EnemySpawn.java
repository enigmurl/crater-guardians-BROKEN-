package com.enigmadux.craterguardians.Animations;

import android.content.Context;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.util.MathOps;

import enigmadux2d.core.quadRendering.QuadTexture;

public class EnemySpawn extends Animation {

    /** The amount of frames in the animation
     *
     */
    private static final int NUM_FRAMES = 5;


    /** The amount of millis in the animation
     *
     */
    private static final long ANIMATION_LENGTH = 1000;


    //current place in animation in milliseconds
    private long currentPosition;

    private Enemy original;

    /** Default constructor
     *
     * @param x center opengl deltX
     * @param y center opengl y
     * @param w opengl width
     * @param h opengl height
     */
    public EnemySpawn(Enemy original,float x, float y, float w, float h){
        //null context bc it isnt needed
        super(null, R.drawable.death_animation,x,y,w,h);
        original.setVisibiility(false);
        this.textureW = 1f/NUM_FRAMES;
        this.original = original;
    }


    /** Binds the sprite sheet to the quad
     *
     * @param context any nonnull context
     */
    public static void loadGLTexture(Context context){
        //loads the texture for future use
        QuadTexture.loadAndroidTexturePointer(context,R.drawable.death_animation);
    }

    /** Updates to the currentFrame
     *
     * @param dt milliseconds since last call of update
     */
    public void update(World world, long dt) {
        super.update(world,dt);
        this.currentPosition += dt;
        this.textureDeltaX = MathOps.getTextureBufferTranslationX((int) (this.currentPosition* EnemySpawn.NUM_FRAMES/EnemySpawn.ANIMATION_LENGTH), EnemySpawn.NUM_FRAMES);
    }

    /** Sees whether it's finished or not
     *
     * @return whether or not the animation is finished
     */
    public boolean isFinished() {
        return this.currentPosition>EnemySpawn.ANIMATION_LENGTH;
    }

    @Override
    public void finish(World world) {
        super.finish(world);
        original.setVisibiility(true);

    }
}



