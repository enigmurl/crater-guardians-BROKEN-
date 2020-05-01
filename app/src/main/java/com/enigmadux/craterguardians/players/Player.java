package com.enigmadux.craterguardians.players;

import android.content.Context;
import android.graphics.Point;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.animations.DeathAnim;
import com.enigmadux.craterguardians.animations.EvolveAnim;
import com.enigmadux.craterguardians.animations.ColoredShader;
import com.enigmadux.craterguardians.animations.ScreenShake;
import com.enigmadux.craterguardians.animations.ShootAnimation;
import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.gameobjects.Plateau;
import com.enigmadux.craterguardians.gameobjects.Shield;
import com.enigmadux.craterguardians.gameobjects.ToxicLake;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.util.SoundLib;

import java.util.ArrayList;
import java.util.LinkedList;

import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

/** Updated Player Class, much more efficient
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Player implements Character {

    public static final int[] UPGRADE_COSTS = new int[] {10,15,25,45,75,130,210,320,450,575,
            800,1200,1700,2650,3500,5000,8100,12000,17000,25000,
            35000,52000,64300,83200,126000,154000,188000,240000,350000,1500000
        };
    static final float TOXIC_LAKE_SLOWNESS = 0.1f;


    private static final float[] STRENGTH_MULTS = new float[] {15.8f,8f,1,1};

    //to avoid floating point errors
    private static final int NUM_EVOLVE_TICKS = 100000;

    //default slowness when in toxic lakes, but subclasses can do technicayl what ever they want

    private static Shield shield;


    /** The center x in openGL terms
     *
     */
    private float x;
    /** The center y in openGL terms
     *
     */
    private float y;

    /** The radius in openGL terms (half the width, half the height)
     *
     */
    private float r;


    /** All the rotatableEntities that need to be rendered that rotate with the joystick
     *
     */
    protected ArrayList<QuadTexture> rotatableEntities = new ArrayList<>();

    /** All the rotatableEntities that need to be rendered that stay the the same orientation regardless
     * of movement joystick, nor is affected by the scale of the player
     *
     */
    protected ArrayList<QuadTexture> staticEntities = new ArrayList<>();

    /** This is the health variable, by default if it's over 0 that indicates this character is alive
     *
     */
    protected int health;

    public float deltaX;

    public float deltaY;

    public float vX,vY;
    //the lakes the player is in as of now
    protected LinkedList<ToxicLake> activeLakes = new LinkedList<>();

    //in degreees
    private float rotation;

    private final float[] transformMatrix = new float[16];
    private final float[] finalMatrix = new float[16];



    //from 0 to NUM_EVOLVE_TICKS
    protected int evolveCharge;
    protected int evolveGen;


    //some arbritarly largenumber, but shouldn't overflow
    protected long millisSinceLastAttack = 1000000L;
    protected long millisBetweenAttacks;



    protected int numLoadedAttacks;
    protected long millisTillFinishedReloading = 0;


    //what makes it red when players are hit
    private ColoredShader currentShader;

    protected Shield currentShield;


    /** Default Constructor
     *
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     * @param r the radius in openGL terms (half the width, half the height)
     * @param millisBetweenAttacks m
     */
    public Player(float x,float y,float r,long millisBetweenAttacks){
        //assign variables to attributes
        this.x = x;
        this.y = y;
        this.r = r;
        this.millisBetweenAttacks = millisBetweenAttacks;
        this.spawn();

        this.numLoadedAttacks = this.getMaxAttacks();
    }

    public void loadComponents(Context context){
        //reset the componnents to avoid null pointers
        this.staticEntities.clear();
        this.rotatableEntities.clear();
        this.addStaticEntities(context);
        this.addRotatableEntities(context);
    }

    public static void loadTexture(Context context){
        shield = new Shield(Shield.DEFAULT_SWEEP,Shield.DEFAULT_RADIUS,context);
    }


    public void spawn(){
        this.health = getMaxHealth();
        this.evolveGen = 0;
        this.evolveCharge = 0;
        this.numLoadedAttacks = getMaxAttacks();


        this.activeLakes.clear();
        //facing up because
        this.rotation = 90;
        if (currentShield != null) this.currentShield.setState(false);


    }

    private void startReloading(){
        this.millisTillFinishedReloading = this.getReloadingTime();
    }

    private void finishReloading(){
        this.numLoadedAttacks = this.getMaxAttacks();
    }

    @Override
    public void translateFromPos(float dX, float dY) {
        this.deltaX += dX;
        this.deltaY += dY;
    }

    @Override
    public void setTranslate(float dX, float dY) {
        this.deltaX = dX;
        this.deltaY = dY;
    }

    /** Draws the player and sub components, given a quad renderer and the model view projection matrix
     *
     * @param mvpMatrix a 4x4 model view projection matrix that describes the outside transforms
     * @param quadRenderer a quadRenderer object, that helps actually put textures onto the screen
     */
    public void draw(float[] mvpMatrix, QuadRenderer quadRenderer){
        if (health > 0) {
            Matrix.setIdentityM(transformMatrix, 0);
            Matrix.translateM(transformMatrix, 0, x + deltaX, y + deltaY, 0);
            Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, transformMatrix, 0);
            quadRenderer.renderQuads(this.staticEntities,finalMatrix);

            Matrix.scaleM(transformMatrix, 0, r * 2, r * 2, 0);
            Matrix.rotateM(transformMatrix, 0, this.rotation, 0, 0, 1);
            Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, transformMatrix, 0);
            quadRenderer.renderQuads(this.rotatableEntities, finalMatrix);
        }
    }

    public void update(long dt, World world){
        this.millisSinceLastAttack += dt;
        if (this.health <= 0){
            this.finish(world);
            world.completeLevelLost();
            return;
        }

        if (this.millisTillFinishedReloading > 0) {
            this.millisTillFinishedReloading -= dt;
            if (this.millisTillFinishedReloading <= 0){
                this.finishReloading();
            }
        }

        //don't need plateau lock because nothing can change it
        ArrayList<Plateau> plats = world.getPlateaus().getInstanceData();
        for (int i = 0,size = plats.size();i<size;i++){
           plats.get(i).clipCharacterPos(this);
        }

        //similar to Kaiser todo, referencing a static class like our own attribute bc only one max instance is bad solution
        currentShield.setTranslation(deltaX,deltaY);
        currentShield.update(world,dt);

    }



    //death
    protected void finish(World world){
        SoundLib.playPlayerDeathSoundEffect();
        world.completeLevelLost();
        synchronized (World.animationLock) {
            world.getAnims().add(new DeathAnim(this.getDeltaX(),this.getDeltaY(),2 * this.getRadius(),2 * this.getRadius()));
        }
    }


    /** Damages the player along, that's it for now but maybe in the future
     *
     * @param damage the amount of damage the character must take, a >0 value will decrease the health, <0 will increase, =0 will do nothing
     */
    @Override
    public void damage(float damage) {
        if (this.currentShader != null){
            this.currentShader.cancel();
        }
        this.currentShader = new ColoredShader(this, ColoredShader.DEFAULT_LEN, true);
        this.health -= damage;

    }

    public void attemptAttack(World world,float cos,float sin){
        Log.d("PLAYER","Attacking: " + cos + " sin: " + sin);
        float angle = MathOps.getAngle(cos, sin);
        if (this.millisSinceLastAttack > this.millisBetweenAttacks && this.numLoadedAttacks > 0){
            this.numLoadedAttacks--;
            this.millisSinceLastAttack = 0;
            this.attack(world,angle);
            if (this.numLoadedAttacks == 0) this.startReloading();

        }
    }


    //in degrees
    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public float getDeltaX(){
        return this.deltaX;
    }

    public float getDeltaY(){
        return this.deltaY;
    }

    public Shield getShield() {
        return currentShield;
    }

    @Override
    public float getRadius() {
        return this.r;
    }


    public LinkedList<ToxicLake> getActiveLakes(){
        return this.activeLakes;
    }

    //-1 indicates that it's fully evolved, 1 indicates it's ready
    public float getEvolveCharge(){
        if (this.evolveGen == this.getNumGens() - 1) return -1;
        if (this.evolveCharge >= NUM_EVOLVE_TICKS) return 1;
        return this.evolveCharge/(float) NUM_EVOLVE_TICKS;
    }

    public void reportDamageDealt(float damageDealt,Object damaged){
        if (damaged instanceof Enemy) {
            int strength = ((Enemy) damaged).getStrength();
            float charge = STRENGTH_MULTS[strength] * damageDealt;
            this.evolveCharge += NUM_EVOLVE_TICKS * charge / this.getDamageForEvolve();
            this.evolveCharge = Math.min(this.evolveCharge, NUM_EVOLVE_TICKS);

        }
    }

    public void evolve(World world){
        //num gens -1, bc num Evolves = numGens - 1
        if (this.evolveCharge >= NUM_EVOLVE_TICKS && this.evolveGen < this.getNumGens() - 1){
           this.incrementEvolveGen(world);
        }
    }

    protected void incrementEvolveGen(World world){
        this.evolveCharge -= NUM_EVOLVE_TICKS;
        this.evolveGen ++;
        this.health = this.getMaxHealth();
        this.numLoadedAttacks = this.getMaxAttacks();
        synchronized (World.animationLock) {
            world.getAnims().add(new EvolveAnim(this.getDeltaX(),this.getDeltaY(),2 * this.r,2 * this.r));
        }
    }


    //stuff that don't rotate with movement joy stick
    protected void addStaticEntities(Context context){
        this.currentShield = shield;
        this.staticEntities.add(currentShield);
        Log.d("WORLD","Static Entities: " + staticEntities);
    }

    public int getHealth(){
        return this.health;
    }

    public int getNumLoadedAttacks(){
        return this.numLoadedAttacks;
    }

    //angle is in radians
    void attack(World world,float angle){
        synchronized (World.animationLock) {
            ShootAnimation shootAnim = new ShootAnimation(deltaX, deltaY, ShootAnimation.STANDARD_DIMENSIONS, ShootAnimation.STANDARD_DIMENSIONS);
            world.getAnims().add(shootAnim);
        }
    }


    public float getVelocityX(){
        return vX;
    }
    public float getVelocityY(){
        return vY;
    }
    public void setVelocity(float x,float y){
        this.vX = x;
        this.vY = y;
    }



    /** This method should add the needed rotatableEntities to the "rotatableEntities" array list, so that it can be drawn.
     * If you want these rotatableEntities to be drawn below, add it to the beginning of the array, using
     * add(0,[object]), if you want to be drawn at the very top, use add([object])
     *
     * @param context
     */
    protected abstract void addRotatableEntities(Context context);


    //should account for number of enemies on the shield, and amount of toxic lakes
    @Override
    public abstract float getCharacterSpeed();

    public abstract float getDamageForEvolve();

    public abstract int getNumGens();

    public abstract int getMaxHealth();

    //R.drawable.*
    public abstract int getPlayerIcon();

    //R.drawable.*;
    public abstract int getPlayerInfo();
    //R.drawble.*
    public abstract  int getAttackSpritesheetPointer();

    public abstract int getPlayerLevel();

    public abstract void setPlayerLevel(int level);

    //in millis
    public abstract long getReloadingTime();
    //number of attacks
    public abstract int getMaxAttacks();

    public void resetShader(){
        this.setShader(1,1,1,1);
    }
}
