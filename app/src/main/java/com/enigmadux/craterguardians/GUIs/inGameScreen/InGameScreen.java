package com.enigmadux.craterguardians.GUIs.inGameScreen;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** This is the layout in game that contains the pause button
 *
 * In the future, it may contain the evolve button and joysticks (todo)
 */
public class InGameScreen implements GUILayout {

    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.IN_GAME_SCREEN_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;


    /** Default Constructor
     *
     */
    public InGameScreen(){
        this.clickables = new ArrayList<>();
    }

    /** Loads components
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String, GUILayout> allLayouts) {
        //the pause button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.pause_button,
                -0.8f,0.75f,0.4f,0.4f,
                null,allLayouts.get(STRINGS.PAUSE_GAME_LAYOUT_ID)));



    }

    /** Handles touch events
     *
     * @param e the motion event that describes the type, and the position
     * @return if the event has been handled
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        if (! this.isVisible) return false;

        for (int i = this.clickables.size()-1;i>= 0;i--){
            if (this.clickables.get(i).onTouch(e)) return true;
        }
        return false;
    }

    /** SEts the visibility
     *
     * @param visibility whether or not to draw it, if this is hidden, the backend thread will be un paused
     */
    @Override
    public void setVisibility(boolean visibility) {
        this.isVisible = visibility;


        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
    }

    /** Renders sub components
     *
     * @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     */
    @Override
    public void render(float[] uMVPMatrix, QuadRenderer renderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.clickables, uMVPMatrix);
        }
    }
}
