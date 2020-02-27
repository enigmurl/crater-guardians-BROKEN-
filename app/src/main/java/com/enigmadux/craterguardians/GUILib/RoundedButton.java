package com.enigmadux.craterguardians.GUILib;

import android.content.Context;

import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.shapes.TexturedRect;

/** A rounded button class, this is just the visual, no touch detection is done here
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class RoundedButton extends TexturedRect {

    //FRACTION of the width of four corner radius pieces
    private static final float CORNERSIZE = 77f/256;




    /** Default Constructor
     *
     */
    public RoundedButton(){
        super(-0.5f,-0.5f,1,1);
        /*this.loadVertexBuffer(new float[]{
                -1, 1, 0,
                -1f / 3, 1, 0,
                1f / 3, 1, 0,
                1, 1, 0,
                -1, 1f / 3, 0,
                -1f / 3, 1f / 3, 0,
                1f / 3, 1f / 3, 0,
                1, 1f / 3, 0,
                -1, -1f / 3, 0,
                -1f / 3, -1f / 3, 0,
                1f / 3, -1f / 3, 0,
                1, -1f / 3, 0,
                -1, 0, 0,
                -1f / 3, 0, 0,
                1f / 3, 0, 0,
                1, 0, 0,
        });*/


        this.loadIndexBuffer(new short[]{
                0, 4, 1,
                4, 1, 5,

                1, 5, 2,
                5, 2, 6,

                2, 6, 3,
                6, 3, 7,


                4, 8, 5,
                8, 5, 9,

                5, 9, 6,
                9, 6, 10,

                6, 10, 7,
                10, 7, 11,


                8, 12, 9,
                12, 9, 13,

                9, 13, 10,
                13, 10, 14,

                10, 14, 11,
                14, 11, 15,

        });

//        this.loadTextureBuffer(new float[]{
//                0, 0,
//                CORNERSIZE, 0,
//                1 - CORNERSIZE, 0,
//                1, 0,
//
//                0, CORNERSIZE,
//                CORNERSIZE,CORNERSIZE,
//                1 - CORNERSIZE, CORNERSIZE,
//                1, CORNERSIZE,
//
//                0,1 - CORNERSIZE,
//                CORNERSIZE, 1 - CORNERSIZE,
//                1 - CORNERSIZE,1 - CORNERSIZE,
//                1, 1 - CORNERSIZE,
//
//                0, 1,
//                CORNERSIZE, 1,
//                1 - CORNERSIZE, 1,
//                1, 1
//
//        });

    }


    /** Loads the gl texture of this button
     *
     * @param context any context used to load resources
     */
    public void loadGLTexture(Context context) {
        super.loadGLTexture(context,R.drawable.button_background);
        this.loadTextureBuffer(new float[]{
                0, 0,
                CORNERSIZE, 0,
                1 - CORNERSIZE, 0,
                1, 0,

                0, CORNERSIZE,
                CORNERSIZE,CORNERSIZE,
                1 - CORNERSIZE, CORNERSIZE,
                1, CORNERSIZE,

                0,1 - CORNERSIZE,
                CORNERSIZE, 1 - CORNERSIZE,
                1 - CORNERSIZE,1 - CORNERSIZE,
                1, 1 - CORNERSIZE,

                0, 1,
                CORNERSIZE, 1,
                1 - CORNERSIZE, 1,
                1, 1

        });
    }

    /** Draws the rounded button with specied width and height
     *  Please do scaling and translating in the parent matrix
     *
     *  @param parentMatrix parent matrix which describes how to transform to eye coordinates
     * @param w the width of the rect
     * @param h the height of the rect
     */
    public void draw(float[] parentMatrix, float w, float h){
        if (! this.visible) return;

        //float inY = 0.5f - BORDERWIDTH/2;
        //float inX = inY * h/w ;

        float cornerY = 1 - CORNERSIZE;
        float cornerX = 1 - (CORNERSIZE* h/w * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH);

        this.loadVertexBuffer(new float[]{
                -0,1,0,
                1-cornerX,1,0,
                cornerX,1,0,
                1,1,0,
                -0,cornerY,0,
                1-cornerX,cornerY,0,
                cornerX,cornerY,0,
                1f,cornerY,0,
                -0f,1-cornerY,0,
                1-cornerX,1-cornerY,0,
                cornerX,1-cornerY,0,
                1f,1-cornerY,0,
                -0f,0,0,
                1-cornerX,0,0,
                cornerX,0,0,
                1f,0,0,
        });

        //Log.d("RoundedButton:","Vertex Buffer: " + Arrays.toString(this.vertices));


        //first and second vertices
        //TODO don't create a new float[] each time, getting lazy so doing it though
        //this.vertices[3] = -inX;
        //this.vertices[4] = -inY;

        this.draw(parentMatrix);
    }
}
