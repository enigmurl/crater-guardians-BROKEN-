package enigmadux2d.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Because OpenGL has no easy way to do text, this class does the work
 *
 * @author  Manu Bhat
 * @version BETA
 */
public class Text extends  EnigmaduxComponent{
    //tag used for logging
    private static final String TAG = "ENIGMADUX_TEXT";
    //The actual text to draw. Can be digits words, letters, etc (but as a String object of course)
    private String text;
    //The pointer to the font in the res directory (how to draw the text) Should be a pointer to a *.ttf file
    private int fontId;

    //a color in hex e.g. 0xFFFF0000 is full alpha full red
    private int color;
    //The formulated and textured TexturedRect after the processing of the input text and font. This is whats used for drawing
    private TexturedRect texturedRect;

    /** Default constructor
     *
     *
     * @param text The actual text to draw. Can be digits, words, letters, etc (but as a String object of course)
     * @param fontID The pointer to the font in the res directory (how to draw the text) Should be a pointer to a *.ttf file
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param color a color in hex e.g. 0xFFFF0000 is full alpha full red 0 green 0 blue
     */

    public Text(String text,int fontID,float x,float y,float w,float h,int color){
        super(x,y,w,h);
        this.text = text;
        this.fontId = fontID;
        this.color = color;
    }

    /** Binds the given image to the rect
     *
     * @param gl an instance of GL10 used to access open gl
     * @param context any android context use to get the resources (this is subject to change)
     */
    public void loadGLTexture(@NonNull GL10 gl, Context context) {
        TextView tv = new TextView(context);
        tv.setText(this.text);
        tv.setTypeface(ResourcesCompat.getFont(context,this.fontId));
        tv.setTextColor(this.color);
        //tv.setTextSize(this.h );
        texturedRect = new TexturedRect(this.x,this.y,this.w,this.h);
        texturedRect.loadGLTexture(gl,this.loadBitmapFromView(tv));
        texturedRect.show();
    }

    /** The draw method for the text. Draws the text to the screen to the frame.
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void draw(GL10 gl,float[] parentMatrix) {
        if (! this.visible){
            return;
        }
        this.texturedRect.draw(gl,parentMatrix);
    }

    /** Static method converting views to Bitmaps. Though it is slightly expensive, it's only needed to be called once. (gotten from https://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android)
     *
     * @param v The original view you want saved to a bitmap.
     * @return The bitmap that has the same visual content as the view, only as a Bitmap object
     */
    private Bitmap loadBitmapFromView(View v) {
        v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(),v.getMeasuredHeight());
        v.draw(c);
        return Bitmap.createScaledBitmap(b,b.getWidth(),(int) (b.getWidth() * this.h/this.w),false);

    }
    /**
     * Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }


}
