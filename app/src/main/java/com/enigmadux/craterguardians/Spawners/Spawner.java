package com.enigmadux.craterguardians.Spawners;


import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;

import java.util.List;

import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.quadRendering.QuadRenderer;
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

    /** A Blue or orange circle that is semi transparent
     *
     */
    private static QuadTexture stateIndicator;

    /** A rectangle that shows the amount of fuel
     *
     */
    private static QuadTexture fuelCell;


    /** A arbirtarily small number
     *
     */
    private static final float EPSILON = 0.1f;


    //amount of health before dieing
    protected float health;



    private long elapsedTime;


    /** Where data is dumped too for the final matrix
     *
     */
    private final float[] finalMatrix = new float[16];
    //translationMatrix*scalarMatrix
    private final float[] translationScalarMatrix = new float[16];
    //aditional translation
    private final float[] fuelCellMatrix = new float[16];
    //fuel scalar
    private final float[] fuelScalarMatrix = new float[16];
    //fuel translation
    private final float[] fuelTranslationMatrix = new float[16];

    //parameters of the equation ax^3 + bx^2 + cx + d
    private CubicSolver healthFunction;


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


    public Spawner(int instanceId, float x, float y, float w, float h, int orangeEndHealth, int blue1EndHealth, int maxHealth,
                   long blue1, long orange, long blue2,
                   short[] numBlueSpawns, long[] blueSpawnJuice,
                   short[] numOrangeSpawns, long[] orangeSpawnJuice){

        super(instanceId);
        this.deltaX = x;
        this.deltaY = y;
        this.width = w;
        this.height = h;

        this.health = maxHealth;



        //cubic
        //such that f(0) = maxHealth,
        //f(blue1) = health0;
        //f(blue1 + orange) = health1,
        //f(blue1 + orange + blue2) = 0;

        this.healthFunction = new CubicSolver(
                0,maxHealth,
                blue1, blue1EndHealth,
                blue1 + orange, orangeEndHealth,
                blue1 + orange + blue2,0
        );



        this.blue1End = blue1;
        this.orangeEnd = blue1 + orange;
        this.blue2End = this.orangeEnd + blue2;

        this.numBlueSpawns = numBlueSpawns;
        this.numOrangeSpawns = numOrangeSpawns;
        this.blueSpawnJuice = blueSpawnJuice;
        this.orangeSpawnJuice = orangeSpawnJuice;


        //translates to appropriate coordinates
        final float[] translationMatrix = new float[16];
        //scales to appropriate size
        final float[] scalarMatrix = new float[16];

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix,0,this.deltaX + w/2,this.deltaY + h/2,0);

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,GLOW_RADIUS * w,GLOW_RADIUS * h,0);

        Matrix.multiplyMM(translationScalarMatrix,0,translationMatrix,0,scalarMatrix,0);

    }

    /** Loads up textures
     *
     * @param context the context to load resources
     */
    public static void loadGLTexture(Context context){
        Spawner.stateIndicator = new QuadTexture(context, R.drawable.spawner_glow,0,0,1,1);
        Spawner.fuelCell = new QuadTexture(context,R.drawable.spawner_fuel_cell,0,0,1,1);
    }

    /** Draws the VISUAL_REPRESENTATION customized for the subclass
     *
     * @param parentMatrix describes how to change from model to world coordinates
     * @param quadRenderer renders
     */
    public void draw(float[] parentMatrix, QuadRenderer quadRenderer){
        //this.healthDisplay.update((int) this.health,this.deltaX,this.height  + this.deltaY);
        //this.healthDisplay.draw(parentMatrix);

        //scale and place it in the right location

        if (this.elapsedTime < this.blue1End){
            Spawner.stateIndicator.setShader(BLUE_SHADER[0],BLUE_SHADER[1],BLUE_SHADER[2],BLUE_SHADER[3]);
        } else if (this.elapsedTime < this.orangeEnd){
            Spawner.stateIndicator.setShader(ORANGE_SHADER[0],ORANGE_SHADER[1],ORANGE_SHADER[2],ORANGE_SHADER[3]);
        } else {
            Spawner.stateIndicator.setShader(BLUE_SHADER[0],BLUE_SHADER[1],BLUE_SHADER[2],BLUE_SHADER[3]);
        }

        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);


        quadRenderer.renderQuad(Spawner.stateIndicator,this.finalMatrix);

        //background
        Spawner.fuelCell.setShader(0,0,0,1);
        Matrix.setIdentityM(this.fuelTranslationMatrix,0);
        Matrix.setIdentityM(this.fuelScalarMatrix,0);

        Matrix.scaleM(this.fuelScalarMatrix,0,this.width/2,this.height/2,0);
        Matrix.translateM(this.fuelTranslationMatrix,0,this.deltaX +this.width/2,this.deltaY + this.height/2f,0);
        Matrix.multiplyMM(this.fuelCellMatrix,0,this.fuelTranslationMatrix,0,this.fuelScalarMatrix,0);
        Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.fuelCellMatrix,0);

        quadRenderer.renderQuad(Spawner.fuelCell,this.finalMatrix);



        //blue 1
        Spawner.fuelCell.setShader(FUEL_BLUE_SHADER[0],FUEL_BLUE_SHADER[1],FUEL_BLUE_SHADER[2],FUEL_BLUE_SHADER[3]);

        Matrix.setIdentityM(this.fuelTranslationMatrix,0);
        Matrix.setIdentityM(this.fuelScalarMatrix,0);

        float h1 = Math.min(1,(float) (this.blue2End-this.elapsedTime)/(this.blue2End-this.orangeEnd)) * this.height/6;
        Matrix.scaleM(this.fuelScalarMatrix,0,this.width/2,h1,0);
        Matrix.translateM(this.fuelTranslationMatrix,0,this.deltaX + this.width/2,this.deltaY + h1/2 + this.height/4,0);
        Matrix.multiplyMM(this.fuelCellMatrix,0,this.fuelTranslationMatrix,0,this.fuelScalarMatrix,0);
        Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.fuelCellMatrix,0);

        quadRenderer.renderQuad(Spawner.fuelCell,this.finalMatrix);

        if (this.elapsedTime < this.orangeEnd){
            Spawner.fuelCell.setShader(FUEL_ORANGE_SHADER[0],FUEL_ORANGE_SHADER[1],FUEL_ORANGE_SHADER[2],FUEL_ORANGE_SHADER[3]);
            Matrix.setIdentityM(this.fuelTranslationMatrix,0);
            Matrix.setIdentityM(this.fuelScalarMatrix,0);
            float h2 = Math.min(1,(float) (this.orangeEnd - this.elapsedTime)/(this.orangeEnd-this.blue1End)) * this.height/6;
            Matrix.scaleM(this.fuelScalarMatrix,0,this.width/2,h2,0);
            Matrix.translateM(this.fuelTranslationMatrix,0,this.deltaX + this.width/2,this.deltaY+ h2/2 + h1 + this.height/4,0);

            Matrix.multiplyMM(this.fuelCellMatrix,0,this.fuelTranslationMatrix,0,this.fuelScalarMatrix,0);
            Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.fuelCellMatrix,0);
            quadRenderer.renderQuad(Spawner.fuelCell,this.finalMatrix);

            if (this.elapsedTime < this.blue1End){
                Spawner.fuelCell.setShader(FUEL_BLUE_SHADER[0],FUEL_BLUE_SHADER[1],FUEL_BLUE_SHADER[2],FUEL_BLUE_SHADER[3]);
                Matrix.setIdentityM(this.fuelTranslationMatrix,0);
                Matrix.setIdentityM(this.fuelScalarMatrix,0);

                float h3 = Math.min(1,(float)(this.blue1End - this.elapsedTime)/(this.blue1End)) * this.height/6;
                Matrix.scaleM(this.fuelScalarMatrix,0,this.width/2,h3,0);
                Matrix.translateM(this.fuelTranslationMatrix,0,this.deltaX + this.width/2,this.deltaY + h3/2 +  h2 + h1 + this.height/4,0);

                Matrix.multiplyMM(this.fuelCellMatrix,0,this.fuelTranslationMatrix,0,this.fuelScalarMatrix,0);
                Matrix.multiplyMM(this.finalMatrix,0,parentMatrix,0,this.fuelCellMatrix,0);

                quadRenderer.renderQuad(Spawner.fuelCell,this.finalMatrix);
            }
        }



    }



    /** Updates the spawner, and retunr
     * Instead of spawning a single enemy, sometimes, a wave spawner is more suitable
     *
     * @param dt milliseconds since last call
     * @param enemiesCollection where enemies vertex data will be dumped
     * @return If enemies are to be spawned, it returns the list of enemies otherwise null
     */
    public List<Enemy> update(long dt,VaoCollection enemiesCollection){

        //this.health -= (float) this.healthDisplay.getMaxHitPoints() * dt/this.decayTime;
        this.health = this.healthFunction.interpelate((float) (this.elapsedTime));
        this.elapsedTime += dt;

        //blue mode
        if (this.elapsedTime < this.blue1End || this.elapsedTime > this.orangeEnd){
            this.currentBlueJuice += dt;
            if (this.currentBlueJuice >= this.blueSpawnJuice[this.bluePointer]){
                this.currentBlueJuice = 0;
                int wavePointer = this.bluePointer;
                this.bluePointer = (this.bluePointer + 1)%this.blueSpawnJuice.length;
                return this.spawnBlueEnemies(this.numBlueSpawns[wavePointer],enemiesCollection);
            } else {
                return null;
            }
        }
        //orange mode
        else{
            this.currentOrangeJuice += dt;
            if (this.currentOrangeJuice >= this.orangeSpawnJuice[this.orangePointer]){
                this.currentOrangeJuice = 0;
                int wavePointer = this.orangePointer;
                this.orangePointer = (this.orangePointer + 1) % this.orangeSpawnJuice.length;
                return this.spawnOrangeEnemies(this.numOrangeSpawns[wavePointer],enemiesCollection);
            } else {
                return null;
            }
        }



        //if (this.waveIndex < this.spawnTime.length && this.millisSinceLastSpawn > this.spawnTime[waveIndex]){
        //   return this.spawnEnemies(this.numSpawns[waveIndex++],enemiesCollection);

        //}

        //return null;
    }


    public abstract List<Enemy> spawnOrangeEnemies(int numEnemies, VaoCollection enemiesCollection);

    public abstract List<Enemy> spawnBlueEnemies(int numEnemies,VaoCollection enemiesCollection);


    /** Called by outside attacks whenever they intersect this spawner
     *
     * @param damage the amount to reduce the health by (a negative value would heal up the spawner)
     */
    public void damage(int damage){
        this.health -= damage;
        //need to find what value this matches too, we basically binary search, until we are close enough
        //todo see how to deal with skipping over spawns, most likely we have to make it like a "juice" system
        //    where you don't do based on times, but amount of juice required, however you can't use juice from
        //    different color
        if (this.health <= 0){
            this.elapsedTime = blue2End;
            return;
        }

        float rhs = this.blue2End;
        float lhs = 0;

        while (lhs + EPSILON  < rhs){
            float mid = (rhs + lhs)/2;
            if (this.healthFunction.interpelate(mid) < this.health){
                rhs = mid;
            } else {
                lhs = mid;
            }
            Log.d("SPAWNER","INTERPELOATING MID (x): " + mid + " F(mid) " +  this.healthFunction.interpelate(mid) + " target " + this.health);
        }

        this.elapsedTime = (long) lhs;



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
        return Math.hypot(this.deltaX - x + this.width/2,this.deltaY - y +this.height/2) < r + this.width/2;
    }


}
