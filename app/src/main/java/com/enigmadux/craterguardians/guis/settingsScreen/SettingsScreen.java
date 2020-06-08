package com.enigmadux.craterguardians.guis.settingsScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.filestreams.SettingsData;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.ImageText;
import com.enigmadux.craterguardians.guilib.MatieralBar;
import com.enigmadux.craterguardians.guilib.TextRenderable;
import com.enigmadux.craterguardians.guilib.VisibilityInducedButton;
import com.enigmadux.craterguardians.guilib.VisibilitySwitch;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.guis.homeScreen.HomeScreen;
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

    private MatieralBar matieralBar;


    private Credits credits;
    private CreditsUpdater creditsUpdater;




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

        credits = new Credits(context);
        credits.setVisibility(false);

        ImageText title = new ImageText(context,R.drawable.layout_background,0f,0.85f,1,0.25f,true);
        ImageText musicText =new ImageText(context,R.drawable.button_background,-0.5f,-0.05f,1.5f,1.5f,true);
        ImageText soundText =new ImageText(context,R.drawable.button_background,0.5f,-0.05f,1.5f,1.5f,true);
        musicText.setShader(GUIClickable.SHADER[0],GUIClickable.SHADER[1],GUIClickable.SHADER[2],GUIClickable.SHADER[3]);
        soundText.setShader(GUIClickable.SHADER[0],GUIClickable.SHADER[1],GUIClickable.SHADER[2],GUIClickable.SHADER[3]);


        musicText.setTextDelta(0,0.35f);
        soundText.setTextDelta(0,0.35f);

        musicText.updateText("Music",0.15f);
        soundText.updateText("Sound FX",0.15f);
        title.updateText("Settings",0.1f);

        this.renderables.add(new QuadTexture(context,R.drawable.gui_background,0,0,2,2));

        CreditsButton creditsButton = new CreditsButton(context,this);
        this.clickables.add(creditsButton);


        this.textRenderables.add(title);
        this.textRenderables.add(musicText);
        this.textRenderables.add(soundText);
        this.textRenderables.addAll(this.clickables);

        this.renderables.add(title);
        this.renderables.add(musicText);
        this.renderables.add(soundText);

        this.matieralBar = ((HomeScreen) allLayouts.get(HomeScreen.ID)).getMatieralBar();
        this.renderables.addAll(matieralBar.getRenderables());
        //causes flickering
        this.allComponents.addAll(matieralBar.getRenderables());
        this.textRenderables.addAll(matieralBar.getRenderables());

        //the home button);
        this.clickables.add(new VisibilityInducedButton(context, R.drawable.home_button,
                1 - 0.15f * LayoutConsts.SCALE_X,0.85f,0.2f,0.2f,
                this,allLayouts.get(STRINGS.HOME_SCREEN_LAYOUT_ID), false));
        //the music button
        this.clickables.add(new MusicSwitch(context,R.drawable.music_on_off_button,
                -0.5f,-0.25f,0.75f,0.75f,
                this.settingsData));
        //the sound effects button
        this.clickables.add(new SoundEffectsSwitch(context,R.drawable.sound_effect_on_off_button,
                0.5f,-0.25f,0.75f,0.75f,
                this.settingsData));



        this.renderables.addAll(clickables);

        this.allComponents.add(this.credits);
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
            if (credits.isVisible()){
                renderer.renderQuad(credits,uMVPMatrix);
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
        if (credits.isVisible()){
            if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                this.creditsUpdater.cancel();
            }
            return true;
        }

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

        for (int i = this.allComponents.size()-1;i>= 0;i--){
            if (this.allComponents.get(i) == this.credits && visibility){
                continue;
            }
            this.allComponents.get(i).setVisibility(visibility);
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

    void startCredits(){
        this.creditsUpdater = new CreditsUpdater(credits);
    }
}
