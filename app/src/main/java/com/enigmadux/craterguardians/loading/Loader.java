package com.enigmadux.craterguardians.loading;

import android.content.Context;
import android.opengl.GLES30;

import enigmadux2d.core.renderEngine.LoadingRenderer;


public class Loader {

    //degrees (converted from radians
    private static final float OFFSET = (float) Math.toDegrees(-0.2);

    private int framenum;

    private Glow glow;

    private Hyperbola hyperbola1;
    private Hyperbola hyperbola2;
    private Hyperbola hyperbola3;

    private LoadingRenderable enigmadux;


    private LoadingRenderer loadingRenderer;
    public Loader(LoadingRenderer loadingRenderer){
        this.loadingRenderer = loadingRenderer;
    }

    //true if it's loading, false otherwise
    public boolean load(Context context){
        if (framenum == 0){
            glow = new Glow(context,System.currentTimeMillis());

            hyperbola1 = new Hyperbola(context,OFFSET);
            hyperbola2 = new Hyperbola(context,OFFSET + 120);
            hyperbola3 = new Hyperbola(context,OFFSET +240);

            enigmadux = new Enigmadux(context);
        }

        hyperbola1.update();
        hyperbola2.update();
        hyperbola3.update();


        if (framenum < 60) {
            GLES30.glClearColor(framenum / 60f, framenum / 60f, framenum / 60f, 1);
        } else{
            hyperbola1.start();
            hyperbola2.start();
            hyperbola3.start();

            if ((hyperbola1.hasSwitchedDirection() || hyperbola2.hasSwitchedDirection() || hyperbola3.hasSwitchedDirection()) && ! glow.isHasStarted()){
                glow.start();
            }

            this.loadingRenderer.buffer(glow,hyperbola1,hyperbola2,hyperbola3,enigmadux);
            this.loadingRenderer.render();

        }

        framenum++;
        return framenum < 60;
    }

}
