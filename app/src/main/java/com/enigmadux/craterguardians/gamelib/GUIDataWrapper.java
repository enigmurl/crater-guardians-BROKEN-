package com.enigmadux.craterguardians.gamelib;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.guilib.GUILayout;

import java.util.ArrayList;
import java.util.HashMap;

/** This class helps draws GUIs, and other quadrilaterals
 *
 * COMMON DEBUGS:
 *  Regarding layouts, if you call the loadComponents method, by default all added items will be invisible, so call
 *  methods like setVisibility() AFTER you load components
 *
 *
 * @author Manu Bhat
 * @version BETA
 */
public class GUIDataWrapper {

    /** The amount of layouts to load
     *
     */
    private static final int NUM_STEPS = 20;


    /** All Gui Layouts are stored here, which will later be rendered
     *
     */
    private ArrayList<GUILayout> layouts;

    /** What step we are in the loading process
     *
     */
    private int stepNum;



    /** Default Constructor
     *
     * @param layouts all gui layouts
     */
    public GUIDataWrapper(ArrayList<GUILayout> layouts){
        this.layouts = layouts;
    }



    /** Handles touch events, by giving it to the ones highest on the screen first (rendered last)
     *
     * @param e the description of the motion event
     * @return whether or not the touch event has been handled (true = handled)
     */
    public boolean onTouch(MotionEvent e){
        for (int i = this.layouts.size()-1;i>=0;i--){
            if (this.layouts.get(i).onTouch(e)) return true;
        }
        return false;
    }

    /** Due to complexities with references, this can't be in the constructor, but basically loads
     * the sub components of each layouts
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){
        for (int i = 0,size = layouts.size();i<size;i++){
            //render current layout
            this.layouts.get(i).loadComponents(context,allLayouts);
        }
    }

}
