package com.enigmadux.craterguardians.guilib;

import android.content.Context;

import enigmadux2d.core.quadRendering.QuadTexture;

/** IN QUAD TEXTURE WHEN LOADING UP THE TEXTURE, THESE TWO LINES OF CODE ARE REQUIRED:
 *
 *  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
 *  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
 *
 */
public class Tileable extends QuadTexture {
    private int maxAmount;
    private int currentAmount;

    private float defaultCenterX;
    private float maxW;

    public Tileable(Context context, int texturePointer, float x, float y, float w, float h,int maxAmnt) {
        super(context, texturePointer, x, y, w, h);
        this.maxAmount = currentAmount = maxAmnt;
        this.textureW = maxAmnt;
        defaultCenterX = x;
        this.maxW = w;
    }

    public void setCurrentAmount(int currentAmount){
        if (currentAmount != this.currentAmount) {
            this.currentAmount = currentAmount;
            this.textureW = currentAmount;
            float w = maxW * currentAmount / maxAmount;
            this.setTransform(defaultCenterX - maxW/2 + w/2,y,w, h);
        }
    }

    public void setMaxAmount(int maxAmount){
        if (maxAmount != this.maxAmount) {
            this.maxAmount = maxAmount;
            float w = maxW * currentAmount / maxAmount;
            this.setTransform(defaultCenterX - maxW/2 + w/2,y,w, h);        }
    }
}
