package com.enigmadux.craterguardians.GameObjects;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.BaseCharacter;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.gameLib.CraterCollectionElem;

import enigmadux2d.core.shapes.TexturedRect;

public class Plateau extends CraterCollectionElem {
    //the points, see constructor details for more
    private float[][] points;



    /** Vertices of the standard quad
     *
     */
    public static final float[] VERTICES = new float[] {
            0,0,0,
            1,0,0,
            0,1,0,
            0,0,1,

    };
    /** texture cords of the quad
     *
     */
    public static final float[] TEX_CORDS = new float[] {
            0,1,
            0,0,
            1,1,
            1,0

    };

    //parentMatrix * translatorMatrix
    private final float[] finalMatrix = new float[16];
    //maps the static visual's vertex personalized for this class
    private final float[] translatorMatrix;

    /** Default Constructor
     *
     *  open gl coordinates; should be in form of x1,y1,x2,y2,x3,y3,x4,y4. Additionally, the vertices should be in the order of bottom left,top left,bottom right,top right

     *
     * @param instanceID the id of the plateau with respects to the vao it's in
     * @param x1 bottomLeft openGL deltX
     * @param y1 bottomLeft openGL y
     * @param x2 topLeft openGL deltX
     * @param y2 topLeft openGL y
     * @param x3 bottomRight openGL deltX
     * @param y3 bottomRight openGL y
     * @param x4 topRight openGL deltX
     * @param y4 topRight openGL y
     */
    public Plateau(int instanceID,float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        super(instanceID);
        this.points = new float[][] {{x1,y1},{x2,y2},{x3,y3},{x4,y4}};


        //this basically converts the vertex coordinates of the static visual, personalized so that each coordinate matches up;
        /* Because the matrices for openGLES are in column major form, the below matrix is equivalent to this
        [ x2-x1 x3-x1 x4-x1 x1 ]
        [ y2-y1 y3-y1 y4-y1 y1 ]
        [ 0 0 0 0 ]
        [ 0 0 0 1 ]

        The vertices of the static visual are
        [0,0,0,1] for p1
        [1,0,0,1] for p2
        [0,1,0,1] for p3
        [0,0,1,1] for p4

        Therefore the matrix times the vertices would be
        [x1,y1,0,1] for p1
        [x2,y2,0,1] for p2
        [x3,y3,0,1] for p3
        [x4,y4,0,1] for p4

        all while sharing just 1 visual as to optimize memory
         */
        translatorMatrix = new float[] {
                x2-x1,y2-y1,0,0,
                x3-x1,y3-y1,0,0,
                x4-x1,y4-y1,0,0,
                x1,y1,0,1
        };



    }

    /** Gets the points that make up the vertices
     *
     * @return Gets the points in the format of (p1,p2,p3,p4) (bottom left top left bottom right top right)
     */
    public float[][] getPoints() {
        return points;
    }

    /** Draws the enemy, and all sub components
     *
     * @param parentMatrix used to translate from model to world space
     */
    public void draw(float[] parentMatrix){
        Matrix.multiplyMM(finalMatrix,0,parentMatrix,0,translatorMatrix,0);
        /*float[] returnVec = new float[4];
        float[][] debug = new float[][] { {0,0,0},
                {1,0,0},
                {0,1,0},
                {0,0,1}};
        for (int i = 0;i<4;i++) {
            float[] currentVec = new float[] {debug[i][0],debug[i][1],debug[i][2],1};
            Matrix.multiplyMV(returnVec,0,finalMatrix,0,currentVec,0);
            Log.d("PLATEAU:", "CORD " + i  + " : " + Arrays.toString(returnVec));
        }*/
        //VISUAL_REPRESENTATION.draw(finalMatrix);

    }

    /** Updates the matrix
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     */
    @Override
    public void updateInstanceTransform(float[] blankInstanceInfo, float[] uMVPMatrix) {
        Matrix.multiplyMM(blankInstanceInfo,0,uMVPMatrix,0,translatorMatrix,0);

    }

    /** Loads the texture for all instances
     *
     * @param context used to access resources
     */
    public static void loadGLTexture(Context context) {
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
    /** Makes sure a character doesnt go inside the plateau, if it is, it moves it outside.
     *
     * @param character the character you are checking
     */
    public void clipCharacterPos(Enemy character) {
        this.clipCharacterEdge(character,this.points[0][0],this.points[0][1],this.points[1][0],this.points[1][1]);
        this.clipCharacterEdge(character,this.points[3][0],this.points[3][1],this.points[1][0],this.points[1][1]);
        this.clipCharacterEdge(character,this.points[0][0],this.points[0][1],this.points[2][0],this.points[2][1]);
        this.clipCharacterEdge(character,this.points[2][0],this.points[2][1],this.points[3][0],this.points[3][1]);

    }


    /** See if a circle intersects this polygon. Note if the circle is completely enclosed by the polygon
     *
     * @param x the center deltX of the circle
     * @param y the center y of the circle
     * @param r the radius
     * @return whether or not the two intersect
     */
    public boolean intersectsCircle(float x, float y, float r){
        return MathOps.segmentIntersectsCircle(x,y,r,this.points[0][0],this.points[0][1],this.points[1][0],this.points[1][1]) ||
                MathOps.segmentIntersectsCircle(x,y,r,this.points[3][0],this.points[3][1],this.points[1][0],this.points[1][1]) ||
                MathOps.segmentIntersectsCircle(x,y,r,this.points[0][0],this.points[0][1],this.points[2][0],this.points[2][1]) ||
                MathOps.segmentIntersectsCircle(x,y,r,this.points[2][0],this.points[2][1],this.points[3][0],this.points[3][1]);

    }


    /** Makes sure a character doesn't pass an edge, if its in it, it moves it outside.
     * Portions of code borrowed from: http://csharphelper.com/blog/2017/08/calculate-where-a-line-segment-and-an-ellipse-intersect-in-c/
     * The clipping part (finding new position of player) I made myself though.
     *
     * @param character the character in question
     * @param pt1x deltX coordinate of line segment point 1
     * @param pt1y y coordinate of line segment point 1
     * @param pt2x deltX coordinate of line segment point 2
     * @param pt2y y coordinate of line segment point 2
     */
    private void clipCharacterEdge(Enemy character, float pt1x, float pt1y, float pt2x, float pt2y){
        // Translate so the ellipse is centered at the origin.
        float cx = character.getDeltaX();
        float cy = character.getDeltaY();

        float pt1X = pt1x - cx;
        float pt1Y = pt1y - cy;
        float pt2X = pt2x - cx;
        float pt2Y = pt2y - cy;

        // Get the semi major and semi minor axes.
        float a = character.getRadius();
        float b = character.getRadius();

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
                //Log.d("PLATEAU","should be 1, is " + (Math.pow(deltX/scaleFactor,2)/(a*a) + Math.pow(y/scaleFactor,2)/(b*b)));

                float dX = (x/scaleFactor) - x;
                float dY = (y/scaleFactor) - y;


                character.translateFromPos(-dX,-dY);
                //deltX^2/a + y^2/b = s
            }

            //((-B - Math.sqrt(discriminant)) /(2*A) >= 0 && (-B - Math.sqrt(discriminant))/(2*A) <= 1);
        }
    }

    /** Makes sure a character doesn't pass an edge, if its in it, it moves it outside.
     * Portions of code borrowed from: http://csharphelper.com/blog/2017/08/calculate-where-a-line-segment-and-an-ellipse-intersect-in-c/
     * The clipping part (finding new position of player) I made myself though.
     *
     * @param character the character in question
     * @param pt1x deltX coordinate of line segment point 1
     * @param pt1y y coordinate of line segment point 1
     * @param pt2x deltX coordinate of line segment point 2
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
        float a = character.getRadius();
        float b = character.getRadius();

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
                //Log.d("PLATEAU","should be 1, is " + (Math.pow(deltX/scaleFactor,2)/(a*a) + Math.pow(y/scaleFactor,2)/(b*b)));

                float dX = (x/scaleFactor) - x;
                float dY = (y/scaleFactor) - y;


                character.translateFromPos(-dX,-dY);
                //deltX^2/a + y^2/b = s
            }

                    //((-B - Math.sqrt(discriminant)) /(2*A) >= 0 && (-B - Math.sqrt(discriminant))/(2*A) <= 1);
        }
    }

}



