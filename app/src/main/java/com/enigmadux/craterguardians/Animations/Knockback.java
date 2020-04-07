package com.enigmadux.craterguardians.Animations;

import com.enigmadux.craterguardians.enemies.Enemy;

/** An helper class that moves a character over some time
 *
 */
public class Knockback extends FrameTransitionAnim {
    //the default amount of time a knockback takes
    //note it is not actually "default" per se but more a reccomended value, it will not be automaticlaly implemented
    public static final long DEFAULT_MILLIS = 250;
    //the default knockback length
    //note it is not actually "default" per se but more a reccomended value, it will not be automaticlaly implemented
    public static final float DEFAULT_KNOCKBACK_LEN = 0.1f;



    //the component that needs to be hidden
    private Enemy enemy;

    //the amount to move in the deltX direction
    private float deltaX;
    //the amount to move in the y direction
    private float deltaY;

    private float startX;
    private float startY;

    /** Default constructor
     *
     * @param enemy The said component that needs to be knockBacked
     * @param millis how long to delay the un shading of the component
     * @param deltaX the amount to move in the deltX direction
     * @param deltaY the amount of move in the y direction
     *
     */
    public Knockback(Enemy enemy, long millis, float deltaX, float deltaY){
        super(millis);
        enemy.stun(millis);


        this.enemy = enemy;

        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.startX = enemy.getDeltaX();
        this.startY = enemy.getDeltaY();
        start();
    }

    @Override
    void step() {
        this.enemy.setTranslate(startX + (float) finishedMillis/totalMillis * (deltaX),startY + (float) finishedMillis/totalMillis * (deltaY));
    }
}
