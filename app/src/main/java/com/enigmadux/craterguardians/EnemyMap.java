package com.enigmadux.craterguardians;

import android.util.Log;


import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.gameobjects.Plateau;
import com.enigmadux.craterguardians.gameobjects.ToxicLake;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.util.FloatPoint;
import com.enigmadux.craterguardians.util.MathOps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

/** This class provides information to an enemy about the current level so they can find out where to target
 *
 *
 *  Because of the complexity, it seems the easiest thing to do as of now is just hardcode enemy paths in level files saving time, and hard to come up with algorithm
 *  do a* from there
 */
public class EnemyMap extends Thread {
    //to add a bit of leeway
    private static final float EPSILON = 0.1f;
    //the granularity of the first map
    private static final float GRANULARITY_1 = 0.3f;
    //the granularity of the second map
    private static final float GRANULARITY_2 = 0.6f;
    //the granularity of the third map
    private static final float GRANULARITY_3 = 1f;

    //used to prevent multiple threads accessing enemy map
    public static final Object LOCK = new Object();

    //each element of this array is itself a node, it tells the deltX and y and neighbours, first node is player, next N are supply, rest are intermediate
    private Node[] nodeMap;


    //Plateaus used for ray casting
    private List<Plateau> plateaus;
    //toxic lakes used for ray casting
    private List<ToxicLake> toxicLakes;
    //enemies we need to handle
    private Queue<Entry> enemies = new LinkedList<>();

    private boolean running = true;
    private volatile boolean paused = false;


    /** Default Constructor
     * @param plateaus all plateaus on the current level
     * @param toxicLakes all toxic lakes on the current level
     * @param nodes the current node maps. NOTE, the format is node 0 must be the player spawn, then the supply nodes, WHICH MUST BE CONNECTED TO SOMETHING OTHER THAN THE NODE0
     */
    public EnemyMap (List<Plateau> plateaus, List<ToxicLake> toxicLakes, Node[] nodes){
        Log.d("Enemy","MAPPING CREATED");
        this.plateaus = plateaus;
        this.toxicLakes = toxicLakes;
        this.nodeMap = nodes;
        this.start();
    }

    public EnemyMap(EnemyMap org){
        Log.d("Enemy","MAPPING CREATED");
        this.plateaus = org.plateaus;
        this.toxicLakes = org.toxicLakes;
        this.nodeMap = org.nodeMap;
        this.enemies = org.enemies;
        this.running = org.running;
        this.paused = org.paused;
        this.start();
    }


    /** Given a line represented by two nodes, it sees if it's possible to traverse this line without intersecting the plateau
     *
     * TODO THIS IS NOT PERFECT RN, here are some future stuff to make it better: the penalty for toxic lakes should be added based on intersection length, also for plateaus the lines should
     *                              be extended so there is "width" paddin on both sides of the line. Additionally look for an optimizatioon for the plateau intersection, 8 checks is too much
     *
     *
     * @param node1 the starting point
     * @param node2 the ending point
     * @return -1 if the line isn't possible otherwise, an integer representing the time it takes to traverse this specific node
     */
    private double isValid(Node node1, Node node2){
        if (node1 == null || node2 == null) return -1;

        double weight = Math.hypot(node1.x-node2.x,node1.y-node2.y);
        /*  C0___C1
         *   | |  |
         * C2|_|_|C3
         */
        //left perpendicular

        //theyre the same point
        if (weight == 0) return 0;


        for (int i = 0,size = this.plateaus.size();i<size;i++){
            Plateau plateau = this.plateaus.get(i);
            float[][] points = plateau.getPoints();

            /*testing this later
            //NOTE to prevent overfitting, what we do is instead of all the times the "rect" formed c0,1,2,3 intersects the plat
            //this is more efficient, and makes it more suited for what we need

            */
            if (MathOps.lineIntersectsLine(node1.x,node1.y,node2.x,node2.y,points[0][0],points[0][1],points[1][0],points[1][1]) ||
                    MathOps.lineIntersectsLine(node1.x,node1.y,node2.x,node2.y,points[3][0],points[3][1],points[1][0],points[1][1]) ||
                    MathOps.lineIntersectsLine(node1.x,node1.y,node2.x,node2.y,points[0][0],points[0][1],points[2][0],points[2][1]) ||
                    MathOps.lineIntersectsLine(node1.x,node1.y,node2.x,node2.y,points[2][0],points[2][1],points[3][0],points[3][1])){
                return -1;
            }

        }
        for (int i = 0,size = this.toxicLakes.size();i<size;i++){
            ToxicLake toxicLake = this.toxicLakes.get(i);
            float x = toxicLake.getDeltaX();
            float y = toxicLake.getDeltaY();
            float r = toxicLake.getWidth()/2;


            if (MathOps.segmentIntersectsCircle(x,y,r,node1.x,node1.y,node2.x,node2.y)) {
                //see what to do, but basically increase the weightage

                //this.map1Nodes[i] = null;
                return -1;
            }

        }


        return weight;
    }


    /** Each frame the player's position needs to be updated once, as opposed to finding it through each enemy
     *
     * @param player the current game player
     */
    public void updatePlayerPosition(Player player){
        //see every connection it can make with every other node, if it's possible
        //add it to neighbours
        synchronized (LOCK) {
            if (this.nodeMap[0] != null) this.nodeMap[0].reset();

            Node playerNode = new Node(player.getDeltaX(), player.getDeltaY());

            for (int i = 1; i < this.nodeMap.length; i++) {

                float weightage = (float) this.isValid(playerNode, nodeMap[i]);
                if (weightage >= 0) {
                    nodeMap[i].addNeighbour(playerNode, weightage);
                    playerNode.addNeighbour(nodeMap[i], weightage);
                }

            }


            this.nodeMap[0] = playerNode;
        }

    }

    public synchronized void requestPath(Enemy enemy,int supplyIndex){
        this.enemies.add(new Entry(enemy, supplyIndex));
    }

    /** Returns a List<Node> that represents the path it must take ALGO from: https://www.youtube.com/watch?v=mZfyt03LDH4
     *
     * @param currentX the starting deltX
     * @param currentY the starting y
     * @param nodeIndex -1 if you're targeting the player, otherwise the index of the supply you want/node
     * @return a path of nodes that represent the path it must take. NOTE if the last Node is null, that means that the last node is  the Player, and appropriate action can be taken from there
     */
    private LinkedList<Node> nextStepMap(float currentX, float currentY, int nodeIndex){
        //first get possibly nodes it can connect with through ray casting
        //get the weightages of each of those nodes
        Node start = new Node(currentX,currentY);
        float targetX = this.nodeMap[nodeIndex + 1].x;
        float targetY = this.nodeMap[nodeIndex + 1].y;

        for (int i = 1;i<this.nodeMap.length;i++){

            float weightage = (float) this.isValid(start,nodeMap[i]);
            //Log.d("ENEMY WEIGHT","weight: " + weightage + " length: "  +this.nodeMap.length);
            if (weightage >= 0){
                //this seems to be causing problems as it lasts for the a* search even after this one,
                //so I'm removing it, as i I don't even think a* edges hve to be bi drirectional
                //nodeMap[i].addNeighbour(start,weightage);
                start.addNeighbour(nodeMap[i],weightage);
            }


            nodeMap[i].gCost = nodeMap[i].fCost = Float.MAX_VALUE;
        }
        this.nodeMap[0].gCost = nodeMap[0].fCost = Float.MAX_VALUE;

        //then use a* to find optimum path



        //open are the ones where we have explored their neighbours, but have not actually explored yet
        List<Node> open = new ArrayList<>();


        open.add(start);
        start.fCost = (float) Math.hypot(currentX - targetX,currentY - targetY);
        start.gCost = 0;

        while (open.size() > 0){
            Node currentNode = open.get(0);
            for (int i = 1;i<open.size();i++){
                if (open.get(i).fCost() < currentNode.fCost() || (open.get(i).fCost() == currentNode.fCost() && open.get(i).gCost > currentNode.gCost)){
                    currentNode = open.get(i);
                }
            }
            open.remove(currentNode);
            //the plus 1 is to offset for the player node
            if (currentNode == nodeMap[nodeIndex + 1]){
                LinkedList<Node> path = computePath(currentNode,start);
                if (nodeIndex == -1){
                    path.set(path.size()-1,null);
                }
                return path;
            }


            for (int i = 0;i<currentNode.connections.size();i++){
                Node neighbour = currentNode.connections.get(i);

                float neighbourGCost = currentNode.gCost + currentNode.weights.get(i);

                boolean notOpen = ! open.contains(neighbour);
                if (neighbourGCost < neighbour.gCost){
                    neighbour.gCost = neighbourGCost;
                    neighbour.fCost = neighbour.gCost + (float) Math.hypot(neighbour.x-targetX,neighbour.y-targetY);
                    neighbour.parentNode = currentNode;

                    if (notOpen){
                        open.add(neighbour);
                    }
                }
            }

        }


        //this makes it as if its pointing to the player as no suitable path was found
        LinkedList<Node> path = new LinkedList<>();
        path.add(null);
        path.add(null);


        return path;
    }

    /** After the path has been laid out, this retraces it and makes it into an array, it makes it a bit randomized though
     *
     * @param target the target node, must be the exact reference
     * @param start the starting node, also must be the exact reference
     * @return the path starting from the first node AFTER the starting position
     */
    private LinkedList<Node> computePath(Node target,Node start){
        LinkedList<Node> path = new LinkedList<>();

        while (target != start){
            path.add(0,target.noisyNode());
            target = target.parentNode;

        }
        return  path;
    }

    public int getNodeIndex(Node target){
        for (int i = 0;i < this.nodeMap.length;i++){
            if (nodeMap[i] == target){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void run() {
        super.run();

        while (running){

            if (paused){
                continue;
            }

            Entry entry = enemies.poll();
            if (entry != null) {
                Enemy e = entry.e;
                LinkedList<Node> path;
                synchronized (LOCK) {
                    path = this.nextStepMap(e.getDeltaX(), e.getDeltaY(), entry.supplyIndex);
                }
                e.setPath(path);
            } else {
                try {
                    Thread.sleep(1000);
                    Log.d("Enemy", "MAPPING: Is paused: " + paused);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void endProcess(){
        this.running = false;
    }



    public void setPaused(boolean paused){
        this.paused = paused;
        Log.d("Enemy","MAPPING PAUSED: " + paused);
    }


    public static class Node {
        /** The distance from the start to this node*/
        float gCost = Float.MAX_VALUE;
        /** The distance from the end to this node*/
        float fCost = Float.MAX_VALUE;

        /** The node that points to this*/
        Node parentNode;

        /** the deltX value of this node*/
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

        public Node orgNode;

        //the deltX and y coorinates of this node
        public Node(float x,float y,float r){
            this.x = x;
            this.y = y;
            this.size = r;
            this.orgNode = this;
        }
        public Node(float x,float y){
            this(x,y,0);
        }

        public Node noisyNode(){
            double randVal = Math.random();
            float r = (float) randVal * size;
            float theta = (float) (Math.random() * 2 * Math.PI);
            float x = this.x + (float) Math.cos(theta) * r;
            float y = this.y + (float) Math.sin(theta) * r;
            Node rNode = new Node(x,y);
            rNode.orgNode = this.orgNode;
            return rNode;
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
            for (int i = 0;i < connections.size();i++){
                connections.get(i).connections.remove(this);
            }
            this.connections.clear();
            this.weights.clear();
        }

        /** Gets the current cost of this node
         *
         * @return the g cost plus the h cost
         */
        public float fCost(){
            return this.fCost;
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
    private static class Entry {
        Enemy e;
        int supplyIndex;
        Entry(Enemy e,int supplyIndex){
            this.e = e;
            this.supplyIndex = supplyIndex;
        }
    }

    /** To string method used to display info to a gui
     *
     * @return a string representation
     */
    @Override
    public String toString(){
        StringBuilder returnString = new StringBuilder("[");
        for (Node node:this.nodeMap){
            if (node != null) {
                for (Node subNode:node.connections) {
                    returnString.append("(").append(node).append(",").append(subNode).append("),");
                }

                //returnString += node + ", ";
            }

        }
        return returnString + "]";
    }


}
