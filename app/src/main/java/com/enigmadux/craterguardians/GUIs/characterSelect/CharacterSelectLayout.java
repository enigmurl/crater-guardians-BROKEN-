package com.enigmadux.craterguardians.GUIs.characterSelect;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.ImageText;
import com.enigmadux.craterguardians.GUILib.MatieralBar;
import com.enigmadux.craterguardians.GUILib.Text;
import com.enigmadux.craterguardians.GUILib.TextRenderable;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.VisibilitySwitch;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.GUIs.homeScreen.HomeScreen;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

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
    private static final float ICON_WIDTH = 0.5f;

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

    private ArrayList<QuadTexture> renderables;

    private ArrayList<TextRenderable> textRenderables;

    private ArrayList<VisibilitySwitch> allComponents;

    private Text instructions;

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

    private CharacterUpgrader characterUpgrader;

    private InfoButton infoButton;

    private MatieralBar matieralBar;

    /** The backend object used to update the current player
     *
     */
    private CraterRenderer craterRenderer;

    private PlayerData playerData;

    /** Default Constructor
     *
     * @param craterRenderer  the frontend object used so we can set the player
     */
    public CharacterSelectLayout(CraterRenderer craterRenderer,PlayerData playerData){
        this.clickables = new ArrayList<>();
        this.renderables = new ArrayList<>();
        this.textRenderables = new ArrayList<>();
        allComponents = new ArrayList<>();
        this.craterRenderer = craterRenderer;
        this.playerData = playerData;
    }

    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){
        this.renderables.add(new QuadTexture(context,R.drawable.gui_background,0,0,2,2));
        this.renderables.add(new ImageText(context,R.drawable.layout_background,0,0.2f,(2 - 1.6f * SIDE_MARGINS )/LayoutConsts.SCALE_X + ICON_WIDTH,0.8f,true));
        QuadTexture bottomBar =  new QuadTexture(context,R.drawable.character_select_bottom,0,-0.7f,2,0.6f);
        bottomBar.setAlpha(0.5f);
        this.renderables.add(bottomBar);
        this.allComponents.addAll(this.renderables);
        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;
        for (int i = 0;i<CharacterSelectLayout.CHARACTERS.length;i++){

            Player player = CharacterSelectLayout.CHARACTERS[i];

            //start at the margin, the (2 - SIDE_MARGINS) is the total width
            //note that there is internal scaling so to combat this we use that as
            float x = -1 + SIDE_MARGINS + ((i*scaleX * (ICON_WIDTH + ICON_MARGINS)) % (2 - 2 * SIDE_MARGINS));
            Log.d("DEBUG","x: " + x);
            this.clickables.add(
                    new PlayerSelecterIcon(context,player.getPlayerIcon(),
                            x,0.2f,ICON_WIDTH,ICON_WIDTH,
                            player,this
                            )
            );
        }

        //the home button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.home_button,
                1 - 0.15f * LayoutConsts.SCALE_X,0.85f,0.2f,0.2f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID), false));



        this.characterSelecter = new CharacterSelecter(context,R.drawable.button_background,this,allLayouts.get(HomeScreen.ID),
                0f,-0.65f,0.8f,0.4f,
                this.craterRenderer, true);

        this.characterUpgrader = new CharacterUpgrader(context,R.drawable.button_background,playerData,this,
                -0.65f,-0.65f,0.8f,0.4f,true);

        this.infoButton = new InfoButton(context,R.drawable.info_button,0.65f,-0.65f,0.4f,0.4f,null,null,false);

        this.clickables.add(infoButton);
        this.clickables.add(this.characterSelecter);
        this.clickables.add(this.characterUpgrader);

        for (int i = 0;i<CharacterSelectLayout.CHARACTERS.length;i++){
            Player player = CharacterSelectLayout.CHARACTERS[i];
            this.clickables.add(new InfoDisplay(context,player,0,0,1,1,false));
        }


        this.renderables.addAll(this.clickables);
        matieralBar = ((HomeScreen) allLayouts.get(HomeScreen.ID)).getMatieralBar();
        this.renderables.addAll(matieralBar.getRenderables());

        this.textRenderables.addAll(this.clickables);
        ImageText title = new ImageText(context,R.drawable.layout_background,0f,0.8f,1.5f,0.25f,true);
        title.updateText("Characters",0.1f);
        this.textRenderables.add(title);
        this.instructions = new Text(0,-0.8f,"Tap on a player icon",0.1f);
        this.textRenderables.add(this.instructions);
        this.renderables.add(title);
        this.allComponents.addAll(this.textRenderables);
        this.allComponents.addAll(matieralBar.getRenderables());
    }

    /** Sets the visibility
     *
     * @param visibility whether to draw it or not
     */
    @Override
    public void setVisibility(boolean visibility) {
        //if it's true, we need to reset the character selecter


        for (int i = this.allComponents.size()-1;i>= 0;i--){
            if (! (allComponents.get(i) instanceof InfoDisplay) || ! visibility) {
                this.allComponents.get(i).setVisibility(visibility);
            }
        }

        if (visibility) {
            this.characterSelecter.updateCurrentPlayer(null);
            this.characterUpgrader.updateCurrentPlayer(null);
            this.infoButton.setVisibility(false);
        } else {
            for (int i = 0; i < matieralBar.getRenderables().size();i++){
                matieralBar.getRenderables().get(i).setVisibility(true);
            }
        }
        this.isVisible = visibility;

    }
    /** Renders all components if they are visible
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible){
            renderer.renderQuads(this.renderables,uMVPMatrix);
            for (int i = 0,size = this.textRenderables.size();i<size;i++){
                this.textRenderables.get(i).renderText(textRenderer,uMVPMatrix);
            }
            this.matieralBar.renderText(textRenderer,uMVPMatrix);

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
    void updateCurrentPlayer(Player newPlayer){
        this.currentPlayer = newPlayer;
        this.instructions.setVisibility(false);
        this.characterSelecter.updateCurrentPlayer(newPlayer);
        this.characterUpgrader.updateCurrentPlayer(newPlayer);
        this.infoButton.setVisibility(true);
        for (int i = this.clickables.size()-1;i>= 0;i--) {
            if (this.clickables.get(i) instanceof InfoDisplay && ((InfoDisplay)this.clickables.get(i)).getPlayer().getClass() == newPlayer.getClass()) {
                this.infoButton.setObjectToShow(this.clickables.get(i));
                break;
            }
        }
        Log.d("CHARACTER SELECT","Current Player: "+ this.currentPlayer);
    }

    void updatePlayerIcons(){
        //first buttons are all player select icons, but check to make sure just in case
        for (int i = 0;i<CharacterSelectLayout.CHARACTERS.length;i++) {
            if (this.clickables.get(i) instanceof PlayerSelecterIcon) {
                ((PlayerSelecterIcon) this.clickables.get(i)).update();
            }
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }
}
