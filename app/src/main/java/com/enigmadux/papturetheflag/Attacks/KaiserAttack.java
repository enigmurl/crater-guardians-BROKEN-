package com.enigmadux.papturetheflag.Attacks;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enigmadux.papturetheflag.BaseCharacter;
import com.enigmadux.papturetheflag.Enemies.Enemy;
import com.enigmadux.papturetheflag.R;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class KaiserAttack extends Attack {
    private float sweepRadians;
    private float angleRadians;
    private float length;
    private int damage;

    private List<BaseCharacter> hits= new ArrayList<>();


    /** Default constructor
     *
     * @param x openGL x
     * @param y openGL y
     * @param damage how much damage to deal to enemies;
     * @param sweepRadians angle in radians, for how wide the sweep is.
     * @param angleRadians angle in radians, for the turn
     * @param length how long the attack is in open gl terms (radius)
     * @param millis how long the attack takes to finish
     */
    public KaiserAttack(float x,float y,int damage,float sweepRadians, float angleRadians,float length,long millis){
        super(x,y,0,0,5,millis);

        Log.d("kaiserAttack", "angle: " + angleRadians);

        this.sweepRadians = sweepRadians;
        this.angleRadians = angleRadians;
        this.damage = damage;
        this.length =length;

        float x1 = this.x + (float) Math.cos(angleRadians + sweepRadians/2) * length;
        float y1 = this.y + (float) Math.sin(angleRadians + sweepRadians/2) * length;
        float x2 = this.x + (float) Math.cos(angleRadians - sweepRadians/2) * length;
        float y2 = this.y + (float) Math.sin(angleRadians - sweepRadians/2) * length;


        this.loadVertexBuffer(new float[]{
                x,y,0,
                x1,y1,0,
                x,y,0,
                x2,y2,0
        });

    }


    public void loadGLTexture(@NonNull GL10 gl, Context context) {
        super.loadGLTexture(gl, context, R.drawable.kaiser_attack_spritesheet);
        this.isTextureLoaded = true;
    }

    @Override
    public void onHitEnemy(Enemy enemy) {
        enemy.damage(this.damage);
    }

    @Override
    public boolean isHit(BaseCharacter character) {
        if (this.hits.contains(character)){
            return false;
        }
        float x1 = this.x + (float) Math.cos(angleRadians + sweepRadians/2) * length * (float) finishedMillis/millis;
        float y1 = this.y + (float) Math.sin(angleRadians + sweepRadians/2) * length * (float) finishedMillis/millis;
        float x2 = this.x + (float) Math.cos(angleRadians - sweepRadians/2) * length * (float) finishedMillis/millis;
        float y2 = this.y + (float) Math.sin(angleRadians - sweepRadians/2) * length * (float) finishedMillis/millis;


        if (character.collidesWithLine(x1,y1,x2,y2)){
            this.hits.add(character);
            return true;
        }
        return false;
    }

    @Override
    public void onHitPlayer(BaseCharacter player) {
        //pass nothing is needed
    }

}
