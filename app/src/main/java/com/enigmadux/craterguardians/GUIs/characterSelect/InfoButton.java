package com.enigmadux.craterguardians.GUIs.characterSelect;

import android.content.Context;

import com.enigmadux.craterguardians.GUILib.VisibilityInducedButton;
import com.enigmadux.craterguardians.GUILib.VisibilitySwitch;
import com.enigmadux.craterguardians.players.Player;

public class InfoButton extends VisibilityInducedButton {
    /**
     * Default constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture
     * @param h              the height of the texture
     * @param objectToHide   the object that should be hidden
     * @param objectToShow   the object that should be shown when the button is presssed
     * @param isRounded      if the image has rounded off corners
     */
    public InfoButton(Context context, int texturePointer, float x, float y, float w, float h, VisibilitySwitch objectToHide, VisibilitySwitch objectToShow, boolean isRounded) {
        super(context, texturePointer, x, y, w, h, objectToHide, objectToShow, isRounded);

    }

    public void setObjectToShow(VisibilitySwitch objectToShow){
        this.objectToShow = objectToShow;
    }



}
