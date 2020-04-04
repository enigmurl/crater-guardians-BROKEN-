package com.enigmadux.craterguardians.GUILib;

import android.content.Context;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

public class HealthBar extends ProgressBar {
    private static final float SCALE_FACTOR = 1.2f;

    public HealthBar(Context context, float x, float y, float w, float h, int maxValue) {
        super(context, x, y, w, h, maxValue);
        float size = SCALE_FACTOR * h;
        this.renderables.add(new QuadTexture(context, R.drawable.heart_icon,x - 3 * w/8,y,size* LayoutConsts.SCALE_X,size));
    }
}