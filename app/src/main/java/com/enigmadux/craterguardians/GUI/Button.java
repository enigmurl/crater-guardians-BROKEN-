package com.enigmadux.craterguardians.GUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.LayoutConsts;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.SoundLib;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Used to detect and react to touch events, while also visually displays text
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Button extends EnigmaduxComponent {
    /** this tells the level of smoothness of the text (high num = more smooth, the draw back being that it is more expensive*/
    private static final float SMOOTHNESS = 3;

    //when the button is selected is should be smaller
    private static final float BUTTON_SELECTED_SCALE = 0.9f;

    /** the margin between all the edges and the text, in openGL coordinates, for a font size of 1 its 0.5, for a font size of 0.5 its 0.25*/
    private static final float MIN_PADDING = 0.5f;

    //static paint used to color bitmaps for anti aliasing
    private static Paint bitmapPainter = new Paint();

    private static int width = LayoutConsts.SCREEN_WIDTH;
    private static int height = LayoutConsts.SCREEN_HEIGHT;


    /**Visual representation of the background of the button*/
    private static final TexturedRect BUTTON_BACKGROUND = new TexturedRect(0,0,1,1);
    /** For level buttons the visual representation is different*/
    private static final TexturedRect LEVEL_BUTTON_BACKGROUND = new TexturedRect(0,0,1,1);


    //parentMatrix * scalarTranslationMatrix
    private final float[] finalMatrix = new float[16];
    //matrix to adjust the background to the right size and place
    private final float[] scalarTranslationMatrix = new float[16];

    //the width and height of the box, not the text itself
    private float boxWidth;
    private float boxHeight;

    //whether or not to draw the background
    private boolean drawBackground = true;

    //whether or not the button is being pressed
    protected boolean down;

    //the actual text or image that is personalized for this text
    protected TexturedRect texturedRect;

    //The actual text to draw. Can be digits, words, letters, etc (but as a String object of course)
    private String text;
    //a color in hex e.g. 0xFFFF0000 is full alpha full red 0 green 0 blue, but as an int
    private int color;

    //whether or not its an image button or text button
    private boolean isImageButton = false;
    //whether or not its a levelButton
    private boolean isLevelButton = false;

    //an array representing the current shader

    private float fontHeight;


    //whether or not the curretn text has been renderer
    private boolean renderedRecentText = false;

    /** Default constructor which makes text buttons
     * @param text The actual text to draw. Can be digits, words, letters, etc (but as a String object of course)
     * @param x the open gl x coordinate of the rect, center
     * @param y the open gl y coordinate of the rect, center
     * @param boxWidth the width of the actual box in openGL coordinates, however, it will be extended if the text exceeds the length
     * @param boxHeight the height of the actual box in openGL coordinates
     * @param fontHeight the height of the text (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param color a color in hex e.g. 0xFFFF0000 is full alpha full red 0 green 0 blue
     * @param isLevelButton whether it's a level button or not
     */
    public Button(String text, float x, float y, float boxWidth, float boxHeight, float fontHeight, int color, boolean isLevelButton){
        super(x,y,-1,fontHeight);//w inited later
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;

        this.text = text;
        this.color = color;

        this.isLevelButton = isLevelButton;

        this.fontHeight = fontHeight;

    }

    /** Constructor used for image buttons
     *
     * @param image The image that is to served as a button
     */
    public Button(TexturedRect image){
        super(image.getX(),image.getY(),image.getW(),image.getH());
        this.texturedRect = image;
        this.drawBackground = false;
        this.isImageButton = true;
    }

    /** Binds the  image to the background rect, also loads the paint's font
     *
     * @param gl an instance of GL10 used to access open gl
     * @param context any android context use to get the resources (this is subject to change)
     */
    public static void loadButtonGLTexture(GL10 gl, Context context) {
        Button.BUTTON_BACKGROUND.loadGLTexture(context, R.drawable.button_background);
        Button.LEVEL_BUTTON_BACKGROUND.loadGLTexture(context,R.drawable.level_button_background);

        Button.bitmapPainter.setTypeface(ResourcesCompat.getFont(context,R.font.baloobhaina));
    }

    /** Binds the text to the rect, should only needed to be called if it's a text based button and not a image based
     *
     */
    public void loadGLTexture() {
        if (this.isImageButton){
            Log.d("BUTTON","called loadGLTEXTURE on text button");
            return;
        }

        this.renderedRecentText = true;
        bitmapPainter.setTextSize((this.fontHeight*height/2));

        this.w = 2* bitmapPainter.measureText(this.text)/(width);

        float[] shader = null;
        if (this.texturedRect != null){
            shader = this.texturedRect.getShader();
        }
        this.texturedRect = new TexturedRect(-this.w / 2, -(3 * this.fontHeight / (2)), this.w, this.fontHeight * 2);//the extra layer

        if (this.text.startsWith("UPGRADE:")){
            Log.d("UPGRADE TEXT:",this.x  + " y: " + this.y);
        }

        this.texturedRect.setTranslate(this.x,this.y);

        if (shader != null) this.texturedRect.setShader(shader[0],shader[1],shader[2],shader[3]);

        texturedRect.loadGLTexture(loadBitmap());
        texturedRect.show();

        float textMinPadding = Button.MIN_PADDING * this.fontHeight;

        this.w = Math.max(this.w + 2*textMinPadding,this.boxWidth);
        this.h = Math.max(this.fontHeight + 2*textMinPadding,this.boxHeight);
        this.x = this.x - this.w/2;
        this.y = this.y - this.h/2;
    }

    /** Binds the given image to the rect, should only needed to be called if it's a image based button and not a text based
     *
     * @param context any android context use to get the resources (this is subject to change)
     */
    public void loadGLTexture(Context context, int fileID){
        if (! this.isImageButton){
            Log.d("BUTTON","called loadGLTEXTURE on image button");
            return;
        }
        this.texturedRect.loadGLTexture(context,fileID);
    }

    /** The draw method for the button. Draws the button text and background to the screen to the frame.
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        //todo OPTIMIZATION OF THE MATRICES
        if (! this.isImageButton && ! this.renderedRecentText){
            this.renderedRecentText = true;
            this.loadGLTexture();
        }
        if (this.visible) {
            if (this.drawBackground) {
                Matrix.setIdentityM(scalarTranslationMatrix, 0);
                if (this.down) {
                    Matrix.translateM(scalarTranslationMatrix, 0, this.x + this.w * (1-Button.BUTTON_SELECTED_SCALE)/2, this.y + this.h * (1-Button.BUTTON_SELECTED_SCALE)/2, 0);
                    Matrix.scaleM(scalarTranslationMatrix, 0, this.w * Button.BUTTON_SELECTED_SCALE, this.h*Button.BUTTON_SELECTED_SCALE, 0);
                } else {
                    Matrix.translateM(scalarTranslationMatrix, 0, this.x, this.y, 0);
                    Matrix.scaleM(scalarTranslationMatrix, 0, this.w, this.h, 0);
                }
                Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, scalarTranslationMatrix, 0);

                if (this.isLevelButton){
                    LEVEL_BUTTON_BACKGROUND.setShader(
                            this.texturedRect.getShader()[0],
                            this.texturedRect.getShader()[1],
                            this.texturedRect.getShader()[2],
                            this.texturedRect.getShader()[3]);
                    LEVEL_BUTTON_BACKGROUND.draw(finalMatrix);
                } else {
                    BUTTON_BACKGROUND.setShader(
                            this.texturedRect.getShader()[0],
                            this.texturedRect.getShader()[1],
                            this.texturedRect.getShader()[2],
                            this.texturedRect.getShader()[3]);
                    BUTTON_BACKGROUND.draw(finalMatrix);
                }
            }
            if (this.isImageButton && this.down){
                Matrix.setIdentityM(scalarTranslationMatrix, 0);
                //translate it correct amount to center it
                Matrix.translateM(scalarTranslationMatrix, 0,  this.x + this.w * (1-Button.BUTTON_SELECTED_SCALE)/2,  this.y + this.h * (1-Button.BUTTON_SELECTED_SCALE)/2, 0);
                //scale it
                Matrix.scaleM(scalarTranslationMatrix, 0,  Button.BUTTON_SELECTED_SCALE, Button.BUTTON_SELECTED_SCALE, 0);
                //translate to center for scaling
                Matrix.translateM(scalarTranslationMatrix,0,-this.x,-this.y,0);
                Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, scalarTranslationMatrix, 0);
                //draws the image
                this.texturedRect.draw(finalMatrix);
            } else {
                if (this.down) {
                    this.texturedRect.setScale((float) Math.sqrt(Button.BUTTON_SELECTED_SCALE),(float)Math.sqrt(Button.BUTTON_SELECTED_SCALE));
                } else {
                    this.texturedRect.setScale(1,1);
                }
                //draws the text
                this.texturedRect.draw(parentMatrix);
            }

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
        Bitmap b = Bitmap.createBitmap((int) (Button.SMOOTHNESS * this.w * width/2),(int) (Button.SMOOTHNESS * this.fontHeight * height), Bitmap.Config.ARGB_8888);//no "/2" because we want to make an extra layer for "y","g", "p","q", etc
        bitmapPainter.setColor(this.color);
        bitmapPainter.setTextSize(Button.SMOOTHNESS * this.fontHeight*height/2);

        Canvas c = new Canvas(b);
        c.drawText(this.text,0,(Button.SMOOTHNESS * this.fontHeight*height/2),Button.bitmapPainter);

        Log.d("TEXTURED RECT:","Button: w" + b.getWidth() +  " h "  + b.getHeight());

        return Bitmap.createScaledBitmap(b,(int) (this.w * width/2),(int) (this.fontHeight * height),true);

    }
    /** Method that creates the visual text, Though it is slightly expensive, it's only needed to be called once. (gotten from https://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android)


    /** Sets the drawing text
     *
     * @param text the new text to draw
     */
    public void setText(String text) {
        this.texturedRect.recycle();
        this.text = text;
        //this basically happens when the text is change twice before the loadGLtextur is called, moving it wihtout an offset
        if (this.renderedRecentText){
            this.x += this.w/2;
            this.y += this.h/2;
        }
        this.renderedRecentText = false;

    }

    /** Gets the text that this is representing
     *
     * @return the text that this is displaying
     */
    public String getText() {
        return text;
    }

    /** When the button is released with the finger on it
     * On Release, the release sound effect is played, and the down is set to false
     *
     */
    public void onRelease(){
        SoundLib.playButtonReleasedSoundEffect();
        this.down = false;
    }

    /** When the button is pressed by the finger
     * On Select the select sound effect is played, and the down is set to true
     *
     */
    public void onSelect(){
        SoundLib.playButtonSelectedSoundEffect();
        this.down = true;
    }

    /** When the button was being pressed, but the finger moved off
     * the down is set to false
     *
     */
    public void onSoftRelease(){
        this.down = false;
    }

    /** Sees whether a touch event selects the button
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not it selects the touch event
     */
    public abstract boolean isSelect(MotionEvent e);

    /** Calls the onRelease
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        if (e.getActionMasked() == MotionEvent.ACTION_UP && this.down && this.isSelect(e)){
            this.onRelease();
            return true;
        } else if (e.getActionMasked() == MotionEvent.ACTION_DOWN && ! this.down && this.isSelect(e)) {
            this.onSelect();
            return true;
        } else if (this.down && ! this.isSelect(e)){
            this.onSoftRelease();
            return true;
        }
        return false;
    }

    /** Sets the pos of the background and text
     *
     * @param x the open gl coordinate of the component, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the component, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     */
    @Override
    public void setPos(float x, float y) {
        super.setPos(x , y);
        this.texturedRect.setTranslate(x-this.texturedRect.getX() + this.w/2 - this.texturedRect.getW()/2,
                y-this.texturedRect.getY()+this.h/2 - this.texturedRect.getH()/2);
    }

}
