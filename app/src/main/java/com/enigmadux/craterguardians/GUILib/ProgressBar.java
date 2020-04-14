package com.enigmadux.craterguardians.GUILib;

import android.content.Context;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

/** DEBUG NOTE, this class is set in such a way that the first left half of the
 * OVERLAY TEXTURE is what it would look like at full health, and the right half is what
 * it would look like at no health
 *
 */
public class ProgressBar implements RenderableCollection {
    //percent of height, which is then inferred to widht
    static final float MARGIN_PERCENT = 0.1f;


    private int maxValue;
    private int currentValue;

    protected ArrayList<QuadTexture> renderables = new ArrayList<>(2);
    //the bar on top of the background
    private QuadTexture overlay;


    //center x and y,
    public ProgressBar(Context context, float x, float y, float w, float h, int maxValue, int barPointer){
        this.maxValue = maxValue;
        this.currentValue = maxValue;

        //background
        QuadTexture background = new QuadTexture(context, R.drawable.hitpoints_bar_holder, x, y, w, h);
        float hMargin = h * MARGIN_PERCENT;

        float overlayH = h - 2 * hMargin;
        float overlayW = w - 2 * hMargin * LayoutConsts.SCALE_X;
        this.overlay = new QuadTexture(context,barPointer,x,y, overlayW, overlayH);
        this.overlay.setTextureCord(0,0,0.5f,1);


        this.renderables.add(background);
        this.renderables.add(overlay);
    }

    public void setValue(int newValue){
        this.currentValue = MathOps.clip(newValue,0,maxValue);
        //float newW = (float) newValue/maxValue * this.overlayW;
        //this.overlay.setTransform(this.x - this.overlayW/2 + newW/2,y,newW,overlayH);
        this.overlay.setTextureCord((1-(float) (currentValue)/maxValue) * 0.5f,0,0.5f,1);
        //this.overlay.setShader(1 - (float) newValue/maxValue,(float) newValue/maxValue,0,1);
    }

    public void setMaxValue(int newMax){
        //the overlay doesn't change bc the current value doesn't either
        this.maxValue = newMax;
    }


    public int getCurrentValue(){
        return this.currentValue;
    }

    @Override
    public ArrayList<QuadTexture> getRenderables() {
        return this.renderables;
    }
}

