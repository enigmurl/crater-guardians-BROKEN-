package com.enigmadux.papturetheflag;

import enigmadux2d.core.Text;
/** Used to detect and react to touch events, while also visually displays text
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Button extends Text {
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
    public Button(String text,int fontID,float x,float y,float w,float h,int color){
        super(text,fontID,x,y,w,h,color);
    }
}
