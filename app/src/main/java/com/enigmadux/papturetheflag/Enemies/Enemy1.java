package com.enigmadux.papturetheflag.Enemies;

import android.content.Context;
import android.support.annotation.NonNull;

import com.enigmadux.papturetheflag.Attacks.Attack;
import com.enigmadux.papturetheflag.Attacks.Enemy1Attack;
import com.enigmadux.papturetheflag.BaseCharacter;
import com.enigmadux.papturetheflag.PapsBackend;
import com.enigmadux.papturetheflag.R;

import javax.microedition.khronos.opengles.GL10;
/** The first type of enemy. todo javadoc
 * @author Manu Bhat
 * @version BETA
 */
public class Enemy1 extends Enemy {
    //a constant that represents how fast the character is, right now there aren't any particular units which needs to change (see todo)
    private static final float CHARACTER_SPEED = 5f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 16;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;


    /** Default Constructor
     *
     */
    public Enemy1(){
        super(NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    @Override
    public void loadGLTexture(@NonNull GL10 gl, Context context) {
        this.loadGLTexture(gl,context, R.drawable.enemy1_sprite_sheet);
    }


    @Override
    public void attack(float angle) {
        //todo
        this.attacks.add(new Enemy1Attack(this.getDeltaX(),this.getDeltaY(),5,1,angle,0.5f,250));
    }

    @Override
    public float getCharacterSpeed() {
        return Enemy1.CHARACTER_SPEED;
    }


    /** Updates the position
     *
     * @param dt amount of milliseconds since last call
     * @param player the current character the player is using.
     * @param gl graphics library used to load textures
     * @param context context used to load resources
     */
    @Override
    public void update(long dt,BaseCharacter player, GL10 gl, Context context){
        super.update(dt, player, gl, context);

        float hypotenuse = (float) Math.hypot(dt* (player.getDeltaX() - this.getDeltaX())/1000,dt* (player.getDeltaY() - this.getDeltaY())/1000);
        this.update(dt,PapsBackend.getAngle(dt* (player.getDeltaX() - this.getDeltaX())/(1000*hypotenuse),dt* (player.getDeltaY() - this.getDeltaY())/(1000*hypotenuse)));

        this.translateFromPos(dt* (player.getDeltaX() - this.getDeltaX())/1000,dt* (player.getDeltaY() - this.getDeltaY())/1000);

        hypotenuse = (float) Math.hypot(this.getDeltaX()-player.getDeltaX(),this.getDeltaY()-player.getDeltaY());
        if ( hypotenuse < 1f && attacks.size() < 1){
            this.attack(PapsBackend.getAngle((player.getDeltaY()- this.getDeltaY())/hypotenuse,(player.getDeltaX()-this.getDeltaX())/hypotenuse));
        }

        for (Attack attack: this.attacks){
            attack.attemptAttack(player);
        }

    }
}
