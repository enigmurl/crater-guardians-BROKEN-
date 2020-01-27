package com.enigmadux.craterguardians.Animations;

import com.enigmadux.craterguardians.Enemies.Enemy;

/** An helper class that moves a character over some time
 *
 */
public class Knockback extends TransitionAnim {
    //the amount of milliseconds in between small pushes
    private static final long DELAY_MILLIS = 16;

    //the default amount of time a knockback takes
    //note it is not actually "default" per se but more a reccomended value, it will not be automaticlaly implemented
    public static final long DEFAULT_MILLIS = 250;
    //the default knockback length
    //note it is not actually "default" per se but more a reccomended value, it will not be automaticlaly implemented
    public static final float DEFAULT_KNOCKBACK_LEN = 0.1f;



    //the component that needs to be hidden
    private Enemy enemy;
    //the amount of milliseconds left
    private long millisLeft;
    //the total amount of milliseconds in this animatino
    private long totalMillis;

    //the amount to move in the x direction
    private float deltaX;
    //the amount to move in the y direction
    private float deltaY;

    /** Default constructor
     *
     * @param enemy The said component that needs to be knockBacked
     * @param millis how long to delay the un shading of the component
     * @param deltaX the amount to move in the x direction
     * @param deltaY the amount of move in the y direction
     *
     */
    public Knockback(Enemy enemy, long millis, float deltaX, float deltaY){
        super();
        enemy.stun(millis);


        this.enemy = enemy;
        this.millisLeft = millis;
        this.totalMillis = millis;

        this.deltaX = deltaX;
        this.deltaY = deltaY;
        HANDLER.postDelayed(this,DELAY_MILLIS);
    }


    /** Hides the enigmadux component
     *
     */
    @Override
    public void run() {
        this.enemy.translateFromPos(this.deltaX * DELAY_MILLIS/this.totalMillis,this.deltaY * DELAY_MILLIS/this.totalMillis);
        this.millisLeft -= DELAY_MILLIS;
        if (this.millisLeft > 0){
            HANDLER.postDelayed(this,DELAY_MILLIS);
        }
    }

}
