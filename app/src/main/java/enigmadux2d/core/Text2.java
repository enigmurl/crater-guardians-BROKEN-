package enigmadux2d.core;

import android.content.Context;

import com.enigmadux.craterguardians.R;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

/** Draws text
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Text2 {


    //the text to draw onto the screen
    private String text;
    //how tall it is in openGl terms
    private float fontSize;

    /** maps characters to their width positions  on the TEXT_MAP, the order is abc..xyzABC...XYZ0123456789 with the last one being a space*/
    private final static float[] CHARACTER_WIDTHS = new float[] {};

    /** the visual representation, it stores each alpha numeric character, it is storred as "2d array" with 8 as the height and width */
    private final static TexturedRect TEXT_MAP = new TexturedRect(0,0,1,1);

    /** Default Constructor
     *
     * @param text the text of which to display
     * @param x the CENTER openGL x
     * @param y the CENTER openGL y
     * @param w this width in openGl terms
     * @param fontSize how tall it is in openGL terms (not the same as height as it's just the text, not the box)
     */
    public Text2(String text,float x,float y,float w,float h,float fontSize){
        this.text = text;
        this.fontSize = fontSize;
    }

    /** Binds the text map texture to the TexturedRect
     *
     * @param gl a GL10 object used to access openGL
     * @param context Any non null context that is used to access resource
     */
    public static void loadGLTexture(GL10 gl, Context context){
        TEXT_MAP.loadGLTexture(gl,context, R.drawable.visual_shield);
    }

    /** Draws the text onto the screen
     *
     * @param gl used to access openGL
     * @param parentMatrix describes how to transform from model to view
     */
    public void draw(GL10 gl,float[] parentMatrix){
        for (int i =0;i<text.length();i++){
            TEXT_MAP.loadTextureBuffer(new float[]{

            });
        }
        //Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translationScalarMatrix,0);
        //VISUAL_REPRESENTATION.draw(gl,finalMatrix);
    }
}
