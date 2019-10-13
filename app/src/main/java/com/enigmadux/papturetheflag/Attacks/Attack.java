package com.enigmadux.papturetheflag.Attacks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enigmadux.papturetheflag.BaseCharacter;
import com.enigmadux.papturetheflag.Enemies.Enemy;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Attacks todo: javadoc
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public abstract class Attack extends TexturedRect {
    protected boolean isFinished;

    protected boolean isTextureLoaded = false;

    protected long millis;
    protected long finishedMillis;

    protected int numFrames;

    /** Default Constructor
     *
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public Attack(float x,float y,float w,float h,int numFrames,long millis){
        super(x, y, w, h);
        this.millis = millis;
        this.numFrames = numFrames;
    }

    public void attemptAttack(BaseCharacter character){
        if (this.isHit(character)) {
            if (Enemy.class.isAssignableFrom(character.getClass())) {
                this.onHitEnemy((Enemy) character);
            } else{
                this.onHitPlayer(character);
            }
        }

    }

    public abstract void loadGLTexture(@NonNull GL10 gl, Context context);

    public abstract boolean isHit(BaseCharacter character);


    public abstract void onHitEnemy(Enemy enemy);

    public abstract void onHitPlayer(BaseCharacter player);

    public void update(long dt) {
        this.finishedMillis += dt;
        if (this.finishedMillis > this.millis){
            this.finishedMillis = this.millis -1;
            this.isFinished = true;
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isTextureLoaded() {
        return isTextureLoaded;
    }

    /** draws the attack
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        if (this.isTextureLoaded) {
            float x1 = ((int) (this.finishedMillis/(this.millis/this.numFrames)) * 1f/this.numFrames) %1 * (float) this.orgW/afterW;
            float x2 = (((int) (this.finishedMillis/(this.millis/this.numFrames)) * 1f/this.numFrames) %1 + 1f/numFrames) * (float) this.orgW/afterW;

            this.loadTextureBuffer(new float[] {
                    x1,1,
                    x1,(float) (afterH - this.orgH)/afterH,
                    x2,1,
                    x2,(float) (afterH - this.orgH)/afterH

            });
            super.draw(gl, parentMatrix);
        }
    }
}
