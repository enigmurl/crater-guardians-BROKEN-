package com.enigmadux.craterguardians;

import com.enigmadux.craterguardians.Spawners.CubicSolver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CubicTester {
    private static final float EPSIOLON = 0.1f;

    @Test
    public void cubicTest1() {
        CubicSolver cubicSolver = new CubicSolver(
                1.72f,3.52f,
                -1.36f,3.2f,
                -0.5f,1,
                2.94f,2.56f
                );



        assertTrue("x0",3.52f == cubicSolver.interpelate(1.72f));
        assertTrue("x1",3.2f == cubicSolver.interpelate(-1.36f));
        assertTrue("x2",1f == cubicSolver.interpelate(-0.5f));
        assertTrue("x3",2.56f == cubicSolver.interpelate(2.94f));
        assertTrue("t0",withinRange(1.01446156238f, cubicSolver.interpelate(0)));
        assertTrue("t1",withinRange(-397310.415061f, cubicSolver.interpelate(100)));

    }


    public boolean withinRange(float expected,float actual){
        return Math.abs(expected-actual) < EPSIOLON;
    }
}