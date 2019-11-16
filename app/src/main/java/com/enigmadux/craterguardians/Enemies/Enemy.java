package com.enigmadux.craterguardians.Enemies;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.ProgressBar;
import com.enigmadux.craterguardians.Supply;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

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

        healthDisplay = new ProgressBar(this.getMaxHealth(),BaseCharacter.CHARACTER_WIDTH,0.05f, true, true);
    }

    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        for (Attack attack: this.attacks){
            attack.draw(gl,parentMatrix);
        }
        healthDisplay.draw(gl,parentMatrix);
    }

    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     */
    public void update(long dt, BaseCharacter player, List<Supply> supplies){
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

        healthDisplay.update(this.health,this.getDeltaX() - BaseCharacter.CHARACTER_WIDTH/2,this.getDeltaY() + BaseCharacter.CHARACTER_HEIGHT/2);

    }
}
