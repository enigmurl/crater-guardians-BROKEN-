package com.enigmadux.craterguardians.guilib;

import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;

public interface TextRenderable extends VisibilitySwitch {
    void renderText(DynamicText renderer, float[] parentMatrix);
    void updateText(String text,float fontSize);
    float getFontSize();
}
