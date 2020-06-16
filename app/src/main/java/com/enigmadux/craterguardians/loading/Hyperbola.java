package com.enigmadux.craterguardians.loading;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.values.LayoutConsts;

public class Hyperbola extends LoadingRenderable {

    private static final float RATE = 3f;


    private float[] clipMatrix = new float[16];
    private float[] rotMatrix = new float[16];

    private float offset;

    private float y;
    private float x;


    private long last = System.currentTimeMillis();

    private boolean started = false;

    private boolean inSecondHalf = false;

    public Hyperbola(Context context,float offset) {
        super(context, R.drawable.loading_branch1);
        this.offset = offset;

        this.y = 1;
        float rawx =this.defaultFunction(y);

        this.x = Math.min(1, Float.isNaN(rawx) ? 1/LayoutConsts.SCALE_X : rawx);

        Matrix.setIdentityM(this.instanceTransform,0);
        Matrix.scaleM(this.instanceTransform,0,0,0,1);
    }

    @Override
    public float getClipR() {
        return Float.MAX_VALUE;
    }


    public void start(){
        this.started = true;
        this.last = System.currentTimeMillis();
    }


    public void update(){
        if (! started) return;
        //anti derivative = 1.6666f * ln(x + 3 * 1.666666)
        //ln(x) =

//        float x = (float) Math.min(2/LayoutConsts.SCALE_X,-3/(22.5f  * LayoutConsts.SCALE_X) -1 + Math.pow(Math.E,(System.currentTimeMillis()-start)/10000f));
//
//        float rawy = this.defaultFunction(x);
//        float y= Math.min(1, Float.isNaN(rawy) ? 1 : rawy);
        //length = 1.666 *1/(x+3)^2
        long delta = System.currentTimeMillis() - last;




        if (y > -5/22.5f) {
            y -= 1 / (y + 5 / 22.5f) * delta / 1000f * RATE;
        } if (y < -3.707/22.5f) {
            this.inSecondHalf = true;
        }



        y= MathOps.clip(y,-1,1);


        float rawx =this.defaultFunction(y);

        this.x = Math.min(1, Float.isNaN(rawx) ? 1/LayoutConsts.SCALE_X : rawx);


        if (-1.6666 * 1/((x+3) * (x+3)) < -1){
            this.inSecondHalf = true;
        }


        //normal height - chopped off/2
        float ht = (1-y);
        float wd = 1+x + 0.3f ;

        this.textureTransform[3] = ht/2;
        this.textureTransform[2] = wd/2;

        Matrix.setIdentityM(clipMatrix,0);
        Matrix.translateM(clipMatrix,0,-1 + wd/2,1-ht/2,0);
        Matrix.scaleM(clipMatrix,0,wd,ht,1);

        Matrix.setIdentityM(rotMatrix,0);
        Matrix.rotateM(rotMatrix,0,offset,0,0,1);

        Matrix.multiplyMM(instanceTransform,0,rotMatrix,0,clipMatrix,0);


        last = System.currentTimeMillis();
    }


    //its the inverse function, so in goes y out comes x
    public float defaultFunction(float y){

        //y = 1.6666 * (1/x+3  -3)
        //x = 1.6666 * (1/y+3 - 3)
        //x/1.666+3 =1/y+3
        //1/(x/1.666 + 3) - 3 = y
        float mappedY = y * 22.5f;
        if (mappedY < -5){
            return Float.NaN;
        }
        float mappedX = 1/(mappedY/1.6666666f +3) - 3;



        return mappedX/(22.5f * LayoutConsts.SCALE_X);
    }


    public boolean hasSwitchedDirection(){
        return inSecondHalf;
    }

}
