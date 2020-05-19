package com.enigmadux.craterguardians;

import android.util.Log;

import com.enigmadux.craterguardians.spawners.CubicSolver;
import com.enigmadux.craterguardians.spawners.MultiSegCubicSolver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit joystick_icon, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CubicSegmentTester {

    @Test
    public void cubicTest1() {
        MultiSegCubicSolver multiSegCubicSolver = new MultiSegCubicSolver(
                0,5000,0,
                37000,2700,-0.03968254f,
                63000,2500,-0.048214287f,
                93000,0,0
        );



        assertEquals(0,multiSegCubicSolver.interpolate(93000),0.1);
        assertEquals(2500,multiSegCubicSolver.interpolate(63000),0.1);
        assertEquals(2700,multiSegCubicSolver.interpolate(37000),0.1);
        assertEquals(2501.22,multiSegCubicSolver.interpolate(61810),0.1);
        assertEquals(5000,multiSegCubicSolver.interpolate(0),0.1);
        assertEquals(63000,getTime(multiSegCubicSolver,2500,93000),20);
        assertEquals(0,getTime(multiSegCubicSolver,5000,93000),20);
        assertEquals(37000,getTime(multiSegCubicSolver,2700,93000),20);


    }


    private long getTime(MultiSegCubicSolver healthFunction,int health,float end){
        float rhs = end;
        float lhs = 0;

        while (lhs + 0.01f  < rhs){
            float mid = (rhs + lhs)/2;
            if (healthFunction.interpolate(mid) < health){
                rhs = mid;
            } else {
                lhs = mid;
            }
        }

        return (long) lhs;

    }
}