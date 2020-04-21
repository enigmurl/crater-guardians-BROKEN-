package com.enigmadux.craterguardians.guis.inGameScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.animations.ZoomFadeIn;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.filestreams.TutorialData;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.VisibilityInducedButton;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.guis.inGameScreen.defaultJoystickLayout.DefaultJoyStickLayout;
import com.enigmadux.craterguardians.guis.inGameScreen.joystickLayouts.JoystickLayout;
import com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers.TutorialWrapper;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

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

    private ArrayList<QuadTexture> renderables = new ArrayList<>();

    private TutorialWrapper tutorialWrapper;


    private WinLossIndicator winLossIndicator;
    private QuadTexture battleStartIndicator;


    private CraterRenderer craterRenderer;


    private VisibilityInducedButton pause;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;


    private JoystickLayout joystickLayout;

    /** Default Constructor
     *
     */
    public InGameScreen(CraterRenderer craterRenderer){
        this.craterRenderer = craterRenderer;

        this.joystickLayout = new DefaultJoyStickLayout(craterRenderer);

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

        float x =-1 + 0.15f * LayoutConsts.SCALE_X;
        pause = new VisibilityInducedButton(context, R.drawable.pause_button,
                x,0.85f,0.2f,0.2f,
                null,allLayouts.get(STRINGS.PAUSE_GAME_LAYOUT_ID), false);
        this.clickables.add(pause);
        this.renderables.add(pause);

        this.joystickLayout.init(context);
        this.renderables.addAll(this.joystickLayout.getRenderables());


        //win or loss
        this.winLossIndicator = new WinLossIndicator(this.craterRenderer,context,0,0.5f,LayoutConsts.SCALE_X * 1.5f,1);
        this.renderables.add(winLossIndicator);
        //battle start
        this.battleStartIndicator = new QuadTexture(context,R.drawable.battle_start,0,0.5f, LayoutConsts.SCALE_X * 1,1);
        this.renderables.add(this.battleStartIndicator);

        this.tutorialWrapper = new TutorialWrapper(context,this.craterRenderer.getCraterBackendThread(),this);
    }

    /** Handles touch events
     *
     * @param e the motion event that describes the type, and the position
     * @return if the event has been handled
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        if (! this.isVisible) return false;

        if (tutorialWrapper.onTouch(e)) return true;

        for (int i = this.clickables.size()-1;i>= 0;i--){
            if (this.clickables.get(i).onTouch(e)) return true;
        }

        if (craterRenderer.getWorld().getCurrentGameState() == World.STATE_INGAME) {
            this.joystickLayout.onTouch(e);
        }
        return false;
    }

    /** SEts the visibility
     *
     * @param visibility whether or not to draw it, if this is hidden, the backend thread will be un paused
     */
    @Override
    public void setVisibility(boolean visibility) {
        boolean tutorialVisibility = visibility && TutorialData.TUTORIAL_ENABLED;
        this.tutorialWrapper.setVisiblility(tutorialVisibility);

        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
        if (tutorialVisibility){
            this.pause.setVisibility(false);
        }
        this.isVisible = visibility;
    }


    //its also rendered with a quad renderer for optimum performance
    //additionally, the tilable can only be rendered with quad renderer, so I have just deleted the entire other render method
    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.renderables,uMVPMatrix);
            for (int i = 0,size = this.clickables.size();i<size;i++){
                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
            }
            this.tutorialWrapper.render(uMVPMatrix,renderer,textRenderer);
        }
    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer) {
//        if (this.isVisible) {
//            for (int i = 0,size = this.clickables.size();i<size;i++){
//                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
//            }
//            renderer.renderQuads(this.renderables,uMVPMatrix);
//
//        }
        //U CAN ONLY RENDER THIS WITH A QUAD RENDERER
    }

    public void setWinLossVisibility(boolean visibility){
        if (visibility){
            new ZoomFadeIn(this.winLossIndicator,ZoomFadeIn.DEFAULT_MILLIS);
        } else {
            this.winLossIndicator.setVisibility(false);
        }
    }

    public void setBattleStartIndicatorVisibility(boolean visibility){
        if (visibility){
            new ZoomFadeIn(this.battleStartIndicator,ZoomFadeIn.DEFAULT_MILLIS);
        } else {
            this.battleStartIndicator.setVisibility(false);
        }
    }
    public void resetJoySticks(){
        this.joystickLayout.resetJoySticks();
    }

    public void update(World world,long dt){
        this.joystickLayout.update(world, dt);
        this.tutorialWrapper.update(world,dt);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }
}
