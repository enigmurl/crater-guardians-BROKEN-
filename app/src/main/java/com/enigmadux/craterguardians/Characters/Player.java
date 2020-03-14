package com.enigmadux.craterguardians.Characters;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.AngleAimers.AngleAimer;
import com.enigmadux.craterguardians.Animations.EvolveAnimation;
import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.GUILib.ProgressBar;
import com.enigmadux.craterguardians.GameObjects.Shield;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;
import com.enigmadux.craterguardians.Spawners.Spawner;

import java.util.Iterator;
import java.util.List;

import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.shapes.TexturedRect;

public abstract class Player extends BaseCharacter {

    /** The radius in openGL terms of any character
     *
     */
    public static final float CHARACTER_RADIUS = 0.1f;


    //the shield sweep and radius (standard)
    private static final float SHIELD_SWEEP = 90f;
    //shield radius
    private static final float SHIELD_RADIUS = CHARACTER_RADIUS * 2;


    /** THe amount of milliseconds the evolving animation takes
     *
     */
    protected static final long EVOLVE_MILLIS = 1000;


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


    //the amount of time the player has left for its evolve, sub classes should update this after an evolve is called
    protected long millisSinceEvolve;

    //an animation used after an evolve, for now it's generic for player, but sub classes may have their individual ones
    protected EvolveAnimation evolveAnimation;


    //the angle at which the player is moving
    private float rotation;

    //the level of this character in upgradability
    protected int level;


    //the shield that blocks attacks
    private static Shield shield;
    //if shiled is on
    private boolean shieldOn;

    //degrees of rotation of shield
    private float shieldRotation;

    /** Default constructor
     *  @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Player(int numRotationOrientations, int framesPerRotation, float fps){
        super(numRotationOrientations, framesPerRotation, fps);

        this.attackAngleAimer = this.createAngleAimer();
        this.attackAngleAimer.hide();

        this.attackChargeUp = this.createAttackChargeUp();

        this.maxAttacks = this.getNumAttacks();
        this.reloadTime = this.getReloadTime();

        Matrix.setIdentityM(scalarMatrix,0);
        Matrix.scaleM(scalarMatrix,0,2*this.getRadius()/maxAttacks,0.1f,1);

        //this basically indicates it has never evolved.
        this.millisSinceEvolve = Player.EVOLVE_MILLIS;

    }


    /** Loads the texture of the sprite sheet
     *
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(Context context) {
        ATTACK_VISUAL.loadGLTexture(context, R.drawable.ammo_visual);
        shield = new Shield(Player.SHIELD_SWEEP,Player.SHIELD_RADIUS,context);
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
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        if (! this.visible){
            return;
        }

        shield.setState(this.shieldOn);
        //TODO Concurrent modification happening here sometimes also on line 178 a null pointer was thrown
        for (int i = 0,size = this.attacks.size();i<size;i++){
            this.attacks.get(i).draw(parentMatrix);
        }
        //Matrix.setIdentityM(this.translationMatrix,0);
        //Matrix.translateM(this.translationMatrix,0,this.getDeltaX(),this.getDeltaY(),0);



        //does the flashing animation
//        if (this.numAttacks == 0){
            //ATTACK_VISUAL.setShader(0.5f,1,1,getAmmoBarAlpha ((float) (reloadTime - millisTillFinishedReloading)/reloadTime));

//            for (int i = 0;i < maxAttacks;i++){
//                Matrix.setIdentityM(translationMatrix,0);
//                Matrix.translateM(translationMatrix,0,this.getDeltaX() + this.getRadius() * 2 * ((float) i/maxAttacks  -0.5f),this.getDeltaY() + this.getRadius(),0 );//0.05 is half of the
//                Matrix.multiplyMM(scalarTranslationMatrix,0,translationMatrix,0,scalarMatrix,0);
//                Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,scalarTranslationMatrix,0);
//
//                ATTACK_VISUAL.draw(finalMatrix);
//            }
//        } else {
            //ATTACK_VISUAL.setShader(1,1,1,1);

//            for (int i = 0;i < numAttacks;i++){
//                Matrix.setIdentityM(translationMatrix,0);
//                Matrix.translateM(translationMatrix,0,this.getDeltaX() + this.getRadius() * 2 * ((float) i/maxAttacks  -0.5f),this.getDeltaY() + this.getRadius(),0 );//0.05 is half of the
//                Matrix.multiplyMM(scalarTranslationMatrix,0,translationMatrix,0,scalarMatrix,0);
//                Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,scalarTranslationMatrix,0);
//
//                ATTACK_VISUAL.draw(finalMatrix);
//            }
//        }


//
//        this.attackAngleAimer.setPosition(this.getDeltaX(),this.getDeltaY());
//        this.attackAngleAimer.draw(parentMatrix);
//        this.attackChargeUp.update(this.attackChargeUp.getCurrentHitPoints(),this.getDeltaX()-this.getRadius(),this.getDeltaY() + this.getRadius() + 0.1f);
//        this.attackChargeUp.draw(parentMatrix);

        if (this.evolveAnimation != null) this.evolveAnimation.draw(parentMatrix);

    }

    public void drawQuads(float[] parentMatrix,QuadRenderer quadRenderer){
        shield.setTranslation(this.getDeltaX(),this.getDeltaY());
        shield.setMidAngle(this.shieldRotation);
        shield.draw(parentMatrix,quadRenderer);
    }


    public void setShieldState(boolean isOn){
        shield.setState(isOn);
        this.shieldOn = isOn;
    }

    //shield rotaion is degrees
    public void setShieldRotation(float shieldRotation){
        this.shieldRotation = shieldRotation;
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
     * @param spawners  all possible spawners on the game map used to see if the attacks
     */
    public void update(long dt, float rotation, List<Enemy> enemies, List<Spawner> spawners) {
        super.update(dt, rotation);

        this.millisSinceEvolve += dt;
        this.rotation = rotation;

        if (this.evolveAnimation != null) {
            this.evolveAnimation.update(dt);
            if (this.evolveAnimation.isFinished()){
                this.evolveAnimation = null;
            }
        }


        Iterator<Attack> attackIterator = this.attacks.iterator();

        while (attackIterator.hasNext()){
            Attack attack = attackIterator.next();

            if (attack.isFinished()){
                attackIterator.remove();
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

    /** If enough damage was dealt, and the evolve button is pressed, the player evolves to a superior state
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

    /** UPDATE: shield has been removed so it's just returning starting damage
     *
     * When an attack wants to damage the player, it needs to see if it hits the shield todo because only the middle angle is taken into account, there are false positive and negatives
     *
     * @param startingDamage how much damage it would do if there was no shield
     * @param x0 line 0 x
     * @param y0 line 0 y
     * @param x1 line 1 x
     * @param y1 line 1 y
     * @return the damage to be applied to the player depending on the shield
     */
    public int getAttackDamage(int startingDamage, float x0, float y0, float x1, float y1) {
        shield.setState(this.shieldOn);
        shield.setTranslation(this.getDeltaX(),this.getDeltaY());
        shield.setMidAngle(this.shieldRotation);
        if (shield.intersects(x0,y0,x1,y1)){
            return 0;
        }

        return startingDamage;
    }


    public boolean intersectsWith( float x0, float y0, float x1, float y1){
        shield.setState(this.shieldOn);
        shield.setTranslation(this.getDeltaX(),this.getDeltaY());
        shield.setMidAngle(this.shieldRotation);
        return shield.intersects(x0,y0,x1,y1);
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
    public float getRadius() {
        return Player.CHARACTER_RADIUS;
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


    /** Sets the level of this player. Child classes should make it a static variable
     *
     * @param level the level of this player starting from 0
     */
    public abstract void setPlayerLevel(int level);

    /** Gets the level of this type of class, should be returning a static variable
     *
     * @return the level of this type of variable
     */
    public abstract int getPlayerLevel();

    /** Gets the pointer (R.drawable.*) to the icon of the player
     *
     * @return the pointer (R.drawable.*) to the icon of the player
     */
    public abstract int getPlayerIcon();

    /** Sees if the player is evolving so the backend can pause everything else
     *
     * @return if the player is currently in the state of evolving
     */
    public  boolean isEvolving(){
        return this.millisSinceEvolve < Player.EVOLVE_MILLIS;
    }
}
