package com.enigmadux.craterguardians.GUIs.settingsScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.FileStreams.SettingsData;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.QuadRenderer;

/** A class used to adjust settings screen
 *
 * @author Manu Bhat
 * @version BETA
 */
public class SettingsScreen implements GUILayout {


    /** The id of this layout so other classes can access it
     *
     */
    public static final String ID = STRINGS.SETTINGS_LAYOUT_ID;


    /** Stores all the components
     *
     */
    private ArrayList<GUIClickable> clickables;

    /** Whether or not to draw the screen
     *
     */
    private boolean isVisible;

    /** a SettingsData object, so updated settings can take effect in files
     *
     */
    private SettingsData settingsData;

    /** Default Constructor
     *
     * @param settingsData a SettingsData object, so updated settings can
     */
    public SettingsScreen(SettingsData settingsData){
        this.clickables = new ArrayList<>();
        this.settingsData = settingsData;
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
        //the music button
        this.clickables.add(new MusicSwitch(context,R.drawable.music_on_off_button,
                -0.4f,0.2f,0.5f,0.5f,
                this.settingsData));
        //the sound effects button
        this.clickables.add(new SoundEffectsSwitch(context,R.drawable.sound_effect_on_off_button,
                0.4f,0.2f,0.5f,0.5f,
                this.settingsData));

    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer renders the text
     */
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

        for (int i = this.clickables.size()-1;i>= 0;i--){
            this.clickables.get(i).setVisibility(visibility);
        }
    }
}
