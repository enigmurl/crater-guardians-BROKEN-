package com.enigmadux.craterguardians.Characters;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.AngleAimers.AngleAimer;
import com.enigmadux.craterguardians.AngleAimers.TriangleAimer;
import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.ProgressBar;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.Shield;
import com.enigmadux.craterguardians.SoundLib;
import com.enigmadux.craterguardians.Spawners.Spawner;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

public abstract class Player extends BaseCharacter {
    //if an attack hits the player through the shield, how much of the total damage reaches
    public static final float SHIELD_DAMAGE_ALLOWED = 0.5f;
    //the angle from start to finish of the shield (radians)
    private static final float SHIELD_SWEEP = 0.5f;

    /** The width in openGL terms of any character
     *
     */
    public static final float CHARACTER_WIDTH = 0.3f;
    /** The height in openGL terms of any character
     *
     */
    public static final float CHARACTER_HEIGHT = 0.3f;

    //the amount of attacks it can hold maximum (think of it as the amount of bullets in a magazine)
    private int maxAttacks;
    //the amount of milliseconds it takes to reload
    private long reloadTime;

    //whenever a attack lands, there is a "bonus" damage that is gained when successive attacks are hit, this shows the player how much that is
    protected ProgressBar attackChargeUp;

    //if reloading, the amount of milliseconds it has remaining
    private long millisTillFinishedReloading;
    //the current amount of attacks the player has before they have to reload
    protected int numAttacks;

    //final matrix = parent * translation * scalar
    private float[] finalMatrix = new float[16];
    //translation * scalar
    private float[] scalarTranslationMatrix = new float[16];
    //scales the attack visual to the appropriate amount
    private float[] scalarMatrix = new float[16];
    //translate the attack visual to the appropriate place
    private float[] translationMatrix = new float[16];

    //the generation in evolve (0 = default, 1 = next...)
    protected int evolveGen = 0;

    /** This is the visual that shows the user how many attacks there are
     *
     */
    private static final TexturedRect ATTACK_VISUAL = new TexturedRect(0,0,1,1f);

    /** Shows the user the angle at which they plan to attack
     *
     */
    protected AngleAimer attackAngleAimer;

    //when entering toxic lakes, the player is slowed down, this is how long the effect last
    private long speedEffectMillis = 0;
    //this is the severity of the affect (high multiplier speeds up, fractions slow it down)
    protected float speedMultiplier = 1;

    //this says how much of the evolution charge it has gained (0 = not at atll, 1= fully)
    protected float evolutionCharge = 0f;


    //shows the user the angle at which they plan to defensd
    private AngleAimer defenseAngleAimer;
    //visual shield shown when it's being use
    private Shield shield;
    //how long the active shield last for
    private long shieldMillis = 0;
    //the middle angle of the shield in radians
    private float shieldRadians = 0;

    //the angle at which the player is moving
    private float rotation;

    /** Default constructor
     *
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Player(int numRotationOrientations, int framesPerRotation,float fps){
        super(numRotationOrientations, framesPerRotation, fps);

        this.defenseAngleAimer = new TriangleAimer(0,0,SHIELD_SWEEP,0,this.getH() *2);
        this.defenseAngleAimer.hide();

        this.attackAngleAimer = this.createAngleAimer();
        this.attackAngleAimer.hide();

        this.attackChargeUp = this.createAttackChargeUp();

        this.maxAttacks = this.getNumAttacks();
        this.reloadTime = this.getReloadTime();

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,this.getW()/maxAttacks,0.1f,1);
    }


    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(GL10 gl, Context context) {
        ATTACK_VISUAL.loadGLTexture(gl,context, R.drawable.ammo_visual);
    }

    /** The sub class should create and return an angle aimer that matches the attack shape
     *
     * @return the sub classes angle aimer that matches
     */
    protected abstract AngleAimer createAngleAimer();

    /** The sub class should create and return a progress bar that illustrates the attack charge up
     *
     * @return the sub classes attack charge up visual
     */
    protected abstract ProgressBar createAttackChargeUp();

    /** Helper method that helps computes the alpha during the flashing animation: todo make it so values where the reload time is high it does more flashes as to keep each wavelength the same
     *
     * @param t amount of time thats passed by (from 0 to 1)
     * @return the alpha value of the ammo bar (from 0 to 1, 0 being completly transparent)
     */
    private float getAmmoBarAlpha(float t){
        if (t < 1f/6){
            return 3 * t;
        }
        if (t > 5f/6){
            return 3 * t - 2;
        }

        return - (float) Math.sin(t * Math.PI * 6)/4 + 0.5f;
    }

    /** Draws the player and
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        if (! this.visible){
            return;
        }

        for (Attack attack: this.attacks){
            attack.draw(gl,parentMatrix);
        }
        Matrix.setIdentityM(this.translationMatrix,0);
        Matrix.translateM(this.translationMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        if (this.shieldMillis > 0){
            Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationMatrix,0);
            this.shield.draw(gl,finalMatrix);
        }

        for (int i = 0;i < numAttacks;i++){
            Matrix.setIdentityM(translationMatrix,0);
            Matrix.translateM(translationMatrix,0,this.getDeltaX() + this.getW() * ((float) i/maxAttacks  -0.5f),this.getDeltaY() + this.getH()/2,0 );//0.05 is half of the
            Matrix.multiplyMM(scalarTranslationMatrix,0,translationMatrix,0,scalarMatrix,0);
            Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,scalarTranslationMatrix,0);

            ATTACK_VISUAL.setShader(1,1,1,1);
            ATTACK_VISUAL.draw(gl,finalMatrix);
        }
        //does the flashing animation
        if (this.numAttacks == 0){
            for (int i = 0;i < maxAttacks;i++){
                Matrix.setIdentityM(translationMatrix,0);
                Matrix.translateM(translationMatrix,0,this.getDeltaX() + this.getW() * ((float) i/maxAttacks  -0.5f),this.getDeltaY() + this.getH()/2,0 );//0.05 is half of the
                Matrix.multiplyMM(scalarTranslationMatrix,0,translationMatrix,0,scalarMatrix,0);
                Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,scalarTranslationMatrix,0);

                ATTACK_VISUAL.setShader(0.5f,1,1,getAmmoBarAlpha ((float) (reloadTime - millisTillFinishedReloading)/reloadTime));
                ATTACK_VISUAL.draw(gl,finalMatrix);
            }
        }



        this.attackAngleAimer.setPosition(this.getDeltaX(),this.getDeltaY());
        this.attackAngleAimer.draw(gl,parentMatrix);

        this.defenseAngleAimer.setPosition(this.getDeltaX(),this.getDeltaY());
        this.defenseAngleAimer.draw(gl,parentMatrix);

        this.attackChargeUp.update(this.attackChargeUp.getCurrentHitPoints(),this.getDeltaX()-this.getW()/2,this.getDeltaY() + this.getH()/2 + 0.1f);
        this.attackChargeUp.draw(gl,parentMatrix);

    }

    @Override
    public void spawn() {
        super.spawn();
        this.attackChargeUp.update(0,0,0);
        this.evolutionCharge = 0;
        this.numAttacks = maxAttacks;
        this.millisTillFinishedReloading = reloadTime;
        this.evolveGen = 0;
        this.attackAngleAimer = this.createAngleAimer();
    }

    /** shows the angle aimer
     *
     */
    public void showAngleAimer(){
        this.attackAngleAimer.show();
    }

    /** Hides the angle aimer; it is not drawn to the screen
     *
     */

    public void hideAngleAimer() {
        this.attackAngleAimer.hide();
    }

    /** Sets the angle of the angle aimer
     *
     * @param angle the angle of the angle aimer in degrees (open gl takes in degrees)
     */
    public void setAngleAimerAngle(float angle){
        this.attackAngleAimer.setMidAngle(angle);
    }
    /** shows the angle aimer
     *
     */
    public void showDefenseAngleAimer(){
        this.defenseAngleAimer.show();
    }

    /** Hides the angle aimer; it is not drawn to the screen
     *
     */

    public void hideDefenseAngleAimer() {
        this.defenseAngleAimer.hide();
    }

    /** Sets the angle of the angle aimer
     *
     * @param angle the angle of the angle aimer in degrees (open gl takes in degrees)
     */
    public void setDefenseAngleAimerAngle(float angle){
        this.defenseAngleAimer.setMidAngle(angle);
    }

    /** Gets the health of the current player (amount of damage it can take before being considered dead)
     *
     * @return the health of the current player
     */
    public int getCurrentHealth(){
        return this.health;
    }

    /** this says how much of the evolution charge it has gained (0 = not at at all, 1= fully)
     *
     * @return the evolution charge
     */
    public float getEvolutionCharge(){
        return this.evolutionCharge;
    }



    /** Updates the frame, and the attacks
     *
     * @param dt amount of time since last call in milliseconds
     * @param rotation rotation of the player
     * @param enemies all possible enemies on the game map
     * @param spawners  all possible spawners on the game map used to see if the atttacks
     */
    public void update(long dt, float rotation, List<Enemy> enemies, List<Spawner> spawners) {
        super.update(dt, rotation);

        this.rotation = rotation;

        Iterator itr = attacks.iterator();

        while (itr.hasNext()){
            Attack attack = (Attack) itr.next();
            if (attack.isFinished()){
                itr.remove();
            }
            attack.update(dt);
            for (Enemy enemy: enemies){
                attack.attemptAttack(enemy);
            }

            for (Spawner spawner: spawners){
                attack.attemptAttack(spawner);
            }
        }

        if (this.numAttacks == 0){
            this.millisTillFinishedReloading -= dt;
            if (this.millisTillFinishedReloading < 0){
                this.millisTillFinishedReloading = reloadTime;
                this.numAttacks = maxAttacks;
                Log.d("PLAYER:","Finished reloading");
            }
        }

        if (this.shieldMillis > 0) {
            shieldMillis -= dt;
        }


            this.speedEffectMillis -= dt;
        if (this.speedEffectMillis < 0){
            this.speedMultiplier = 1;
        }

        this.attackChargeUp.update(Math.max(0,this.attackChargeUp.getCurrentHitPoints() - (int) dt),0,0);

    }

    /** When entering toxic lakes, the speed is changed
     *
     * @param speedMultiplier how much slower/faster the speed will be
     * @param speedEffectMillis how long the effect will last
     */
    public void addSpeedEffect(float speedMultiplier,long speedEffectMillis){
        this.speedMultiplier = speedMultiplier;
        this.speedEffectMillis = speedEffectMillis;
    }

    /** If enough damage was dealed, and the evolve button is pressed, the player evolves to a superior state
     *
     * @return Whether or not it evolved (true = evolved, false = not)
     */
    public abstract boolean attemptEvolve();

    /** This is the amount of attacks it can perform before it needs to reload
     *
     * @return integer amount of attacks it can perform before it needs to reload
     */
    public abstract int getNumAttacks();


    /** The time in milliseconds it takes to fully reload
     *
     * @return amount of milliseconds it takes to fully reload
     */
    public abstract long getReloadTime();

    /** When an attack makes contact with an enemy, the evolve ability is charge slighlty, this tells how much to increase it by
     *
     * @param damage  how much damage was dealt to the enemy;
     */
    public abstract void gainEvolveCharge(int damage);

    /** Whenever an attack does not hit an enemy at all, the bonus is reset to 0. This method is called whenever that happens
     *
     */
    public void failedAttack(){
        this.attackChargeUp.update(0,this.getDeltaX(),this.getDeltaY());
    }

    /** When an attack wants to damage the player, it needs to see if it hits the shield todo because only the middle angle is taken into account, there are false positive and negatives
     *
     * @param startingDamage how much damage it would do if there was no shield
     * @param angle the angle in radians, in relation to the player, as in, what angle does the enemy attack make with the player
     * @return the damage to be applied to the player depending on the shield
     */
    public int getAttackDamage(int startingDamage,float angle) {
        if (this.shieldMillis > 0 && angle > shieldRadians - SHIELD_SWEEP && angle < shieldRadians + SHIELD_SWEEP) {
            SoundLib.playPlayerShieldBlockSoundEffect();
            return (int) (startingDamage * SHIELD_DAMAGE_ALLOWED);
        }
        return startingDamage;
    }

    /** Spawns a shield for the specified duration at an angle, this reduces the damages for attacks coming at the angle
     *
     *
     * @param duration length of the shield in millis
     * @param angle middle angle of the shield in radians
     */
    public void spawnShield(long duration,float angle){
        SoundLib.playPlayerSpawnShieldSoundEffect();

        this.shieldMillis = duration;
        this.shieldRadians = angle;

        this.hideDefenseAngleAimer();
        this.shield = new Shield(2*this.getW(),2*this.getH(),angle);
    }

    /** Whether or not the health is greater than 0, also plays sound effect if it's dead
     *
     * @return whether or not this is alive (See BaseCharacter for full documentation)
     */
    @Override
    public boolean isAlive() {
        if (super.isAlive()){
            return true;
        } else {
            SoundLib.playPlayerDeathSoundEffect();
            return false;
        }
    }

    /** Gets the angle in radians at which the player is currently moving at
     *
     * @return gets the angle in radians at which the player is currently moving at, if it's stationary it returns the last moving angle
     */
    public float getRotation() {
        return this.rotation;
    }

    /** Gets the width of the players
     *
     * @return the width of any player
     */
    @Override
    public float getW() {
        return Player.CHARACTER_WIDTH;
    }
    /** Gets the height of the players
     *
     * @return the height of any player
     */
    @Override
    public float getH() {
        return Player.CHARACTER_HEIGHT;
    }

    /** Tries to attack if there are any, it also plays sound effects if it can attack
     *
     * @param angle the angle at which to attack (in radians)
     */
    @Override
    public void attack(float angle) {
        if (this.numAttacks > 0){
            SoundLib.playPlayerShootSoundEffect();
        } else {
            //todo: make a ("no current attacks" sound effect/reloading)
        }
    }
}