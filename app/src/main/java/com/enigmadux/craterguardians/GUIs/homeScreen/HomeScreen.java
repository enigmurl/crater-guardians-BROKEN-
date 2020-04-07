package com.enigmadux.craterguardians.GUIs.homeScreen;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.MatieralBar;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.GUIs.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.players.Kaiser;
import com.enigmadux.craterguardians.players.TutorialPlayer;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

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

    private ArrayList<QuadTexture> renderables;


    /** Theres only one static display, so no need for an array list.
     *  This tells the user the
     */
    private CharacterDisplay characterDisplay;


    /** Backend object used to query the state
     *
     */
    private CraterRenderer craterRenderer;

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

    private MatieralBar matieralBar;

    /** Default Constructor
     *
     * @param craterRenderer the backend object used so we can query the state of the current palyer
     */
    public HomeScreen(CraterRenderer craterRenderer){
        this.clickables = new ArrayList<>();
        this.renderables = new ArrayList<>();
        this.craterRenderer = craterRenderer;
    }

    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){
        this.renderables.add(new QuadTexture(context,R.drawable.gui_background,0,0,2,2));
        //the firstButton (settings button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.settings_button,
                1 - 0.15f * LayoutConsts.SCALE_X,0.85f,0.2f,0.2f,
                this,allLayouts.get(STRINGS.SETTINGS_LAYOUT_ID), false));
        //second button is the character select layout
        VisibilityInducedButton characterSelectButton = new VisibilityInducedButton(context,R.drawable.button_background,
                0,-0.3f,1.5f,0.4f,
                this,allLayouts.get(STRINGS.CHARACTER_SELECT_LAYOUT_ID), true);
        characterSelectButton.updateText(STRINGS.CHARACTER_SELECT_BUTTON_TEXT,0.1f);
        this.clickables.add(characterSelectButton);


        //third one is a display, but not a clickable
        this.characterDisplay = new CharacterDisplay(context,this.craterRenderer.getWorld().getPlayer().getPlayerIcon(),
                0,0.45f,1f,1f);



        //fourth one is the level select button
        VisibilityInducedButton levelSelectButton = new VisibilityInducedButton(context,R.drawable.button_background,
                0,-0.75f,1.5f,0.4f,
                this,allLayouts.get(STRINGS.LEVEL_SELECT_LAYOUT_ID), true);
        levelSelectButton.updateText(STRINGS.LEVEL_SELECT_BUTTON_TEXT,0.1f);
        this.clickables.add(levelSelectButton);

        matieralBar = new MatieralBar(context);
        this.renderables.addAll(this.clickables);
        this.renderables.addAll(matieralBar.getRenderables());
        this.renderables.add(this.characterDisplay);

        this.context = context;

        this.inGameLayout = allLayouts.get(InGameScreen.ID);

    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.renderables, uMVPMatrix);
            for (int i = 0,size = this.clickables.size();i<size;i++){
                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
            }
            matieralBar.renderText(textRenderer,uMVPMatrix);

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

            this.characterDisplay.setGLTexture(QuadTexture.loadAndroidTexturePointer(this.context,this.craterRenderer.getWorld().getPlayer().getPlayerIcon()));
            this.craterRenderer.getWorld().setState(World.STATE_GUI);

            //hide the pause button
            this.inGameLayout.setVisibility(false);
            Log.d("HOME SCREEN","Set visibility");

            //if it's tutorial, want to change it to kaiser instead
            if (this.craterRenderer.getWorld().getPlayer() instanceof TutorialPlayer){
                this.craterRenderer.getWorld().setPlayer(new Kaiser());
            }
        }

        for (int i = this.renderables.size()-1;i>= 0;i--){
            this.renderables.get(i).setVisibility(visibility);
        }
    }

    public MatieralBar getMatieralBar(){
        return this.matieralBar;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }
}

