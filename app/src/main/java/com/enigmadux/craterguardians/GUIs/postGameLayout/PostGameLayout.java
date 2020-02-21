package com.enigmadux.craterguardians.GUIs.postGameLayout;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;
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

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** Backend object
     *
     */
    private CraterBackendThread backend;

    /** Default Constructor
     *
     * @param backend a backend thread object
     */
    public PostGameLayout(CraterBackendThread backend){
        this.clickables = new ArrayList<>();
        this.backend = backend;
    }

    /** Loads components
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String, GUILayout> allLayouts) {
        //the home button);
        this.clickables.add(new PostGameVisibilityButton(context, R.drawable.home_button,
                0,-0.4f,0.4f,0.4f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID)
                ,this.backend,false));

        //go to levels
        PostGameVisibilityButton levelsButton = new PostGameVisibilityButton(context, R.drawable.button_background,
                0,0.1f,0.4f,0.4f,
                this,allLayouts.get(STRINGS.LEVEL_SELECT_LAYOUT_ID),this.backend,true);
        levelsButton.updateText(STRINGS.BACK_TO_LEVELS_BUTTON,0.1f);
        this.clickables.add(levelsButton);

        //play next level
        this.clickables.add(new PlayNextLevel(context,R.drawable.resume_button,
                0,0.75f,0.4f,0.4f,
                this.backend.getBackend(),this));

        this.background = new QuadTexture(context,R.drawable.layout_background,0,0,2,2);


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

        this.isVisible = visibility;


        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    @Override
    public void render(float[] uMVPMatrix, QuadRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuad(this.background,uMVPMatrix);
            renderer.renderQuads(this.clickables, uMVPMatrix);
            for (int i = 0,size = this.clickables.size();i<size;i++){
                this.clickables.get(i).renderText(textRenderer,uMVPMatrix);
            }
        }
    }



}
