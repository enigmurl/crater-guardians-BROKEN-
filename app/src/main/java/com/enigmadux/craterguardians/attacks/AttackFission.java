package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;
import com.enigmadux.craterguardians.gamelib.World;

public class AttackFission extends PlayerAttack {
    private static final int MAX_LEVEL = 5;
    private static final float LENGTH = 1;
    private static final float MAX_RADIUS = 0.2f;
    private static final float SPEED = 1f;
    private static final float DAMAGE = 15;


    private float damage;
    private int level;
    private int evolveGen;
    private CraterCollectionElem org;
    private AttackFission(int instanceID, float x, float y, float r, float angle, float speed, float damage,int level,int evolveGen,CraterCollectionElem org) {
        super(instanceID, x, y, r*2,r*2, angle, speed, LENGTH, damage);
        this.damage = damage;
        this.level = level;
        this.evolveGen = evolveGen;
        this.org = org;
    }

    public AttackFission(int instanceID,float x,float y,float angle,int evolveGen){
        this(instanceID,x,y,MAX_RADIUS,angle,SPEED,DAMAGE,MAX_LEVEL,evolveGen,null);
    }

    @Override
    boolean collidesWithEnemy(Enemy e) {
        return e != org && ! this.isFinished && Math.hypot(e.getDeltaX() - deltaX,e.getDeltaY() - deltaY) < e.getRadius() + this.width/2;
    }

    @Override
    boolean collidesWithSpawner(Spawner s) {
        return s != org && ! this.isFinished && s.collidesWithCircle(deltaX,deltaY,this.width/2);
    }

    @Override
    void onHitEnemy(Enemy e, World w) {
        super.onHitEnemy(e, w);
        this.isFinished = true;
        if (this.level > 1) {
            int id1 = w.getPlayerAttacks().createVertexInstance();
            int id2 = w.getPlayerAttacks().createVertexInstance();

            AttackFission a1 = new AttackFission(id1, deltaX, deltaY, this.width / 2 /(float) Math.sqrt(2), angle - (float) Math.PI / 4, speed, damage / 2, level - 1,evolveGen,e);
            AttackFission a2 = new AttackFission(id2, deltaX, deltaY, this.width / 2 /(float) Math.sqrt(2), angle + (float) Math.PI / 4, speed, damage / 2, level - 1,evolveGen,e);
            w.getPlayerAttacks().addInstance(a1);
            w.getPlayerAttacks().addInstance(a2);
        }
    }

    @Override
    void onHitSpawner(Spawner s, World w) {
        super.onHitSpawner(s, w);
        this.isFinished = true;
        if (this.level > 1) {
            int id1 = w.getPlayerAttacks().createVertexInstance();
            int id2 = w.getPlayerAttacks().createVertexInstance();

            AttackFission a1 = new AttackFission(id1, deltaX, deltaY,  this.width / 2 /(float) Math.sqrt(2), angle - (float) Math.PI / 4, speed, damage / 2, level - 1,evolveGen,s);
            AttackFission a2 = new AttackFission(id2, deltaX, deltaY, this.width / 2 /(float) Math.sqrt(2), angle + (float) Math.PI / 4, speed, damage / 2, level - 1,evolveGen,s);
            w.getPlayerAttacks().addInstance(a1);
            w.getPlayerAttacks().addInstance(a2);
        }
    }
}
