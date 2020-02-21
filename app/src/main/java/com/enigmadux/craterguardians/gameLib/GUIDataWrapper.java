package com.enigmadux.craterguardians.gameLib;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;

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


    /** Loading of data is buffered, in small steps, so we can draw the loading screen while we load stuff
     *
     * @return if the loading is complete
     */
    public boolean loadStep(){
        GUILayout layoutToBeAdded = null;
        switch (this.stepNum){
            case 0:

                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            case 14:
                break;
            case 15:
                break;
            case 16:
                break;
            case 17:
                break;
            case 18:
                break;
            case 19:
                break;
        }
        if (layoutToBeAdded != null){
            this.layouts.add(layoutToBeAdded);
        }

        //one more step has been completed
        this.stepNum++;

        return (this.stepNum >= GUIDataWrapper.NUM_STEPS);
    }




    /** Renders All GUIs, specifically the layouts
     *
     * @param mvpMatrix the parent matrix, though there might not be that much data since it's just GUIs
     * @param quadRenderer a renderer that can display quads to the screen
     * @param textRenderer renders text components
     */
    public void renderData(float[] mvpMatrix, QuadRenderer quadRenderer, DynamicText textRenderer){
        quadRenderer.startRendering();
        for (int i = 0,size = layouts.size();i<size;i++){
            //render current layout
            this.layouts.get(i).render(mvpMatrix,quadRenderer, textRenderer);
        }
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
