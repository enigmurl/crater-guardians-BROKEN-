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
    private static final float[] RADIUS =  new float[] {0.2f,0.205f,0.21f,0.215f};
    public static final float[] LENGTH = new float[] {1,1.5f,2f,2.5f};
    //per second
    //th
    private static final float[] SPEED = new float[] {2,2.5f,3f,3.5f};


    private static final int[] DAMAGE = new int[] {-4,-6,-10,-14};


    private boolean isBlue;
    private int strength;

    public AttackEnemy3(int instanceID, float x, float y, float angle,boolean isBlue,int strength) {
        super(instanceID, x, y, RADIUS[strength] *2,RADIUS[strength]*2, angle, SPEED[strength], LENGTH[strength], DAMAGE[strength]);
        this.setShader(0.5f,1,0.5f,1);
        this.isBlue = isBlue;
        this.strength = strength;
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
                if (blueE.isVisible() && Math.hypot(blueE.getDeltaX() - deltaX, blueE.getDeltaY() - deltaY) < RADIUS[strength] + blueE.getRadius()) {
                    blueE.damage(DAMAGE[strength]);
                    this.isFinished = true;
                    return;
                }
            }
        } else {
            for (int i = 0, size = world.getOrangeEnemies().size(); i < size; i++) {
                Enemy orangeE = world.getOrangeEnemies().getInstanceData().get(i);
                if (orangeE instanceof Enemy3) continue;
                if (orangeE.isVisible() && Math.hypot(orangeE.getDeltaX() - deltaX, orangeE.getDeltaY() - deltaY) < RADIUS[strength] + orangeE.getRadius()) {
                    orangeE.damage(DAMAGE[strength]);
                    this.isFinished = true;
                    return;
                }
            }
        }

    }
}
