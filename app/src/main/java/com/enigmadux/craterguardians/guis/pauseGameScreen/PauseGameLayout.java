package com.enigmadux.craterguardians.guis.pauseGameScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.ImageText;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.guilib.TextRenderable;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.guis.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

/** Layout shown when the game is paused
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PauseGameLayout implements GUILayout {

    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.PAUSE_GAME_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;
    private ArrayList<QuadTexture> quadTextures;
    private ArrayList<TextRenderable> textRenderables;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** Backend object used to go into the actual game
     *
     */
    private CraterRenderer craterRenderer;
    private Text countDown;

    private InGameScreen inGameScreen;

    /** Default Constructor
     *
     */
    public PauseGameLayout(CraterRenderer craterRenderer){
        this.clickables = new ArrayList<>();
        this.textRenderables = new ArrayList<>();
        this.quadTextures = new ArrayList<>();
        this.craterRenderer = craterRenderer;
    }

    /** Loads components
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String, GUILayout> allLayouts) {

        ImageText bg = new ImageText(context,R.drawable.layout_background,0,0,1.75f,1.75f,true);
        bg.updateText("Game Paused",0.1f);
        bg.setTextDelta(0,0.6f);
        this.quadTextures.add(bg);
        this.textRenderables.add(bg);

        //purposefully not putting countdown inside components that are switched on by visibility
        this.countDown = new Text(0,0," ",0.1f);

        //the home button);
        this.clickables.add(new PauseScreenVisibilityButton(context, R.drawable.home_button,
                0.15f,0.1f,0.4f,0.4f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID),
                this.craterRenderer,false));

        //go to levels
        PauseScreenVisibilityButton levelButton = new PauseScreenVisibilityButton(context, R.drawable.button_background,
                0,-0.4f,1.25f,0.4f,
                this,allLayouts.get(STRINGS.LEVEL_SELECT_LAYOUT_ID),
                this.craterRenderer, true);
        levelButton.updateText(STRINGS.BACK_TO_LEVELS_BUTTON,0.1f);
        this.clickables.add(levelButton);

        //just hide this (resume button)
        this.clickables.add(new PauseScreenVisibilityButton(context,R.drawable.resume_button,
                -0.15f,0.1f,0.4f,0.4f,
                this,countDown,
                this.craterRenderer,false));

        this.inGameScreen = (InGameScreen) allLayouts.get(InGameScreen.ID);


        this.quadTextures.addAll(clickables);
        this.textRenderables.addAll(clickables);
        this.textRenderables.add(countDown);


    }

    /** Handles touch events
     *
     * @param e the motion event that describes the type, and the position
     * @return if the event has been handled (so if it's visible)
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        if (! this.isVisible) return false;

        for (int i = this.clickables.size()-1;i>= 0;i--){
            if (this.clickables.get(i).onTouch(e)) return true;
        }
        return true;
    }

    /** SEts the visibility
     *
     * @param visibility whether or not to draw it, if this is hidden, the backendThread thread will be un paused
     */
    @Override
    public void setVisibility(boolean visibility) {
        //if this is being seen, the game should be paused

        for (int i = this.quadTextures.size()-1;i>= 0;i--){
            this.quadTextures.get(i).setVisibility(visibility);
        }
        if (visibility) {
            this.craterRenderer.getCraterBackendThread().setGamePaused(true);
            this.inGameScreen.resetJoySticks();
        }

        this.isVisible = visibility;
    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.quadTextures, uMVPMatrix);
            for (int i = 0,size = this.textRenderables.size();i<size;i++){
                this.textRenderables.get(i).renderText(textRenderer,uMVPMatrix);
            }
        }
        if (this.countDown.isVisible()){
            this.countDown.renderText(textRenderer,uMVPMatrix);
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible || countDown.isVisible();
    }
}
