package com.enigmadux.craterguardians.util;

import com.enigmadux.craterguardians.Character;
import com.enigmadux.craterguardians.values.LayoutConsts;

/** Performs useful math operations
 * @author Manu Bhat
 * @version BETA
 */
public class MathOps {

    public static float sqrDist(float dx,float dy){
        return dx * dx + dy * dy;
    }
    //rad1 - rad2
    public static float radDist(float rad1,float rad2){
        float r1 = (float) ((rad1 % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI));
        float r2 = (float) ((rad2 % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI));
        float dist = (float) (((r1 - r2) + 2 * Math.PI) % (2 * Math.PI));
        float counter = (float) (dist - 2 * Math.PI);
        if (Math.abs(dist) < Math.abs(counter)){
            return dist;
        } else {
            return counter;
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
     * @return true if it had to clip it,false otherwie
     */
    public static boolean clipCharacterEdge(Character character, float pt1x, float pt1y, float pt2x, float pt2y) {
        // Translate so the ellipse is centered at the origin.
        float cx = character.getDeltaX();
        float cy = character.getDeltaY();

        float pt1X = pt1x - cx;
        float pt1Y = pt1y - cy;
        float pt2X = pt2x - cx;
        float pt2Y = pt2y - cy;

        // Get the semi major and semi minor axes
        float a = character.getRadius();
        float b = character.getRadius();

        // Calculate the quadratic parameters.
        float A = (pt2X - pt1X) * (pt2X - pt1X) / (a * a) +
                (pt2Y - pt1Y) * (pt2Y - pt1Y) / (b * b);
        float B = 2 * pt1X * (pt2X - pt1X) / (a * a) +
                2 * pt1Y * (pt2Y - pt1Y) / (b * b);
        float C = pt1X * pt1X / (a * a) + pt1Y * pt1Y / (b * b) - 1;


        // Calculate the discriminant.
        float discriminant = B * B - 4 * A * C;

        //Log.d("TAG","disc: " + discriminant);

        /*if (discriminant == 0){
            return -B/(2*A) >= 0 && -B/(2* A) <= 1;
        } else */ //Technically the tangent line does intersect, however for purposes of bounding box collisions it doesn't matter
        if (discriminant > 0) {
            float tValue1 = (float) (-B + Math.sqrt(discriminant)) / (2 * A); //||
            float tValue2 = (float) (-B - Math.sqrt(discriminant)) / (2 * A);


            float defaultTangentValue = (tValue1 + tValue2) / 2;
            float tangentTValue = Float.NEGATIVE_INFINITY;
            if (tValue1 >= 0 && tValue1 <= 1 && tValue2 >= 0 && tValue2 <= 1) {
                tangentTValue = defaultTangentValue;
            } else if ((tValue1 >= 0 && tValue1 <= 1)) {
                if (tValue2 > 1) {
                    tangentTValue = Math.min(1, defaultTangentValue);
                } else {
                    tangentTValue = Math.max(0, defaultTangentValue);
                }

            } else if ((tValue2 >= 0 && tValue2 <= 1)) {
                if (tValue1 > 1) {
                    tangentTValue = Math.min(1, defaultTangentValue);
                } else {
                    tangentTValue = Math.max(0, defaultTangentValue);
                }

            }

            if (tangentTValue != Float.NEGATIVE_INFINITY) {
                float x = pt1X + tangentTValue * (pt2X - pt1X);
                float y = pt1Y + tangentTValue * (pt2Y - pt1Y);

                float scaleFactor = (float) Math.sqrt(x * x / (a * a) + y * y / (b * b));

                //Log.d("PLATEAU","t value1 " + tValue1 + " tvalue 2 " + tValue2 + " tangent T " + tangentTValue + " scale " + scaleFactor);
                //Log.d("PLATEAU","should be 1, is " + (Math.pow(deltX/scaleFactor,2)/(a*a) + Math.pow(y/scaleFactor,2)/(b*b)));

                float dX = (x / scaleFactor) - x;
                float dY = (y / scaleFactor) - y;


                character.translateFromPos(-dX, -dY);
                return true;
                //deltX^2/a + y^2/b = s
            }

            //((-B - Math.sqrt(discriminant)) /(2*A) >= 0 && (-B - Math.sqrt(discriminant))/(2*A) <= 1);
        }
        return false;
    }


    /** Sees if a point is in circle
     *
     */
    public static boolean pointInCircle(float x,float y,float x0,float y0,float r0){
        return Math.hypot(x0 - x,y0 - y) < r0;
    }
    /** Clips the value to the range specified
     *
     * @param val value to be clipped
     * @param min min clip
     * @param max max clip (must be larger than or equal to min)
     * @return clipped val
     */
    public static float clip(float val, float min,float max){
        if (val > max){
            return max;
        } else if (val < min) {
            return min;
        } else {
            return val;
        }
    }
    /** Clips the value to the range specified
     *
     * @param val value to be clipped
     * @param min min clip
     * @param max max clip (must be larger than or equal to min)
     * @return clipped val
     */
    public static int clip(int val, int min,int max){
        if (val > max){
            return max;
        } else if (val < min) {
            return min;
        } else {
            return val;
        }
    }

    /**
     * Rather than re initing a whole buffer, this returns a deltX translation
     * IT IS ASSUMED THAT THE STARTING FRAME FOR THE TEXTURE BUFFER IS AT THE TOP LEFT CORNER
     *
     * @param frameNum                the frame# to display in the animation
     * @param framesPerRotation       in a single rotation, how many frames are there (the "width" of the texture)
     * @return gets the deltX delta position
     */
    public static float getTextureBufferTranslationX(int frameNum, float framesPerRotation) {
        return (float) frameNum / framesPerRotation;
    }

    /*** Rather than re initing a whole buffer, this returns a y translation
     * IT IS ASSUMED THAT THE STARTING FRAME FOR THE TEXTURE BUFFER IS AT THE TOP LEFT CORNER
     *
     * @param rotation the rotation in degrees
     * @param numRotationOrientations the amount of different rotation (the "height"/num rows of the texture)
     * @return the y delta position
     */
    public static float getTextureBufferTranslationY(float rotation, int numRotationOrientations) {
        return (float) ((int) rotation / (int) (360f / numRotationOrientations) + 1) / numRotationOrientations - 1;
    }

    /** In addition to having multiple rotations, the image is also turned a bit, this calculates how much
     *
     * @param rotation the rotation in degrees
     * @param numRotationOrientations how many different rotations are printed (the "height" of the texture)
     * @return how much to rotate the image in degrees
     */
    public static float getOffsetDegrees(float rotation,float numRotationOrientations){
        return rotation % (360f/numRotationOrientations);
    }



    /** sees whether two line segments intersect
     *
     * @param x00 line 1 point 1 deltX
     * @param y00 line 1 point 1 y
     * @param x10 line 1 point 2 deltX
     * @param y10 line 1 point 2 y
     * @param x01 line 2 point 1 deltX
     * @param y01 line 2 point 1 y
     * @param x11 line 2 point 2 deltX
     * @param y11 line 2 point 2 y
     * @return whether or not the two intersect
     */
    public static boolean lineIntersectsLine(float x00,float y00,float x10,float y10,float x01,float y01,float x11,float y11) {
        float cmpX = x01 - x00;
        float cmpy = y01 - y00;
        float rx = x10 - x00;
        float ry = y10 - y00;
        float sx = x11 - x01;
        float sy = y11 - y01;

        float cmPxR = cmpX * ry - cmpy * rx;
        float cmPxS = cmpX * sy - cmpy * sx;
        float RxS = rx * sy - ry * sx;

        //colinear
        if (cmPxR == 0){
            return ((x01 - x00 < 0f) != (x01 - x10 < 0f))
                    || ((y01 - y00 < 0f) != (y01 - y00 < 0f));
        }
        //paralel
        if (RxS == 0) return false;

        float rxsr = 1f / RxS;
        float t = cmPxS * rxsr;
        float u = cmPxR * rxsr;

        return (t >= 0f) && (t <= 1f) && (u >= 0f) && (u <= 1f);
    }



    /** Gets the t value where deltX = t(x10 - x00) + x00 y = t(y10 - y00) + y00 where (deltX,y) is the intersection point of the two lines
     * even if 0<t<1, there may not be an intersection because it may be only on line 1 but not line segment 2, so use the lineIntersectsLineFunction
     * https://stackoverflow.com/a/1968345/10030086, todo look at "Qwertie"'s optimizations and implement them
     *
     *
     * @param x00 line 1 point 1 deltX
     * @param y00 line 1 point 1 y
     * @param x10 line 1 point 2 deltX
     * @param y10 line 1 point 2 y
     * @param x01 line 2 point 1 deltX
     * @param y01 line 2 point 1 y
     * @param x11 line 2 point 2 deltX
     * @param y11 line 2 point 2 y
     * @return  Gets the t value where deltX = t(x10 - x00) + x00 y = t(y10 - y00) + y00 where (deltX,y) is the intersection point of the two lines, -1 if they don't intersect/are collinear
     */
    public static float tValueSegmentIntersection(float x00,float y00,float x10,float y10,float x01,float y01,float x11,float y11){
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = x10 - x00;
        s1_y = y10 - y00;
        s2_x = x11 - x01;
        s2_y = y11 - y01;

        float divisor = (-s2_x * s1_y + s1_x * s2_y);
        float t;//,s;

        if (divisor == 0){
            return -1;
        }

        //s = (-s1_y * (x00 - x01) + s1_x * (y00 - y01)) / divisor;
        t = ( s2_x * (y00 - y01) - s2_y * (x00 - x01)) / divisor;

        return t;

    }

    /** Given a circle and line segment, see if they intersect. Algorithm borrowed from https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
     *
     * @param x center deltX coordinate of the circle
     * @param y center y coordinate of the circle
     * @param r the radius of circle
     * @param x0 p1 deltX
     * @param y0 p1 y
     * @param x1 p2 deltX
     * @param y1 p2 y
     * @return if the line segment intersects the circle (or if it is fully enclosed by it
     */
    public static boolean segmentIntersectsCircle(float x,float y,float r,float x0,float y0,float x1,float y1){
        //if the point is inside
        float dX1 = x0 - x;
        float dY1 = y0 - y;
        if (dX1 * dX1 + dY1*dY1 < r*r ){
            return true;
        }
        //if the point 2i sinside
        float dX2 = x1 - x;
        float dY2 = y1 - y;
        if (dX2 * dX2  + dY2*dY2 < r* r){
            return  true;
        }

        float vu = ((x1-x0) * (x0 - x))  + ((y1 - y0) * (y0 - y));
        float vv  = (x1-x0) * (x1-x0)  + (y1-y0) * (y1-y0);

        float t = -vu/vv;
        if (t < 0){
            t = 0;
        } else if (t > 1){
            t = 1;
        }

        float deltaX = t * (x1 - x0) + x0 - x;
        float deltaY = t * (y1 - y0) + y0 - y;

        return Math.hypot(deltaX,deltaY) < r;
    }

    /** Given a Point specified by px, py, see if it inside the rectangle (axis aligned or rotated)/  ALGO from https://stackoverflow.com/questions/17136084/checking-if-a-point-is-inside-a-rotated-rectangle
     *  ABCD should be specified like so
     *   A_____B
     *   |     |
     *   |_____|
     *   C      D
     * @param px target point deltX
     * @param py target point y
     * @param ax corner 1 deltX
     * @param ay corner 1 y
     * @param bx corner 2 deltX
     * @param by corner 2 y
     * @param cx corner 3 deltX
     * @param cy corner 3 y
     * @param dx corner 4 deltX
     * @param dy corner 4 y
     * @return whether or not the point is inside the rectangle
     */
    public static boolean pointInRect(float px,float py,float ax,float ay,float bx,float by,float cx,float cy,float dx,float dy){
        double rectArea = Math.hypot(bx-ax,by-ay) * Math.hypot(cx - ax,cy - ay);

        double apd = MathOps.triangleArea(ax,ay,px,py,dx,dy);
        double dpc = MathOps.triangleArea(dx,dy,px,py,cx,cy);
        double cpb = MathOps.triangleArea(cx,cy,px,py,bx,by);
        double pba = MathOps.triangleArea(px,py,bx,by,ax,ay);


        return rectArea >= apd + dpc + cpb + pba;
    }

    /** Given a triangle specified by a,b,c caclulate the area
     *
     * @param ax p1 deltX
     * @param ay p1 y
     * @param bx p2 deltX
     * @param by p2 y
     * @param cx p3 deltX
     * @param cy p3 y
     * @return the area of the triangle
     */
    public static double triangleArea(float ax,float ay, float bx,float by,float cx,float cy){
        return Math.abs( ax * (by - cy)  + bx * (cy - ay) + cx * (ay -by) ) / 2;
    }




    /** Given the sin and cosine, it can compute the angle, not limited to any quadrant
     *
     * @param cos cosine (value between -1,1) Sin^2 + cos^2 should be equal to 1
     * @param sin the sine (value between -1,1)
     * @return the angle at which both the sin and cosine values are satisfied in radians
     */
    public static float getAngle(float cos, float sin){
        if (sin > 0 ){
            return (float) Math.acos(cos);
        }
        return (float) (2 * Math.PI - Math.acos(cos));
    }

    /** Given a number input, returns the first power of two to be higher or equal to it
     * retrieved from https://www.geeksforgeeks.org/smallest-power-of-2-greater-than-or-equal-to-n/
     *
     * @param n the minimum threshold of the output
     * @return the first integer power of two to be greater than or equal to input
     */
    public static int nextPowerTwo(int n){
        int count = 0;

        // First n in the below
        // condition is for the
        // case where n is 0
        if (n > 0 && (n & (n - 1)) == 0)
            return n;

        while(n != 0)
        {
            n >>= 1;
            count += 1;
        }

        return 1 << count;
    }
    /** Converts android canvas deltX coordinate into openGL coordinate
     *
     * @param x android canvas deltX coordinate
     * @return openGL deltX coordinate equivalent of (deltX)
     */
    public static float getOpenGLX(float x){
        return  2* (x-(float) (LayoutConsts.SCREEN_WIDTH)/2) /( LayoutConsts.SCREEN_WIDTH);
    }

    /** Converts android canvas y coordinate into openGL coordinate
     *
     * @param y android canvas y coordinate
     * @return openGL y coordinate equivalent of (y)
     */
    public static float getOpenGLY(float y){
        return  2* (-y+(float) LayoutConsts.SCREEN_HEIGHT/2)/(LayoutConsts.SCREEN_HEIGHT);
    }
}
