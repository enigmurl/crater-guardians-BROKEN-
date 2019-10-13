package com.enigmadux.papturetheflag.Characters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.enigmadux.papturetheflag.Attacks.KaiserAttack;
import com.enigmadux.papturetheflag.R;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** The main character
 * @see com.enigmadux.papturetheflag.BaseCharacter
 * @author Manu Bhat
 * @version BETA
 */
public class Kaiser extends Player {
    //a constant that represents how fast the character is, right now there aren't any particular units which needs to change (see todo)
    private static final float CHARACTER_SPEED = 5f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 1;//todo change back to 4 later
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 1;//todo change back to 4 later
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 8;



    /** Default Constructor
     *
     */
    public Kaiser(){
        super(NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    @Override
    public void loadGLTexture(@NonNull GL10 gl, Context context) {
        this.loadGLTexture(gl,context, R.drawable.ellipse);//todo switch back to R.drawable.kaiser_sprite_sheet
        this.angleAimer.loadGLTexture(gl,context,R.drawable.kaiser_attack_aimer);
    }

    /** Attacks enemies
     *
     * @param angle the angle at which to attack in radians
     */
    @Override
    public void attack(float angle) {
        this.angleAimer.hide();
        this.attacks.add(new KaiserAttack(this.getDeltaX(),this.getDeltaY(),5,1,angle,0.5f,250));
        //pass for now
    }

    /** Tells how fast this character is
     *
     * @return the speed of this character (no units, see parent class javadoc)
     */
    @Override
    public float getCharacterSpeed() {
        return Kaiser.CHARACTER_SPEED;
    }
}
