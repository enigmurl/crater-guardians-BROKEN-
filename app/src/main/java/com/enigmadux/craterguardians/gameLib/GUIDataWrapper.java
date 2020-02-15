package com.enigmadux.craterguardians.gameLib;

import android.view.MotionEvent;

import com.enigmadux.craterguardians.GUILib.GUILayout;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** This class helps draws GUIs, and other quadrilaterals
 *
 * @author Manu Bhat
 * @version BETA
 */
public class GUIDataWrapper {


    /** All Gui Layouts are stored here, which will later be rendered
     *
     */
    private ArrayList<GUILayout> layouts;


    /** Default Constructor
     *
     * @param layouts all gui layouts
     */
    public GUIDataWrapper(ArrayList<GUILayout> layouts){
        this.layouts = layouts;
    }


    /** Renders All GUIs, specifically the layouts
     *
     * @param mvpMatrix the parent matrix, though there might not be that much data since it's just GUIs
     * @param quadRenderer a renderer that can display quads to the screen
     */
    public void renderData(float[] mvpMatrix, QuadRenderer quadRenderer){
        for (int i = 0,size = layouts.size();i<size;i++){
            //render current layout
            this.layouts.get(i).render(mvpMatrix,quadRenderer);
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

}
