package com.enigmadux.papturetheflag.Characters;

import android.content.Context;

import com.enigmadux.papturetheflag.Attacks.Attack;
import com.enigmadux.papturetheflag.BaseCharacter;
import com.enigmadux.papturetheflag.Enemies.Enemy;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

public abstract class Player extends BaseCharacter {
    protected TexturedRect angleAimer;


    /**
     *
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public Player(int numRotationOrientations, int framesPerRotation,float fps){
        super(numRotationOrientations, framesPerRotation, fps);
        this.angleAimer = new TexturedRect(0,-0.25f,1,0.5f);//todo hardcoded
        angleAimer.hide();
    }

    /** Draws the player and
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        this.angleAimer.setTranslate(this.getDeltaX(),this.getDeltaY());
        this.angleAimer.draw(gl,parentMatrix);
        super.draw(gl, parentMatrix);

    }

    /** shows the angle aimer
     *
     */
    public void showAngleAimer(){
        this.angleAimer.show();
    }

    /** Sets the angle of the angle aimer
     *
     * @param angle the angle of the angle aimer in degrees (open gl takes in degrees)
     */
    public void setAngleAimerAngle(float angle){
        this.angleAimer.setAngle(angle);
    }




    /** Updates the frame, and the attacks
     *
     * @param dt amount of time since last call in milliseconds
     * @param rotation rotation of the player
     * @param enemies all possible enemies on the game map
     * @param gl graphics library used to load textures
     * @param context context used to load resources
     */
    public void update(long dt, float rotation, List<Enemy> enemies, GL10 gl, Context context) {

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
            for (Enemy enemy: enemies){
                attack.attemptAttack(enemy);
            }
        }
        super.update(dt, rotation);
    }


}
