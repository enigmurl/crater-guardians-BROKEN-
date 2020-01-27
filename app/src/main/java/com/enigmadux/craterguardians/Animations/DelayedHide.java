package com.enigmadux.craterguardians.Animations;

import android.os.Handler;
import android.os.Looper;

import enigmadux2d.core.EnigmaduxComponent;

/** This takes in an Enigmadux Component and hides it after the specified time
 *
 */
public class DelayedHide extends TransitionAnim {


    //the component that needs to be hidden
    private EnigmaduxComponent enigmaduxComponent;
    /** Default constructor
     *
     * @param enigmaduxComponent The said component that needs to be hidden
     * @param millis how long to delay the hiding of the enigmadux component
     */
    public DelayedHide(EnigmaduxComponent enigmaduxComponent,long millis){
        super();
        this.enigmaduxComponent = enigmaduxComponent;
        HANDLER.postDelayed(this,millis);
    }


    /** Hides the enigmadux component
     *
     */
    @Override
    public void run() {
        this.enigmaduxComponent.hide();
    }



}
