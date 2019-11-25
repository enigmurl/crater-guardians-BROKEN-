package com.enigmadux.craterguardians.Enemies;

import android.util.Log;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.Plateau;
import com.enigmadux.craterguardians.ProgressBar;
import com.enigmadux.craterguardians.Supply;

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

    /** Default Constructor
     *
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Enemy(int numRotationOrientations, int framesPerRotation,float fps){
        super(numRotationOrientations,framesPerRotation,fps);

        healthDisplay = new ProgressBar(this.getMaxHealth(),this.getW(),0.05f, true, true);
    }

    /** Draws the player
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        for (Attack attack: this.attacks){
            attack.draw(gl,parentMatrix);
        }
        healthDisplay.draw(gl,parentMatrix);
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
    public float[] getTargetPosition(float x,float y,List<Plateau> plateaus) {
        float maxDimension = Math.max(this.getW(),this.getH());
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
     */
    public void update(long dt, BaseCharacter player, List<Supply> supplies,List<Plateau> plateaus){
        Iterator itr = attacks.iterator();


        while (itr.hasNext()){
            Attack attack = (Attack) itr.next();
            if (attack.isFinished()){
                itr.remove();
            }
            attack.update(dt);
            attack.attemptAttack(player);

            for (Supply supply:supplies){
                attack.attemptAttack(supply);
            }
        }

        //updates the position todo current this decides which one to go to by the euclidean distance, whereas it should be the distance using the algorithm, it shouldnt affect it too much, but this way is easier and less expensive
        //find the path of each one
        healthDisplay.update(this.health,this.getDeltaX() - this.getW()/2,this.getDeltaY() + this.getH()/2);

        float minLength = Math.max(0.01f,(float) Math.hypot(this.getDeltaX() - player.getDeltaX(),this.getDeltaY()-player.getDeltaY()));
        int supplyIndex = -1;//if it's negative 1 that refers to the player


        for (int i = 0;i<supplies.size();i++) {

            float hypotenuse = Math.max(0.01f,(float) Math.hypot(this.getDeltaX() - supplies.get(i).getX(),this.getDeltaY()-supplies.get(i).getY()));
            if (hypotenuse < minLength){
                minLength = hypotenuse;
                supplyIndex = i;
            }

        }

        float clippedLength = (minLength >dt/1000f) ? dt/1000f:minLength;

        if (supplyIndex == -1){
            if (minLength < 1 && attacks.size() < 1) {//todo this is hardcoded
                this.attack(MathOps.getAngle((player.getDeltaX()-this.getDeltaX())/minLength,(player.getDeltaY()-this.getDeltaY())/minLength));
            }
            float[] nextPos = getTargetPosition(player.getDeltaX(),player.getDeltaY(),plateaus);
            //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
            this.translateFromPos((nextPos[0]-this.getDeltaX()) * clippedLength,(nextPos[1]-this.getDeltaY()) * clippedLength);
            this.update(dt,180/(float) Math.PI * MathOps.getAngle((nextPos[0]-this.getDeltaX())/minLength,(nextPos[1]-this.getDeltaY())/minLength));

        }else {

            if (minLength < 1 && attacks.size() < 1) {//todo this is hardcoded
                this.attack(MathOps.getAngle((supplies.get(supplyIndex).getX() - this.getDeltaX()) / minLength, (supplies.get(supplyIndex).getY() - this.getDeltaX()) / minLength));
            }
            float[] nextPos = getTargetPosition(supplies.get(supplyIndex).getX(),supplies.get(supplyIndex).getY(),plateaus);
            //Log.d("PLAYER","X: " +this.getDeltaX() + " px " + player.getDeltaX() + " Y: " + this.getDeltaY() + " py " + player.getDeltaY() +  " len " + minLength + " cl "  +clippedLength);
            this.translateFromPos((nextPos[0]-this.getDeltaX()) * clippedLength,(nextPos[1]-this.getDeltaY()) * clippedLength);
            this.update(dt,180/(float) Math.PI * MathOps.getAngle((nextPos[0]-this.getDeltaX())/minLength,(nextPos[1]-this.getDeltaY())/minLength));
        }

        for (Attack attack: this.attacks){
            attack.attemptAttack(player);
        }
    }
}
