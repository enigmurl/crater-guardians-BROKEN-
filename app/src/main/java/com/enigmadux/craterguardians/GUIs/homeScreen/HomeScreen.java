package com.enigmadux.craterguardians.GUIs.homeScreen;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.GUIs.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** The default Layout that the user opens up too
 *
 * @author Manu Bhat
 * @version BETA
 */
public class HomeScreen implements GUILayout {

    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.HOME_SCREEN_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;


    /** Theres only one static display, so no need for an array list.
     *  This tells the user the
     */
    private CharacterDisplay characterDisplay;


    /** Backend object used to query the state
     *
     */
    private CraterBackend backend;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** Context will technically never be used, but is needed for a parameter (updating the character display)
     *
     */
    private Context context;

    /** The layout in game that we need to hide some times
     *
     */
    private GUILayout inGameLayout;

    /** Default Constructor
     *
     * @param backend the backend object used so we can query the state of the current palyer
     */
    public HomeScreen(CraterBackend backend){
        this.clickables = new ArrayList<>();
        this.backend = backend;
    }

    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){
        //the firstButton (settings button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.settings_button,
                0.8f,0.8f,0.2f,0.2f,
                this,allLayouts.get(STRINGS.SETTINGS_LAYOUT_ID), false));
        //second button is the character select layout
        VisibilityInducedButton characterSelectButton = new VisibilityInducedButton(context,R.drawable.button_background,
                0,0.3f,1.5f,0.4f,
                this,allLayouts.get(STRINGS.CHARACTER_SELECT_LAYOUT_ID), true);
        characterSelectButton.updateText(STRINGS.CHARACTER_SELECT_BUTTON_TEXT,0.1f);
        this.clickables.add(characterSelectButton);


        //third one is a display, but not a clickable
        this.characterDisplay = new CharacterDisplay(context,this.backend.getPlayer().getPlayerIcon(),
                0,0.75f,0.4f,0.4f);



        //fourth one is the level select button
        VisibilityInducedButton levelSelectButton = new VisibilityInducedButton(context,R.drawable.button_background,
                0,-0.2f,1.5f,0.4f,
                this,allLayouts.get(STRINGS.LEVEL_SELECT_LAYOUT_ID), true);
        levelSelectButton.updateText(STRINGS.LEVEL_SELECT_BUTTON_TEXT,0.1f);
        this.clickables.add(levelSelectButton);

        this.context = context;

        this.inGameLayout = allLayouts.get(InGameScreen.ID);

    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.clickables, uMVPMatrix);
            renderer.renderQuad(this.characterDisplay,uMVPMatrix);
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
            SoundLib.setStateLobbyMusic(true);

            this.characterDisplay.loadAndroidTexturePointer(this.context,this.backend.getPlayer().getPlayerIcon());
            this.backend.setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);

            //hide the pause button
            this.inGameLayout.setVisibility(false);
            Log.d("HOME SCREEN","Set visibility");
        }

        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
    }

}
