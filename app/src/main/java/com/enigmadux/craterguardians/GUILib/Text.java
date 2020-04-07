package com.enigmadux.craterguardians.GUILib;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.GUILib.dynamicText.TextMesh;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

/** Any class that wants to be a clickable must extend this class.
 *
 *
 *
 * COMMON DEBUGS:
 * make sure the clickable is viewable (isVisible = true), this is crucial
 * isDown must be handled by the sub class, as in when it's pressed it must become true, todo might want to change this in future
 *
 */
public class Text implements TextRenderable {
    //if this should be drawn or not, if it's not visisble it also shouldn't be clicked
    protected boolean isVisible;


    /** This translates the text to the appropriate place
     *
     */
    private final float[] textTranslationMatrix = new float[16];
    /** Scales the text to the appropriate place
     *
     */
    private final float[] textScalarMatrix = new float[16];
    /** Transforms the text to the appropriate location
     *
     */
    private final float[] textTransformationMatrix = new float[16];

    /**
     * This is where intermediate dumping of the the matrices is put into
     */
    private final float[] finalMatrix = new float[16];


    /** ImageText that will be shown to the screen,it's really just vertices and texture cords if it's null, no text is rendered
     *
     */
    private TextMesh visibleText;

    /** This is the actual text that needs to be rendered
     *
     */
    private String text;

    /** The height of the text that should be rendered
     *
     */
    private float fontSize;

    /** ImageText color that subclasses can change
     *
     */
    protected float[] textColor = LayoutConsts.CRATER_FLOAT_TEXT_COLOR;

    protected float textDeltaX;
    protected float textDeltaY;

    private float x;
    private float y;

    /**
     * Default Constructor

     */
    public Text(float x, float y,String text,float fontSize) {
        this.x = x;
        this.y = y;

        this.updateText(text,fontSize);
    }

    /**
     * If this is visible, and being drawn to the screen
     *
     * @return If this is being drawn right now
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * Sets whether this should be drawn or not
     *
     * @param visible whether to be drawn or not
     */
    public void setVisibility(boolean visible) {
        this.isVisible = visible;
    }


    /** Renders text, if there is any
     *
     * @param renderer the renderer with the font's loaded and such
     * @param parentMatrix the mvp matrix that describe model view projection transformations
     */
    public void renderText(DynamicText renderer,float[] parentMatrix){
        if (! this.isVisible) return;
        if (this.text != null && (this.visibleText == null || ! this.visibleText.getActualText().equals(this.text))){
            this.visibleText = renderer.generateTextMesh(this.text,this.textColor);

        }
        if (this.visibleText != null) {
            Matrix.setIdentityM(textTranslationMatrix,0);
            //3/2 for the y component bc idk
            Matrix.translateM(textTranslationMatrix,0,  this.textDeltaX- this.fontSize * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH * this.visibleText.getW()/2 + this.x,- this.fontSize * 3 * this.visibleText.getH()/2+ this.y +  this.textDeltaY ,0);
            Matrix.setIdentityM(textScalarMatrix,0);
            Matrix.scaleM(textScalarMatrix,0, this.fontSize * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, this.fontSize, 1);
            Matrix.multiplyMM(textTransformationMatrix,0,textTranslationMatrix,0,textScalarMatrix,0);
            Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,textTransformationMatrix,0);

            renderer.renderText(this.visibleText, finalMatrix);
        }
    }



    /** Updates the new text
     *
     * @param newText the new text that should be rendered
     * @param fontSize the height of the font
     */
    public void updateText(String newText,float fontSize){
        if (newText == null ||newText.length() == 0){
            this.visibleText = null;
            this.text = null;
            this.fontSize = fontSize;
        } else {
            this.text = newText;
            this.fontSize = fontSize;
        }

    }
    public void setColor(float[] color){
        this.textColor = color;
    }

    public float getFontSize(){
        return this.fontSize;
    }

    public String getText(){
        return this.text;
    }
}

