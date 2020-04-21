package com.enigmadux.craterguardians.guilib.dynamicText;

/** Stores data about a character from an atlas
 *
 * @author Manu Bhat
 * @version BETA
 */
public class DynamicChar {

    /**
     * xTravel the pixels you have to travel after writing the font / font size in pixels
     */
    float xTravel;
    /** the pixels you have to travel from current pointer in x direction / font size in pixels
     *
     */
    float xOffset;
    /**
     * the pixels you have to travel from current pointer in t direction (to get to the top left point) / font size in pixels
     */
    float yOffset;
    /**
     * the distance from the left edge to the rect left edge / TOTAL WIDTH OF ATLAS
     */
    float rectX;
    /**
     *  the distance from the edge edge to the rect top edge / TOTAL HEIGHT OF ATLAS
     */
    float rectY;
    /**
     *  width / TOTAL WIDTH OF ATLAS
     */
    float rectW;
    /**
     *  height / TOTAL HEIGHT OF ATLAS
     */
    float rectH;

    /** the width/ font size in pixels
     *
     */
    float rectFontSizeW;
    /** The height/ font size in pixels
     *
     */
    float rectFontSizeH;

    /** Default Constructor
     *
     * @param xTravel the pixels you have to travel after writing the font / font size in pixels
     * @param xOffset the pixels you have to travel from current pointer in x direction / font size in pixels
     * @param yOffset the pixels you have to travel from current pointer in t direction (to get to the top left point) / font size in pixels
     * @param rectX the distance from the left edge to the rect left edge / TOTAL WIDTH OF ATLAS
     * @param rectY the distance from the edge edge to the rect top edge / TOTAL HEIGHT OF ATLAS
     * @param rectW width / TOTAL WIDTH OF ATLAS
     * @param rectH height / TOTAL HEIGHT OF ATLAS
     * @param fontSize the size in pixels
     * @param bitmapWidth the width in pixels of the entire atlas
     * @param bitmapHeight the height in pixels of the entire atlas
     */
    public DynamicChar(float xTravel,float xOffset,float yOffset, float rectX,float rectY,float rectW,float rectH,
                       int fontSize,int bitmapWidth,int bitmapHeight){
        this.xTravel = xTravel;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.rectX = rectX;
        this.rectY = rectY;
        this.rectW = rectW;
        this.rectH = rectH;

        this.rectFontSizeW = rectW * bitmapWidth/fontSize;
        this.rectFontSizeH = rectH * bitmapHeight/fontSize;
    }
}
