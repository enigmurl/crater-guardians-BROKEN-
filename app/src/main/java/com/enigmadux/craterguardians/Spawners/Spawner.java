package com.enigmadux.craterguardians.Spawners;


import android.util.Log;

import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.GUI.ProgressBar;
import com.enigmadux.craterguardians.MathOps;

import java.util.List;

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
    protected float health;

    //at each spawn time, the amount of enemies that are spawned
    protected short[] numSpawns;
    //specifies each spawn time in millies
    protected long[] spawnTime;
    //the amount of milliseconds before a wave resets
    private long waveTime;

    //the amount of milliseconds it takes to fully decay
    private long decayTime;

    //inside the current "spawning wave", how many waves have been spawned
    private int waveIndex;



    //the amount of milli seconds since the last spawn
    protected long millisSinceLastSpawn;



    /** Constructor used for waves, this is in future will be the only one TODO actually implement this
     *
     * @param x the open gl coordinate of the spawner, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the spawner, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the spawner (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the spawner (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param numSpawns for each spawn location specified by times: how many enemies are spawning
     * @param times the milliseconds at which a spawn will happen
     * @param totalWaveTime the total milli seconds of a wave, this means that after the sai amount of milliseconds, the cycle will repeat
     * @param health the health of the spawner
     */
    public Spawner(float x,float y,float w,float h,short[] numSpawns,long[] times,long totalWaveTime, long decayTime,int health){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.numSpawns = numSpawns;
        this.spawnTime = times;
        this.waveTime = totalWaveTime;

        this.health = health;
        this.decayTime = decayTime;


        this.healthDisplay = new ProgressBar(health,w,0.05f);

    }

    /** Draws the VISUAL_REPRESENTATION customized for the subclass
     *
     * @param parentMatrix describes how to change from model to world coordinates
     */
    public void draw(float[] parentMatrix){
        this.healthDisplay.update((int) this.health,this.x,this.h  + this.y);
        this.healthDisplay.draw(parentMatrix);
    }



    /** Updates the spawner, and retunr
     * Instead of spawning a single enemy, sometimes, a wave spawner is more suitable
     *
     * @param dt milliseconds since last call
     * @return If enemies are to be spawned, it returns the list of enemies otherwise null
     */
    public List<Enemy> update(long dt){

        this.health -= (float) this.healthDisplay.getMaxHitPoints() * dt/this.decayTime;
        this.healthDisplay.update((int) Math.ceil(this.health),this.x,this.h + this.y);

        this.millisSinceLastSpawn += dt;

        if (this.millisSinceLastSpawn > this.waveTime) {
            this.millisSinceLastSpawn = 0;
            this.waveIndex = 0;
        }


        if (this.waveIndex < this.spawnTime.length && this.millisSinceLastSpawn > this.spawnTime[waveIndex]){
            return this.spawnEnemies(this.numSpawns[waveIndex++]);

        }

        return null;
    }

    /** Returns null, over here as it's an optional override,
     *
     * @param numEnemies the amount of enemies in the wave
     * @return a List of enemies that is (numEnemies) long
     */
    public abstract List<Enemy> spawnEnemies(int numEnemies);


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

    /** Sees if this collides with a circle
     *
     * @param x the center x of the circle
     * @param y the center y of the circle
     * @param r the radius of the circle
     * @return whether the circle collides with this or not
     */
    public boolean collidesWithCircle(float x,float y,float r){
        return Math.hypot(this.x - x + this.w/2,this.y - y +this.h/2) < r + this.w/2;
    }




}
