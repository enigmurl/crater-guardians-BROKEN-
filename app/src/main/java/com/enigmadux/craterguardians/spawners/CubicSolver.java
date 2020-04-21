package com.enigmadux.craterguardians.spawners;

/** Solves cubics to make math easy for spawner calculations (Using Langrage's Formula via https://www.desmos.com/calculator/dffnj2jbow)
 *
 * Note, this will no longer be used because even if four points are all downwards, the slope
 * may become positive, I am instead switching to bicubic interpolation
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CubicSolver {


    /** The inputs and outputs
     *
     */
    private float x0;
    private float y0;

    private float x1;
    private float y1;

    private float x2;
    private float y2;

    private float x3;
    private float y3;
    /** Default constructor
     *
     * @param x0 input 0
     * @param y0 output 0
     * @param x1 input 1
     * @param y1 output 1
     * @param x2 input 2
     * @param y2 output 2
     * @param x3 input 3
     * @param y3 output 3
     */
    public CubicSolver(float x0,float y0,float x1,float y1,float x2,float y2,float x3,float y3){
        this.x0 = x0;
        this.y0 = y0;

        this.x1 = x1;
        this.y1 = y1;

        this.x2 = x2;
        this.y2 = y2;

        this.x3 = x3;
        this.y3 = y3;
    }


    /**Finds the interpeloated x value
     *
     * @param x the input value you want to find
     * @return f(x) given the inputs
     */
    public float interpelate(float x){
        //predict divide by zeros
        if (x == x0){
            return y0;
        } else if (x== x1){
            return y1;
        } else if (x == x2){
            return y2;
        } else if (x == x3){
            return y3;
        }


        return p0(x)*y0/p0(x0) + p1(x)*y1/p1(x1) +  p2(x)*y2/p2(x2) + p3(x)*y3/p3(x3);
    }

    /** Partial function
     *
     * @param x the input value
     * @return the partial function
     */
    private float p0(float x){
        return (x- x1) * (x-x2) * (x-x3);
    }

    /** Partial function
     *
     * @param x the input value
     * @return the partial function
     */
    private float p1(float x){
        return (x- x0) * (x-x2) * (x-x3);
    }

    /** Partial function
     *
     * @param x the input value
     * @return the partial function
     */
    private float p2(float x){
        return (x- x1) * (x-x0) * (x-x3);
    }

    /** Partial function
     *
     * @param x the input value
     * @return the partial function
     */
    private float p3(float x){
        return (x- x1) * (x-x2) * (x-x0);
    }





}
