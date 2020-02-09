package enigmadux2d.core.gameObjects;


/** One element that could be put into an vao collection
 *
 * @see VaoCollection
 * @author Manu Bhat
 * @version BETA
 */
public abstract class CollectionElem {

    /** The id of this particular instance in the VaoCollection. It's received using the VaoCollection.addInstance() method.
     * We can use this id to update info into in the Vao, or hide it completely
     *
     */
    private int instanceID;


    /** Default Constructor
     *
     * @param instanceID The id of this particular instance in the VaoCollection. It should be received using the VaoCollection.addInstance() method.
     */
    public CollectionElem(int instanceID){
        this.instanceID = instanceID;
    }


    /** This is a very important method. In a VaoCollection, each alive instance should update it's info in the vao using a float[]
     * that tells about the information.
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     *                   array.
     */
    public abstract void updateInstanceInfo(float[] blankInstanceInfo,float[] uMVPMatrix);

    /** This is also a pretty important method. Over here, the local scaling, translation, rotation, and other transformations
     * are applied. In this method write the complete transformation to the appropriate slot in the instanceData (this is based
     * on the VaoCollection you are using, for all known cases this is the first 16 floats). To write the complete transformation,
     * calculate the local transformation, that is scaling, translation, rotation, etc (localTransformM). Then in the blankInstanceData
     * write uMVPMatrix * localTransformM. Note that you don't have to create an entire new matrix. Rather you can just,
     * use commands like Matrix.translateM, as these are more efficient. However, OUTSIDE CLASSES SHOULD NOT CALL THIS METHOD. It's only for use by super classes.
     * Instead, call updateInstanceInfo
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     *                   array.
     */
    public abstract void updateInstanceTransform(float[] blankInstanceInfo,float[] uMVPMatrix);



    /** Gets the id of the this particular instance, with respect to the Vao it's in. It should be used to update info
     * inside the vao
     *
     * @return Gets the id of the this particular instance, with respect to the Vao it's in.
     */
    public int getInstanceID() {
        return this.instanceID;
    }




}
