package com.enigmadux.craterguardians.guis.homeScreen;

import android.content.Context;

import com.enigmadux.craterguardians.guilib.VisibilitySwitch;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

/** It's basically just a Quad texture, but it can change between a few different textures,
 *
 */
public class CharacterDisplay extends QuadTexture implements VisibilitySwitch {

    /** Whether or not to draw this
     *
     */
    public boolean isVisible = false;

    /** Default Constructor, most likely will only work in a GL THREAD
     *
     * @param context any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     */
    public CharacterDisplay(Context context, int texturePointer, float x, float y, float w, float h) {
        super(context, texturePointer, x, y, w * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH, h);
    }


    /** Sets the OPEN GL texture
     *
     * @param texture the open gl texture pointer NOT R.drawable.*;
     */
    public void setTexture(int texture){
        this.texture[0] = texture;
    }

    /** Sets whether or not to draw this
     *
     * @param visibility whether or not to draw this
     */
    @Override
    public void setVisibility(boolean visibility) {
        this.isVisible = visibility;
    }
}
