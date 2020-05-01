package com.enigmadux.craterguardians.animations;

import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;

/** Shades a component red for a couple of seconds
 *
 */
public class ColoredShader extends TransitionAnim {
    //to shade it red u need to shader the green and blue
    private static final float GB_VALUE = 0.5f;
    //default duration
    public static final long DEFAULT_LEN = 250;


    //the component that needs to be hidden
    private Character character;
    private CraterCollectionElem craterCollectionElem;

    private boolean cancel = false;


    //if it's an enemy that needs to be hidden
    /** Default constructor
     * @param millis how long to delay the un shading of the component
     * @param isRed
     */
    public ColoredShader(Character s, long millis, boolean isRed){
        super();
        this.character = s;
        if(isRed)
            s.setShader(1.0f,GB_VALUE,GB_VALUE,1);
        else
            s.setShader(GB_VALUE,1.0f,GB_VALUE,1);
        GAME_HANDLER.postDelayed(this,millis);
    }
    public ColoredShader(CraterCollectionElem craterCollectionElem, long millis, boolean isRed){
        super();
        this.craterCollectionElem = craterCollectionElem;
        if (isRed)
            craterCollectionElem.setShader(1.0f,GB_VALUE,GB_VALUE,1);
        else
            craterCollectionElem.setShader(GB_VALUE,1,GB_VALUE,1);
        GAME_HANDLER.postDelayed(this,millis);

    }



    /** Hides the enigmadux component todo, might wanna make it change back to original color
     *
     */
    @Override
    public void run() {
        if (! cancel) {
            if (this.character != null) {
                this.character.resetShader();
            } else {
                this.craterCollectionElem.resetShader();
            }
        }
    }

    public void cancel(){
        this.cancel = true;
    }
}
