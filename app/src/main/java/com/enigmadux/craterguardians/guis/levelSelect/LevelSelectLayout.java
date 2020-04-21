package com.enigmadux.craterguardians.guis.levelSelect;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.enigmadux.craterguardians.animations.FlingingAnim;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.ImageText;
import com.enigmadux.craterguardians.guilib.MatieralBar;
import com.enigmadux.craterguardians.guilib.TextRenderable;
import com.enigmadux.craterguardians.guilib.VisibilityInducedButton;
import com.enigmadux.craterguardians.guilib.VisibilitySwitch;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.guis.homeScreen.HomeScreen;
import com.enigmadux.craterguardians.guis.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.GameMap;
import com.enigmadux.craterguardians.players.Kaiser;
import com.enigmadux.craterguardians.players.TutorialPlayer;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

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
    private static final float TOP_MARGIN = 0.6f;

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

    private ArrayList<QuadTexture> renderables;

    private float prevX;
    private int currentID = -1;
    private ArrayList<LevelSelector> levelSelectors;

    private ArrayList<TextRenderable> textRenderables;

    private ArrayList<VisibilitySwitch> allComponents;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** Backend object used to go into the actual game
     *
     */
    private CraterRenderer craterRenderer;


    /** The layout in game that we need to hide some times
     *
     */
    private GUILayout inGameLayout;

    private MatieralBar matieralBar;

    private float startX;
    private float cameraX = 1 - SIDE_MARGINS;

    private FlingingAnim flingingAnim;
    private VelocityTracker velocityTracker;

    private long prevMillis = System.currentTimeMillis();

    /** Default Constructor
     *
     */
    public LevelSelectLayout(CraterRenderer craterRenderer){
        this.clickables = new ArrayList<>();
        this.renderables = new ArrayList<>();
        this.levelSelectors = new ArrayList<>();
        textRenderables = new ArrayList<>();
        allComponents = new ArrayList<>();
        this.craterRenderer = craterRenderer;
        this.velocityTracker = VelocityTracker.obtain();
    }

    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){
        //background
        this.renderables.add(new QuadTexture(context,R.drawable.gui_background,0,0,2,2));
        //the home button);
        VisibilityInducedButton homeButton = new VisibilityInducedButton(context, R.drawable.home_button,
                1 - 0.15f * LayoutConsts.SCALE_X,0.85f,0.2f,0.2f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID), false);
        this.clickables.add(homeButton);
        this.allComponents.add(homeButton);

        float h = (ICON_WIDTH) * (int) (((GameMap.NUM_LEVELS*LayoutConsts.SCALE_X * (ICON_WIDTH + ICON_MARGINS))/(2 - SIDE_MARGINS)));
        ImageText background = new ImageText(context,R.drawable.layout_background,0,1-TOP_MARGIN - h/2,(2 - SIDE_MARGINS/2)/LayoutConsts.SCALE_X,h +  2 * ICON_MARGINS + ICON_WIDTH,true);
        this.renderables.add(background);

        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;

        for (int i = 0;i < GameMap.NUM_LEVELS;i++){
            float x = -1 +SIDE_MARGINS +  (i*scaleX * (ICON_WIDTH + ICON_MARGINS)) /*% (2 - SIDE_MARGINS)*/;
            float y =  1- TOP_MARGIN -  ICON_WIDTH /* * (int) (((i*scaleX * (ICON_WIDTH + ICON_MARGINS))/(2 - SIDE_MARGINS)))*/;
            LevelSelector levelSelector = new LevelSelector(context,R.drawable.level_button_background,
                    x,y,ICON_WIDTH,ICON_WIDTH,
                    this,allLayouts.get(InGameScreen.ID),
                    this.craterRenderer,i+1);
            levelSelector.setCameraX(cameraX);
            this.levelSelectors.add(levelSelector);
            this.clickables.add(levelSelector);
            this.textRenderables.add(levelSelector);
        }
        this.renderables.addAll(this.clickables);


        ImageText title = new ImageText(context,R.drawable.layout_background,0,0.8f,1.25f,0.2f,true);
        title.updateText("Levels",0.1f);
        this.textRenderables.add(title);
        this.renderables.add(title);
        this.allComponents.addAll(this.textRenderables);
        this.allComponents.addAll(this.renderables);
        this.allComponents.add(background);
        this.matieralBar = ((HomeScreen) allLayouts.get(HomeScreen.ID)).getMatieralBar();
        this.renderables.addAll(matieralBar.getRenderables());
        this.allComponents.addAll(matieralBar.getRenderables());
        this.textRenderables.addAll(matieralBar.getRenderables());

        this.inGameLayout = allLayouts.get(InGameScreen.ID);

    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
 e     */
    @Override
    public void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            for (int i = 0,size = this.levelSelectors.size();i<size;i++){
                this.levelSelectors.get(i).setCameraX(this.cameraX);
            }
            renderer.renderQuads(this.renderables, uMVPMatrix);
            for (int i = 0,size = this.textRenderables.size();i<size;i++){
                this.textRenderables.get(i).renderText(textRenderer,uMVPMatrix);
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

        if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (this.flingingAnim != null) flingingAnim.cancel();
        }

        for (int i = this.clickables.size()-1;i>= 0;i--){
            if (this.clickables.get(i).onTouch(e)){
                return true;
            }
        }

        if (e.getActionMasked() == MotionEvent.ACTION_DOWN){
            prevX = MathOps.getOpenGLX(e.getRawX());
            startX = prevX;
            currentID = e.getPointerId(e.getActionIndex());
            Log.d("Level Select","Started scroll: " + currentID);
            this.prevMillis = System.currentTimeMillis();
        }
        else if (e.getActionMasked() == MotionEvent.ACTION_MOVE && e.getPointerId(e.getActionIndex()) == currentID){
            float x = MathOps.getOpenGLX(e.getRawX());
            this.cameraX += x - prevX;
            if (-this.cameraX < -1 +SIDE_MARGINS){
                this.cameraX = 1 - SIDE_MARGINS;
            } else if (-this.cameraX >-1 +SIDE_MARGINS +  (GameMap.NUM_LEVELS*LayoutConsts.SCALE_X * (ICON_WIDTH + ICON_MARGINS))){
                this.cameraX = -(-1 +SIDE_MARGINS +  (GameMap.NUM_LEVELS*LayoutConsts.SCALE_X * (ICON_WIDTH + ICON_MARGINS)));
            }
            prevX = x;
            velocityTracker.addMovement(e);
        } else if (e.getActionMasked() == MotionEvent.ACTION_UP && e.getPointerId(e.getActionIndex()) == currentID){
            this.currentID = -1;
            //seconds
            velocityTracker.computeCurrentVelocity(1000);
            float velocity = 2 * velocityTracker.getXVelocity()/LayoutConsts.SCREEN_WIDTH;
            velocityTracker.clear();
            this.flingingAnim = new FlingingAnim(this,velocity);
        }

        return false;
    }

    /** Sets the visibility
     *
     * @param visibility whether to draw it or not
     */
    @Override
    public void setVisibility(boolean visibility) {

        if (visibility){
            SoundLib.setStateGameMusic(false);
            SoundLib.setStateVictoryMusic(false);
            SoundLib.setStateLossMusic(false);
            SoundLib.setStateLobbyMusic(true);
            //hide the pause button
            this.inGameLayout.setVisibility(false);

            //if it's tutorial, want to change it to kaiser instead
            if (this.craterRenderer.getWorld().getPlayer() instanceof TutorialPlayer){
                this.craterRenderer.getWorld().setPlayer(new Kaiser());
            }
        }

        for (int i = 0;i< this.allComponents.size();i++){
            this.allComponents.get(i).setVisibility(visibility);
        }
        for (int i = 0,size = this.levelSelectors.size();i<size;i++){
            this.levelSelectors.get(i).setCameraX(cameraX);
        }
        if (! visibility){
            for (int i = 0; i < matieralBar.getRenderables().size();i++){
                matieralBar.getRenderables().get(i).setVisibility(true);
            }
        }
        this.isVisible = visibility;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    public float getCameraX(){
        return cameraX;
    }

    public void setCameraX(float cameraX){
        this.cameraX = cameraX;
        if (-this.cameraX < -1 +SIDE_MARGINS){
            this.cameraX = 1 - SIDE_MARGINS;
        } else if (-this.cameraX >-1 +SIDE_MARGINS +  (GameMap.NUM_LEVELS*LayoutConsts.SCALE_X * (ICON_WIDTH + ICON_MARGINS))){
            this.cameraX = -(-1 +SIDE_MARGINS +  (GameMap.NUM_LEVELS*LayoutConsts.SCALE_X * (ICON_WIDTH + ICON_MARGINS)));
        }

        ///level selectors updated inside draw

    }


}
