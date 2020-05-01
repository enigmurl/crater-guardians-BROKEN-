package com.enigmadux.craterguardians;

import com.enigmadux.craterguardians.util.MathOps;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Example local unit joystick_icon, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MathOpsTester {
    @Test
    public void lineCheck(){
        float[][] points = new float[][] {{-3.3f, 0.0f}, {0.0f, -3.9f}, {-2.9f, 0.0f}, {0.0f, -3.1f}};
        EnemyMap.Node c0 = new EnemyMap.Node(0.0f,-0.50f);
        EnemyMap.Node c1 = new EnemyMap.Node(0.0f,-0.50f);
        EnemyMap.Node c2 = new EnemyMap.Node(-0.25f,-0.18f);
        EnemyMap.Node c3 = new EnemyMap.Node(-0.25f,-0.18f);

        assertFalse((MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[0][0],points[0][1],points[1][0],points[1][1])));
        assertFalse(MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[3][0],points[3][1],points[1][0],points[1][1]));
        assertFalse(MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[0][0],points[0][1],points[2][0],points[2][1]));
        assertFalse(MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[2][0],points[2][1],points[3][0],points[3][1]));
        assertFalse(MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[0][0],points[0][1],points[1][0],points[1][1]));
        assertFalse(MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[3][0],points[3][1],points[1][0],points[1][1]));
        assertFalse(MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[0][0],points[0][1],points[2][0],points[2][1]));
        assertFalse(MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[2][0],points[2][1],points[3][0],points[3][1]));

        assertTrue(MathOps.lineIntersectsLine(0,10,2,0,10,0,0,5));

        assertFalse(MathOps.lineIntersectsLine(0,0,2,8,8,0,0,20));
        assertFalse(MathOps.lineIntersectsLine(0,0,0,10,2,0,2,10));
        assertFalse(MathOps.lineIntersectsLine(0,0,5,5,2,0,7,5));
        assertTrue(MathOps.lineIntersectsLine(0,0,5,5,2,2,7,7));
        assertFalse(MathOps.lineIntersectsLine(0,0,5,5,7,7,10,10));

    }

    @Test
    public void radCheck(){
        float r1 = -0.00000009f;
        float r10 = (float) (2 * Math.PI + r1);
        float r2 = 0.0001f;

        float a1 = MathOps.radDist(r1,r2);
        assertTrue( a1 < 0);
        assertTrue(MathOps.radDist(r10,r2) < 0);
        assertEquals(0, MathOps.radDist(r1, r10), 0.1);

        r1 = 0;
        r2 = (float) (- 5 * Math.PI/4);
        assertEquals(-3 * Math.PI/4,MathOps.radDist(r1,r2),0.1);
        r1 = 5.711132f;
        r2 = 1.5632614f;
        a1 = MathOps.radDist(r1,r2);
        assertEquals(-2.135314707f,a1,0.1);
    }

    @Test
    public void lineIntersectsCircle(){
        float x = 3;
        float y = 2;
        float r = 2;
        assertTrue(MathOps.segmentIntersectsCircle(x,y,r,1,3,6,2));
        assertFalse(MathOps.segmentIntersectsCircle(x,y,r,1,3,4,6));
    }

}
