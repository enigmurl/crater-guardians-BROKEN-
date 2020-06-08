package com.enigmadux.craterguardians.guis.settingsScreen;

import android.content.Context;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

public class Credits extends QuadTexture {
    private static final float ASPECT_RATIO = 1750f/1000f;



    public Credits(Context context) {
        super(context, R.drawable.credits,0,0, 2, ASPECT_RATIO * 2/ LayoutConsts.SCALE_X);
    }




}
