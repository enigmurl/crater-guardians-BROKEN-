package com.enigmadux.craterguardians.Attacks;

import com.enigmadux.craterguardians.Animations.Knockback;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.util.SoundLib;

/** ALL SUBCLASSES SPRITESHEETS MUST HAVE THE SAME NUM_FRAMES AS SPECIFIED BELOW, THEIR TEXTURES
 * CAN BE DIFFERENT THOUGH
 *
 */
public abstract class PlayerAttack extends BaseAttack {

    private boolean dealtDamage;

    private int damage;


    //animation spritesheet stuff
    private static final float FPS = 10;
    private static final int NUM_FRAMES = 5;


    public static final float[] TEXTURE_MAP = new float[] {
            0,0,
            0,1,
            1/(float) PlayerAttack.NUM_FRAMES,0,
            1/(float) PlayerAttack.NUM_FRAMES,1,
    };

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
    public PlayerAttack(int instanceID, float x, float y, float w, float h, float angle, float speed, float length,float normalDamage,float attackChargeUp) {
        super(instanceID, x, y, w, h, angle, speed, length, BaseAttack.PLAYER_ATTACK);

        this.damage = (int) (normalDamage * (1 + attackChargeUp));
        SoundLib.playPlayerShootSoundEffect();
    }

    @Override
    public void collisionCheck(World world) {
        for (int i = 0, size = world.getBlueEnemies().size(); i < size; i++) {
            Enemy blueE = world.getBlueEnemies().getInstanceData().get(i);
            if (blueE.isVisible() && this.collidesWithEnemy(blueE)){
                this.onHitEnemy(blueE,world);
                if (this.isFinished) return;
            }
        }

        for (int i = 0, size = world.getOrangeEnemies().size(); i < size; i++) {
            Enemy orangeE = world.getOrangeEnemies().getInstanceData().get(i);
            if (orangeE.isVisible() && this.collidesWithEnemy(orangeE)) {
                this.onHitEnemy(orangeE,world);
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
        SoundLib.playPlayerAttackLandSoundEffect();
        e.damage(this.damage);
        new Knockback(e,Knockback.DEFAULT_MILLIS,Knockback.DEFAULT_KNOCKBACK_LEN * (float) Math.cos(angle),Knockback.DEFAULT_KNOCKBACK_LEN * (float) Math.sin(angle));
        w.getPlayer().reportDamageDealt(this.damage, e);
        this.dealtDamage = true;
    }
    void onHitSpawner(Spawner s,World w){
        SoundLib.playSpawnerDamageSoundEffect();
        s.damage(this.damage);
        w.getPlayer().reportDamageDealt(this.damage,s);
        this.dealtDamage = true;
    }

    abstract boolean collidesWithEnemy(Enemy e);

    //true if it does
    abstract boolean collidesWithSpawner(Spawner s);

    @Override
    public void finish(World world){
        if (! this.dealtDamage){
            world.getPlayer().reportFailedAttack();
        }
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
