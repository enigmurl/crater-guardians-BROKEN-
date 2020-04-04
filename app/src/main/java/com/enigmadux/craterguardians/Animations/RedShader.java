package com.enigmadux.craterguardians.Animations;

import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;

/** Shades a component red for a couple of seconds
 *
 */
public class RedShader extends TransitionAnim {
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
     */
    public RedShader(Character s, long millis){
        super();
        this.character = s;
        s.setShader(1.0f,GB_VALUE,GB_VALUE,1);
        HANDLER.postDelayed(this,millis);
    }
    public RedShader(CraterCollectionElem craterCollectionElem,long millis){
        super();
        this.craterCollectionElem = craterCollectionElem;
        craterCollectionElem.setShader(1.0f,GB_VALUE,GB_VALUE,1);
        HANDLER.postDelayed(this,millis);
    }



    /** Hides the enigmadux component todo, might wanna make it change back to original color
     *
     */
    @Override
    public void run() {
        if (! cancel) {
            if (this.character != null) {
                this.character.setShader(1, 1, 1, 1);
            } else {
                this.craterCollectionElem.setShader(1,1,1,1);
            }
        }
    }

    public void cancel(){
        this.cancel = true;
    }
}
