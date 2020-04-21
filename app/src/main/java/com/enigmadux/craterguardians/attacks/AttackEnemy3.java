package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.gameobjects.Shield;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.enemies.Enemy3;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.gamelib.World;

/** Special class that ignores a lot of the superclasses stuff
 *
 */
public class AttackEnemy3 extends EnemyAttack {
    private static final float RADIUS = 0.2f;
    public static final float LENGTH = 2f;
    //per second
    //th
    private static final float SPEED = 3f;


    private static final int DAMAGE = -10;


    private boolean isBlue;
    public AttackEnemy3(int instanceID, float x, float y, float angle,boolean isBlue) {
        super(instanceID, x, y, RADIUS *2,RADIUS*2, angle, SPEED, LENGTH, DAMAGE);
        this.setShader(0.5f,1,0.5f,1);
        this.isBlue = isBlue;
    }


    @Override
    public void update(long dt, World world) {
        //point to nearest enemy
        if (isBlue) {
            float minDist = Float.MAX_VALUE;
            Enemy minE = null;
            for (int i = 0, size = world.getBlueEnemies().size(); i < size; i++) {
                Enemy blueE = world.getBlueEnemies().getInstanceData().get(i);
                if (blueE instanceof Enemy3) continue;
                double dist;
                if (blueE.isVisible() && (dist = Math.hypot(blueE.getDeltaX() - deltaX, blueE.getDeltaY() - deltaY)) <minDist) {
                    minDist = (float) dist;
                    minE = blueE;
                }
            }
            if (minE != null) {
                this.angle = MathOps.getAngle((minE.getDeltaX() - deltaX)/minDist, (minE.getDeltaY() - deltaY)/minDist);
            }
        } else {
            float minDist = Float.MAX_VALUE;
            Enemy minE = null;
            for (int i = 0, size = world.getOrangeEnemies().size(); i < size; i++) {
                Enemy orangeE = world.getOrangeEnemies().getInstanceData().get(i);
                if (orangeE instanceof Enemy3) continue;
                double dist;
                if (orangeE.isVisible() && (dist = Math.hypot(orangeE.getDeltaX() - deltaX, orangeE.getDeltaY() - deltaY)) <minDist) {
                    minDist = (float) dist;
                    minE = orangeE;
                }
            }
            if (minE != null) {
                this.angle = MathOps.getAngle((minE.getDeltaX() - deltaX)/minDist, (minE.getDeltaY() - deltaY)/minDist);
            }
        }
        super.update(dt, world);
    }

    @Override
    boolean collidesWithPlayer(Player p) {
        return false;
    }

    @Override
    boolean collidesWithSupply(Supply s) {
        return false;
    }

    @Override
    boolean collidesWithShield(Shield s) {
        return false;
    }

    @Override
    public void collisionCheck(World world) {
        if (isBlue) {
            for (int i = 0, size = world.getBlueEnemies().size(); i < size; i++) {
                Enemy blueE = world.getBlueEnemies().getInstanceData().get(i);
                if (blueE instanceof Enemy3) continue;
                if (blueE.isVisible() && Math.hypot(blueE.getDeltaX() - deltaX, blueE.getDeltaY() - deltaY) < RADIUS + blueE.getRadius()) {
                    blueE.damage(DAMAGE);
                    this.isFinished = true;
                    return;
                }
            }
        } else {
            for (int i = 0, size = world.getOrangeEnemies().size(); i < size; i++) {
                Enemy orangeE = world.getOrangeEnemies().getInstanceData().get(i);
                if (orangeE instanceof Enemy3) continue;
                if (orangeE.isVisible() && Math.hypot(orangeE.getDeltaX() - deltaX, orangeE.getDeltaY() - deltaY) < RADIUS + orangeE.getRadius()) {
                    orangeE.damage(DAMAGE);
                    this.isFinished = true;
                    return;
                }
            }
        }

    }
}
