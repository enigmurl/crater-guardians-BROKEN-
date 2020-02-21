package com.enigmadux.craterguardians.GUIs.levelSelect;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.GUIs.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.GameMap;
import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** This is where the player selects the level
 *
 * @author Manu Bhat
 * @version BETA
 */
public class LevelSelectLayout implements GUILayout {


    /** The width and height of each icon, but will be scale down for x axis to make a square
     *
     */
    private static final float ICON_WIDTH = 0.3f;
    /** The amount of space to the left and right
     * Technically it's a bit less, because the margins defined where the center starts, not where the edges do
     *
     */
    private static final float SIDE_MARGINS = 0.2f;

    /** The space between consecutive icons (for the y axis, for x it's scale)
     *
     */
    private static final float ICON_MARGINS = 0.1f;

    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.LEVEL_SELECT_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** Backend object used to go into the actual game
     *
     */
    private CraterBackendThread backend;


    /** The layout in game that we need to hide some times
     *
     */
    private GUILayout inGameLayout;

    /** Default Constructor
     *
     * @param backendThread the backend thread
     */
    public LevelSelectLayout(CraterBackendThread backendThread){
        this.clickables = new ArrayList<>();
        this.backend = backendThread;
    }

    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){
        //the home button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.home_button,
                0,-0.4f,0.4f,0.4f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID), false));


        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;

        for (int i = 0;i < GameMap.NUM_LEVELS;i++){
            float x = -1 + SIDE_MARGINS + ((i*scaleX * (ICON_WIDTH + ICON_MARGINS)) % (2 - 2 * SIDE_MARGINS));
            float y = 1- SIDE_MARGINS -  ICON_WIDTH * (int) ((i*scaleX * (ICON_WIDTH + ICON_MARGINS))/(2 - 2 * SIDE_MARGINS));
            this.clickables.add(new LevelSelector(context,R.drawable.level_button_background,
                    x,y,ICON_WIDTH,ICON_WIDTH,
                    this,allLayouts.get(InGameScreen.ID),
                    this.backend,i+1));
        }

        this.inGameLayout = allLayouts.get(InGameScreen.ID);

    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
 e     */
    @Override
    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.clickables, uMVPMatrix);
            for (int i = 0,size = this.clickables.size();i<size;i++){
                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
            }
        }
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

    /** Sets the visibility
     *
     * @param visibility whether to draw it or not
     */
    @Override
    public void setVisibility(boolean visibility) {
        this.isVisible = visibility;
        if (this.isVisible){
            SoundLib.setStateGameMusic(false);
            SoundLib.setStateVictoryMusic(false);
            SoundLib.setStateLossMusic(false);
            SoundLib.setStateGameMusic(true);
            this.backend.getBackend().setCurrentGameState(CraterBackend.GAME_STATE_LEVELSELECT);
            //hide the pause button
            this.inGameLayout.setVisibility(false);
        }

        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
    }
}
