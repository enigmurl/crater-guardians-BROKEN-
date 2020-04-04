package com.enigmadux.craterguardians.Attacks;

import com.enigmadux.craterguardians.GameObjects.Shield;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.util.SoundLib;

public abstract class EnemyAttack extends BaseAttack {

    //animation spritesheet stuff
    private static final float FPS = 10;
    private static final int NUM_FRAMES = 8;

    public static final float[] TEXTURE_MAP = new float[] {
            0,0,
            0,1,
            1/(float) EnemyAttack.NUM_FRAMES,0,
            1/(float) EnemyAttack.NUM_FRAMES,1,
    };

    private int damage;
    /**
     *
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
    public EnemyAttack(int instanceID, float x, float y, float w, float h, float angle, float speed, float length,int damage) {
        super(instanceID, x, y, w, h, angle, speed, length, BaseAttack.ENEMY_ATTACK);
        this.damage = damage;
    }

    @Override
    public void collisionCheck(World world) {
        if (this.collidesWithShield(world.getPlayer().getShield())){
            this.isFinished = true;
            SoundLib.playPlayerShieldBlockSoundEffect();
            return;
        }

        Player p = world.getPlayer();
        if (this.collidesWithPlayer(p)){
            this.onHitPlayer(p);
            if (this.isFinished) return;
        }
        for (int i = 0,size = world.getSupplies().size();i<size;i++){
            Supply s =  world.getSupplies().getInstanceData().get(i);
            if (this.collidesWithSupply(s)){
                this.onHitSupply(s);
                if (this.isFinished) return;
            }
        }
    }

    abstract boolean collidesWithPlayer(Player p);

    abstract boolean collidesWithSupply(Supply s);

    abstract boolean collidesWithShield(Shield s);

    void onHitPlayer(Player player){
        player.damage(this.damage);
        SoundLib.playEnemyDamagePlayerSoundEffect();
    }

    void onHitSupply(Supply s){
        s.damage(this.damage);
        SoundLib.playEnemyDamageSupplySoundEffect();

    }

    @Override
    public void finish(World world){
        world.getEnemyAttacks().delete(this);
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
