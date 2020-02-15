package com.enigmadux.craterguardians.gameLib;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.renderEngine.MeshRenderer;

/** Stores multiple VAOs, and renders them easily. Many of them are hardcoded however, as locks are needed complicating
 * the process, so it's just easiest (and fastest) to hard code it
 *
 * @author Manu Bhat
 * @version BETA
 */
public class InstancedDataWrapper {


    /** This is basically an array of instanced data
     *
     */
    private ArrayList<VaoCollection> vaoCollectionArrayList;

    /** Since each vao collection does not contain data about what instance it is, we need to map
     * each class with it's corresponding index in the arrayList
     *
     */
    private HashMap<Class,Integer> vaoCollectionSlots;


    /** Default Constructor, no parameters required, but stuff are set up
     *
     */
    public InstancedDataWrapper(){
        this.vaoCollectionArrayList = new ArrayList<>();

        this.vaoCollectionSlots = new HashMap<>();
    }

    /** Draws all instanced arrays. Locks are not required, as we're not accessing the instance array list Note that you must first update the data.
     * Do this by calling the "updateVboData"
     *
     * @param collectionsRenderer a MeshRenderer, that performs the rendering of the vao collections
     */
    public void draw(MeshRenderer collectionsRenderer){
        //for each vao, render it
        for (int i = 0,size =vaoCollectionArrayList.size();i<size;i++){
            collectionsRenderer.renderCollection(this.vaoCollectionArrayList.get(i));
        }
    }

    /** Before actually drawing, you must first update the data. This method uses the proper locks, before accessing data.
     *
     * @param mvpMatrix the model view projection matrix that describes outside transformations
     */
    public void updateVboData(float[] mvpMatrix){

    }


}
