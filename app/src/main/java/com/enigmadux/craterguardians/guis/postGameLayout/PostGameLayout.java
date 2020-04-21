package com.enigmadux.craterguardians.guis.postGameLayout;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.animations.XpGainedAnimation;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.ImageText;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.guis.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.players.Kaiser;
import com.enigmadux.craterguardians.players.TutorialPlayer;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.values.STRINGS;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

/** Layout shown after a game is finished win or loss
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PostGameLayout implements GUILayout {


    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.POST_GAME_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;



    /** A static background image used to hide behind the scenes action
     *
     */
    private QuadTexture background;

    private ArrayList<QuadTexture> craterArt;

    private ImageText experienceBg;

    private ImageText actionBg;

    private ImageText victoryIndicator;


    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** Backend object
     *
     */
    private CraterRenderer craterRenderer;

    private Text xpAmount;



    /** Default Constructor
     *
     */
    public PostGameLayout(CraterRenderer craterRenderer){
        this.clickables = new ArrayList<>();
        this.craterRenderer = craterRenderer;
    }

    /** Loads components
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String, GUILayout> allLayouts) {
        float[] black = new float[] {0,0,0,1};

        this.background = new QuadTexture(context,R.drawable.gui_background,0,0,2,2);
        this.experienceBg = new ImageText(context,R.drawable.layout_background,0.5f,-0.6f,1.5f,0.7f,true);
        this.experienceBg.updateText("Experience",0.1f);
        this.experienceBg.setTextDelta(0,0.2f);
        this.actionBg = new ImageText(context,R.drawable.layout_background,0.5f,0.35f,1.5f,1.2f,true);
        this.victoryIndicator = new ImageText(context,R.drawable.layout_background,-0.5f,0.75f,1,0.2f,true);
        this.victoryIndicator.updateText(" ",0.1f);
        this.victoryIndicator.setTextColor(black);

        //the home button);
        this.clickables.add(new PostGameVisibilityButton(context, R.drawable.home_button,
                0.35f,0.55f,0.4f,0.4f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID)
                ,this.craterRenderer,false));

        //go to levels
        PostGameVisibilityButton levelsButton = new PostGameVisibilityButton(context, R.drawable.button_background,
                0.5f,0.1f,1.25f,0.4f,
                this,allLayouts.get(STRINGS.LEVEL_SELECT_LAYOUT_ID),this.craterRenderer,true);
        levelsButton.updateText(STRINGS.BACK_TO_LEVELS_BUTTON,0.1f);
        this.clickables.add(levelsButton);

        //play next level
        this.clickables.add(new PlayNextLevel(context,R.drawable.resume_button,
                0.65f,0.55f,0.4f,0.4f,
                this.craterRenderer,this,allLayouts.get(InGameScreen.ID)));

        this.xpAmount = new Text(0.5f,-0.71f,"+ XP",0.12f);
        this.xpAmount.setColor(black);


        craterArt = new ArrayList<>();
        craterArt.add(new QuadTexture(context,R.drawable.level_background_crater,-0.55f,-0.45f,1 * LayoutConsts.SCALE_X,1f));
        craterArt.add(new QuadTexture(context,R.drawable.level_background_crater,-0.6f,0.30f,0.45f * LayoutConsts.SCALE_X,0.45f));
        craterArt.add(new QuadTexture(context,R.drawable.level_background_crater,-0.2f,0.05f,0.6f * LayoutConsts.SCALE_X,0.6f));

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
        return false;
    }

    /** SEts the visibility
     *
     * @param visibility whether or not to draw it, if this is hidden, the backendThread thread will be un paused
     */
    @Override
    public void setVisibility(boolean visibility) {

        if (visibility){
            //pause backend
            this.craterRenderer.getCraterBackendThread().setPause(true);
            //if it's tutorial, want to change it to kaiser instead
            if (this.craterRenderer.getWorld().getPlayer() instanceof TutorialPlayer){
                this.craterRenderer.getWorld().setPlayer(new Kaiser());
            }
            if (craterRenderer.getWorld().hasWonLastLevel()){
                //-1 because the player just won, so level num was increased, not the best solution
                int xp = World.getXpGainPerLevel(craterRenderer.getWorld().getLevelNum() -1);
                new XpGainedAnimation(XpGainedAnimation.DEFAULT_MILLIS,this.xpAmount,xp);
                this.victoryIndicator.updateText("Victory",victoryIndicator.getFontSize());
            } else {
                this.xpAmount.setVisibility(true);
                this.xpAmount.updateText("No XP Gained", xpAmount.getFontSize());
                this.victoryIndicator.updateText("Loss",victoryIndicator.getFontSize());
            }
        } else {
            this.xpAmount.setVisibility(false);

        }

        this.background.setVisibility(visibility);
        this.experienceBg.setVisibility(visibility);
        this.actionBg.setVisibility(visibility);
        this.victoryIndicator.setVisibility(visibility);
        for (int i = this.craterArt.size() -1;i>=0;i--){
            this.craterArt.get(i).setVisibility(visibility);
        }
        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
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
            //todo not really going for efficiency here
            renderer.renderQuad(this.background,uMVPMatrix);
            renderer.renderQuad(this.experienceBg,uMVPMatrix);
            renderer.renderQuad(this.actionBg,uMVPMatrix);
            renderer.renderQuad(this.victoryIndicator,uMVPMatrix);
            renderer.renderQuads(this.clickables, uMVPMatrix);
            renderer.renderQuads(this.craterArt,uMVPMatrix);
            for (int i = 0,size = this.clickables.size();i<size;i++){
                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
            }
            this.xpAmount.renderText(textRenderer,uMVPMatrix);
            this.experienceBg.renderText(textRenderer,uMVPMatrix);
            this.victoryIndicator.renderText(textRenderer,uMVPMatrix);
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

}
