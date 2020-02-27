package com.enigmadux.craterguardians.GUIs.characterSelect;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** This is where the character selects the player, as well as being able to upgrade them
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CharacterSelectLayout implements GUILayout {

    /** This is a list of all the players
     *
     */
    private static final Player[] CHARACTERS = PlayerData.CHARACTERS;


    /** The width of each icon
     *
     */
    private static final float ICON_WIDTH = 0.4f;

    /** THe left and right margins
     *
     */
    private static final float SIDE_MARGINS = 0.2f;

    /** The margin between Icons
     *
     */
    private static final float ICON_MARGINS = 0.1f;


    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.CHARACTER_SELECT_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** The current player being selected, if it's null that means no player is being selected
     *
     */
    private Player currentPlayer;

    /** Held outside as well because we need to do some operations to it
     *
     */
    private CharacterSelecter characterSelecter;

    /** The backend object used to update the current player
     *
     */
    private CraterRenderer renderer;


    /** Default Constructor
     *
     * @param craterRenderer  the frontend object used so we can set the player
     */
    public CharacterSelectLayout(CraterRenderer craterRenderer){
        this.clickables = new ArrayList<>();
        this.renderer = craterRenderer;
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
        for (int i = 0;i<CharacterSelectLayout.CHARACTERS.length;i++){

            Player player = CharacterSelectLayout.CHARACTERS[i];

            //start at the margin, the (2 - SIDE_MARGINS) is the total width
            //note that there is internal scaling so to combat this we use that as
            float x = -1 + SIDE_MARGINS + ((i*scaleX * (ICON_WIDTH + ICON_MARGINS)) % (2 - 2 * SIDE_MARGINS));
            Log.d("DEBUG","x: " + x);
            this.clickables.add(
                    new PlayerSelecter(context,player.getPlayerIcon(),
                            x,0.4f,ICON_WIDTH,ICON_WIDTH,
                            player,this
                            )
            );
        }

        this.characterSelecter = new CharacterSelecter(context,R.drawable.button_background,
                -0.5f,-0.5f,0.8f,0.4f,
                this.renderer, true);
        this.characterSelecter.updateText(STRINGS.CHARACTER_SELECTER_TEXT,0.1f);

        this.clickables.add(this.characterSelecter);

    }

    /** Sets the visibility
     *
     * @param visibility whether to draw it or not
     */
    @Override
    public void setVisibility(boolean visibility) {
        //if it's true, we need to reset the character selecter
        if (visibility) this.characterSelecter.updateCurrentPlayer(null);

        this.isVisible = visibility;

        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
    }
    /** Renders all components if they are visible
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible){
            renderer.renderQuads(this.clickables,uMVPMatrix);
            for (int i = 0,size = this.clickables.size();i<size;i++){
                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
            }
        }
    }

    /** Handles touch events
     *
     * @param e the motion event that describes the type, and the position
     * @return whether or not that touch event has been handled
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        if (! this.isVisible) return false;

        for (int i = this.clickables.size()-1;i>= 0;i--){
            if (this.clickables.get(i).onTouch(e)) return true;
        }
        return false;
    }

    /** Sets the current player
     *
     * @param newPlayer sets the current player, but does not take effect in game
     *
     */
    public void updateCurrentPlayer(Player newPlayer){
        this.currentPlayer = newPlayer;

        this.characterSelecter.updateCurrentPlayer(newPlayer);
        Log.d("CHARACTER SELECT","Current Player: "+ this.currentPlayer);
    }
}
