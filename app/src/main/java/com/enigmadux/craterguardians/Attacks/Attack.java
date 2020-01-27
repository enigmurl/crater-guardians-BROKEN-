package com.enigmadux.craterguardians.Attacks;

import com.enigmadux.craterguardians.Animations.Knockback;
import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.SoundLib;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.GameObjects.Supply;

import java.util.ArrayList;
import java.util.List;

/** Attacks todo: javadoc
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public abstract class Attack {

    //open gl x coordinate (read constructor javadoc for more details)
    protected float x;
    //open gl y coordinate (read constructor javadoc for more details)
    protected float y;
    //open gl width (read constructor javadoc for more details)
    protected float w;
    //open gl height (read constructor javadoc for more details)
    protected float h;

    protected BaseCharacter initializer;

    protected boolean isFinished;

    protected long millis;
    protected long finishedMillis;

    protected int numFrames;

    //angle (radians) at which it was shot at
    protected float attackAngle;

    protected List<Object> hits= new ArrayList<>();

    /** Default Constructor
     *  @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param numFrames the amount of frames in the attack
     * @param millis how long the attack will take in millis
     * @param initializer the Enemy or player who summoned the attack
     * @param angle the angle at which this attack is shooting at (RADIANS)
     */
    public Attack(float x, float y, float w, float h, int numFrames, long millis, BaseCharacter initializer, float angle){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.millis = millis;
        this.numFrames = numFrames;

        this.initializer = initializer;
        this.attackAngle = angle;


    }

    public void attemptAttack(BaseCharacter character){
        if (isFinished) return;
        if (this.isHit(character)) {
            //all enemies are stunned
            if (Enemy.class.isAssignableFrom(character.getClass())) {
                this.onHitEnemy((Enemy) character);

                float cos = (float) Math.cos(attackAngle);
                float sin = (float) Math.sin(attackAngle);

                //todo find better solution, also theres hard coded value
                new Knockback((Enemy) character,Knockback.DEFAULT_MILLIS,cos * Knockback.DEFAULT_KNOCKBACK_LEN,sin * Knockback.DEFAULT_KNOCKBACK_LEN);
                SoundLib.playPlayerAttackLandSoundEffect();
            } else{
                this.onHitPlayer((Player) character);
                SoundLib.playEnemyDamagePlayerSoundEffect();
            }
        }

    }

    /** Should only be called for player attack's
     *
     * @param spawner the spawner's that it's checking to see if it attacked
     */
    public void attemptAttack(Spawner spawner){
        if (isFinished) return;

        if (this.isHit(spawner)) {
            this.onHitSpawner(spawner);
            SoundLib.playPlayerAttackLandSoundEffect();
        }
    }

    public void attemptAttack(Supply supply){
        if (isFinished) return;

        if (this.isHit(supply)){
            this.onHitSupply(supply);
            SoundLib.playEnemyDamageSupplySoundEffect();
        }
    }

    public abstract boolean isHit(Spawner spawner);


    public abstract boolean isHit(BaseCharacter character);

    public abstract boolean isHit(Supply supply);


    public abstract void onHitEnemy(Enemy enemy);

    public abstract void onHitPlayer(Player player);

    public abstract void onHitSpawner(Spawner spawner);

    public abstract void onHitSupply(Supply supply);

    public abstract void onAttackFinished();

    public void update(long dt) {
        this.finishedMillis += dt;
        if (this.finishedMillis > this.millis){
            this.finishedMillis = this.millis -1;
            this.isFinished = true;
            this.onAttackFinished();
        }
    }



    public boolean isFinished() {
        return isFinished;
    }


    /** DOES NOT DO THE ACTUAL DRAWING, ONLY PR
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public abstract void draw(float[] parentMatrix);
}
