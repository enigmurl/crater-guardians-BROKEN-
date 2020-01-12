package com.enigmadux.craterguardians.Enemies;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.GUI.ProgressBar;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.R;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import enigmadux2d.core.shapes.TexturedRect;

/** Any character that is trying to harm the player
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends BaseCharacter {
    //health display of the player
    private ProgressBar healthDisplay;
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

    /** Default Constructor
     *
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Enemy(int numRotationOrientations, int framesPerRotation,float fps){
        super(numRotationOrientations,framesPerRotation,fps);

        healthDisplay = new ProgressBar(this.getMaxHealth(),this.getRadius()*2,0.05f, true, true);
    }

    /** Draws the player
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        for (int i = 0;i<attacks.size();i++) {
            attacks.get(i).draw(parentMatrix);
        }
        healthDisplay.draw(parentMatrix);
    }

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

    /** Preparing drawing period
     *
     */
    public static void prepareDraw(){
        VISUAL_REPRESENTATION.prepareDraw(0);
    }

    /** Ends the drawing period
     *
     */
    public static void endDrawing(){
        VISUAL_REPRESENTATION.endDraw();
    }

    /** Draw the actual enemy
     *
     * @param parentMatrix the parent matrix
     */
    public abstract void drawIntermediate(float[] parentMatrix);


    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param supplies  all alive supplies on the map
     * @param enemyMap A map of where and how the enemy should go
     */
    public void update(long dt, BaseCharacter player, List<Supply> supplies, EnemyMap enemyMap) {
        Iterator itr = attacks.iterator();


        while (itr.hasNext()) {
            Attack attack = (Attack) itr.next();
            if (attack.isFinished()) {
                itr.remove();
            }
            attack.update(dt);
            attack.attemptAttack(player);

            for (Supply supply : supplies) {
                attack.attemptAttack(supply);
            }
        }

        //updates the position todo current this decides which one to go to by the euclidean distance, whereas it should be the distance using the algorithm, it shouldnt affect it too much, but this way is easier and less expensive
        //find the path of each one
        this.healthDisplay.update(this.health, this.getDeltaX() - this.getRadius(), this.getDeltaY() + this.getRadius());
        if (this.canMove) {
            float minLength = Math.max(0.01f, (float) Math.hypot(this.getDeltaX() - player.getDeltaX(), this.getDeltaY() - player.getDeltaY()));
            int supplyIndex = -1;//if it's negative 1 that refers to the player


            for (int i = 0; i < supplies.size(); i++) {

                float hypotenuse = Math.max(0.01f, (float) Math.hypot(this.getDeltaX() - supplies.get(i).getX(), this.getDeltaY() - supplies.get(i).getY()));
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
            float clippedLength = (travelLen > dt / 1000f) ? (dt / 1000f)/ (float) travelLen: 1;



            
            if (currentPath != null && currentPath.size() > 1 && Math.hypot(targetX -this.getDeltaX(),targetY- this.getDeltaY()) < Enemy.MIN_DISTANCE) this.currentPath.remove(0);

            if (supplyIndex == -1) {
                if (minLength < 1 && attacks.size() < 1) {//todo this is hardcoded
                    this.attack(MathOps.getAngle((player.getDeltaX() - this.getDeltaX()) / minLength, (player.getDeltaY() - this.getDeltaY()) / minLength));
                }
                //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
                this.update(dt, 180 / (float) Math.PI * MathOps.getAngle((targetX - this.getDeltaX()) / minLength, (targetY - this.getDeltaY()) / minLength));

            } else {

                if (minLength < 1 && attacks.size() < 1) {//todo this is hardcoded
                    this.attack(MathOps.getAngle((supplies.get(supplyIndex).getX() - this.getDeltaX()) / minLength, (supplies.get(supplyIndex).getY() - this.getDeltaY()) / minLength));
                }
                //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
                this.update(dt, 180 / (float) Math.PI * MathOps.getAngle((targetX - this.getDeltaX()) / minLength, (targetY - this.getDeltaY()) / minLength));
            }

            //incase the path is still trying to be figured out
            if (this.currentPath != null) {
                this.translateFromPos((targetX - this.getDeltaX()) * clippedLength, (targetY - this.getDeltaY()) * clippedLength);
            }

            this.lastTarget = supplyIndex;
        }

        for (Attack attack: this.attacks){
            attack.attemptAttack(player);
        }
    }

    /** Used to find a path without holding up other threads
     *
     */
    private class PathFinder extends Thread {
        //the map used to actually determine the path
        private EnemyMap enemyMap;
        //the target supply, -1 represents targetting the player
        private int supplyIndex;
        //the x position of the enemy
        private float x;
        //the y position of the enemy
        private float y;


        /**  Default constructor
         *
         * @param enemyMap the map used to actually determine the path which has information about the level
         * @param supplyIndex the target supply, -1 represents targetting the player
         * @param x the current x position of this enemy
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
