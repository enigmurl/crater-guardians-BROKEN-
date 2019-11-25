package com.enigmadux.craterguardians;

/** Performs useful math operations
 * @author Manu Bhat
 * @version BETA
 */
public class MathOps {
    /** Gets the openGL textureBuffer
     *
     * @param rotation the rotation in degrees
     * @param frameNum the frame# to display in the animation
     * @param framesPerRotation in a single rotation, how many frames are there (the "width" of the texture)
     * @param numRotationOrientations how many different rotations are printed (the "height" of the texture)
     * @return the float[] that represents where to clip the frame
     */
    public static float[] getTextureBuffer(float rotation,int frameNum,float framesPerRotation,float numRotationOrientations){
        float x1 = (float) frameNum/framesPerRotation;
        float x2 = (float) (frameNum+1)/framesPerRotation;
        float y1 = (float) ((int) rotation/(int) (360f/numRotationOrientations))/numRotationOrientations;
        float y2 = (float) ((int) rotation/(int) (360f/numRotationOrientations) +1)/numRotationOrientations;

        return new float[] {
                x1,y2,
                x1,y1,
                x2,y2,
                x2,y1
        };
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

    /** Given three colinear points p, q, r, the function checks if point q lies on line segment 'pr'
     * Following three functions borrowed from https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
     *
     * @param x0 p1 x
     * @param y0 p1 y
     * @param x1 p2 x
     * @param y1 p2 y
     * @param x2 p3 x
     * @param y2 p3 y
     * @return if point q lies on line segment 'pr'
     */
    private static boolean onSegment(float x0,float y0,float x1,float y1,float x2,float y2) {
        return (x2 <= Math.max(x0, x1) && x2 >= Math.min(x0,x1) &&
                y1 <= Math.max(y0, y1) && y2 >= Math.min(y0, y1));
    }

    /** Gets the orientation of colinear = 0, clock = 1, counterclockwise = 2
     *
     * @param x0 p1 x
     * @param y0 p1 y
     * @param x1 p2 x
     * @param y1 p2 y
     * @param x2 p3 x
     * @param y2 p3 y
     * @return colinear clockwise or counterclockwise
     */
    private static int orientation(float x0,float y0,float x1,float y1,float x2,float y2) {
        double val = (y1 - y0) * (x2 - x1)
                - (x1 - x0) * (y2 - y1);

        if (val == 0)
            return 0; // colinear
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    /** sees whether two line segments intersect
     *
     * @param x00 line 1 point 1 x
     * @param y00 line 1 point 1 y
     * @param x10 line 1 point 2 x
     * @param y10 line 1 point 2 y
     * @param x01 line 2 point 1 x
     * @param y01 line 2 point 1 y
     * @param x11 line 2 point 2 x
     * @param y11 line 2 point 2 y
     * @return whether or not the two intersect
     */
    public static boolean lineIntersectsLine(float x00,float y00,float x10,float y10,float x01,float y01,float x11,float y11) {

        int o1 = orientation(x00,y00,x10,y10,x01,y01);
        int o2 = orientation(x00,y00,x10,y10,x11,y11);
        int o3 = orientation(x01,y01,x11,y11,x00,y00);
        int o4 = orientation(x01,y01,x11,y11,x10,y10);

        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(x00,y00, x10,y10,x01,y01)) return true;

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(x00,y00, x10,y10, x11,y11)) return true;

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(x01,y01, x11,y11,x00,y00)) return true;

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        return (o4 == 0 && onSegment(x01,y01,x11,y11,x10,y10));
    }

    /** Gets the t value where x = t(x10 - x00) + x00 y = t(y10 - y00) + y00 where (x,y) is the intersection point of the two lines
     * even if 0<t<1, there may not be an intersection because it may be only on line 1 but not line segment 2, so use the lineIntersectsLineFunction
     * https://stackoverflow.com/a/1968345/10030086, todo look at "Qwertie"'s optimizations and implement them
     *
     *
     * @param x00 line 1 point 1 x
     * @param y00 line 1 point 1 y
     * @param x10 line 1 point 2 x
     * @param y10 line 1 point 2 y
     * @param x01 line 2 point 1 x
     * @param y01 line 2 point 1 y
     * @param x11 line 2 point 2 x
     * @param y11 line 2 point 2 y
     * @return  Gets the t value where x = t(x10 - x00) + x00 y = t(y10 - y00) + y00 where (x,y) is the intersection point of the two lines, -1 if they don't intersect/are collinear
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
     * @param x center x coordinate of the circle
     * @param y center y coordinate of the circle
     * @param r the radius of circle
     * @param x0 p1 x
     * @param y0 p1 y
     * @param x1 p2 x
     * @param y1 p2 y
     * @return if the line segment intersects the circle (or if it is fully enclosed by it
     */
    public static boolean segmentIntersectsCircle(float x,float y,float r,float x0,float y0,float x1,float y1){
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
    /** Converts android canvas x coordinate into openGL coordinate
     *
     * @param x android canvas x coordinate
     * @return openGL x coordinate equivalent of (x)
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