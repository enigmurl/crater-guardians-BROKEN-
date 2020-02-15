package com.enigmadux.craterguardians.Enemies;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.Animations.RedShader;
import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.GUILib.HealthBar;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import enigmadux2d.core.shapes.TexturedRect;

/** Any character that is trying to harm the player
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends CraterCollectionElem {
    //the minimum amount of waiting time between attacks
    private static final long ATTACK_MILLIS = 1500;

    //health display of the player
    private HealthBar healthDisplay;
    //whether or not it can move, it may not be able to move because it's attacking (Enemy 2)
    protected boolean canMove = true;

    //visual is shared by all objects as they all have the same sprite
    protected static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-1,-1,2,2);


    //the distance the enemy must be before moving onto the next node
    private static final float MIN_DISTANCE = 0.1f;

    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 8;

    //this tells what the last target the enemy was locked onto, if this changes, a new path is calculated
    //it's the index of supplies usually, but -1 means it's locked onto the player
    private int lastTarget = -2;
    //the actual position it's targetting
    private List<EnemyMap.Node> currentPath;

    //millis since last attack
    private long millisSinceAttack;


    //how longs it's stunned for
    private long stunnedMillis;

    //the health of the player
    private int health;
    //a list of alive attacks
    protected List<Attack> attacks = new ArrayList<Attack>();

    //iterates over attacks to see which should be removed
    private Iterator<Attack> attackIterator;

    //the amount of rotation orientations
    protected int numRotationOrientations;
    //the amount of frames per rotations
    protected int framesPerRotation;
    //the millis per frame
    protected long mpf;



    //milliseconds since creation
    private long millisSinceCreation;


    //how much to rotate the enemy, based on the rotation it is, as we dont' have 360 different orientations
    protected float offsetDegrees;

    //the current rotation
    private float rotation;


    /** Default Constructor
     *
     * @param instanceID the id of this instance in reference to the VAO it's contained in
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Enemy(int instanceID,int numRotationOrientations, int framesPerRotation,float fps){
        super(instanceID);
        this.numRotationOrientations = numRotationOrientations;
        this.framesPerRotation = framesPerRotation;
        this.mpf  = (long) (1000/fps);

        this.health = getMaxHealth();
        healthDisplay = new HealthBar(this.getDeltaX(),this.getRadius()*2,this.getWidth(),this.getMaxHealth());
    }

    /** Gets the radius of this character
     * @return the radius of this character
     *
     */
    public abstract float getRadius();

    /** Spawn an attack at a particular angle
     *
     * @param angle the angle
     */
    public abstract void attack(float angle);

    /** Gets the maximum health
     *
     * @return the maximum health of this enemy
     */
    public abstract int getMaxHealth();

    /** Draws the player
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void draw(float[] parentMatrix) {
        for (int i = 0;i<attacks.size();i++) {
            attacks.get(i).draw(parentMatrix);
        }


        healthDisplay.draw(parentMatrix);
    }

    /** Unlike setTranslate, this moves the character from it's current position
     *
     * @param deltaX how much to translate in the X direction from the current position
     * @param deltaY how much to translate in the y direction from the current position
     */
    public void translateFromPos(float deltaX,float deltaY){
        this.deltaX += deltaX;
        this.deltaY += deltaY;
    }

    /** Tells how fast the character is
     *
     * @return how fast the object is (as of now there are no units) todo add units
     */
    public abstract float getCharacterSpeed();

    /** Loads the texture of the sprite sheet
     *
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context, R.drawable.enemy1_sprite_sheet);

        VISUAL_REPRESENTATION.loadTextureBuffer(new float[] {
                0,1,
                0,(NUM_ROTATION_ORIENTATIONS-1f)/NUM_ROTATION_ORIENTATIONS,
                1/(float) FRAMES_PER_ROTATION,1,
                1/(float) FRAMES_PER_ROTATION,(NUM_ROTATION_ORIENTATIONS-1f)/NUM_ROTATION_ORIENTATIONS,
        });
    }

    /** Knocks back the enemy and stuns it
     *
     * @param stunnedMillis the amount of millis the enemy is stunned for
     */
    public void stun(long stunnedMillis){
        //this is the stun
        this.stunnedMillis = Math.max(this.stunnedMillis,stunnedMillis);
    }


    /** translates the TexturedRect
     *
     * @param x how much to translate in the deltX direction
     * @param y how much to translate in the y direction
     */
    public void setTranslate(float x,float y){
        this.deltaX = x;
        this.deltaY = y;
    }

    /** Gets if this is alive or not
     *
     * @return if this is alive or not
     */
    public boolean isAlive(){
        return this.health>0;
    }

    /** Damages this enemy, use -damage to heal
     *
     * @param damage the amount to damage, use negative to heal
     */
    public void damage(int damage){
        new RedShader(this,RedShader.DEFAULT_LEN);

        this.health -= damage;
    }


    /** Gets the speed of the enemy
     *
     * @return the speed of the enemy
     */
    public abstract float getSpeed();

    /** Gets the attack range of this enemy
     *
     * @return the attack range of this enemy (how far it can shoot
     */
    public abstract float getAttackRange();

    /** Sees if it collides with a line, but does not indicate where
     * It will return true if both points are withing the ellipse
     *
     * @param x1 openGL deltX of first point
     * @param y1 openGL y of first point
     * @param x2 openGL deltX of second point
     * @param y2 openGL y of second point
     * @return whether or not they collide
     */
    public boolean collidesWithLine(float x1, float y1, float x2, float y2) {
        float dX1 = x1 - this.getDeltaX();
        float dY1 = y1 - this.getDeltaY();
        if (dX1 * dX1 / (this.getRadius() * this.getRadius()) + dY1 * dY1 / (this.getRadius() * this.getRadius()) < 1) {
            return true;
        }
        float dX2 = x2 - this.getDeltaX();
        float dY2 = y2 - this.getDeltaY();
        if (dX2 * dX2 / (this.getRadius() * this.getRadius()) + dY2 * dY2 / (this.getRadius() * this.getRadius()) < 1) {
            return true;
        }


        float cx = this.getDeltaX();
        float cy = this.getDeltaY();

        float pt1X = x1 - cx;
        float pt1Y = y1 - cy;
        float pt2X = x2 - cx;
        float pt2Y = y2 - cy;

        // Get the semi major and semi minor axes.
        float a = this.getRadius();
        float b = this.getRadius();

        // Calculate the quadratic parameters.
        float A = (pt2X - pt1X) * (pt2X - pt1X) / (a * a) +
                (pt2Y - pt1Y) * (pt2Y - pt1Y) / (b * b);
        float B = 2 * pt1X * (pt2X - pt1X) / (a * a) +
                2 * pt1Y * (pt2Y - pt1Y) / (b * b);
        float C = pt1X * pt1X / (a * a) + pt1Y * pt1Y / (b * b) - 1;


        // Calculate the discriminant.
        float discriminant = B * B - 4 * A * C;

        if (discriminant >= 0) {
            float tValue1 = (float) (-B + Math.sqrt(discriminant)) / (2 * A); //||
            float tValue2 = (float) (-B - Math.sqrt(discriminant)) / (2 * A);

            return (tValue1 >= 0 && tValue1 <= 1) || (tValue2 >= 0 && tValue2 <= 1);
        }
        return false;
    }

    /** based on the current state, which frame should it be?
     *
     * @param rotation the angle at which the character is in degrees
     * @param frameNum the frame# to display in the animation
     */
    public abstract void setFrame(float rotation,int frameNum);

    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param supplies  all alive supplies on the map
     * @param enemyMap A map of where and how the enemy should go
     */
    public void update(long dt, BaseCharacter player, List<Supply> supplies, EnemyMap enemyMap) {
        if (this.stunnedMillis <= 0) {
            this.millisSinceAttack += dt;
        }

        this.millisSinceCreation+= dt;
        this.healthDisplay.setTranslate(this.getDeltaX(),this.getDeltaY());


        this.stunnedMillis -= dt;

        this.attackIterator = attacks.iterator();


        while (this.attackIterator.hasNext()) {
            Attack attack =  this.attackIterator.next();
            if (attack.isFinished()) {
                this.attackIterator.remove();
            }
            attack.update(dt);
            attack.attemptAttack(player);

            for (Supply supply : supplies) {
                attack.attemptAttack(supply);
            }
        }

        //updates the position todo current this decides which one to go to by the euclidean distance, whereas it should be the distance using the algorithm, it shouldnt affect it too much, but this way is easier and less expensive
        //find the path of each one

        this.healthDisplay.updateHealth(this.health);
        //this.healthDisplay.update(this.health, this.getDeltaX() - this.getRadius(), this.getDeltaY() + this.getRadius());
        if (this.canMove) {
            float minLength = Math.max(0.01f, (float) Math.hypot(this.getDeltaX() - player.getDeltaX(), this.getDeltaY() - player.getDeltaY()));
            int supplyIndex = -1;//if it's negative 1 that refers to the player


            for (int i = 0; i < supplies.size(); i++) {

                float hypotenuse = Math.max(0.01f, (float) Math.hypot(this.getDeltaX() - supplies.get(i).getDeltaX(), this.getDeltaY() - supplies.get(i).getDeltaY()));
                if (hypotenuse < minLength) {
                    minLength = hypotenuse;
                    supplyIndex = i;
                }

            }



            //if (this.lastTarget != supplyIndex) {
            if (this.lastTarget == -2){
                PathFinder pathFinder = new PathFinder(enemyMap,supplyIndex,getDeltaX(),getDeltaY());
                pathFinder.start();
                //this.currentPath = enemyMap.nextStepMap(this.getRadius(), this.getDeltaX(), this.getDeltaY(), supplyIndex);
            }

            float targetX;
            float targetY;

            //means that the player is the current target
            if (currentPath == null || currentPath.get(0) == null){


                targetX = player.getDeltaX();
                targetY = player.getDeltaY();

            } else {
                targetX = this.currentPath.get(0).x;
                targetY = this.currentPath.get(0).y;
            }

            double travelLen = (float) Math.hypot(this.getDeltaX() - targetX,this.getDeltaY()-targetY);

            //this may be wrong
            float clippedLength = (travelLen >  this.getSpeed() * dt / 1000f) ? (this.getSpeed() *  dt / 1000f)/ (float) travelLen: 1;



            
            if (currentPath != null && currentPath.size() > 1 && Math.hypot(targetX -this.getDeltaX(),targetY- this.getDeltaY()) < Enemy.MIN_DISTANCE) this.currentPath.remove(0);

            if (supplyIndex == -1) {
                if (minLength < this.getAttackRange() && attacks.size() < 1 && this.millisSinceAttack > ATTACK_MILLIS) {//todo this is hardcoded
                    this.attack(MathOps.getAngle((player.getDeltaX() - this.getDeltaX()) / minLength, (player.getDeltaY() - this.getDeltaY()) / minLength));
                    this.millisSinceAttack = 0;
                }
                //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
                this.rotation = 180 / (float) Math.PI * MathOps.getAngle((targetX - this.getDeltaX()) / minLength, (targetY - this.getDeltaY()) / minLength);
            } else {

                if (minLength < this.getAttackRange() && attacks.size() < 1 && this.millisSinceAttack > ATTACK_MILLIS) {//todo this is hardcoded
                    this.attack(MathOps.getAngle((supplies.get(supplyIndex).getDeltaX() - this.getDeltaX()) / minLength, (supplies.get(supplyIndex).getDeltaY() - this.getDeltaY()) / minLength));
                    this.millisSinceAttack = 0;
                }
                //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
                this.rotation = 180 / (float) Math.PI * MathOps.getAngle((targetX - this.getDeltaX()) / minLength, (targetY - this.getDeltaY()) / minLength);
            }

            //incase the path is still trying to be figured out
            if (this.currentPath != null && this.stunnedMillis <= 0 && (currentPath.size() > 2 || minLength > this.getAttackRange())) {
                this.translateFromPos((targetX - this.getDeltaX()) * clippedLength, (targetY - this.getDeltaY()) * clippedLength);
            }

            this.lastTarget = supplyIndex;
        }
        int frameNum = (int) (((int) (this.millisSinceCreation % (this.mpf * this.framesPerRotation)))/this.mpf);
        this.setFrame(this.rotation ,frameNum);


        for (Attack attack: this.attacks){
            attack.attemptAttack(player);
        }
    }

    /** Used to find a path without holding up other threads.
     *
     */
    private class PathFinder extends Thread {
        //the map used to actually determine the path
        private EnemyMap enemyMap;
        //the target supply, -1 represents targetting the player
        private int supplyIndex;
        //the deltX position of the enemy
        private float x;
        //the y position of the enemy
        private float y;


        /**  Default constructor
         *
         * @param enemyMap the map used to actually determine the path which has information about the level
         * @param supplyIndex the target supply, -1 represents targetting the player
         * @param x the current deltX position of this enemy
         * @param y the current y position of this enemy
         */
        public PathFinder(EnemyMap enemyMap,int supplyIndex,float x,float y){
            this.enemyMap = enemyMap;
            this.supplyIndex = supplyIndex;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            super.run();

            try {
                if (EnemyMap.LOCK.tryLock(5, TimeUnit.SECONDS)) {
                    try {
                        currentPath = this.enemyMap.nextStepMap(getRadius(), x, y, this.supplyIndex);
                    } finally {
                        EnemyMap.LOCK.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Log.d("ENEMY PATH","PATH FAILED");

            }


            //Log.d("ENEMY PATH", "path: "  + currentPath + " supply: " + supplyIndex);

            try {
                this.join();
            } catch (InterruptedException e){
                Log.d("PathFinder","interrupted self");
            }
        }
    }
}
