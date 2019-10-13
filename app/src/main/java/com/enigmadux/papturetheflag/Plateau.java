package com.enigmadux.papturetheflag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;

public class Plateau extends TexturedRect {
    //the points, see constructor details for more
    private float[][] points;
    //has the texture been loaded yet
    private boolean isTextureLoaded = false;


    /** Default Constructor
     *
     * @param points open gl coordinates; should be in form of x1,y1,x2,y2,x3,y3,x4,y4. Additionally, the vertices should be in the order of bottom left,top left,bottom right,top right
     *
     */
    public Plateau(float[] ... points) {
        super(0,0,1.0f,1.0f);

        this.points = points;

        this.loadVertexBuffer(new float[]{
                points[0][0],points[0][1],0,
                points[1][0],points[1][1],0,
                points[2][0],points[2][1],0,
                points[3][0],points[3][1],0,
        });

    }

    /** Draws the plateau onto the screen, rotated
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        if (this.isTextureLoaded) {
            super.draw(gl, parentMatrix);
        }
    }

    /** Loads the texture
     *
     * @param gl used to tell openGL what the new texture is
     * @param context used to access resources
     */
    public void loadGLTexture(@NonNull GL10 gl, Context context) {
        this.isTextureLoaded = true;
        super.loadGLTexture(gl,context, R.drawable.plateau);
    }


    /** Makes sure a character doesnt go inside the plateau, if it is, it moves it outside.
     *
     * @param character the character you are checking
     */
    public void clipCharacterPos(BaseCharacter character) {
        this.clipCharacterEdge(character,this.points[0][0],this.points[0][1],this.points[1][0],this.points[1][1]);
        this.clipCharacterEdge(character,this.points[3][0],this.points[3][1],this.points[1][0],this.points[1][1]);
        this.clipCharacterEdge(character,this.points[0][0],this.points[0][1],this.points[2][0],this.points[2][1]);
        this.clipCharacterEdge(character,this.points[2][0],this.points[2][1],this.points[3][0],this.points[3][1]);

    }

    /** Makes sure a character doesn't pass an edge, if its in it, it moves it outside.
     * Portions of code borrowed from: http://csharphelper.com/blog/2017/08/calculate-where-a-line-segment-and-an-ellipse-intersect-in-c/
     * The clipping part (finding new position of player) I made myself though.
     *
     * @param character the character in question
     * @param pt1x x coordinate of line segment point 1
     * @param pt1y y coordinate of line segment point 1
     * @param pt2x x coordinate of line segment point 2
     * @param pt2y y coordinate of line segment point 2
     */
    private void clipCharacterEdge(BaseCharacter character, float pt1x, float pt1y, float pt2x, float pt2y){
        // Translate so the ellipse is centered at the origin.
        float cx = character.getDeltaX();
        float cy = character.getDeltaY();

        float pt1X = pt1x - cx;
        float pt1Y = pt1y - cy;
        float pt2X = pt2x - cx;
        float pt2Y = pt2y - cy;

        // Get the semi major and semi minor axes.
        float a = character.CHARACTER_WIDTH / 2;
        float b = character.CHARACTER_HEIGHT / 2;

        // Calculate the quadratic parameters.
        float A = (pt2X - pt1X) * (pt2X - pt1X) / (a*a) +
                (pt2Y - pt1Y) * (pt2Y - pt1Y) /(b* b);
        float B = 2 * pt1X * (pt2X - pt1X) /(a* a) +
                2 * pt1Y * (pt2Y - pt1Y) /(b*b);
        float C = pt1X * pt1X /(a*a)+ pt1Y * pt1Y /(b*b)- 1;


        // Calculate the discriminant.
        float discriminant = B * B - 4 * A * C;

        //Log.d("TAG","disc: " + discriminant);

        /*if (discriminant == 0){
            return -B/(2*A) >= 0 && -B/(2* A) <= 1;
        } else */ //Technically the tangent line does intersect, however for purposes of bounding box collisions it doesn't matter
        if (discriminant > 0){
            float tValue1 = (float) (-B + Math.sqrt(discriminant))/(2*A); //||
            float tValue2 = (float) (-B - Math.sqrt(discriminant))/(2*A);


            float defaultTangentValue = (tValue1 + tValue2)/2;
            float tangentTValue = Float.NEGATIVE_INFINITY;
            if (tValue1 >= 0 && tValue1 <= 1 && tValue2 >= 0 && tValue2 <= 1){
                tangentTValue = defaultTangentValue;
            }
            else if ((tValue1 >= 0 && tValue1 <= 1)){
                if (tValue2 >1) {
                    tangentTValue = Math.min(1, defaultTangentValue);
                } else {
                    tangentTValue = Math.max(0,defaultTangentValue);
                }

            } else if ((tValue2 >= 0 && tValue2 <= 1)){
                if (tValue1 >1) {
                    tangentTValue = Math.min(1, defaultTangentValue);
                } else {
                    tangentTValue = Math.max(0,defaultTangentValue);
                }

            }

            if (tangentTValue != Float.NEGATIVE_INFINITY){
                float x = pt1X + tangentTValue * (pt2X - pt1X);
                float y = pt1Y + tangentTValue * (pt2Y - pt1Y);

                float scaleFactor = (float) Math.sqrt(x*x/(a*a) + y*y/(b*b));

                //Log.d("PLATEAU","t value1 " + tValue1 + " tvalue 2 " + tValue2 + " tangent T " + tangentTValue + " scale " + scaleFactor);
                //Log.d("PLATEAU","should be 1, is " + (Math.pow(x/scaleFactor,2)/(a*a) + Math.pow(y/scaleFactor,2)/(b*b)));

                float dX = (x/scaleFactor) - x;
                float dY = (y/scaleFactor) - y;


                character.translateFromPos(-dX,-dY);
                //x^2/a + y^2/b = s
            }

                    //((-B - Math.sqrt(discriminant)) /(2*A) >= 0 && (-B - Math.sqrt(discriminant))/(2*A) <= 1);
        }
    }

    /** Sees whether or not the texture is loaded as not make a null reference draw itself
     *
     * @return whether or not the texture is loaded
     */
    public boolean isTextureLoaded() {
        return this.isTextureLoaded;
    }
}
