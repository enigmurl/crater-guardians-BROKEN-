package com.enigmadux.craterguardians.loading;

import android.content.Context;
import android.opengl.Matrix;

import enigmadux2d.core.quadRendering.QuadTexture;
import enigmadux2d.core.renderEngine.Renderable;

public class LoadingRenderable implements Renderable {


    private int id;

    private long start = System.currentTimeMillis();

    float[] instanceTransform = new float[16];

    float[] textureTransform = new float[] {0,0,1,1};

    public LoadingRenderable(Context context,int pointer){
        id = QuadTexture.loadAndroidTexturePointer(context, pointer)[0];
        Matrix.setIdentityM(instanceTransform,0);
    }

    public float getClipR(){
        return Math.min(System.currentTimeMillis() - start,500)/1200f;
    }


    public float[] getTextureTransform(){
        return textureTransform;
    }

    @Override
    public float[] getLocalTransform() {
        return instanceTransform;
    }

    @Override
    public int getTextureID() {
        return id;
    }
}
