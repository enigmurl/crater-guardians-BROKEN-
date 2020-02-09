package com.enigmadux.craterguardians.GUI;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Displays a list of the players resources, for now just the experience
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class MatieralsBar extends EnigmaduxComponent {


    //this is the actual bar background, for aesthetic purposes
    private TexturedRect bar;
    //this tells the experience
    private InGameTextbox experienceIndicator;

    /** Shows the player how many resources they have
     *
     * @param x the left deltX
     * @param y the bottom y
     * @param w the width
     * @param h the height
     */
    public MatieralsBar(float x, float y, float w, float h){
        super(x,y,w,h);

        this.bar = new TexturedRect(x,y,w,h);

        this.experienceIndicator = new InGameTextbox("Experience: " + PlayerData.getExperience(),0,y+h/2,0.05f, LayoutConsts.CRATER_TEXT_COLOR,false);
    }

    /** Hides all sub components
     *
     */
    @Override
    public void hide() {
        super.hide();
        this.experienceIndicator.hide();
        this.bar.hide();
    }

    /** Shows all sub components
     *
     */
    @Override
    public void show() {
        super.show();
        this.experienceIndicator.show();
        this.bar.show();
    }

    /** Updates resources based on current game state
     *
     */
    public void updateResources(){
        this.experienceIndicator.setText("Experience: " + PlayerData.getExperience());
    }

    /** Loads the gl texture of sub components
     *
     * @param context context used to load resources
     */
    public void loadGLTexture(Context context){

        this.bar.loadGLTexture(context, R.drawable.character_select_bottom);

        this.experienceIndicator.loadGLTexture();
    }


    /** Draws the bar along with the experience display
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        this.bar.draw(parentMatrix);

        this.experienceIndicator.draw(parentMatrix);

    }


    /** Processes a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not to dispose of the touch event
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
