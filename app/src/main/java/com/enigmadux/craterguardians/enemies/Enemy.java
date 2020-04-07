package com.enigmadux.craterguardians.enemies;

import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.Animations.EnemySpawn;
import com.enigmadux.craterguardians.Animations.Knockback;
import com.enigmadux.craterguardians.Animations.RedShader;
import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.GameObjects.Plateau;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.GameObjects.ToxicLake;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.util.PairIntFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/** New Enemy
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Enemy extends CraterCollectionElem implements Character {

    //amount of distance required to query onto the next pos
    private static final float DIST_TILL_NEXT_POS = 0.3f;
    //distance player had to move required to redirect
    private static final float DIST_TILL_RESEARCH = 1f;





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

    private Queue<EnemyMap.Node> currentPath;
    private boolean searchedForPath = false;

    //when it starts, so that we can calculate correct frame num
    private long startMillis = System.currentTimeMillis();

    private long stunnedMillis = 0;

    //direction we're heading rads
    private float rotation;

    private boolean spawned = false;

    //set to true in the spawning animation
    protected boolean isVisible = false;

    private float velocityX;
    private float velocityY;

    //previous position
    private float prevDeltaX;
    private float prevDeltaY;

    private Knockback knockback;

    //if it's targetting player, where was it when it first tried it
    private float playerStartX;
    private float playerStartY;
    /** Default Constructor
     *
     * @param instanceID the id of the instance in reference to the vao it's in (received using VaoCollection.addInstance());
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     * @param r the radius in openGL terms (half the width, half the height)
     * @param milliSeconds ms between attacks
     */
    public Enemy(int instanceID,float x,float y,float r,float attackLen,boolean isBlue,long milliSeconds){
        super(instanceID);

        //assign variables to attributes
        this.deltaX = x;
        this.deltaY = y;
        this.r = r;

        this.width = r*2;
        this.height = r*2;

        this.isBlue = isBlue;
        this.attackLen = attackLen;

        this.health = this.getMaxHealth();

        if (isBlue){
            this.setShader(0,0,1,1);
        } else{
            this.setShader(1,0,0,1);
        }

        this.attackRate = milliSeconds;
        this.millisSinceLastAttack = attackRate;

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
    public void damage(int damage) {
        //if its not visible shouldn't be able to be damaged
        if (! this.isVisible) return;
        //need to cast to avoid ambiguity
        new RedShader((Character) this,RedShader.DEFAULT_LEN);
        this.health -= damage;
    }


    /** Sees if this enemy is alive
     *
     * @return if the enemy health is above 0
     */
    public boolean isAlive(){
        return this.health > 0;
    }


    /** Sets the filtration of specific channels to the desired values. The most common uses may be shade the character when
     * losing or gaining health
     *
     * @param r the filter of the red channel
     * @param b the filter of the blue channel
     * @param g the filter of the green channel
     * @param a the filter of the alpha channel
     */
    @Override
    public void setShader(float r, float b, float g, float a) {
        super.setShader(r, b, g, a);
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
            Matrix.scaleM(blankInstanceInfo,0,uMVPMatrix,0,0,0,0);
            return;
        }
        Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,this.getDeltaX(),this.getDeltaY(),0);
        Matrix.scaleM(blankInstanceInfo,0,2 * this.r,2 * this.r,0);
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



    public void update(long dt, World world){
        if (! spawned){
            world.getAnims().add(new EnemySpawn(this,this.getDeltaX(),this.getDeltaY(),this.r * 2,this.r * 2));
            this.spawned = true;
            return;
        } if (! isVisible){
            return;
        }
        if (! this.isAlive()){
            if (this.isBlue){
                world.getBlueEnemies().delete(this);
            } else {
                world.getOrangeEnemies().delete(this);
            }
            return;
        }
        this.millisSinceLastAttack += dt;

        if (! this.searchedForPath){
            this.searchedForPath = true;
            PairIntFloat target = this.getNearestTarget(world);
            PathFinder pf = new PathFinder(world.getEnemyMap(),target.first,this.getDeltaX(),this.getDeltaY(),world.getPlayer());
            pf.start();
        }


        if (readyToAttack()){
            PairIntFloat target = this.getNearestTarget(world);
            int maxIndex = target.first;
            float minDist = target.second;
            if (minDist <= this.attackLen){
                if (maxIndex == -1){
                    float angle = MathOps.getAngle((world.getPlayer().getDeltaX() - this.getDeltaX())/minDist,(world.getPlayer().getDeltaY() - this.getDeltaY())/minDist);
                    this.attack(world,angle);

                } else {
                    Supply s =  world.getSupplies().getInstanceData().get(maxIndex);
                    float angle = MathOps.getAngle((s.getDeltaX() - this.getDeltaX())/minDist,(s.getDeltaY() - this.getDeltaY())/minDist);
                    this.attack(world,angle);

                }
                this.millisSinceLastAttack = 0;
            }
        }
        stunnedMillis -= dt;
        if (this.stunnedMillis <= 0) {
            this.move(world, dt);
        }
        this.setFrame(this.rotation);

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

            if (Math.hypot(deltaX-targetX,deltaY - targetY) > this.attackLen && Math.hypot(playerStartX-targetX,playerStartY - targetY) > DIST_TILL_RESEARCH){
                PairIntFloat target = this.getNearestTarget(world);
                PathFinder pf = new PathFinder(world.getEnemyMap(),target.first,this.getDeltaX(),this.getDeltaY(),world.getPlayer());
                pf.start();
            }
        } else {
            targetX = this.currentPath.peek().x;
            targetY = this.currentPath.peek().y;
        }

        double travelLen = (float) Math.hypot(this.getDeltaX() - targetX,this.getDeltaY()-targetY);

        if (currentPath != null && currentPath.size() > 1 && travelLen <  Enemy.DIST_TILL_NEXT_POS) this.currentPath.poll();


        //this may be wrong
        float clippedLength = (travelLen >  this.getCharacterSpeed() * dt / 1000f) ? (this.getCharacterSpeed() *  dt / 1000f)/ (float) travelLen: 1;



        //see if intersecting any toxic lakes
        boolean slowed = false;
        synchronized (World.toxicLakeLock){
            ArrayList<ToxicLake> toxicLakes = world.getToxicLakes().getInstanceData();
            for (int i = 0;i < toxicLakes.size();i++){
                if (toxicLakes.get(i).intersectsCharacter(this)){
                    slowed = true;
                    break;
                }
            }
        }
        if (slowed){
            clippedLength *= this.getSpeedInToxicLake();
        }

        float startX = this.getDeltaX();
        float startY = this.getDeltaY();

        this.translateFromPos(clippedLength * (targetX - this.getDeltaX()),clippedLength * (targetY - this.getDeltaY()) );

        //dont need lock bc not doing anything with it
        ArrayList<Plateau> plateaus = world.getPlateaus().getInstanceData();
        for (int i =0,size = plateaus.size();i < size;i++){
            plateaus.get(i).clipCharacterPos(this);
        }


        float actualLen = (float) Math.hypot(this.getDeltaX() - startX,this.getDeltaY() - startY);

        this.velocityX = (this.getDeltaX() - startX) * 1000/dt;
        this.velocityY = (this.getDeltaY() - startY) * 1000/dt;

        this.rotation = MathOps.getAngle( (this.getDeltaX() - startX)/actualLen, (this.getDeltaY() - startY)/actualLen);


    }

    //gets index of nearest supply, -1 if it's player, along with distance to that
    private PairIntFloat getNearestTarget(World world){
        int maxIndex = -1;
        float minDist = (float) Math.hypot(this.deltaX - world.getPlayer().getDeltaX(),this.deltaY - world.getPlayer().getDeltaY());
        for (int i = 0,size = world.getSupplies().size();i<size;i++){
            Supply s =  world.getSupplies().getInstanceData().get(i);
            double dist = Math.hypot(s.getDeltaX() - deltaX,s.getDeltaY() - deltaY);
            if (dist < minDist){
                maxIndex = i;
                minDist = (float) dist;
            }
        }
        return new PairIntFloat(maxIndex,minDist);
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



    /** Used to find a path without holding up other threads.
     *
     */
    private class PathFinder extends Thread {
        //the map used to actually determine the path
        private EnemyMap enemyMap;
        //the target supply, -1 represents targetting the player
        private int supplyIndex;
        //the deltX position of the enemy
        private float x;
        //the y position of the enemy
        private float y;

        private Player player;


        /**  Default constructor
         *
         * @param enemyMap the map used to actually determine the path which has information about the level
         * @param supplyIndex the target supply, -1 represents targeting the player
         * @param x the current deltX position of this enemy
         * @param y the current y position of this enemy
         * @param player current Player
         */
        PathFinder(EnemyMap enemyMap, int supplyIndex, float x, float y, Player player){
            this.enemyMap = enemyMap;
            this.supplyIndex = supplyIndex;
            this.x = x;
            this.y = y;
            this.player = player;
        }

        @Override
        public void run() {
            super.run();

            try {
                if (EnemyMap.LOCK.tryLock(5, TimeUnit.SECONDS)) {
                    try {
                        this.enemyMap.updatePlayerPosition(player);
                        currentPath = this.enemyMap.nextStepMap(getRadius(), x, y, this.supplyIndex);
                    } catch (Exception e){
                        Log.d("ENEMY PATH","Exception trying to gain path: ",e);
                    }
                    finally {
                        EnemyMap.LOCK.unlock();
                    }
                    if (currentPath== null  || currentPath.peek() == null){
                        playerStartX = player.getDeltaX();
                        playerStartY = player.getDeltaY();
                        Log.d("Enemy Path","Locked onto player x: " + x + " y: " + y + " deltaX: " + deltaX + " deltaY: " + deltaY);
                    }
                }
            } catch (InterruptedException e) {
                Log.d("ENEMY PATH","PATH FAILED");

            }



            //Log.d("ENEMY PATH", "path: "  + currentPath + " supply: " + supplyIndex);

            try {
                this.join();
            } catch (InterruptedException e){
                Log.d("PathFinder","interrupted self");
            }
        }
    }
    //row = rotation, col = frame num
    private void setFrame(float rotation){
        float framesPerMilli =  getFramesPerSecond()/1000f;
        int frameNum = (int) (((System.currentTimeMillis() - this.startMillis) % (this.getFramesPerRotation()/framesPerMilli)) * framesPerMilli);
        this.deltaTextureX = MathOps.getTextureBufferTranslationX(frameNum,this.getFramesPerRotation());
        this.deltaTextureY = MathOps.getTextureBufferTranslationY(rotation,this.getNumRotationOrientations());
    }


    protected abstract int getNumRotationOrientations();

    protected abstract int getFramesPerRotation();

    protected abstract float getFramesPerSecond();

    public abstract int getMaxHealth();

    public abstract void attack(World world,float angle);

    //can be overrided if sub classes want to change (this is multiplier)
    protected float getSpeedInToxicLake(){
        return 0.1f;
    }
    //can be override
    protected float getMass(){
        return 10;
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


}
