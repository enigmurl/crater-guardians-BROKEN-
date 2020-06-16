package com.enigmadux.craterguardians.spawners;


import java.util.Arrays;

/**https://en.wikipedia.org/wiki/Cubic_Hermite_spline
 *
 */
public class MultiSegCubicSolver {
    private float[] data;
    //should be in form of (x0,y0,slope,x1,y1,slope,x2,y3,slope...)
    //x0 < x1 < x2..
    public MultiSegCubicSolver(float... pairs){
        this.data = pairs;
    }

    public float interpolate(float x){
        ///technically could binary search, but this is small enough that it doesnt matter
        for (int i = 0;i < data.length/3 - 1;i++){
            int j = i + 1;
            if ( data[i * 3] <=x && data[j * 3] >= x){
                float t = (x - data[i * 3])/(data[j * 3] - data[i * 3]);
                float p0 = data[i * 3 + 1];
                float p1 = data[j * 3 + 1];
                float m0 = data[i * 3 + 2];
                float m1 = data[j * 3 + 2];
                float h00 = 2 * t * t *t - 3 * t *t  + 1;
                float h10 = t * t * t - 2  * t *t  + t;
                float h01 = -2 *t * t *t  + 3 * t * t;
                float h11 = t * t * t - t * t;
                return h00 * p0 + h10 * m0 + h01 * p1 + h11 * m1;
            }
        }
        //not in interval
        return -1;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
