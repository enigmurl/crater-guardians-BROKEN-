package com.enigmadux.craterguardians.animations;

import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.util.FloatPoint;

import java.util.LinkedList;

public class ScreenShake extends FrameTransitionAnim {
    //millis if 100% of the player is killed
    private static final long MAX_MILLIS = 1500;
    //the amount of points per milliseconds
    private static final float POINTS_PER_MILLIS = 35f/2000;
    //max radius if the damage was 100%
    private static final float MAX_RADIUS = 0.6f;

    private World world;


    private LinkedList<FloatPoint> pointQueue;
    private int orgSize;


    private boolean finished = true;

    //private FloatPoint prevNode;
    public ScreenShake(float damagePercent, World world) {
        super((long) (damagePercent * MAX_MILLIS));

        this.pointQueue = new LinkedList<>();
        int numPoints = Math.max(1,(int) (this.totalMillis * POINTS_PER_MILLIS));
        this.pointQueue = ScreenShake.getRandomShake(MAX_RADIUS*damagePercent,numPoints, 0, 0);
        orgSize = pointQueue.size();
        this.world  =world;
        this.inGameAnim = true;

        start();
    }

    @Override
    void step() {
        int index = Math.min((int) (finishedMillis * (orgSize-1)/totalMillis),orgSize - 2) + 1;
        //todod this is inefficient linked List O(N) get
        float denom = (totalMillis/(float) (orgSize - 1));
        float x = (finishedMillis % denom)/denom * (pointQueue.get(index).x - pointQueue.get(index-1).x) + pointQueue.get(index-1).x;
        float y = (finishedMillis % denom)/denom * (pointQueue.get(index).y - pointQueue.get(index-1).y) + pointQueue.get(index-1).y;

        world.setCameraDelta(x,y);



    }

    @Override
    public void cancel() {
        //CANNOT BE CANCELLED
        this.finished = true;
    }

    @Override
    void finish() {
        super.finish();
        world.setCameraDelta(0,0);
        this.finished = true;
    }


    public static LinkedList<FloatPoint> getRandomShake(float maxRadius, int numPoints, float startX, float startY){
        LinkedList<FloatPoint> returnList = new LinkedList<>();
        returnList.add(new FloatPoint(startX,startY));
        for (int i = 0;i < numPoints;i++){
            returnList.add(new FloatPoint(Math.random() * maxRadius  + startX,Math.random() * maxRadius + startY));
        }
        returnList.add(new FloatPoint(startX,startY));
        return returnList;
    }

    @Override
    public boolean isFinished() {
        return super.isFinished() || finished;
    }
}
