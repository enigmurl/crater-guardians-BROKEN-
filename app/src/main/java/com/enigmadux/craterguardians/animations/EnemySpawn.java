package com.enigmadux.craterguardians.animations;

import android.util.Log;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.util.MathOps;

public class EnemySpawn extends Animation {

    public static final float SCALE = 1.6f;
    /** The amount of frames in the animation
     *
     */
    private static final int NUM_FRAMES = 4;


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
        super(null, R.drawable.enemy_spawn_animation,x,y,w,h);
        original.setVisibility(false);
        original.setEgged(true);
        this.textureW = 1f/NUM_FRAMES;
        this.original = original;
    }



    /** Updates to the currentFrame
     *
     * @param dt milliseconds since last call of update
     */
    public void update(World world, long dt) {
        super.update(world,dt);
        this.currentPosition += dt;

        int frameNum =Math.min((int) (this.currentPosition* EnemySpawn.NUM_FRAMES/EnemySpawn.ANIMATION_LENGTH),NUM_FRAMES-1);
        if (frameNum == NUM_FRAMES-1){
            this.original.setVisibility(true);
        }

        this.textureDeltaX = MathOps.getTextureBufferTranslationX(frameNum, EnemySpawn.NUM_FRAMES);
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
        original.setVisibility(true);
        original.setEgged(false);
        super.finish(world);

    }
}



