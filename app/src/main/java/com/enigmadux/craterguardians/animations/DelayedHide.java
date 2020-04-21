package com.enigmadux.craterguardians.animations;

import com.enigmadux.craterguardians.guilib.VisibilitySwitch;


/** This takes in an Enigmadux Component and hides it after the specified time
 *
 */
public class DelayedHide extends TransitionAnim {


    //the component that needs to be hidden
    private VisibilitySwitch enigmaduxComponent;
    /** Default constructor
     *
     * @param enigmaduxComponent The said component that needs to be hidden
     * @param millis how long to delay the hiding of the enigmadux component
     */
    public DelayedHide(VisibilitySwitch enigmaduxComponent, long millis){
        super();
        this.enigmaduxComponent = enigmaduxComponent;
        HANDLER.postDelayed(this,millis);
    }


    /** Hides the enigmadux component
     *
     */
    @Override
    public void run() {
        this.enigmaduxComponent.setVisibility(false);
    }



}
