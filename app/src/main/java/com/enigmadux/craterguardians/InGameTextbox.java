package com.enigmadux.craterguardians;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Used to display just text, customized for this game todo this is basically the same as screen textbox except the width and height, which means we should probably make them 1
 * @author Manu Bhat
 * @version BETA
 */
public class InGameTextbox extends EnigmaduxComponent {

    /** this tells the level of smoothness of the text (high num = more smooth, the draw back being that it is more expensive*/
    private static final float SMOOTHNESS = 3;
    //** the margin between all the edges and the text, in openGL coordinates*/
    //private static final float MIN_PADDING = 0.15f;

    //static paint used to color bitmaps for anti aliasing
    private static Paint bitmapPainter = new Paint();


    //todo make these passed in as a parameter at some point
    private static int screenWidth = 1440;


    private static int screenHeight = 720;
    private static int inGameHeight = 1440;





    //the actual text or image that is personalized for this text
    private TexturedRect texturedRect;

    //The actual text to draw. Can be digits, words, letters, etc (but as a String object of course), split by line
    private String[] text;
    //a color in hex e.g. 0xFFFF0000 is full alpha full red 0 green 0 blue, but as an int
    private int color;


    //if its a textbox thats linked to the game map or linked to the screen
    private boolean isInGame = true;





    /** Default constructor which makes text Textboxs
     * @param text The actual text to draw. Can be digits, words, letters, etc (but as a String object of course)
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param fontHeight the height of the text (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param color a color in hex e.g. 0xFFFF0000 is full alpha full red 0 green 0 blue
     * @param isInGame if its a textbox thats linked to the game map or linked to the screen
     */
    public InGameTextbox(String text, float x, float y, float fontHeight, int color, boolean isInGame){
        super(x,y,-1,fontHeight);//w inited later

        this.text = text.split("\n");
        this.color = color;
        this.isInGame = isInGame;
    }

    /** Loads the font
     *
     * @param context any android context use to get the resources (this is subject to change)
     */
    public static void loadFont(Context context) {

        InGameTextbox.bitmapPainter.setTypeface(ResourcesCompat.getFont(context,R.font.baloobhaina));
    }

    /** Binds the text to the rect, should only needed to be called if it's a text based InGameTextbox and not a image based
     *
     * @param gl an instance of GL10 used to access open gl
     */
    public void loadGLTexture(@NonNull GL10 gl) {
        int height = (this.isInGame) ? inGameHeight:screenHeight;

        bitmapPainter.setTextSize((this.h*height/2));

        for (String text: this.text) {
            this.w = Math.max(2 * bitmapPainter.measureText(text) / (screenWidth),this.w);
        }

        this.h *= this.text.length;


        texturedRect = new TexturedRect(- this.w/2,-(3*this.h/(this.text.length*2)),this.w,this.h + this.h/this.text.length);//extra layer

        texturedRect.setTranslate(this.x,this.y);
        texturedRect.loadGLTexture(gl,loadBitmap());
        texturedRect.show();

        /*this.widMod = this.w + 2* InGameTextbox.MIN_PADDING;
        this.htMod = this.h + 2* InGameTextbox.MIN_PADDING;
        this.xMod = this.x - this.w/2;
        this.yMod = this.y - this.h/2;*/
    }


    /** The draw method for the InGameTextbox. Draws the InGameTextbox text and background to the screen to the frame.
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        if (this.visible) {
            this.texturedRect.draw(gl, parentMatrix);
        }
    }

    /** Sets the shader, which modifies the color of the texture
     *
     * @param r the red value of the filter, 1.0 is full red
     * @param g the green value of the filter, 1.0 is full red
     * @param b the blue value of the filter, 1.0 is full red
     * @param a the alpha value of the filter, 1.0 is full alpha
     */
    public void setShader(float r,float g,float b,float a){
        this.texturedRect.setShader(r,g,b,a);
    }


    /** Method that creates the visual text, Though it is slightly expensive, it's only needed to be called once. (gotten from https://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android)
     *
     * @return The bitmap that has the same visual content as the text, only as a Bitmap object
     */
    private Bitmap loadBitmap() {
        int height = (this.isInGame) ? inGameHeight:screenHeight;

        //the extra layer is needed for the bitmap
        Bitmap b = Bitmap.createBitmap((int) (InGameTextbox.SMOOTHNESS * this.w * screenWidth/2),(int) (InGameTextbox.SMOOTHNESS *(this.h + this.h/this.text.length) * height/2), Bitmap.Config.ARGB_8888);
        bitmapPainter.setColor(this.color);
        bitmapPainter.setTextSize(InGameTextbox.SMOOTHNESS * (this.h)/this.text.length*height/2);

        Canvas c = new Canvas(b);
        for (int i = 0;i <this.text.length;i++) {
            c.drawText(this.text[i], 0, (InGameTextbox.SMOOTHNESS * (this.h)/this.text.length * height/2) * (i+1), InGameTextbox.bitmapPainter);
        }

        return b;

    }

    /** Changes the text, however it will only be updated after loadGLTexture is called. Use "\n" to indicate line splits
     *
     * @param text the new text to draw
     */
    public void setText(String text) {
        this.text = text.split("\n");
    }

    /** Returns the current text, may not be the same text being displayed because loadGLTexture wasn't called
     *
     * @return this text
     */
    public String getText() {
        String returnString = "";
        for (String str: this.text){
            returnString = returnString + str;
        }
        return returnString;
    }

    /** Sees whether the texture has been loaded atleast once
     *
     * @return whether or not it has been loaded atleast once
     */
    public boolean isTextureLoaded(){
        return this.texturedRect != null;
    }

    /** Does nothing
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return false all the time, there is no need for a textbox to observe
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
