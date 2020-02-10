package com.enigmadux.craterguardians.Animations;

import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Enemies.Enemy;

/** Shades a component red for a couple of seconds
 *
 */
public class RedShader extends TransitionAnim {
    //to shade it red u need to shader the green and blue
    private static final float GB_VALUE = 0.5f;
    //default duration
    public static final long DEFAULT_LEN = 250;


    //the component that needs to be hidden
    private BaseCharacter baseCharacter;

    //if it's an enemy that needs to be hidden
    private Enemy enemy;
    /** Default constructor
     *
     * @param baseCharacter The said component that needs to be shaded red
     * @param millis how long to delay the un shading of the component
     */
    public RedShader(BaseCharacter baseCharacter, long millis){
        super();
        this.baseCharacter = baseCharacter;
        this.baseCharacter.setShader(1.0f,GB_VALUE,GB_VALUE,1);
        HANDLER.postDelayed(this,millis);
    }

    /** Default constructor
     *
     * @param baseCharacter The said component that needs to be shaded red
     * @param millis how long to delay the un shading of the component
     */
    public RedShader(Enemy baseCharacter, long millis){
        super();
        this.enemy = baseCharacter;
        this.enemy.setShader(1.0f,GB_VALUE,GB_VALUE,1);
        HANDLER.postDelayed(this,millis);
    }



    /** Hides the enigmadux component
     *
     */
    @Override
    public void run() {
        if (this.baseCharacter != null)
            this.baseCharacter.setShader(1.0f,1,1,1);
        else
            this.enemy.setShader(1.0f,1,1,1);
    }
}
