package com.enigmadux.craterguardians.GUIs.inGameScreen;

import android.content.Context;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.quadRendering.QuadTexture;

public class WinLossIndicator extends QuadTexture {

    private CraterRenderer craterRenderer;

    private QuadTexture lossSign;

    //this stores the victory sign, but it will access the loss attribute if it needs to
    public WinLossIndicator(CraterRenderer craterRenderer, Context context, float x, float y, float w, float h) {
        super(context, R.drawable.victory_sign, x, y, w, h);
        this.lossSign = new QuadTexture(context,R.drawable.loss_sign,0,0,0,0);
        this.craterRenderer = craterRenderer;
    }


    @Override
    public int getTexture() {
        return (craterRenderer.getWorld().hasWonLastLevel()) ? super.getTexture(): lossSign.getTexture();
    }
}
