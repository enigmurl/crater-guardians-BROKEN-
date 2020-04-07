package com.enigmadux.craterguardians.GUIs.settingsScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.FileStreams.SettingsData;
import com.enigmadux.craterguardians.GUILib.GUIClickable;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.ImageText;
import com.enigmadux.craterguardians.GUILib.Text;
import com.enigmadux.craterguardians.GUILib.TextRenderable;
import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.VisibilitySwitch;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

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

    private ArrayList<QuadTexture> renderables;

    private ArrayList<TextRenderable> textRenderables;

    private ArrayList<VisibilitySwitch> allComponents;

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
        renderables = new ArrayList<>();
        textRenderables = new ArrayList<>();
        allComponents = new ArrayList<>();
        this.settingsData = settingsData;
    }

    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    @Override
    public void loadComponents(Context context, HashMap<String,GUILayout> allLayouts){

        ImageText title = new ImageText(context,R.drawable.button_background,-0.5f,0.85f,1,0.25f,true);
        ImageText musicText =new ImageText(context,R.drawable.button_background,-0.4f,0.15f,1,1f,true);
        ImageText soundText =new ImageText(context,R.drawable.button_background,0.4f,0.15f,1,1f,true);

        musicText.setTextDelta(0,0.25f);
        soundText.setTextDelta(0,0.25f);

        musicText.updateText("Music",0.1f);
        soundText.updateText("Sound FX",0.1f);
        title.updateText("Settings",0.1f);

        this.renderables.add(new QuadTexture(context,R.drawable.gui_background,0,0,2,2));
        this.textRenderables.add(title);
        this.textRenderables.add(musicText);
        this.textRenderables.add(soundText);
        this.textRenderables.addAll(this.clickables);

        this.renderables.add(title);
        this.renderables.add(musicText);
        this.renderables.add(soundText);

        //the home button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.home_button,
                1 - 0.15f * LayoutConsts.SCALE_X,0.85f,0.2f,0.2f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID), false));
        //the music button
        this.clickables.add(new MusicSwitch(context,R.drawable.music_on_off_button,
                -0.4f,0f,0.5f,0.5f,
                this.settingsData));
        //the sound effects button
        this.clickables.add(new SoundEffectsSwitch(context,R.drawable.sound_effect_on_off_button,
                0.4f,0f,0.5f,0.5f,
                this.settingsData));

        this.renderables.addAll(clickables);

        this.allComponents.addAll(clickables);
        allComponents.addAll(textRenderables);

    }

    /** Renders sub components
     *  @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer renders the text
     */
    @Override
    public void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer) {
        if (this.isVisible) {
            renderer.renderQuads(this.renderables, uMVPMatrix);
            for (int i = 0, size = this.textRenderables.size(); i<size; i++){
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

        for (int i = this.allComponents.size()-1;i>= 0;i--){
            this.allComponents.get(i).setVisibility(visibility);
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }
}
