package com.enigmadux.craterguardians.gamelib;


import enigmadux2d.core.gameObjects.VaoCollection;

/** This is the specific VaoCollection, that has the format required for CraterGuardians. That is,
 * each instance is 22 floats. 16 for the uMVPMatrix, 4 for the RGBA filter, and 2 for the texture coordinates offset.
 *
 * @author Manu Bhat
 */
public class CraterVaoCollection extends VaoCollection {
    /** The amount of floats a single entity takes up
     *
     */
    private static final int FLOATS_PER_ENTITY = 22;


    /** the amount of Vertex Buffer Objects that will be needed. For this class. Vertices, Texture Cords, and Instanced
     * Data is needed, so three VBOs are needed
     *
     */
    private static final int VBOS_NEEDED = 3;

    /** In a VAO, the view matrix and other data go into specific slots. This tells which slots
     * (index) the view matrix  fall into
     *
     */
    public static final int VIEW_MATRIX_ATTRIBUTE_SLOT = 2;

    /** In a VAO, the ARGB shader and other data go into specific slots. This tells which slots
     * (index) ARGB shaders fall into
     *
     */
    public static final int ARGB_SHADER_ATTRIBUTE_SLOT = 6;
    /** In a VAO, the delta texture coordinates and other data go into specific slots. This tells which slots
     * (index) delta texture coordinates fall into
     *
     */
    public static final int DELTA_TEXTURE_COORDINATES_ATTRIBUTE_SLOT = 7;





    /** Default Constructor
     *
     * @param vertices an array of positions in the form of {x1,y1,z1,x2,y2,z2...,xn,yn,zn}. Rather than repeating
     *                  vertices, which takes lots of data, repeat the indices in the second parameter. This is where each
     *                  vertex is in model space.
     * @param textureCords an array of 2d positions in the form of {u1,v1,u2,v2...un,vn}. Also referred to as, uv coordinates
     *                     or s-t coordinates. Essentially they map each vertex to a point in 2d space, which is then
     *                     interpolated so each pixel falls on a 2d area, which we define to be our texture later on.
     *                     Note that unless you know what you're doing (such as tiling), leave the stuff between 0 and 1.
     *                     Also note that (0,0) indicates top left, and (1,1) indicates bottom right.
     * @param indices an int[] of indexes. Each element should correspond to a vertex, that is
     *                if the first element of the index array is 2, it is corresponding to vertices[2]
     *                this is useful when each vertex is comprised of lots of data
     */
    public CraterVaoCollection(int numEntities,float[] vertices,float[] textureCords,int[] indices){
        super(
                numEntities,
                vertices,
                textureCords,
                indices,
                CraterVaoCollection.VBOS_NEEDED,
                CraterVaoCollection.FLOATS_PER_ENTITY
        );
    }

    /** Links the specified data points with the appropriate VAO slot.
     *
     * This is current order (it may have changed at the time of the reading)
     * [16 floats of uMVPMatrix,4 floats of RGBA channel filters, 2 floats of delta texture coordinates], which is for
     * each instance (22 floats per instance).
     */
    @Override
    public void bindInstancedData() {
        //matrix col 0
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, CraterVaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT, 4, CraterVaoCollection.FLOATS_PER_ENTITY, 0);
        //matrix col 1
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, CraterVaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT + 1, 4, CraterVaoCollection.FLOATS_PER_ENTITY, 4);
        //matrix col 2
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, CraterVaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT + 2, 4, CraterVaoCollection.FLOATS_PER_ENTITY, 8);
        //matrix col 3
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, CraterVaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT + 3, 4, CraterVaoCollection.FLOATS_PER_ENTITY, 12);
        //shader (a,r,g,b)
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, CraterVaoCollection.ARGB_SHADER_ATTRIBUTE_SLOT, 4, CraterVaoCollection.FLOATS_PER_ENTITY, 16);
        //texture cord offset
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, CraterVaoCollection.DELTA_TEXTURE_COORDINATES_ATTRIBUTE_SLOT, 2, CraterVaoCollection.FLOATS_PER_ENTITY, 20);
    }



    @Override
    public String toString() {
        return "Crater Vao Collection of " + getNumInstances();
    }
}
