package com.enigmadux.craterguardians;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.layouts.EnigmaduxLayout;
/** Layout that has some personal properties. Used to contain components
 *
 * @see enigmadux2d.layouts.EnigmaduxLayout
 * @see enigmadux2d.core.EnigmaduxComponent
 * @author Manu Bhat
 * @version BETA
 */
public class CraterLayout extends EnigmaduxLayout {
    private static final String TAG = "Crater_LAYOUT";
    /** Default constructor
     *
     * @param components The components that are part of the layout. This includes TexturedRect, Text, a Layout itself and anything else that derives from EnigmaduxComponent that is part of the layout
     */
    public CraterLayout(EnigmaduxComponent[] components,float x,float y,float w,float h){
        super(components,x,y,w,h);
    }

    /** Draws the component onto the screen
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        //Log.d(TAG,"drawingFrame " + components[0].getClass());
        for (int i = 0, size = this.components.length;i<size;i++){
            components[i].draw(parentMatrix);
        }

    }

    /** Gets the enigmadux component array
     *
     * @return all components in this layout
     */
    public EnigmaduxComponent[] getComponents(){
        return this.components;
    }


}
