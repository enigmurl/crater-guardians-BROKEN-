package com.enigmadux.craterguardians.spawners;


import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.animations.ExpansionAnim;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.CraterCollection;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.util.SoundLib;

import java.util.ArrayList;
import java.util.Arrays;

import enigmadux2d.core.quadRendering.QuadTexture;

/**
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Spawner extends CraterCollectionElem {
    /** The glow radius, (in relation to the width of the spawner)
     *
     */
    private static final float GLOW_RADIUS = 2f;

    /** The blue shader
     *
     */
    private static float[] BLUE_SHADER = new float[] {0.24f,1,0.886f,1f};
    /** The shader that turns white into orange
     *
     */
    private static float[] ORANGE_SHADER = new float[] {1,0.701f,0.4f,1f};

    /** Blue shader of the fuel cells, differs slightly
     *
     */

    private static float[] FUEL_BLUE_SHADER = new float[] {0.24f,1,0.95f,1f};
    /** The shader that turns white into orange for fuel cells
     *
     */
    private static float[] FUEL_ORANGE_SHADER = new float[] {1,0.751f,0.45f,1f};



    /** A arbirtarily small number
     *
     */
    private static final float EPSILON = 0.1f;


    //amount of health before dieing
    protected float health;



    private long elapsedTime;




    //parameters of the equation ax^3 + bx^2 + cx + d
    private MultiSegCubicSolver healthFunction;


    //when blue1 ends (millisecond Time wise)
    private long blue1End;
    //when orange ends (millisecond Time wise)
    private long orangeEnd;
    //when blue 2 ends (milli second)
    private long blue2End;


    /** The amount of enemies for each bluespawn
     *
     */
    private short[] numBlueSpawns;
    /** The amount of juice (milliseconds) required for each blue spawn
     *
     */
    private long[] blueSpawnJuice;

    /** The amount of enemies for each orange spawn
     *
     */
    private short[] numOrangeSpawns;

    /** The amount of juice (milliseconds) required for each orange spawn
     *
     */
    private long[] orangeSpawnJuice;


    /** Which "wave" in the blue spawner are we pointing too
     *
     */
    private int bluePointer = 0;

    /** Which wave in tn the orange spawner are we pointing too
     *
     */
    private int orangePointer = 0;

    /** The amount of juice being put into the current blue wave
     *
     */
    private long currentBlueJuice;

    /** The amount of juice being put into the current orange wave
     *
     */
    private long currentOrangeJuice;

    private ArrayList<QuadTexture> renderables;

    private QuadTexture spawnGlowIndicator;

    //optimize drawing by combine fuel cell 1 and 3 into 1 quatexture, and render that first
    private QuadTexture blueFuelCell;
    private QuadTexture orangeFuelCell;



    private ExpansionAnim expansionAnim;

    int strength;


    public Spawner(Context context,int instanceId, float x, float y, float w, float h, int orangeEndHealth, int blue1EndHealth, int maxHealth,
                   long blue1, long orange, long blue2,
                   short[] numBlueSpawns, long[] blueSpawnJuice,
                   short[] numOrangeSpawns, long[] orangeSpawnJuice,int strength){

        super(instanceId);
        this.deltaX = x;
        this.deltaY = y;
        this.width = w;
        this.height = h;

        this.health = maxHealth;
        this.strength = strength;

        this.renderables = new ArrayList<>();

        this.spawnGlowIndicator = new QuadTexture(context,R.drawable.spawner_glow,x,y,GLOW_RADIUS * w,GLOW_RADIUS * h);

        this.blueFuelCell = new QuadTexture(context,R.drawable.spawner_fuel_cell,x ,y ,w/2,h/2);
        this.blueFuelCell.setShader(FUEL_BLUE_SHADER[0],FUEL_BLUE_SHADER[1],FUEL_BLUE_SHADER[2],FUEL_BLUE_SHADER[3]);
        this.orangeFuelCell = new QuadTexture(context,R.drawable.spawner_fuel_cell,x,y ,w/2,h/2);
        this.orangeFuelCell.setShader(FUEL_ORANGE_SHADER[0],FUEL_ORANGE_SHADER[1],FUEL_ORANGE_SHADER[2],FUEL_ORANGE_SHADER[3]);


        //cubic
        //such that f(0) = maxHealth,
        //f(blue1) = health0;
        //f(blue1 + orange) = health1,
        //f(blue1 + orange + blue2) = 0;

        //dy over dx
        float m1 = (float) (orangeEndHealth - maxHealth)/(blue1 + orange);
        float m2 = (float) (- blue1EndHealth)/(orange + blue2);

        this.healthFunction = new MultiSegCubicSolver(
                0,maxHealth,0,
                blue1, blue1EndHealth,m1,
                blue1 + orange, orangeEndHealth,m2,
                blue1 + orange + blue2,0,0
        );



        this.blue1End = blue1;
        this.orangeEnd = blue1 + orange;
        this.blue2End = this.orangeEnd + blue2;

        this.numBlueSpawns = numBlueSpawns;
        this.numOrangeSpawns = numOrangeSpawns;
        this.blueSpawnJuice = blueSpawnJuice;
        this.orangeSpawnJuice = orangeSpawnJuice;

        this.renderables.add(this.spawnGlowIndicator);
        this.renderables.add(this.blueFuelCell);
        this.renderables.add(this.orangeFuelCell);
        this.updateGraphics();
    }

    /**
     *
     */
    public ArrayList<QuadTexture> getRenderables(){
        return this.renderables;
    }



    /** Updates the spawner, and retunr
     * Instead of spawning a single enemy, sometimes, a wave spawner is more suitable
     *
     * @param dt milliseconds since last call
     * @return If enemies are to be spawned, it returns the list of enemies otherwise null
     */
    public void update(long dt,World world){



        //this.health -= (float) this.healthDisplay.getMaxHitPoints() * dt/this.decayTime;
        this.elapsedTime += dt;
        this.health = this.healthFunction.interpolate((float) (this.elapsedTime));

        if (! this.isAlive()){
            world.getSpawners().delete(this);
            return;
        }

        //blue mode
        if (this.elapsedTime < this.blue1End || this.elapsedTime > this.orangeEnd){
            this.currentBlueJuice += dt;
            if (this.currentBlueJuice >= this.blueSpawnJuice[this.bluePointer]){
                this.currentBlueJuice = 0;
                int wavePointer = this.bluePointer;
                this.bluePointer = (this.bluePointer + 1)%this.blueSpawnJuice.length;
                synchronized (World.blueEnemyLock) {
                    this.spawnBlueEnemies(this.numBlueSpawns[wavePointer], world.getBlueEnemies());
                }
            }
        }
        //orange mode
        else{
            this.currentOrangeJuice += dt;
            if (this.currentOrangeJuice >= this.orangeSpawnJuice[this.orangePointer]){
                this.currentOrangeJuice = 0;
                int wavePointer = this.orangePointer;
                this.orangePointer = (this.orangePointer + 1) % this.orangeSpawnJuice.length;
                synchronized (World.orangeEnemyLock) {
                    this.spawnOrangeEnemies(this.numOrangeSpawns[wavePointer], world.getOrangeEnemies());
                }
            }
        }

        this.updateGraphics();
    }

    private void updateGraphics(){
        if (this.elapsedTime < this.blue1End){
            if (! Arrays.equals(spawnGlowIndicator.getShader(),BLUE_SHADER)){
                if (this.expansionAnim != null) this.expansionAnim.cancel();
                this.expansionAnim = new ExpansionAnim(this.spawnGlowIndicator,ExpansionAnim.DEFAULT_MILLIS,0,0);
            }
            this.spawnGlowIndicator.setShader(BLUE_SHADER[0],BLUE_SHADER[1],BLUE_SHADER[2],BLUE_SHADER[3]);
        } else if (this.elapsedTime < this.orangeEnd){
            if (! Arrays.equals(spawnGlowIndicator.getShader(),ORANGE_SHADER)){
                if (this.expansionAnim != null) this.expansionAnim.cancel();
                this.expansionAnim = new ExpansionAnim(this.spawnGlowIndicator,ExpansionAnim.DEFAULT_MILLIS,0,0);
            }
            this.spawnGlowIndicator.setShader(ORANGE_SHADER[0],ORANGE_SHADER[1],ORANGE_SHADER[2],ORANGE_SHADER[3]);
        } else {
            if (! Arrays.equals(spawnGlowIndicator.getShader(),BLUE_SHADER)){
                if (this.expansionAnim != null) this.expansionAnim.cancel();
                this.expansionAnim = new ExpansionAnim(this.spawnGlowIndicator,ExpansionAnim.DEFAULT_MILLIS,0,0);
            }
            this.spawnGlowIndicator.setShader(BLUE_SHADER[0],BLUE_SHADER[1],BLUE_SHADER[2],BLUE_SHADER[3]);
        }


        float h1 = Math.min(1,(float) (this.blue2End-this.elapsedTime)/(this.blue2End-this.orangeEnd)) * this.height/6;
        float h0 = h1;
        //h0 is the height of blue, h1 is the height of the bottom blue section
        if (this.elapsedTime < this.blue1End){
            //first 1 is to clear the orange section
            h0 += (1 + Math.min(1,(float)(this.blue1End - this.elapsedTime)/(this.blue1End))) * this.height/6;
        }

        this.blueFuelCell.setTransform(this.deltaX,this.deltaY + h0/2 - this.height/4,this.width/2,h0);


        //quadRenderer.renderQuad(Spawner.fuelCell,this.finalMatrix);

        if (this.elapsedTime < this.orangeEnd){
            //Spawner.fuelCell.setShader(FUEL_ORANGE_SHADER[0],FUEL_ORANGE_SHADER[1],FUEL_ORANGE_SHADER[2],FUEL_ORANGE_SHADER[3]);

            float h2 = Math.min(1,(float) (this.orangeEnd - this.elapsedTime)/(this.orangeEnd-this.blue1End)) * this.height/6;

            this.orangeFuelCell.setTransform(this.deltaX ,this.deltaY + h2/2 + h1 - this.height/4,this.width/2,h2);
        } else {
            this.renderables.remove(this.orangeFuelCell);
        }
    }


    public abstract void spawnOrangeEnemies(int numEnemies, CraterCollection<Enemy> orangeEnemies);

    public abstract void spawnBlueEnemies(int numEnemies, CraterCollection<Enemy> blueEnemies);


    /** Called by outside attacks whenever they intersect this spawner
     *
     * @param damage the amount to reduce the health by (a negative value would heal up the spawner)
     */
    public void damage(float damage){
        this.health -= damage;
        //need to find what value this matches too, we basically binary search, until we are close enough
        if (this.health <= 0){
            SoundLib.playSpawnerDeathSoundEffect();
            this.elapsedTime = blue2End;
            return;
        }

        float rhs = this.blue2End;
        float lhs = 0;

        while (lhs + EPSILON  < rhs){
            float mid = (rhs + lhs)/2;
            if (this.healthFunction.interpolate(mid) < this.health){
                rhs = mid;
            } else {
                lhs = mid;
            }
            Log.d("SPAWNER","INTERPELOATING MID (x): " + mid + " F(mid) " +  this.healthFunction.interpolate(mid) + " target " + this.health);
        }

        this.elapsedTime = (long) lhs;



        Log.d("SPAWNER","took " + damage + " damage, health is now: " + health) ;

    }

    /** Sees whether the spawner has more than 0 health, and is thus alive
     *
     * @return whether or not the spawner has more than 0 health; whether or not it is alive and capable of spawning enemies
     */
    private boolean isAlive(){
        return this.health > 0;
    }

    /** Sees whether this hit box collides with a line. If the line segment is completely enclosed by the line it returns true
     *
     * @param x1 deltaX coordinate of point 1
     * @param y1 y coordinate of point 1
     * @param x2 deltaX coordinate of point 2
     * @param y2 y coordinate of point 2
     * @return whether the line collides
     */
    public boolean collidesWithLine(float x1,float y1,float x2,float y2){
        if (MathOps.lineIntersectsLine(x1,y1,x2,y2, deltaX +width,deltaY, deltaX +width,deltaY+height) ||
            MathOps.lineIntersectsLine(x1,y1,x2,y2, deltaX,deltaY, deltaX,deltaY+height) ||
            MathOps.lineIntersectsLine(x1,y1,x2,y2, deltaX,deltaY+height, deltaX +width,deltaY+height) ||
            MathOps.lineIntersectsLine(x1,y1,x2,y2, deltaX,deltaY, deltaX +width,deltaY)) {
            return true;
        }

        return x1 > deltaX && x2 < deltaX + this.width && y1 > deltaY && y2 < deltaY + this.height;

    }

    /** Sees if this collides with a circle
     *
     * @param x the center deltaX of the circle
     * @param y the center y of the circle
     * @param r the radius of the circle
     * @return whether the circle collides with this or not
     */
    public boolean collidesWithCircle(float x,float y,float r){
        return Math.hypot(this.deltaX - x ,this.deltaY - y ) < r + this.width/2;
    }


}
