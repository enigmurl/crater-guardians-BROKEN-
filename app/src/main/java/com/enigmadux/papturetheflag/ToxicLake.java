package com.enigmadux.papturetheflag;

import android.content.Context;
import android.support.annotation.NonNull;

import com.enigmadux.papturetheflag.Characters.Player;
import com.enigmadux.papturetheflag.Enemies.Enemy;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Hurts and damages players, bots are immune to it though for now
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class ToxicLake extends TexturedRect {
    /** How much damage it does at once
     *
     */
    private static final int DAMAGE  = 5;
    /** The damage is discrete, not continuous so this says how many milliseconds before asserting a new damage
     *
     */
    private static final long MILLIS_BETWEEN_DAMAGE = 1000L;

    //radius of the image
    private float radius;

    //the amount of millis since the last damage
    private long currentMillis;

    /** Default Constructor
     *
     * @param x the openGL x coordinate
     * @param y the openGL y coordinate
     * @param r the radius in openGL space
     */
    public ToxicLake(float x,float y,float r){
        super(x-r,y-r,2*r,2*r);
        this.radius = r;
    }

    /** Loads the texture
     *
     * @param gl access to openGL
     * @param context context used to load resources, and non null context should work
     */
    public void loadGLTexture(@NonNull GL10 gl, Context context) {
        super.loadGLTexture(gl, context, R.drawable.toxic_lake_texture);
    }

    /** Tries to attack the enemies and the bots
     *
     * @param dt milliseconds since last call
     * @param player the player it attempts to damage
     * @param enemyList all enemies it attempts to damage
     */
    public void update(long dt, Player player, List<Enemy> enemyList){
        currentMillis += dt;
        if (currentMillis > MILLIS_BETWEEN_DAMAGE){
            currentMillis = 0;
            List<float[]> collisions;
            for (Enemy enemy: enemyList){
                collisions = enemy.getCollisionsWithLine(enemy.getDeltaX(),enemy.getDeltaY(),this.getX()+this.radius,this.getY()+this.radius);
                if (collisions.size() == 0 || Math.hypot(collisions.get(0)[0]-this.getX()-this.radius,collisions.get(0)[1]-this.getY()-this.radius) < this.radius){
                    enemy.damage(DAMAGE);
                }
            }
            collisions = player.getCollisionsWithLine(player.getDeltaX(),player.getDeltaY(),this.getX()+this.radius,this.getY()+this.radius);
            if (collisions.size() == 0 || Math.hypot(collisions.get(0)[0]-this.getX()-this.radius,collisions.get(0)[1]-this.getY()-this.radius) < this.radius){
                player.damage(DAMAGE);
            }

        }
    }
}
