package com.enigmadux.papturetheflag.Enemies;

import android.content.Context;
import android.support.annotation.NonNull;

import com.enigmadux.papturetheflag.Attacks.Attack;
import com.enigmadux.papturetheflag.BaseCharacter;

import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

/** Any character that is trying to harm the player
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends BaseCharacter {
    /** Default Constructor
     *
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Enemy(int numRotationOrientations, int framesPerRotation,float fps){
        super(numRotationOrientations,framesPerRotation,fps);
    }

    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        for (Attack attack: this.attacks){
            attack.draw(gl,parentMatrix);
        }
        super.draw(gl, parentMatrix);
    }

    /** Updates the position, and other attributes
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param gl graphics library used to load textures
     * @param context context used to load resources
     */
    public void update(long dt,BaseCharacter player, GL10 gl, Context context){
        Iterator itr = attacks.iterator();

        while (itr.hasNext()){
            Attack attack = (Attack) itr.next();
            if (! attack.isTextureLoaded()){
                attack.loadGLTexture(gl,context);
            }
            if (attack.isFinished()){
                itr.remove();
            }
            attack.update(dt);
            attack.attemptAttack(player);
        }

    }
}
