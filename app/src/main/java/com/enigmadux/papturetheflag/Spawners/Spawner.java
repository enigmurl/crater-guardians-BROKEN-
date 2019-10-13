package com.enigmadux.papturetheflag.Spawners;


import android.content.Context;
import android.support.annotation.NonNull;

import com.enigmadux.papturetheflag.Enemies.Enemy;
import com.enigmadux.papturetheflag.Enemies.Enemy1;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/**
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Spawner extends TexturedRect {
    //if the texture has been loaded or not
    protected boolean textureLoaded = false;
    //amount of millis in between spawns
    protected final long millisPerSpawn;
    //the amount of milli seconds since the last spawn
    protected long millisSinceLastSpawn;

    /** Default Constructor
     *
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public Spawner(float x, float y, float w, float h,long millisPerSpawn) {
        super(x, y, w, h);
        this.millisPerSpawn = millisPerSpawn;
    }

    /** Call every frame, whenever this returns
     *
     *
     * @param gl a GL10 object used to access open gl
     * @param context any non null context used to access resources
     * @param dt how many milliseconds since the last call
     * @return an enemy if it is ready, or null if it is not ready.
     */
    public Enemy trySpawnEnemy(GL10 gl, Context context, long dt){
        this.millisSinceLastSpawn += dt;
        if (this.millisSinceLastSpawn >= this.millisPerSpawn){
            this.millisSinceLastSpawn = 0;
            return this.instantiateSingleEnemy(gl,context);
        }
        return null;
    }

    /** Sees if the texture is already loaded as to not repeat doing the same thing
     *
     * @return whether or not the texture is loaded
     */
    public boolean isTextureLoaded() {
        return textureLoaded;
    }

    /** Creates 1 enemy, is abstract as child classes need to do for the individual enemy spawner
     *
     * @param gl a GL10 object used to access open gl
     * @param context any non null context used to access resources
     *
     * @return an enemy, of any given spawner there is a specific set of enemies that it can spawn
     */
    public abstract Enemy instantiateSingleEnemy(GL10 gl, Context context);

    /** Loads the texture
     *
     * @param gl used to tell openGL what the new texture is
     * @param context used to access resources
     */
    public abstract void loadGLTexture(@NonNull GL10 gl, Context context);

}
