package com.enigmadux.craterguardians.values;

/** Holds lots of colors, strings, and other constants related to the layout, that multiple classes may refer to
 *
 * @author Manu Bhat
 * @version BETA
 */
public class LayoutConsts {
    /** Serves as the color of the majority of the text, the color is a sort of brown
     *
     */
    public static final int CRATER_TEXT_COLOR = 0xFFFFFFAA;// 0xFF99462D;

    /** Serves as the color of the majority of the text, in float[] form
     *
     */
    public static final float[] CRATER_FLOAT_TEXT_COLOR = new float[] {1,1,0.664f,1};


    /** Serves as the color of the level buttons, the color is black
     *
     */
    public static final float[] LEVEL_FLOAT_TEXT_COLOR = new float[] {1,1,0.664f,1};// 0xFF99462D;

    /** The width of the screen in pixels, it is set by the Renderer
     *
     */
    public static int SCREEN_WIDTH = -1;
    /** The height of the screen in pixels, it is set by the Renderer
     *
     */
    public static int SCREEN_HEIGHT = -1;


    public static float SCALE_X = -1;
    public static float SCALE_Y = -1;
}
