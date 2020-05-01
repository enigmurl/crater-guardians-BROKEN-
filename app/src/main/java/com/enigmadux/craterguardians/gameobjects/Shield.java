package com.enigmadux.craterguardians.gameobjects;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.animations.ShieldSpawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

/** Visual for shield
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Shield extends QuadTexture {
    public static final float DEFAULT_SWEEP = 100;
    public static final float DEFAULT_RADIUS = 0.25f;
    //the percentage to extend out to
    private static final float SHIELD_EXTRA = 0.4f;
    //an enemy of radius 1, will exert  directional force per second on the player
    private static final float FORCE_PER_RADIUS = 3f;



    //sweep in degrees
    private float sweep;
    //mid angle in degrees
    private float midAngle;
    //radius from starting point
    private float radius;


    //the offset x
    private float dX;
    //the offset y
    private float dY;


    //if it's active or not
    private boolean isActive;



    //intermediate scalar rotational matrix
    private final float[] translationalRotationalMatrix = new float[16];

    private ShieldSpawner shieldSpawner;

    private float maxRadius;

    /** the shield

    /** Default Constructor
     *
     * @param sweep the sweep in degrees
     * @param radius the radius of the shield
     * @param context anything that can be used to get resources
     */
    public Shield(float sweep, float radius, Context context){
        //stuff initialized later
        super(context,R.drawable.shield,-1,-1,-1,-1);
        this.sweep = sweep;
        this.maxRadius = radius;
        this.setRadius(radius);


        this.isVisible = false;


    }

    /** Set the middle pointer angle
     *
     * @param midAngle the middle pointer angle in degrees
     */
    public void setMidAngle(float midAngle) {
        this.midAngle = midAngle;
    }


    /** Sees if a line intersects this
     *
     * @param x0 point 0 x
     * @param y0 point 0 y
     * @param x1 point 1 x
     * @param y1 point 1 y
     * @return sees if it intersects with the shield, (if it's active)
     */
    public boolean intersects(float x0,float y0,float x1,float y1){
        if (! isActive) return false;
        //need to multiply it, and offset it
        float x2 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * radius + this.dX;
        float y2 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * radius + this.dY;
        float x3 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * radius + this.dX;
        float y3 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * radius + this.dY;


        float x4 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y4 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;
        float x5 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y5 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;

        return  MathOps.lineIntersectsLine(x0,y0,x1,y1,x2,y2,x3,y3) ||
                MathOps.lineIntersectsLine(x0,y0,x1,y1,x4,y4,x5,y5) ||
                MathOps.lineIntersectsLine(x0,y0,x1,y1,x2,y2,x4,y4) ||
                MathOps.lineIntersectsLine(x0,y0,x1,y1,x3,y3,x5,y5);
    }


    /** Sets whether the shield should be on or off
     *
     * @param isOn whether the shield is active or not
     */
    public void setState(boolean isOn){
        if (isOn && ! isActive){
            SoundLib.playPlayerSpawnShieldSoundEffect();
            if (shieldSpawner != null){
                shieldSpawner.cancel();
            }
            shieldSpawner = new ShieldSpawner(ShieldSpawner.DEFAULT_MILLIS,this);
        }
        this.isActive = isOn;
        this.isVisible = this.isActive;
    }



    @Override
    public void dumpOutputMatrix(float[] dumpMatrix, float[] mvpMatrix) {
        Matrix.setIdentityM(this.translationalRotationalMatrix,0);
        Matrix.translateM(this.translationalRotationalMatrix,0,this.x,this.y,0);
        Matrix.rotateM(this.translationalRotationalMatrix,0,this.midAngle,0,0,1);
        Matrix.translateM(this.translationalRotationalMatrix,0,this.radius * (1 - SHIELD_EXTRA/2),0,0);
        Matrix.scaleM(this.translationalRotationalMatrix,0,this.w,this.h,0);

        Matrix.multiplyMM(dumpMatrix,0,mvpMatrix,0,this.translationalRotationalMatrix,0);
    }

    //x,y,r = circle
    public boolean intersectsCircle(float x,float y,float r){
        if (! isActive) return false;
        float x2 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * radius + this.dX;
        float y2 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * radius + this.dY;
        float x3 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * radius + this.dX;
        float y3 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * radius + this.dY;


        float x4 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y4 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;
        float x5 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y5 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;
        return  MathOps.segmentIntersectsCircle(x,y,r,x2,y2,x3,y3) ||
                MathOps.segmentIntersectsCircle(x,y,r,x4,y4,x5,y5) ||
                MathOps.segmentIntersectsCircle(x,y,r,x2,y2,x4,y4) ||
                MathOps.segmentIntersectsCircle(x,y,r,x3,y3,x5,y5);
    }

    public void setTranslation(float x, float y){
        this.dX = x;
        this.dY = y;
    }

    public void update(World world,long dt){
        float forceX = 0;
        float forceY = 0;
        if (! isActive) return;
        float x2 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * radius + this.dX;
        float y2 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * radius + this.dY;
        float x3 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * radius + this.dX;
        float y3 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * radius + this.dY;


        float x4 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y4 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;
        float x5 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y5 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;

        //mid point of the sides
        float x6 = (x3 + x5)/2;
        float y6 = (y3 + y5)/2;
        float x7 = (x2 + x4)/2;
        float y7 = (y2 + y2)/2;
        synchronized (World.blueEnemyLock) {
            ArrayList<Enemy> blueEnemies = world.getBlueEnemies().getInstanceData();
            for (int i = 0; i <blueEnemies.size(); i++) {
                Enemy e = blueEnemies.get(i);
                //use the previous position for the other stuff to basically counter act frame
                boolean b1 = MathOps.segmentIntersectsCircle(e.getPrevDeltaX(),e.getPrevDeltaY(),e.getRadius(),x2,y2,x3,y3);
                boolean b2 = MathOps.pointInCircle(x6,y6,e.getPrevDeltaX(),e.getPrevDeltaY(),e.getRadius());
                boolean b3 = MathOps.pointInCircle(x7,y7,e.getPrevDeltaX(),e.getPrevDeltaY(),e.getRadius());
                boolean b4 = MathOps.segmentIntersectsCircle(e.getDeltaX(),e.getDeltaY(),e.getRadius(),x4,y4,x5,y5);

                //if its closer

                //not only intersecting
                if (b4 && ! (b1 || b2 || b3)){
                    MathOps.clipCharacterEdge(e,x4,y4,x5,y5);
                    forceX += (e.getVelocityX()- world.getPlayer().getVelocityX())* e.getRadius() * FORCE_PER_RADIUS;
                    forceY += (e.getVelocityY()- world.getPlayer().getVelocityY()) * e.getRadius() * FORCE_PER_RADIUS;
                }

            }
        }
        synchronized (World.orangeEnemyLock) {
            ArrayList<Enemy> orangeEnemies = world.getOrangeEnemies().getInstanceData();
            for (int i = 0; i <orangeEnemies.size(); i++) {
                Enemy e = orangeEnemies.get(i);
                boolean b1 = MathOps.segmentIntersectsCircle(e.getDeltaX(),e.getDeltaY(),e.getRadius(),x2,y2,x3,y3);
                boolean b2 = MathOps.pointInCircle(x6,y6,e.getDeltaX(),e.getDeltaY(),e.getRadius());
                boolean b3 = MathOps.pointInCircle(x7,y7,e.getDeltaX(),e.getDeltaY(),e.getRadius());
                boolean b4 = MathOps.segmentIntersectsCircle(e.getDeltaX(),e.getDeltaY(),e.getRadius(),x4,y4,x5,y5);

                //not only intersecting
                if (b4 && ! (b1 || b2 || b3)){
                    MathOps.clipCharacterEdge(e,x4,y4,x5,y5);
                    forceX += (e.getVelocityX() - world.getPlayer().getVelocityX()) * e.getRadius() * FORCE_PER_RADIUS;
                    forceY += (e.getVelocityY() - world.getPlayer().getVelocityY()) * e.getRadius() * FORCE_PER_RADIUS;
                }
            }
        }
        world.getPlayer().translateFromPos(forceX * dt/1000,forceY * dt/1000);
    }

    public float getRadius(){
        return radius;
    }
    public void setRadius(float radius){
        this.radius = radius;
        float x2 = (float) Math.cos((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y2 = (float) Math.sin((Math.PI)/180f * (this.midAngle + this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;
        float x3 = (float) Math.cos((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dX;
        float y3 = (float) Math.sin((Math.PI)/180f * (this.midAngle - this.sweep/2)) * (1 + Shield.SHIELD_EXTRA) * radius + this.dY;

        float dist = (float) Math.hypot(x3-x2,y3-y2);

        this.setTransform(0,0,radius * Shield.SHIELD_EXTRA,dist);
    }


}
