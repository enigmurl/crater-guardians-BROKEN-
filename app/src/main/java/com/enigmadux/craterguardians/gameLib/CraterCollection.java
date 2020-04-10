package com.enigmadux.craterguardians.gameLib;

import android.content.Context;


import com.enigmadux.craterguardians.worlds.World;

import java.util.ArrayList;
import java.util.Iterator;

/** Holds items and their vertex data
 *
 */
public class CraterCollection<T extends CraterCollectionElem> implements Iterable<T>{

    /** Vertex data of the item per instance
     *
     */
    private CraterVaoCollection vertexData;

    /** Actual Data about the game object
     *
     */
    private ArrayList<T> instanceData = new ArrayList<T>();

    //so that theres no garbage collection
    private final float[] bufferData = new float[22];


    /** Default constructor
     *
     * @param maxInstances maximum amount of instances
     * @param positions x1,y1,z1 ...
     * @param textureCords u1,v1...
     * @param indices indices
     */
    public CraterCollection(int maxInstances,float[] positions,float[] textureCords, int[] indices){
        this.vertexData = new CraterVaoCollection(maxInstances,positions,textureCords,indices);

    }

    public void update(long dt, World world){
        for (int i = 0;i<instanceData.size();i++){
            instanceData.get(i).update(dt,world);
        }
    }

    public void prepareFrame(float[] mvpMatrix){
        for (int i = 0,size = instanceData.size();i<size;i++){
            this.instanceData.get(i).updateInstanceInfo(bufferData,mvpMatrix);
            this.vertexData.updateInstance(this.instanceData.get(i).getInstanceID(),bufferData);
        }

    }

    //texture pointer = R.drawable.*;
    public void loadTexture(Context context,int texturePointer){
        this.vertexData.loadTexture(context, texturePointer);
    }

    public void updateVBO(){
        this.vertexData.updateInstancedVbo();
    }

    public void clear(){
        this.vertexData.clearInstanceData();
        this.instanceData.clear();
    }

    public int size(){
        return this.instanceData.size();
    }

    //creates a spot in the vao for an instance
    public int createVertexInstance() {
        return this.vertexData.addInstance();
    }

    //adds an actual instance
    public void addInstance(T instance){
        this.instanceData.add(instance);
    }



    public void delete(T objectToDelete){
        this.vertexData.deleteInstance(objectToDelete.getInstanceID());
        this.instanceData.remove(objectToDelete);
    }

    public CraterVaoCollection getVertexData(){
        return this.vertexData;
    }
    public ArrayList<T> getInstanceData(){
        return this.instanceData;
    }

    @Override
    public Iterator<T> iterator(){
        return this.instanceData.iterator();
    }

}
