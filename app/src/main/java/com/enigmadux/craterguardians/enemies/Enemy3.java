package com.enigmadux.craterguardians.enemies;


import android.util.Log;

import com.enigmadux.craterguardians.attacks.AttackEnemy3;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;
import java.util.LinkedList;

public class Enemy3 extends Enemy {

    private static final float RADIUS = 0.1f;
    //milliseconds between attacks
    private static final long[] ATTACK_RATE = new long[] {1000,975,950,925};
    private static final float[] SPEEDS = new float[] {0.80f,0.825f,0.85f,0.87f};

    //radians
    private static final float SEPARATION = 0.1f;

    //distance needed to move

    private static final float[] ATTACK_LEN = AttackEnemy3.LENGTH;

    private static final int MIN_HEALER_RETARGET = 3;

    private boolean moving = true;

    private Enemy target;

    private static final int[] HEALTHS = new int[] {20,33,50,70};

    private float offset;

    private int strength;

    private float moveDist;
    private float hoverDist;

    public Enemy3(int instanceID, float x, float y, boolean isBlue,int strength) {
        super(instanceID, x, y, RADIUS, ATTACK_LEN[strength], isBlue, ATTACK_RATE[strength],strength);
        this.strength = strength;
        this.moveDist = ATTACK_LEN[strength] - 0.1f;
        this.hoverDist = ATTACK_LEN[strength] * 0.5f;


    }

    @Override
    public int getMaxHealth() {
        return HEALTHS[strength];
    }

    @Override
    public void attack(World world, float angle) {
        super.attack(world,angle);
        int id = world.getEnemyAttacks().createVertexInstance();
        AttackEnemy3 attackEnemy3 = new AttackEnemy3(id,deltaX,deltaY,angle,this.isBlue,strength);
        world.getEnemyAttacks().addInstance(attackEnemy3);
    }

    @Override
    public void update(long dt, World world) {
        super.update(dt, world);
        if (target != null && (target.isDead() ||  (target.getHealth() == target.getMaxHealth() && target.getHealers().size() >MIN_HEALER_RETARGET))){
            target = null;
            this.currentPath = null;
        }
        if (target == null ){
            this.research(world);
        }
        if (this.currentPath == null && this.target != null && this.target.getPath() != null){
            EnemyMap.Node start  = this.target.getPath().getFirst();

            int index = Math.max(-1,start == null ? -1 : world.getEnemyMap().getNodeIndex(start.orgNode) - 1);
            world.getEnemyMap().requestPath(this,index);

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

            int size = maxEnemy.getHealers().size();
            int multiplier = size % 2 ==0 ? -1 * (size+1)/2 : (size+1)/2;
            this.offset = SEPARATION * multiplier;

        }
    }

    @Override
    public float getCharacterSpeed() {
        return SPEEDS[strength];
    }

    @Override
    protected boolean attemptAttack(World world) {
        if (this.target == null){
            return moving = false;
        }
        float minDist = (float) Math.hypot(target.getDeltaX()-deltaX,target.getDeltaY()-deltaY);
        moving = minDist >= moveDist && target.getPath() != null && (getPath() == null || target.getPath().size() < getPath().size());
        if (minDist <= ATTACK_LEN[strength]){
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
            //sandwich the target and player
            float dX = target.getDeltaX() - world.getPlayer().getDeltaX();
            float dY = target.getDeltaY() - world.getPlayer().getDeltaY();
            float hypotenuse = (float) Math.hypot(dX,dY);
            float angle = MathOps.getAngle(dX/hypotenuse,dY/hypotenuse) + offset;

            float targetX = target.getDeltaX() + (float) (Math.cos(angle) * hoverDist);
            float targetY = target.getDeltaY() + (float) (Math.sin(angle) * hoverDist);
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


    @Override
    public void setPath(LinkedList<EnemyMap.Node> path) {
        if (this.target != null && this.target.getPath() != null) {
            super.setPath(path);
            if (this.currentPath != null && this.target != null && this.target.getPath() != null) {
                this.currentPath.addAll(this.target.getPath());
            }
        }
    }

}
