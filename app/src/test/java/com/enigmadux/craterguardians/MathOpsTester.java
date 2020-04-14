package com.enigmadux.craterguardians;

import com.enigmadux.craterguardians.util.MathOps;

import org.junit.Test;

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

        assertFalse((MathOps.lineIntersection(c0.x,c0.y,c2.x,c2.y,points[0][0],points[0][1],points[1][0],points[1][1])));
        assertFalse(MathOps.lineIntersection(c0.x,c0.y,c2.x,c2.y,points[3][0],points[3][1],points[1][0],points[1][1]));
        assertFalse(MathOps.lineIntersection(c0.x,c0.y,c2.x,c2.y,points[0][0],points[0][1],points[2][0],points[2][1]));
        assertFalse(MathOps.lineIntersection(c0.x,c0.y,c2.x,c2.y,points[2][0],points[2][1],points[3][0],points[3][1]));
        assertFalse(MathOps.lineIntersection(c1.x,c1.y,c3.x,c3.y,points[0][0],points[0][1],points[1][0],points[1][1]));
        assertFalse(MathOps.lineIntersection(c1.x,c1.y,c3.x,c3.y,points[3][0],points[3][1],points[1][0],points[1][1]));
        assertFalse(MathOps.lineIntersection(c1.x,c1.y,c3.x,c3.y,points[0][0],points[0][1],points[2][0],points[2][1]));
        assertFalse(MathOps.lineIntersection(c1.x,c1.y,c3.x,c3.y,points[2][0],points[2][1],points[3][0],points[3][1]));

        assertTrue(MathOps.lineIntersection(0,10,2,0,10,0,0,5));
    }
}
