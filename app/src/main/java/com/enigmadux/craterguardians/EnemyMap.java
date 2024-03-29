package com.enigmadux.craterguardians;

import android.util.Log;

import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.GameObjects.Plateau;
import com.enigmadux.craterguardians.GameObjects.ToxicLake;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** This class provides information to an enemy about the current level so they can find out where to target
 *
 *
 *  Because of the complexity, it seems the easiest thing to do as of now is just hardcode enemy paths in level files saving time, and hard to come up with algorithm
 *  do a* from there
 */
public class EnemyMap {
    //to add a bit of leeway
    private static final float EPSILON = 0.1f;
    //the granularity of the first map
    private static final float GRANULARITY_1 = 0.3f;
    //the granularity of the second map
    private static final float GRANULARITY_2 = 0.6f;
    //the granularity of the third map
    private static final float GRANULARITY_3 = 1f;

    //used to prevent multiple threads accessing enemy map
    public static final Lock LOCK = new ReentrantLock();

    //each element of this array is itself a node, it tells the x and y and neighbours, first node is player, next N are supply, rest are intermediate
    private Node[] nodeMap;


    //Plateaus used for ray casting
    private List<Plateau> plateaus;
    //toxic lakes used for ray casting
    private List<ToxicLake> toxicLakes;


    private long startTime;
    private int calls;
    private long runTime;

    private long countRunTime;

    /** Default Constructor
     * @param plateaus all plateaus on the current level
     * @param toxicLakes all toxic lakes on the current level
     * @param nodes the current node maps
     */
    public EnemyMap (List<Plateau> plateaus, List<ToxicLake> toxicLakes, Node[] nodes){
        this.plateaus = plateaus;
        this.toxicLakes = toxicLakes;
        this.nodeMap = nodes;

        this.startTime = System.currentTimeMillis();
    }




    /** Given a line represented by two nodes, it sees if it's possible to traverse this line without intersecting the plateau
     *
     * TODO THIS IS NOT PERFECT RN, here are some future stuff to make it better: the penalty for toxic lakes should be added based on intersection length, also for plateaus the lines should
     *                              be extended so there is "width" paddin on both sides of the line. Additionally look for an optimizatioon for the plateau intersection, 8 checks is too much
     *
     *
     * @param node1 the starting point
     * @param node2 the ending point
     * @param width the width of the path
     * @return -1 if the line isn't possible otherwise, an integer representing the time it takes to traverse this specific node
     */
    private double isValid(Node node1,Node node2,float width){
        if (node1 == null || node2 == null) return -1;

        double weight = Math.hypot(node1.x-node2.x,node1.y-node2.y);
        /*  C0___C1
         *   | |  |
         * C2|_|_|C3
         */
        //left perpendicular
        float scalarL = (width/2)/(float) Math.hypot(node1.x-node2.x,node1.y-node2.y);

        //todo MIGTH BE CAUSING GARBAGE COLLECTO
        Node lP = new Node(-scalarL * (node1.y-node2.y),scalarL * (node1.x-node2.x));

        //rightperpendicular
        Node rP = new Node(-lP.x,-lP.y);

        Node c0 = new Node(node1.x + lP.x,node1.y + lP.y);
        Node c1 = new Node(node1.x + rP.x,node1.y + rP.y);
        Node c2 = new Node(node2.x + lP.x,node2.y + lP.y);
        Node c3 = new Node(node2.x + rP.x,node2.y + rP.y);


        //Log.d("ENEMYMAP:","node1: " + node1 + " node2: " + node2 + "c0: " + c0 + " c1: " + c1  + " c2 " + c2 + " c3: " + c3);

        for (int i = 0,size = this.plateaus.size();i<size;i++){
            Plateau plateau = this.plateaus.get(i);
            float[][] points = plateau.getPoints();

            /*testing this later
            //NOTE to prevent overfitting, what we do is instead of all the times the "rect" formed c0,1,2,3 intersects the plat
            //this is more efficient, and makes it more suited for what we need

            if (MathOps.pointInRect(points[0][0],points[0][1],c0.x,c0.y,c1.x,c1.y,c2.x,c2.y,c3.x,c3.y) ||
                    MathOps.pointInRect(points[1][0],points[1][1],c0.x,c0.y,c1.x,c1.y,c2.x,c2.y,c3.x,c3.y) ||
                    MathOps.pointInRect(points[2][0],points[2][1],c0.x,c0.y,c1.x,c1.y,c2.x,c2.y,c3.x,c3.y) ||
                    MathOps.pointInRect(points[3][0],points[3][1],c0.x,c0.y,c1.x,c1.y,c2.x,c2.y,c3.x,c3.y)) return -1;
            */
            if (MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[0][0],points[0][1],points[1][0],points[1][1]) ||
                    MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[3][0],points[3][1],points[1][0],points[1][1]) ||
                    MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[0][0],points[0][1],points[2][0],points[2][1]) ||
                    MathOps.lineIntersectsLine(c0.x,c0.y,c2.x,c2.y,points[2][0],points[2][1],points[3][0],points[3][1]) ||
                    MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[0][0],points[0][1],points[1][0],points[1][1]) ||
                    MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[3][0],points[3][1],points[1][0],points[1][1]) ||
                    MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[0][0],points[0][1],points[2][0],points[2][1]) ||
                    MathOps.lineIntersectsLine(c1.x,c1.y,c3.x,c3.y,points[2][0],points[2][1],points[3][0],points[3][1])) return -1;

        }
        for (int i = 0,size = this.toxicLakes.size();i<size;i++){
            ToxicLake toxicLake = this.toxicLakes.get(i);
            float x = toxicLake.getX();
            float y = toxicLake.getY();
            float r = toxicLake.getWidth()/2;


            if (MathOps.segmentIntersectsCircle(x,y,r,node1.x,node1.y,node2.x,node2.y)) {
                //see what to do, but basically increase the weightage

                //this.map1Nodes[i] = null;
                weight += r; 
            }

        }



        return weight;
    }
//
//    /*
//    private void createMap1(){
//        //first create the nodes 4 for each toxic lake, 4 for each plateau
//        //see every connection it can make with every other node, if it's possible
//        //add it to neighbours
//
//        this.map1Nodes = new Node[1 + supplies.size() + 4 * (this.plateaus.size() + this.toxicLakes.size())];
//
//        int offset = 1;
//        for (int i = 0;i<this.supplies.size();i++){
//            this.map1Nodes[offset] = new Node(this.supplies.get(i).getX(),this.supplies.get(i).getX());
//            offset++;
//        }
//
//        float length = GRANULARITY_1 + EPSILON;
//
//        for (int i = 0;i<this.toxicLakes.size();i++){
//            //the center x and y, plus the radius
//            float x = this.toxicLakes.get(i).getX();
//            float y = this.toxicLakes.get(i).getY();
//            float r = this.toxicLakes.get(i).getWidth()/2;
//
//            this.map1Nodes[offset] = new Node(x,y + r + length);
//            this.map1Nodes[offset+1] = new Node(x,y - r - length);
//            this.map1Nodes[offset+2] = new Node(x - r - length,y);
//            this.map1Nodes[offset+3] = new Node(x + r +length,y);
//
//
//            offset += 4;
//        }
//
//
//        for (int i = 0;i<this.plateaus.size();i++){
//            float[][] unBoundPoints = this.plateaus.get(i).getPoints();
//            *//*  C1___C3
//             *   |   |
//             * C0|___|C2
//             *//*
//            float x0 = unBoundPoints[0][0];
//            float y0 = unBoundPoints[0][1];
//            float x1 = unBoundPoints[1][0];
//            float y1 = unBoundPoints[1][1];
//            float x2 = unBoundPoints[2][0];
//            float y2 = unBoundPoints[2][1];
//            float x3 = unBoundPoints[3][0];
//            float y3 = unBoundPoints[3][1];
//
//            this.map1Nodes[offset]   = this.getPlateauNode(x0,y0,x3,y3,x1,y1,x2,y2,length);
//            this.map1Nodes[offset+1] = this.getPlateauNode(x3,y3,x0,y0,x1,y1,x2,y2,length);
//            this.map1Nodes[offset+2] = this.getPlateauNode(x1,y1,x2,y2,x3,y3,x0,y0,length);
//            this.map1Nodes[offset+3] = this.getPlateauNode(x2,y2,x1,y1,x3,y3,x0,y0,length);
//            offset += 4;
//        }
//
//
//
//
//        *//* No snipping for now as it's expensive and overactive
//        for (Plateau plateau:this.plateaus){
//            for (ToxicLake toxicLake:this.toxicLakes){
//                for (int i = this.supplies.size();i<this.map1Nodes.length;i++){
//                    Node node = this.map1Nodes[i];
//                    if (node == null) continue;
//                    if (Math.hypot(toxicLake.getX() - node.x,toxicLake.getY() - node.y) < toxicLake.getWidth()/2 + length){
//                        this.map1Nodes[i] = null;
//                        continue;
//                    }
//
//                    if (plateau.intersectsCircle(node.x,node.y,length)){
//                        this.map1Nodes[i] = null;
//                    }
//
//                }
//
//            }
//        }
//        *//*
//        for (int i = 1;i<this.map1Nodes.length;i++){
//            if (this.map1Nodes[i] != null && Math.hypot(this.map1Nodes[i].x,this.map1Nodes[i].y) > this.radius) this.map1Nodes[i] = null;
//        }
//
//        for (int i = 0;i<this.map1Nodes.length;i++){
//            for (int j = i+1;j<this.map1Nodes.length;j++){
//                int weightage = this.isValid(map1Nodes[i],map1Nodes[j],GRANULARITY_1);
//                if (weightage != -1){
//                    map1Nodes[i].addNeighbour(map1Nodes[j],weightage);
//                    map1Nodes[j].addNeighbour(map1Nodes[i],weightage);
//                }
//            }
//        }
//
//        Log.d("ENEMY MAP:","Completed");
//
//    }
//
//    *//** Given points on plateau it calculates where the
//     *
//     * @param x0 center x
//     * @param y0 center y
//     * @param x1 opposite x
//     * @param y1 opposite y
//     * @param x2 left x
//     * @param y2 left y
//     * @param x3 right x
//     * @param y3 right y
//     * @param length  the minimum distance away the node should be
//     * @return a Node of where it should be based on parameters so that a  node outside of the plateau by a cleareence of length
//     *//*
//    private Node getPlateauNode(float x0,float y0,float x1,float y1,float x2,float y2,float x3,float y3,float length){
//        //centering the coordinates
//        x1 -= x0;
//        x2 -= x0;
//        x3 -= x0;
//        y1 -= y0;
//        y2 -= y0;
//        y3 -= y0;
//
//        float[] scaledVec = this.bisector(x2,y2,x3,y3);
//        float scalar = length/(float) Math.hypot(scaledVec[0],scaledVec[1]);
//
//        if (Math.hypot(x1,y1) > Math.hypot(x1-scaledVec[0],y1-scaledVec[1])){
//            scalar = -scalar;
//
//
//        }
//        scaledVec[0] *= scalar;
//        scaledVec[1] *= scalar;
//
//        return new Node(scaledVec[0] + x0,scaledVec[1] + y0);
//
//    }
//
//    private float[] bisector(float x0,float y0,float x1,float y1) {
//        float hypot1 = (float) Math.hypot(x0,y0);
//        float hypot2 = (float) Math.hypot(x1,y1);
//
//        return new float[] {hypot2 * x0 + hypot1*x1,hypot2 * y0 + hypot1*y1};
//    }
//
//
//    private void createMap2(){
//        //first create the nodes
//        //see every connection it can make with every other node, if it's possible
//        //add it to neighbours
//    }
//
//    private void createMap3(){
//        //first create the nodes
//        //see every connection it can make with every other node, if it's possible
//        //add it to neighbours
//    }
//    */

    /** Each frame the player's position needs to be updated once, as opposed to finding it through each enemy
     *
     * @param player the current game player
     */
    public void updatePlayerPosition(Player player){
        //see every connection it can make with every other node, if it's possible
        //add it to neighbours
        if (this.nodeMap[0] != null) this.nodeMap[0].reset();

        Node playerNode = new Node(player.getDeltaX(),player.getDeltaY());

        for (int i = 1;i<this.nodeMap.length;i++){
            //0 for now todo
            float weightage = (float) this.isValid(playerNode,nodeMap[i],0);
            if (weightage != -1){
                nodeMap[i].addNeighbour(playerNode,weightage);
                playerNode.addNeighbour(nodeMap[i],weightage);
            }
        }


        this.nodeMap[0] = playerNode;



    }

    /** Returns a List<Node> that represents the path it must take ALGO from: https://www.youtube.com/watch?v=mZfyt03LDH4
     *
     * @param radius the radius of the character
     * @param currentX the starting x
     * @param currentY the starting y
     * @param supplyIndex -1 if you're targeting the player, otherwise the index of the supply you want
     * @return a path of nodes that represent the path it must take. NOTE if the last Node is null, that means that the last node is  the Player, and appropriate action can be taken from there
     */
    public List<Node> nextStepMap(float radius,float currentX,float currentY,int supplyIndex){
        //first get possibly nodes it can connect with through ray casting
        //get the weightages of each of those nodes
        long startTime = System.currentTimeMillis();
        calls ++;
        if (startTime - this.startTime > 20 * 1000){
            Log.d("PERCENTAGE TIME:", "TIME:" +  ((float) this.runTime/(startTime - this.startTime )) + "Average: ms" + (this.runTime/calls) + " calls: " + this.calls + " iinTime:" + this.countRunTime);
            this.startTime = startTime;
            calls = 0;
        }

        Node start = new Node(currentX,currentY);
        float targetX = this.nodeMap[supplyIndex + 1].x;
        float targetY = this.nodeMap[supplyIndex + 1].y;

        for (int i = 1;i<this.nodeMap.length;i++){
            //Log.d("ENEMY WEIGHT","NULL POINTER: " + (this.nodeMap[i] == null));

            float weightage = (float) this.isValid(start,nodeMap[i],GRANULARITY_1);
            //Log.d("ENEMY WEIGHT","weight: " + weightage + " length: "  +this.nodeMap.length);
            if (weightage != -1){
                nodeMap[i].addNeighbour(start,weightage);
                start.addNeighbour(nodeMap[i],weightage);
            }
        }

        //then use a* to find optimum path



        //open are the ones where we have explored their neighbours, but have not actually explored yet
        List<Node> open = new ArrayList<>();
        //nodes already explored
        List<Node> closed = new ArrayList<>();

        open.add(start);
        start.hCost = (float) Math.hypot(currentX - targetX,currentY - targetY);

        while (open.size() > 0){
            long inStart = System.currentTimeMillis();

            Node currentNode = open.get(0);
            for (int i = 1;i<open.size();i++){
                if (open.get(i).fCost() < currentNode.fCost() || (open.get(i).fCost() == currentNode.fCost() && open.get(i).hCost < currentNode.hCost)){
                    currentNode = open.get(i);
                }
            }
            this.countRunTime += System.currentTimeMillis() - inStart;
            //the plus 1 is to offset for the player node
            if (currentNode == nodeMap[supplyIndex + 1]){
                List<Node> path = computePath(currentNode,start);
                if (supplyIndex == -1){
                    path.set(path.size()-1,null);
                }

                runTime += System.currentTimeMillis() - startTime;

                return path;
            }

            open.remove(currentNode);
            closed.add(currentNode);

            for (int i = 0;i<currentNode.connections.size();i++){
                Node neighbour = currentNode.connections.get(i);
                if (closed.contains(neighbour) || neighbour.size > radius) continue;

                float neighbourGCost = currentNode.gCost + currentNode.weights.get(i);

                boolean notOpen = ! open.contains(neighbour);
                if (neighbourGCost < neighbour.gCost || notOpen){
                    neighbour.gCost = neighbourGCost;
                    neighbour.hCost = (float) Math.hypot(neighbour.x-targetX,neighbour.y-targetY);
                    neighbour.parentNode = currentNode;

                    if (notOpen){
                        open.add(neighbour);
                    }
                }
            }

        }


        //this makes it as if its pointing to the player as no suitable path was found
        List<Node> path = new ArrayList<Node>();
        path.add(null);
        path.add(null);
        runTime += System.currentTimeMillis() - this.startTime;

        Log.d("ENEMY MAP:" ,"Player Position:" + this.nodeMap[0] + " Enemy Pos: "  + start  + " player connections: " + this.nodeMap[0].connections +  " enemy connections " +  start.connections + " supply index: " + supplyIndex);

        return path;
    }

    /** After the path has been laid out, this retraces it and makes it into an array
     *
     * @param target the target node, must be the exact reference
     * @param start the starting node, also must be the exact reference
     * @return the path starting from the first node AFTER the starting position
     */
    private List<Node> computePath(Node target,Node start){
        List<Node> path = new ArrayList<>();

        while (target != start){
            path.add(0,target);
            target = target.parentNode;

        }
        return  path;
    }

    public static class Node {
        /** The distance from the start to this node*/
        public float gCost;
        /** The distance from the end to this node*/
        public float hCost;


        /** The node that points to this*/
        public Node parentNode;

        /** the x value of this node*/
        public float x;
        /** the y value of this node*/
        public float y;

        /** The "radius" of this Node. Anything withing the size is guranteed to have no plateaus.
         *
         */
        public float size;

        //this tells what it neighbours with
        private List<Node> connections = new ArrayList<>();
        //this tells for each connection how much time it will take
        private List<Float> weights = new ArrayList<>();

        //the x and y coorinates of this node
        public Node(float x,float y){
            this.x = x;
            this.y = y;
        }

        /** Adds a connection
         *
         * @param neighbour the actual neighbour
         * @param weightage how long it "takes" to travel, a high weightage means it's not that optimum path
         */
        public void addNeighbour(Node neighbour,float weightage){
            this.connections.add(neighbour);
            this.weights.add(weightage);
        }

        /** Clears the connections and weights
         *
         */
        public void reset(){
            this.connections.clear();
            this.weights.clear();
        }

        /** Gets the current cost of this node
         *
         * @return the g cost plus the h cost
         */
        public float fCost(){
            return this.gCost + this.hCost;
        }

        /** To string method used to display info to a gui
         *
         * @return a string representation
         */
        @Override
        public String toString(){
            return String.format(Locale.ENGLISH,"(%.2f , %.2f )",this.x,this.y);
        }
    }
    /** To string method used to display info to a gui
     *
     * @return a string representation
     */
    @Override
    public String toString(){
        String returnString = "[";
        for (Node node:this.nodeMap){
            if (node != null) {
                for (Node subNode:node.connections) {
                    returnString += "(" + node + "," + subNode + "),";
                }

                //returnString += node + ", ";
            }

        }
        return returnString + "]";
    }

}
