package com.enigmadux.craterguardians.enemies;


import com.enigmadux.craterguardians.Attacks.AttackEnemy1;
import com.enigmadux.craterguardians.worlds.World;

public class Enemy1 extends Enemy {
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 8;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 8;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 16;

    private static final float RADIUS = 0.3f;
    //milliseconds between attacks
    private static final long ATTACK_RATE = 1000;

    public static final float[] TEXTURE_MAP = new float[] {
            0,(Enemy1.NUM_ROTATION_ORIENTATIONS-1f)/Enemy1.NUM_ROTATION_ORIENTATIONS,
            0,1,
            1/(float) Enemy1.FRAMES_PER_ROTATION,(Enemy1.NUM_ROTATION_ORIENTATIONS-1f)/Enemy1.NUM_ROTATION_ORIENTATIONS,
            1/(float) Enemy1.FRAMES_PER_ROTATION,1,
    };


    /**
     * Default Constructor
     *
     * @param instanceID the id of the instance in reference to the vao it's in (received using VaoCollection.addInstance());
     * @param x          the center x in openGL terms
     * @param y          the center y in openGL terms
     * @param isBlue    if its blue
     */
    public Enemy1(int instanceID, float x, float y, boolean isBlue) {
        super(instanceID, x, y, RADIUS, isBlue,ATTACK_RATE);
    }


    public float getCharacterSpeed(){
        return 0.5f;
    }

    @Override
    public int getMaxHealth() {
        return 40;
    }

    @Override
    public void update(long dt, World world) {
        super.update(dt, world);
    }

    @Override
    protected int getNumRotationOrientations() {
        return NUM_ROTATION_ORIENTATIONS;
    }

    @Override
    protected int getFramesPerRotation() {
        return FRAMES_PER_ROTATION;
    }

    @Override
    protected float getFramesPerSecond() {
        return FPS;
    }

    public void attack(World world,float angle){
        int id = world.getEnemyAttacks().createVertexInstance();
        AttackEnemy1 a = new AttackEnemy1(id,this.getDeltaX(),this.getDeltaY(),angle);
        world.getEnemyAttacks().addInstance(a);
    }
}
