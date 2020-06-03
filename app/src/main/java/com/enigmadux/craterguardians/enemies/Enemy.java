package com.enigmadux.craterguardians.enemies;

import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.animations.DeathAnim;
import com.enigmadux.craterguardians.animations.EnemySpawn;
import com.enigmadux.craterguardians.animations.Knockback;
import com.enigmadux.craterguardians.animations.ColoredShader;
import com.enigmadux.craterguardians.animations.ShootAnimation;
import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.gameobjects.Plateau;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.gameobjects.ToxicLake;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.util.PairIntFloat;
import com.enigmadux.craterguardians.util.SoundLib;

import java.util.ArrayList;
import java.util.LinkedList;

/** New Enemy
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends CraterCollectionElem implements Character {
    //strengths depending on level
    public static int[] STRENGTHS = new int[] {
            0,0,0,0,0,0,0,
            1,1,1,1,1,1,1,
            2,2,2,2,2,2,2,
            3,3,3,3,3,3,3,
    };

    //gives the texture based on strength
    public static int[] STRENGTH_TEXTURES = new int[]{
            R.drawable.enemy_strength1,
            R.drawable.enemy_strength2,
            R.drawable.enemy_strength3,
            R.drawable.enemy_strength3,
            R.drawable.enemy_strength3
    };


    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 1;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 1;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;

    private static final float STUNNED_SHRINK = 0.8f;

    //radians per second
    private static final float TURNSPEED = 200f;

    //speed it pushes forward
    private static final float PUSH_SPEED = 0.2f;

    //every radian per second turned = 0.9f * speed
    private static final float TURN_SLOWER = 0.25f;
    //a little noise to each turn (per second)
    private static final float MAX_NOISE = 1f;

    //amount of distance required to query onto the next pos
    private static final float DIST_TILL_NEXT_POS = 0.1f;
    //distance player had to move required to redirect
    private static final float DIST_TILL_RESEARCH = 1f;

    //chance to redirect when scrambling
    private static final float REDIRECTION_CHANCE = 0.1f;
    //the last part of the attack is lunging
    private static final float LUNGE_PERCENTAGE = 0.075f;

    public static final float[] TEXTURE_MAP = new float[] {
            0,(Enemy.NUM_ROTATION_ORIENTATIONS-1f)/Enemy.NUM_ROTATION_ORIENTATIONS,
            0,1,
            1/(float) Enemy.FRAMES_PER_ROTATION,(Enemy.NUM_ROTATION_ORIENTATIONS-1f)/Enemy.NUM_ROTATION_ORIENTATIONS,
            1/(float) Enemy.FRAMES_PER_ROTATION,1,
    };

    public static final float ASPECT_RATIO = 271f/170f;


    static final float GUN_LENGTH = ASPECT_RATIO - 1;

    //
    static final float GUN_OFFSET_Y =  -34/170f;

    /** The radius in openGL terms (half the width, half the height)
     *
     */
    private float r;

    private float attackLen;

    /** This is the health variable, by default if it's over 0 that indicates this character is alive
     *
     */
    protected int health;



    protected boolean isBlue;

    //milliseconds
    private long attackRate;
    private long millisSinceLastAttack;

    LinkedList<EnemyMap.Node> currentPath;
    private boolean searchedForPath = false;

    //when it starts, so that we can calculate correct frame num
    private long startMillis = System.currentTimeMillis();

    private long stunnedMillis = 0;

    //direction we're heading rads
    float rotation = 0;
    float attackRotation;
    boolean isAttacking;

    private boolean startedPathTraverse;

    private boolean spawned = false;

    private boolean egged = false;

    //set to true in the spawning animation
    protected boolean isVisible = false;

    float velocityX;
    float velocityY;

    //previous position
    private float prevDeltaX;
    private float prevDeltaY;

    private Knockback knockback;

    //if it's targetting player, where was it when it first tried it
    private float finalTargetX;
    private float finalTargetY;


    //don't continue closer to player if this close
    float minDist = 0;

    private boolean scrambling;
    //where it starts running around
    private float startX;
    private float startY;

    private LinkedList<Enemy3> healers;

    int strength;


    /** Default Constructor
     *
     * @param instanceID the id of the instance in reference to the vao it's in (received using VaoCollection.addInstance());
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     * @param r the radius in openGL terms (half the width, half the height)
     * @param milliSeconds ms between attacks
     */
    public Enemy(int instanceID,float x,float y,float r,float attackLen,boolean isBlue,long milliSeconds,int strength){
        super(instanceID);

        //assign variables to attributes
        this.deltaX = x;
        this.deltaY = y;
        this.r = r;

        this.width = r*2 * ASPECT_RATIO;
        this.height = r*2;

        this.isBlue = isBlue;
        this.attackLen = attackLen;
        this.strength = strength;

        this.health = this.getMaxHealth();


        this.resetShader();

        this.attackRate = milliSeconds;
        this.millisSinceLastAttack = attackRate;

        this.healers = new LinkedList<>();
    }



    @Override
    public float getRadius() {
        return this.r;
    }

    /** Damages the player along, that's it for now but maybe in the future
     *
     * @param damage the amount of damage the character must take, a >0 value will decrease the health, <0 will increase, =0 will do nothing
     */
    @Override
    public void damage(float damage) {
        //if its not visible shouldn't be able to be damaged
        if (! this.isVisible) return;
        //need to cast to avoid ambiguity
        new ColoredShader((Character) this, ColoredShader.DEFAULT_LEN, damage > 0);
        this.health -= damage;
        this.health = Math.min(this.health,this.getMaxHealth());
    }


    /** Sees if this enemy is alive
     *
     * @return if the enemy health is above 0
     */
    public boolean isDead(){
        return this.health <= 0;
    }



    /** Updates the transform
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     */
    @Override
    public void updateInstanceTransform(float[] blankInstanceInfo, float[] uMVPMatrix) {
        if (! this.isVisible){
            Matrix.setIdentityM(blankInstanceInfo,0);
            Matrix.scaleM(blankInstanceInfo,0,0,0,0);
            return;
        }
        float r = (float) Math.toDegrees(this.isAttacking ? this.attackRotation : this.rotation % (2 * Math.PI/NUM_ROTATION_ORIENTATIONS));

        float hScale = stunnedMillis <= 0 ? 1: STUNNED_SHRINK;
        //offset for the gun
        Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,this.getDeltaX() +(float) Math.cos(this.isAttacking ? this.attackRotation : this.rotation) * GUN_LENGTH * this.height/2,this.getDeltaY() + (float) Math.sin(this.isAttacking ? this.attackRotation : this.rotation) * GUN_LENGTH * this.height/2,0);
        Matrix.rotateM(blankInstanceInfo,0,r,0,0,1);
        Matrix.scaleM(blankInstanceInfo,0,this.width* hScale,this.height * hScale,0);
    }

    /** Moves the enemy to a specific position
     *
     * @param x how much to translate in the deltX direction
     * @param y how much to translate in the y direction
     */
    @Override
    public void setTranslate(float x,float y){
        this.deltaX = x;
        this.deltaY = y;
    }

    public void setVisibility(boolean isVisible){
        this.isVisible = isVisible;
    }

    public void setEgged(boolean egged) {
        this.egged = egged;
    }

    public void update(long dt, World world){
        if (! this.searchedForPath){
            this.searchedForPath = true;
            this.searchPath(world);

        }
        if (! spawned){
            world.addAnim(new EnemySpawn(this,this.getDeltaX(),this.getDeltaY(),EnemySpawn.SCALE * this.r * 2,EnemySpawn.SCALE * this.r * 2));
            this.spawned = true;
            return;
        }
        if (! isVisible || egged){
            return;
        }



        if (this.isDead()){

            world.addAnim(new DeathAnim(deltaX,deltaY,this.r * 2,this.r * 2));
            if (this.isBlue){
                world.getBlueEnemies().delete(this);
            } else {
                world.getOrangeEnemies().delete(this);
            }
            SoundLib.playPlayerKillSoundEffect();
            return;
        }
        this.millisSinceLastAttack += dt;




        if (readyToAttack()){
            if (attemptAttack(world)){
                this.millisSinceLastAttack = 0;
            }
        }

        stunnedMillis -= dt;
        prevDeltaX = deltaX;
        prevDeltaY = deltaY;
        boolean scrambling = isScrambling(world);
        if (this.canMove(world)) {
            if (scrambling){
                scramble(world,dt);
            }else {
                this.move(world, dt);
            }
            this.scrambling = scrambling;
        } else {
            this.velocityX = velocityY = 0;
        }
        this.clipPos(world);
        this.setFrame(this.rotation);
    }

    private void scramble(World world, long dt){
        //first call
        if (! scrambling){
            this.startX = deltaX;
            this.startY = deltaY;
        } else {
            //push a bit towards the player
            float dx = world.getPlayer().getDeltaX() - deltaX;
            float dy = world.getPlayer().getDeltaY() - deltaY;
            float dist = (float) Math.hypot(dx,dy);
            float mult = dist < dt * PUSH_SPEED/1000 ? 1 : dt * PUSH_SPEED/1000/dist;
            startX += dx * mult;
            startY += dy * mult;
        }

        boolean slowed = this.isSlowed(world);

        if ((float) this.millisSinceLastAttack/this.attackRate >1 - LUNGE_PERCENTAGE/2){
            float dx = world.getPlayer().getDeltaX() - deltaX;
            float dy = world.getPlayer().getDeltaY() - deltaY;
            float dist = (float) Math.hypot(dx,dy);
            float angle = dist == 0 ? 0:MathOps.getAngle(dx/dist,dy/dist);
            float speed = this.getCharacterSpeed() * (slowed ? this.getSpeedInToxicLake():1);
            this.velocityX = (float) Math.cos(angle) *speed;
            this.velocityY = (float) Math.sin(angle) *speed;
            this.rotation = angle;
        }
        else if ((float) this.millisSinceLastAttack/this.attackRate < LUNGE_PERCENTAGE/2){
            float dx = deltaX - world.getPlayer().getDeltaX();
            float dy = deltaY - world.getPlayer().getDeltaY();
            float dist = (float) Math.hypot(dx,dy);
            float angle = dist == 0 ? 0:MathOps.getAngle(dx/dist,dy/dist);
            float speed = this.getCharacterSpeed() * (slowed ? this.getSpeedInToxicLake():1);
            this.velocityX = (float) Math.cos(angle) *speed;
            this.velocityY = (float) Math.sin(angle) *speed;
        }

        else {
            float dist = (float)Math.hypot(deltaX-startX,deltaY-startY);
            boolean redirect = Math.random()  < REDIRECTION_CHANCE * dist;
            if (redirect || velocityX == 0 && velocityY == 0) {
                float returnAngle = (dist == 0) ? 0 : MathOps.getAngle((startX - deltaX) / dist, (startY - deltaY) / dist);
                float randVal = (float) Math.random() - 0.5f;
                float angle = (float) (randVal * randVal * 2 * Math.PI) + returnAngle;
                float speed = this.getCharacterSpeed() * (slowed ? this.getSpeedInToxicLake() : 1);
                this.velocityX = (float) Math.cos(angle) * speed;
                this.velocityY = (float) Math.sin(angle) * speed;
            }
        }
        float nextX = this.deltaX + this.velocityX * dt/1000;
        float nextY = this.deltaY + this.velocityY * dt/1000;
        boolean intersects = false;
        if (! slowed) {
            synchronized (World.toxicLakeLock) {
                ArrayList<ToxicLake> toxicLakes = world.getToxicLakes().getInstanceData();
                for (int i = 0; i < toxicLakes.size(); i++) {
                    if (toxicLakes.get(i).intersectsCircle(nextX, nextY, r)) {
                        intersects = true;
                        break;
                    }
                }
            }
        }
        if (! intersects ) this.setTranslate(nextX,nextY);
    }

    protected void searchPath(World world){
        PairIntFloat target = this.getNearestTarget(world,true, 0);
        world.getEnemyMap().updatePlayerPosition(world.getPlayer());
        world.getEnemyMap().requestPath(this,target.first);
        if (target.first == -1){
            this.finalTargetX = world.getPlayer().deltaX;
            this.finalTargetY = world.getPlayer().deltaY;
        }
    }

    protected boolean attemptAttack(World world){
        PairIntFloat target = this.getNearestTarget(world,false, this.minDist * 0.9f);
        int maxIndex = target.first;
        float minDist = target.second;

        if (maxIndex == -1 && minDist > this.minDist * 0.9f){
            target = this.getNearestTarget(world,false, 0);
            maxIndex = target.first;
            minDist = target.second;
        }


        if (minDist <= this.attackLen){
            if (maxIndex == -1){
                float angle = minDist == 0 ? 0: MathOps.getAngle((world.getPlayer().getDeltaX() - this.getDeltaX())/minDist,(world.getPlayer().getDeltaY() - this.getDeltaY())/minDist);
                this.attack(world,angle);
                this.attackRotation = angle;
            } else {
                Supply s =  world.getSupplies().getInstanceData().get(maxIndex);
                float angle = minDist == 0 ? 0: MathOps.getAngle((s.getDeltaX() - this.getDeltaX())/minDist,(s.getDeltaY() - this.getDeltaY())/minDist);
                this.attack(world,angle);
                this.attackRotation = angle;
            }
            this.isAttacking = true;
            return true;
        } else if (this.currentPath != null && this.currentPath.size() == 1 && this.currentPath.peek() != null){
            this.searchPath(world);
        }
        this.isAttacking = false;
        return false;
    }

    protected void move(World world,long dt){
        float targetX;
        float targetY;
        prevDeltaX = deltaX;
        prevDeltaY = deltaY;
        //means that the player is the current target
        if (currentPath == null || currentPath.peek() == null){
            targetX = world.getPlayer().getDeltaX();
            targetY = world.getPlayer().getDeltaY();

            if (Math.hypot(deltaX-targetX,deltaY - targetY) > this.attackLen && Math.hypot(finalTargetX -targetX, finalTargetY - targetY) > DIST_TILL_RESEARCH){
               this.searchPath(world);
            }
        } else {
            targetX = this.currentPath.peek().x;
            targetY = this.currentPath.peek().y;
        }

        float travelLen = (float) Math.hypot(this.getDeltaX() - targetX,this.getDeltaY()-targetY);

        if (currentPath != null && currentPath.size() > 1 && travelLen <  Enemy.DIST_TILL_NEXT_POS) this.currentPath.poll();

        float clippedLength = Math.min(travelLen, this.getCharacterSpeed() * dt / 1000f);

        //see if intersecting any toxic lakes
        if (this.isSlowed(world)){
            clippedLength *= this.getSpeedInToxicLake();
        }

        float startX = this.getDeltaX();
        float startY = this.getDeltaY();


        float actualLen = (float) Math.hypot( (targetX - this.getDeltaX()),(targetY - this.getDeltaY()));
        float targetRot = actualLen == 0 ? rotation: MathOps.getAngle( (targetX - this.getDeltaX())/actualLen,(targetY - this.getDeltaY())/actualLen);

        if (! startedPathTraverse && this.currentPath!= null ){
            this.rotation = targetRot;
            this.startedPathTraverse = true;
        } else {
            float rawAngle = MathOps.radDist(targetRot, rotation);
            float delta = MathOps.clip(rawAngle, -TURNSPEED * dt / 1000, TURNSPEED * dt / 1000);
            clippedLength -= clippedLength * Math.min(1,TURN_SLOWER * Math.abs(delta) * 1000/dt);
            float noise =(float) (Math.random() * MAX_NOISE - MAX_NOISE/2) * dt/1000;
            this.rotation += delta + noise;
        }

        if (currentPath == null || currentPath.size() > 1 || travelLen > minDist){
            this.translateFromPos((float) Math.cos(rotation) * clippedLength,(float) Math.sin(rotation) * clippedLength);
        } else if (travelLen < minDist * 0.95f) {
            this.translateFromPos(-(float) Math.cos(rotation) * clippedLength,-(float) Math.sin(rotation) * clippedLength);

        }
        this.velocityX = (this.getDeltaX() - startX) * 1000/dt;
        this.velocityY = (this.getDeltaY() - startY) * 1000/dt;

    }

    boolean isSlowed(World world){
        synchronized (World.toxicLakeLock){
            ArrayList<ToxicLake> toxicLakes = world.getToxicLakes().getInstanceData();
            for (int i = 0;i < toxicLakes.size();i++){
                if (toxicLakes.get(i).intersectsCharacter(this)){
                    return true;
                }
            }
        }
        return false;
    }
    private  void clipPos(World world){
        //dont need lock bc not doing anything with it
        ArrayList<Plateau> plateaus = world.getPlateaus().getInstanceData();
        for (int i =0,size = plateaus.size();i < size;i++){
            plateaus.get(i).clipCharacterPos(this);
        }
        float hypotenuse = (float) Math.hypot(getDeltaX(), getDeltaY());
        if (hypotenuse > world.getCraterRadius()) {
            setTranslate(getDeltaX() * world.getCraterRadius() / hypotenuse, getDeltaY() * world.getCraterRadius() / hypotenuse);
        }
    }

    //gets index of nearest supply, -1 if it's player, along with distance to that
    private PairIntFloat getNearestTarget(World world, boolean weighted, float minDistToConsider){
        int maxIndex = -1;
        float divisor = weighted ? this.getPlayerVsSupplyBias():1;
        float minDist = (float) Math.hypot(this.deltaX - world.getPlayer().getDeltaX(),this.deltaY - world.getPlayer().getDeltaY());
        for (int i = 0,size = world.getSupplies().size();i<size;i++){
            Supply s =  world.getSupplies().getInstanceData().get(i);
            double dist = Math.hypot(s.getDeltaX() - deltaX,s.getDeltaY() - deltaY) * divisor;
            if (dist < minDist && dist > minDistToConsider){
                maxIndex = i;
                minDist = (float) dist;
            }
        }
        return new PairIntFloat(maxIndex,minDist);
    }


    public void setPath(LinkedList<EnemyMap.Node> path){
        synchronized ( this.isBlue ? World.blueEnemyLock : World.orangeEnemyLock) {
            this.currentPath = path;
        }
    }
    @Override
    public void translateFromPos(float dX, float dY) {
        this.deltaX += dX;
        this.deltaY += dY;
    }
    /** Knocks back the enemy and stuns it
     *
     * @param stunnedMillis the amount of millis the enemy is stunned for
     */
    public void stun(long stunnedMillis){
        //this is the stun
        this.stunnedMillis = Math.max(this.stunnedMillis,stunnedMillis);
    }



    //row = rotation, col = frame num
    private void setFrame(float rotation){
        float framesPerMilli =  FPS/1000f;
        int frameNum = (int) (((System.currentTimeMillis() - this.startMillis) % (FRAMES_PER_ROTATION/framesPerMilli)) * framesPerMilli);
        this.deltaTextureX = MathOps.getTextureBufferTranslationX(frameNum,FRAMES_PER_ROTATION);
        this.deltaTextureY = MathOps.getTextureBufferTranslationY(rotation,NUM_ROTATION_ORIENTATIONS);
    }


    public abstract int getMaxHealth();

    public void attack(World world,float angle){
        float gunTipX = GUN_LENGTH * this.height + ShootAnimation.STANDARD_DIMENSIONS/2;
        //don't need h/2 because its in the middle
        float gunTipY = GUN_OFFSET_Y * this.height;
        float x = (float) (gunTipX * Math.cos(angle) - Math.sin(angle) * gunTipY);
        float y = (float) (gunTipX * Math.sin(angle) + Math.cos(angle) * gunTipY);
        ShootAnimation shootAnim = new ShootAnimation(deltaX + x, deltaY + y, ShootAnimation.STANDARD_DIMENSIONS, ShootAnimation.STANDARD_DIMENSIONS,(float)Math.toDegrees(angle));
        world.addAnim(shootAnim);
    }

    //can be overrided if sub classes want to change (this is multiplier)
    protected float getSpeedInToxicLake(){
        return 0.1f;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public float getVelocityX(){
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }
    public float getPrevDeltaX(){
        return prevDeltaX;
    }

    public float getPrevDeltaY() {
        return prevDeltaY;
    }

    public void addKnockback(Knockback k){
        if (this.knockback != null){
            knockback.cancel();
        }
        this.knockback = k;
    }

    //whether it should attack or not
    private boolean readyToAttack(){
        return millisSinceLastAttack > attackRate;
    }


    boolean canMove(World world){
        return stunnedMillis <= 0;
    }

    public float getHealth(){
        return health;
    }

    //how much more t would like to target players vs supplies
    float getPlayerVsSupplyBias(){
        return 1;
    }

    public void resetShader(){
        if (isBlue){
            this.setShader(0.5f,0.5f,1,1);
        } else{
            this.setShader(1,0.701f,0.4f,1);
        }

    }

    abstract boolean isScrambling(World world);

    LinkedList<Enemy3> getHealers(){
        return healers;
    }

    LinkedList<EnemyMap.Node> getPath(){
        return currentPath;
    }

    public int getStrength(){
        return strength;
    }

}
