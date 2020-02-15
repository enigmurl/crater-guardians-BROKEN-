package com.enigmadux.craterguardians.GUILib;

import android.graphics.Typeface;

/** Draws text on the spot rather than having to reload
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class DynamicText {

    /** Max length of a string (in characters)
     *
     */
    private static final int MAX_CHARACTER_LENGTH = 128;
    /** Max length of a string (in vertices) = num charactres
     *
     */

    //this is where the texture atlas is stored
    private int[] textures = new int[1];

    //maximum text size
    private float maxTextSize;

    //the font that is used to generate the atlas
    private Typeface typeface;




    /** The max test size is generated, and then mipmaps are used from there
     *
     * @param maxTextSize the maximum height of the text (openGL terms not pixels)
     * @param typeface the font that describes how to draw each character
     */
    public DynamicText(float maxTextSize, Typeface typeface){
        this.maxTextSize = maxTextSize;

    }

    /** Creates the texture atlas
     *
     */
    private void createAtlas(){

    }


    /** Draws text. Note /n Characters are not supported
     *
     * @param text the text that needs to be rendered, escape characters like /n and /t not supported
     */
    public void renderText(String text){


    }



}
