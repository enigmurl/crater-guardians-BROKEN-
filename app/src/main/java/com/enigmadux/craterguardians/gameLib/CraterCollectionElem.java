package com.enigmadux.craterguardians.gameLib;

import enigmadux2d.core.gameObjects.CollectionElem;

/** This is a collection element tailored to Crater Guardians. That is,
 * each instance contains 22 floats. 16 for the uMVPMatrix, 4 for the RGBA filter, and 2 for the texture coordinates offset.
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class CraterCollectionElem extends CollectionElem {
    /** How much to translate the vertices to the right in openGL terms
     *
     */
    protected float deltaX;
    /** How much to translate the vertices to the top in openGL terms
     *
     */
    protected float deltaY;

    /** How much to translate the texture cords to the right in openGL terms
     *
     */
    protected float deltaTextureX;
    /** How much to translate the texture cords to the bottom in openGL terms
     * Remember that for texture cords, the y axis is flipped
     */
    protected float deltaTextureY;


    /** A shader that limits the R,G,B or A channels. They range from 0 to 1, where 1 means allow all of it,
     * and 0 means allow none of it
     */
    private float[] shader = new float[]{1,1,1,1};

    /** Default Constructor
     *
     * @param instanceID The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     */
    public CraterCollectionElem(int instanceID){
        super(instanceID);
    }


    /** Updates the instance info into the float[]
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     */
    public void updateInstanceInfo(float[] blankInstanceInfo,float[] uMVPMatrix){
        //first write the matrix from slots 0 to 15 (inclusive)
        this.updateInstanceTransform(blankInstanceInfo, uMVPMatrix);
        //now update the filter parameters
        blankInstanceInfo[16] = this.shader[0];
        blankInstanceInfo[17] = this.shader[1];
        blankInstanceInfo[18] = this.shader[2];
        blankInstanceInfo[19] = this.shader[3];
        //now update the delta texture cords
        blankInstanceInfo[20] = this.deltaTextureX;
        blankInstanceInfo[21] = this.deltaTextureY;
    }


}
