package com.enigmadux.craterguardians.Enemies;

import android.util.Log;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.Plateau;
import com.enigmadux.craterguardians.ProgressBar;
import com.enigmadux.craterguardians.Supply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;

/** Any character that is trying to harm the player
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends BaseCharacter {
    //health display of the player
    private ProgressBar healthDisplay;
    //whether or not it can move, it may not be able to move because it's attacking (Enemy 2)
    protected boolean canMove = true;
    
    //the distance the enemy must be before moving onto the next node
    private static final float MIN_DISTANCE = 0.1f;


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
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        //for (Attack attack: this.attacks){
        //    if (attack == null) continue;
            //attack.draw(gl,parentMatrix);
        //}
        //healthDisplay.draw(gl,parentMatrix);
    }


    /** To avoid plateaus it travels a non straight path, this tells the next vertice of that path
     *todo don't make it so memory intensive, also right now its not checking the most efficient path, but the node closest to the target something like that, also there is bug where
     * the target position somwhere it can't fit, so we need to make sure the width and height are more included
     *
     * @param x the center x of the target
     * @param y the center y of the target
     * @param plateaus all plateaus on the game map
     * @return vertices that represent the triangles
     */
    private float[] getTargetPosition(float x,float y,List<Plateau> plateaus) {
        float maxDimension = this.getRadius();
        float epsilon = 0.001f;

        double minDist = Double.MAX_VALUE;
        float[] nextTarget = new float[2];

        float[] holder = new float[2];

        for (Plateau plateau:plateaus){
            float[][] points = plateau.getPoints();
            for (int i = 0;i<4;i++) {//iterate through each side
                int point1 = -1;
                int point2 = -1;
                switch (i){
                    case 0:
                        point1 = 0;
                        point2 = 1;
                        break;
                    case 1:
                        point1 = 0;
                        point2 = 2;
                        break;
                    case 2:
                        point1 = 1;
                        point2 = 3;
                        break;
                    case 3:
                        point1 = 2;
                        point2 = 3;
                        break;
                }
                if (MathOps.lineIntersectsLine(this.getDeltaX(), this.getDeltaY(), x,y,
                        points[point1][0], points[point1][1], points[point2][0], points[point2][1])) {
                    float tValue = MathOps.tValueSegmentIntersection(points[point1][0], points[point1][1], points[point2][0], points[point2][1],
                            this.getDeltaX(), this.getDeltaY(), x,y);
                    if (tValue == -1){
                        continue;
                    }
                    if (Math.hypot(x - points[point1][0],y - points[point1][1]) < Math.hypot(x - points[point2][0],y - points[point2][1])) {
                        //subtract a little from the tValue as too make it so that target is not directly on the vertice
                        tValue = -(float) (epsilon + maxDimension / Math.hypot(points[point2][0] - points[point1][0], points[point2][1] - points[point1][1]));
                        holder[0] = tValue * (points[point2][0] - points[point1][0]) + points[point1][0];
                        holder[1] = tValue * (points[point2][1] - points[point1][1]) + points[point1][1];

                        float perpX = -(float) (epsilon + maxDimension / Math.hypot(points[point2][0] - points[point1][0], points[point2][1] - points[point1][1])) * tValue * (points[point2][0] - points[point1][0]);
                        float perpY = -(float) (epsilon + maxDimension / Math.hypot(points[point2][0] - points[point1][0], points[point2][1] - points[point1][1])) * tValue * (points[point2][1] - points[point1][1]);
                        //check the two perpendiculars

                        double hypot1 = Math.hypot(this.getDeltaX() - (holder[0] - perpY), this.getDeltaY() - (holder[1]+perpX));
                        double hypot2 = Math.hypot(this.getDeltaX() - (holder[0] + perpY), this.getDeltaY() - (holder[1]-perpX));
                        if (hypot1 < hypot2) {
                            if (hypot1 < minDist) {
                                minDist = hypot1;
                                holder[0] -= perpY;
                                holder[1] += perpX;
                                nextTarget = holder;
                            }
                        } else if (hypot2 <minDist){
                            minDist = hypot2;
                            holder[0] += perpY;
                            holder[1] -= perpX;
                            nextTarget = holder;
                        }
                    } else  {
                        tValue = 1 + (float) (epsilon + maxDimension / Math.hypot(points[point2][0] - points[point1][0], points[point2][1] - points[point1][1]));
                        holder[0] = tValue * (points[point2][0] - points[point1][0]) + points[point1][0];
                        holder[1] = tValue * (points[point2][1] - points[point1][1]) + points[point1][1];
                        float perpX = (float) (epsilon + maxDimension / Math.hypot(points[point2][0] - points[point1][0], points[point2][1] - points[point1][1])) * tValue * (points[point2][0] - points[point1][0]);
                        float perpY = (float) (epsilon + maxDimension / Math.hypot(points[point2][0] - points[point1][0], points[point2][1] - points[point1][1])) * tValue * (points[point2][1] - points[point1][1]);
                        //check the two perpendiculars

                        double hypot1 = Math.hypot(this.getDeltaX() - (holder[0] - perpY), this.getDeltaY() - (holder[1]+perpX));
                        double hypot2 = Math.hypot(this.getDeltaX() - (holder[0] + perpY), this.getDeltaY() - (holder[1]-perpX));
                        if (hypot1 < hypot2) {
                            if (hypot1 < minDist) {
                                minDist = hypot1;
                                holder[0] -= perpY;
                                holder[1] += perpX;
                                nextTarget = holder;
                            }
                        } else if (hypot2 <minDist){
                            minDist = hypot2;
                            holder[0] += perpY;
                            holder[1] -= perpX;
                            nextTarget = holder;
                        }
                    }

                }
            }
        }

        if (minDist == Double.MAX_VALUE){
            return new float[] {x,y};
        } else {
            return nextTarget;
        }
    }



    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param supplies  all alive supplies on the map
     * @param enemyMap A map of where and how the enemy should go
     */
    public void update(long dt, BaseCharacter player, Supply[] supplies, EnemyMap enemyMap) {
        for (int i = 0;i<this.attacks.length;i++){
            Attack attack = this.attacks[i];
            if (attack == null) continue;
            if (attack.isFinished()){
                this.attacks[i] = null;
            }
            attack.update(dt);
            attack.attemptAttack(player);

            for (Supply supply : supplies) {
                if (supply != null) attack.attemptAttack(supply);
            }
        }

        //updates the position todo current this decides which one to go to by the euclidean distance, whereas it should be the distance using the algorithm, it shouldnt affect it too much, but this way is easier and less expensive
        //find the path of each one
        this.healthDisplay.update(this.health, this.getDeltaX() - this.getRadius(), this.getDeltaY() + this.getRadius());
        if (this.canMove) {
            float minLength = Math.max(0.01f, (float) Math.hypot(this.getDeltaX() - player.getDeltaX(), this.getDeltaY() - player.getDeltaY()));
            int supplyIndex = -1;//if it's negative 1 that refers to the player


            for (int i = 0; i < supplies.length; i++) {
                if (supplies[i] == null) continue;
                float hypotenuse = Math.max(0.01f, (float) Math.hypot(this.getDeltaX() - supplies[i].getX(), this.getDeltaY() - supplies[i].getY()));
                if (hypotenuse < minLength) {
                    minLength = hypotenuse;
                    supplyIndex = i;
                }

            }



            //if (this.lastTarget != supplyIndex) {
            if (this.lastTarget == -2){
                //PathFinder pathFinder = new PathFinder(enemyMap,supplyIndex);
                //pathFinder.run();
                this.currentPath = new ArrayList<>();
                this.currentPath.add(null);
                //this.currentPath = enemyMap.nextStepMap(this.getRadius(), this.getDeltaX(), this.getDeltaY(), supplyIndex);
                Log.d("ENEMY PATH", "path: "  + this.currentPath + " supply: " + supplyIndex);
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
                if (minLength < 1 && attacks[0] == null) {//todo this is hardcoded
                    this.attack(MathOps.getAngle((player.getDeltaX() - this.getDeltaX()) / minLength, (player.getDeltaY() - this.getDeltaY()) / minLength));
                }
                //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
                this.update(dt, 180 / (float) Math.PI * MathOps.getAngle((targetX - this.getDeltaX()) / minLength, (targetY - this.getDeltaY()) / minLength));

            } else {

                if (minLength < 1 && attacks[0] == null) {//todo this is hardcoded
                    this.attack(MathOps.getAngle((supplies[supplyIndex].getX() - this.getDeltaX()) / minLength, (supplies[supplyIndex].getY() - this.getDeltaY()) / minLength));
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
            if (attack == null) continue;
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

        /**  Default constructor
         *
         * @param enemyMap the map used to actually determine the path which has information about the level
         * @param supplyIndex the target supply, -1 represents targetting the player
         */
        public PathFinder(EnemyMap enemyMap,int supplyIndex){
            this.enemyMap = enemyMap;
            this.supplyIndex = supplyIndex;
        }

        @Override
        public void run() {
            super.run();

            currentPath = this.enemyMap.nextStepMap(getRadius(),getDeltaX(),getDeltaY(),this.supplyIndex);
            try {
                this.join();
            } catch (InterruptedException e){
                Log.d("PathFinder","interrupted self");
            }
        }
    }
}
