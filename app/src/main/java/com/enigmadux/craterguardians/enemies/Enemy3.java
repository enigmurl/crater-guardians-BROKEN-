package com.enigmadux.craterguardians.enemies;

import android.util.Log;

import com.enigmadux.craterguardians.attacks.AttackEnemy3;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Enemy3 extends Enemy {

    private static final float RADIUS = 0.1f;
    //milliseconds between attacks
    private static final long ATTACK_RATE = 1000;

    private static final float RAND_MULT = 0.2f;

    //distance needed to move
    private static final float MOVE_DIST = AttackEnemy3.LENGTH - 0.1f;

    private static final float ATTACK_LEN = AttackEnemy3.LENGTH;
    private static final float HOVER_DIST = ATTACK_LEN * 0.5f;


    private boolean moving = true;

    private Enemy target;

    private static final int[] HEALTHS = new int[] {20,33,50,70};


    public Enemy3(int instanceID, float x, float y, boolean isBlue,int strength) {
        super(instanceID, x, y, RADIUS, ATTACK_LEN, isBlue, ATTACK_RATE,strength);
    }

    @Override
    public int getMaxHealth() {
        return HEALTHS[strength];
    }

    @Override
    public void attack(World world, float angle) {
        super.attack(world,angle);
        int id = world.getEnemyAttacks().createVertexInstance();
        AttackEnemy3 attackEnemy3 = new AttackEnemy3(id,deltaX,deltaY,angle,this.isBlue);
        world.getEnemyAttacks().addInstance(attackEnemy3);
    }

    @Override
    public void update(long dt, World world) {
        super.update(dt, world);
        if (target != null && target.isDead()){
            target = null;
            this.currentPath = null;
        }
        if (target == null ){
            this.research(world);
        }
        if (this.currentPath == null && this.target != null && this.target.getPath() != null){
            Pathfinder pathfinder = new Pathfinder(world.getEnemyMap(),this.target);
            pathfinder.run();

        }
    }
    private void research(World world) {
        ArrayList<Enemy> enemies = this.isBlue ? world.getBlueEnemies().getInstanceData() : world.getOrangeEnemies().getInstanceData();
        Enemy maxEnemy = null;
        int minHealers = Integer.MAX_VALUE;
        float minSqrDist = Float.MAX_VALUE;
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (! (e instanceof Enemy3)){
                float dist ;
                if (((dist = MathOps.sqrDist(deltaX-e.getDeltaX(),deltaY - e.getDeltaY())) < minSqrDist && e.getHealers().size() == minHealers) || e.getHealers().size() < minHealers){
                    minSqrDist = dist;
                    minHealers = e.getHealers().size();
                    maxEnemy = e;
                }
            }
        }
        if (maxEnemy != null){
            this.target = maxEnemy;
            maxEnemy.getHealers().add(this);
            this.currentPath = null;
        }
    }

    @Override
    public float getCharacterSpeed() {
        return 0.85f;
    }

    @Override
    protected boolean attemptAttack(World world) {
        if (this.target == null){
            return moving = false;
        }
        float minDist = (float) Math.hypot(target.getDeltaX()-deltaX,target.getDeltaY()-deltaY);
        moving = minDist >= MOVE_DIST && target.getPath() != null && (getPath() == null || target.getPath().size() < getPath().size());
        if (minDist <= ATTACK_LEN){
            float angle = minDist == 0 ? 0: MathOps.getAngle((target.getDeltaX() - this.getDeltaX())/minDist,(target.getDeltaY() - this.getDeltaY())/minDist);
            this.attack(world,angle);
            return true;
        }

        return false;
    }

    @Override
    boolean canMove(World world) {
        return super.canMove(world);
    }

    @Override
    protected void move(World world, long dt) {
        if (moving) {
            super.move(world, dt);
        } else if (target != null) {
            //sandwich the target and palyer
            float dX = target.getDeltaX() - world.getPlayer().getDeltaX();
            float dY = target.getDeltaY() - world.getPlayer().getDeltaY();
            float hypot = (float) Math.hypot(dX,dY);
            float mult = (hypot + HOVER_DIST)/hypot;
            dX *= mult * (1 + RAND_MULT * (Math.random() - 0.5));
            dY *= mult * (1 + RAND_MULT * (Math.random() - 0.5));
            float targetX = world.getPlayer().getDeltaX() + dX;
            float targetY = world.getPlayer().getDeltaY() + dY;
            float travelLen = (float) Math.hypot(this.getDeltaX() - targetX,this.getDeltaY()-targetY);

            float clippedLength = Math.min(travelLen, this.getCharacterSpeed() * dt / 1000f);

            //see if intersecting any toxic lakes
            if (this.isSlowed(world)){
                clippedLength *= this.getSpeedInToxicLake();
            }


            float startX = this.getDeltaX();
            float startY = this.getDeltaY();


            float actualLen = (float) Math.hypot( (targetX - this.getDeltaX()),(targetY - this.getDeltaY()));

            this.rotation = actualLen == 0 ? rotation: MathOps.getAngle( (targetX - this.getDeltaX())/actualLen,(targetY - this.getDeltaY())/actualLen);

            this.translateFromPos((float) Math.cos(rotation) * clippedLength,(float) Math.sin(rotation) * clippedLength);


            this.velocityX = (this.getDeltaX() - startX) * 1000/dt;
            this.velocityY = (this.getDeltaY() - startY) * 1000/dt;

        }
    }

    @Override
    boolean isScrambling(World world) {
        return false;
    }

    @Override
    float getPlayerVsSupplyBias() {
        return 5;
    }

    @Override
    protected void searchPath(World world) {
        //the super class does not decide our path, todo find better solution, we only search for it when needed
    }



    class Pathfinder implements Runnable{

        private EnemyMap enemyMap;
        private Enemy target;
        /**
         * Default constructor
         *
         * @param enemyMap    the map used to actually determine the path which has information about the level

         */
        Pathfinder(EnemyMap enemyMap,Enemy target) {
            super();
            this.enemyMap = enemyMap;
            this.target = target;
        }

        @Override
        public void run() {
            try {
                if (EnemyMap.LOCK.tryLock(5, TimeUnit.SECONDS)) {
                    //the minus 1 is too offset the player node
                    EnemyMap.Node start  = this.target.getPath().getFirst();

                    int index = start == null ? -1 : this.enemyMap.getNodeIndex(start.orgNode) - 1;
                    Log.d("Enemy Path"," Index: " + (index +1));
                    try {
                        LinkedList<EnemyMap.Node> prePath = this.enemyMap.nextStepMap(getRadius(), deltaX,deltaY,index);
                        prePath.addAll(target.getPath());
                        currentPath = prePath;
                    } catch (Exception e){
                        Log.d("ENEMY PATH","Exception trying to gain path: ",e);
                    }
                    finally {
                        EnemyMap.LOCK.unlock();
                    }
                    Log.d("Enemy","PAth: " + currentPath);
                }
            } catch (InterruptedException e) {
                Log.d("ENEMY PATH","PATH FAILED");

            }

        }
    }
}
