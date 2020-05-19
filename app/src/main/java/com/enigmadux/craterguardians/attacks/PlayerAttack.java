package com.enigmadux.craterguardians.attacks;

import com.enigmadux.craterguardians.animations.Knockback;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.util.SoundLib;

/** ALL SUBCLASSES SPRITESHEETS MUST HAVE THE SAME NUM_FRAMES AS SPECIFIED BELOW, THEIR TEXTURES
 * CAN BE DIFFERENT THOUGH
 *
 */
public abstract class PlayerAttack extends BaseAttack {


    float damage;


    //animation spritesheet stuff
    private static final float FPS = 22;
    private static final int NUM_FRAMES = 8;


    public static final float[] TEXTURE_MAP = new float[] {
            0,0,
            0,1,
            1/(float) PlayerAttack.NUM_FRAMES,0,
            1/(float) PlayerAttack.NUM_FRAMES,1,
    };

    boolean enableKnockback = true;
    float knockbackLength = Knockback.DEFAULT_KNOCKBACK_LEN;
    long knockbackMillis = Knockback.DEFAULT_MILLIS;

    float spawnerDamageMult = 1;
    /**
     * Default Constructor
     *
     * @param instanceID     The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     * @param x
     * @param y
     * @param w
     * @param h
     * @param angle          RADIANS
     * @param speed          openGL coords/ per second
     * @param length         max length before dying
     */
    public PlayerAttack(int instanceID, float x, float y, float w, float h, float angle, float speed, float length,float damage) {
        super(instanceID, x, y, w, h, angle, speed, length, BaseAttack.PLAYER_ATTACK);

        this.damage = damage;
    }

    @Override
    public void collisionCheck(World world) {
        for (int i = 0, size = world.getBlueEnemies().size(); i < size; i++) {
            Enemy blueE = world.getBlueEnemies().getInstanceData().get(i);
            if (blueE.isVisible() && this.collidesWithEnemy(blueE)) {
                this.onHitEnemy(blueE, world);
                if (this.isFinished) return;
            }
        }


        for (int i = 0, size = world.getOrangeEnemies().size(); i < size; i++) {
            Enemy orangeE = world.getOrangeEnemies().getInstanceData().get(i);
            if (orangeE.isVisible() && this.collidesWithEnemy(orangeE)) {
                this.onHitEnemy(orangeE, world);
                if (this.isFinished) return;
            }
        }


        for (int i = 0, size = world.getSpawners().size(); i < size; i++) {
            Spawner s = world.getSpawners().getInstanceData().get(i);
            if (this.collidesWithSpawner(s)) {
                this.onHitSpawner(s,world);
                if (this.isFinished) return;
            }
        }
    }

    void onHitEnemy(Enemy e,World w){
        float orgH = e.getHealth();
        e.damage(this.damage);
        float after = e.getHealth();
        if (enableKnockback && e.isVisible()) {
            e.addKnockback(new Knockback(e,knockbackMillis,knockbackLength * (float) Math.cos(angle), knockbackLength * (float) Math.sin(angle)));
        }
        w.getPlayer().reportDamageDealt(orgH - after, e);
    }
    void onHitSpawner(Spawner s,World w){
        s.damage(this.damage * this.spawnerDamageMult);
        w.getPlayer().reportDamageDealt(this.damage * this.spawnerDamageMult,s);
    }

    abstract boolean collidesWithEnemy(Enemy e);

    //true if it does
    abstract boolean collidesWithSpawner(Spawner s);

    @Override
    public void finish(World world){
        world.getPlayerAttacks().delete(this);
    }


    @Override
    protected int getNumFrames() {
        return NUM_FRAMES;
    }

    @Override
    protected float getFramesPerSecond() {
        return FPS;
    }

}
