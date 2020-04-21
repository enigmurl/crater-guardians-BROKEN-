package com.enigmadux.craterguardians.guilib;

import android.content.Context;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

public class AmmoBar extends ProgressBar {
    private static final float SCALE_FACTOR = 2f;

    private Tileable ammos;

    public AmmoBar(Context context, float x, float y, float w, float h, int maxValue) {
        super(context, x, y, w, h, maxValue, R.drawable.hitpoints_ammo_bar);
        float size = SCALE_FACTOR * h;
        float overlayW = w - 2 * h * MARGIN_PERCENT * LayoutConsts.SCALE_X;
        this.ammos = new Tileable(context,R.drawable.ammo_visual,x,y,overlayW,h * (1 - 2 * MARGIN_PERCENT),maxValue);
        this.renderables.add(new QuadTexture(context, R.drawable.ammo_bar_icon,x - 5 * w/8,y,size* LayoutConsts.SCALE_X,size));
        this.renderables.add(this.ammos);
    }

    @Override
    public void setMaxValue(int newMax) {
        super.setMaxValue(newMax);
        this.ammos.setMaxAmount(newMax);
    }

    @Override
    public void setValue(int newValue) {
        super.setValue(newValue);
        this.ammos.setCurrentAmount(newValue);
    }
}