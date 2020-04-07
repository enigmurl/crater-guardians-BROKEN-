package com.enigmadux.craterguardians.GUILib;

import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;

public interface TextRenderable extends VisibilitySwitch {
    void renderText(DynamicText renderer, float[] parentMatrix);
    void updateText(String text,float fontSize);
    float getFontSize();
}
