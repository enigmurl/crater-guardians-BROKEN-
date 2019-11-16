package com.enigmadux.craterguardians.Spawners;


import android.util.Log;

import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.ProgressBar;
import com.enigmadux.craterguardians.MathOps;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Spawner {
    //shows the user how much health it has remaining
    private ProgressBar healthDisplay;


    //open gl x coordinate (read constructor javadoc for more details)
    protected float x;
    //open gl y coordinate (read constructor javadoc for more details)
    protected float y;
    //open gl width (read constructor javadoc for more details)
    protected float w;
    //open gl height (read constructor javadoc for more details)
    protected float h;
    //amount of health before dieing
    protected int health;


    //amount of millis in between spawns
    protected final long millisPerSpawn;
    //the amount of milli seconds since the last spawn
    protected long millisSinceLastSpawn;

    /** Default Constructor
     *
     * @param x the open gl coordinate of the spawner, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the spawner, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the spawner (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the spawner (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param health the amount of damage this can take before dieing
     */
    public Spawner(float x, float y, float w, float h,long millisPerSpawn,int health) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.millisPerSpawn = millisPerSpawn;
        this.health = health;

        this.healthDisplay = new ProgressBar(health,w,0.05f, true, true);
    }

    /** Draws the VISUAL_REPRESENTATION customized for the subclass
     *
     * @param gl access to openGL
     * @param parentMatrix describes how to change from model to world coordinates
     */
    public void draw(GL10 gl,float[] parentMatrix){
        this.healthDisplay.update(this.health,this.x,this.h  + this.y);
        this.healthDisplay.draw(gl,parentMatrix);
    }

    /** Call every frame, whenever this returns a non null entity an enemy has been spawned, otherwise the spawner is not ready for another enemy to be spawned
     *
     *
     * @param dt how many milliseconds since the last call
     * @return an enemy if it is ready, or null if it is not ready.
     */
    public Enemy trySpawnEnemy(long dt){
        this.millisSinceLastSpawn += dt;
        if (this.millisSinceLastSpawn >= this.millisPerSpawn){
            this.millisSinceLastSpawn = 0;
            return this.instantiateSingleEnemy();
        }
        return null;
    }


    /** Creates 1 enemy, is abstract as child classes need to do for the individual enemy spawner
     *
     * @return an enemy, of any given spawner there is a specific set of enemies that it can spawn
     */
    public abstract Enemy instantiateSingleEnemy();

    /** Called by outside attacks whenever they intersect this spawner
     *
     * @param damage the amount to reduce the health by (a negative value would heal up the spawner)
     */
    public void damage(int damage){
        this.health -= damage;
        Log.d("SPAWNER","took " + damage + " damage, health is now: " + health) ;

    }

    /** Sees whether the spawner has more than 0 health, and is thus alive
     *
     * @return whether or not the spawner has more than 0 health; whether or not it is alive and capable of spawning enemies
     */
    public boolean isAlive(){
        return this.health > 0;
    }

    /** Sees whether this hit box collides with a line. If the line segment is completely enclosed by the line it returns true
     *
     * @param x1 x coordinate of point 1
     * @param y1 y coordinate of point 1
     * @param x2 x coordinate of point 2
     * @param y2 y coordinate of point 2
     * @return whether the line collides
     */
    public boolean collidesWithLine(float x1,float y1,float x2,float y2){
        if (MathOps.lineIntersectsLine(x1,y1,x2,y2,x+w,y,x+w,y+h) ||
            MathOps.lineIntersectsLine(x1,y1,x2,y2,x,y,x,y+h) ||
            MathOps.lineIntersectsLine(x1,y1,x2,y2,x,y+h,x+w,y+h) ||
            MathOps.lineIntersectsLine(x1,y1,x2,y2,x,y,x+w,y)) {
            return true;
        }

        if (x1 > x && x2< x + this.w && y1 > y && y2 < y + this.h){
            return true;
        }
        return false;

    }




}
