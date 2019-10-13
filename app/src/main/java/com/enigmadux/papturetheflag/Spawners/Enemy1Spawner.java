package com.enigmadux.papturetheflag.Spawners;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.enigmadux.papturetheflag.Enemies.Enemy;
import com.enigmadux.papturetheflag.Enemies.Enemy1;
import com.enigmadux.papturetheflag.R;

import javax.microedition.khronos.opengles.GL10;

/** Spawns enemy 1
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy1Spawner extends Spawner {


    /** Default Constructor
     *
     * @param x the open gl coordinate of the spawner, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the spawner, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the spawner (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the spawner (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public Enemy1Spawner(float x, float y, float w, float h, long millisPerSpawn) {
        super(x, y, w, h,millisPerSpawn);
    }

    /** Creates an enemy (does not load the glTexture
     *
     * @param gl a GL10 object used to access open gl
     * @param context any non null context used to access resources
     * @return always returns type of enemy1, as this is an enemy1 spawner
     */
    @Override
    public Enemy instantiateSingleEnemy(GL10 gl, Context context) {
        Enemy1 e = new Enemy1();
        e.setTranslate(this.x,this.y);
        e.loadGLTexture(gl,context);
        return e;
    }

    /** Loads the texture
     *
     * @param gl used to tell openGL what the new texture is
     * @param context used to access resources
     */
    public void loadGLTexture(@NonNull GL10 gl,Context context) {
        this.textureLoaded = true;
        super.loadGLTexture(gl,context, R.drawable.enemy1_spawner);
    }

}
