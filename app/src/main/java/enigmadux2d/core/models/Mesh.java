package enigmadux2d.core.models;

import java.nio.IntBuffer;

/** Stores data about a model, but doesn't really do any drawing
 *
 * Inspired by: https://www.youtube.com/watch?v=WMiggUPst-Q&t=417s
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Mesh {

    //a pointer to the vao that describes this model
    private int vaoID;
    //the number of vertices in this model
    private int vertexCount;


    /** an IntBuffer of indexes. Each element should correspond to a vertex, that is
     * if the first element of the index array is 2, it is corresponding to vertices[2]
     * this is useful as many vertices are repeated
     */
    private IntBuffer elementArray;


    /** Creates a Mesh
     *
     * @param vaoID pointer to a VAO that describes data about this model (pos, texture cord, etc)
     * @param vertexCount the amount of vertices in this model
     * @param elementArray an IntBuffer of indexes. Each element should correspond to a vertex, that is
     *                     if the first element of the index array is 2, it is corresponding to vertices[2]
     */
    public Mesh(int vaoID,int vertexCount,IntBuffer elementArray){
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.elementArray = elementArray;
    }

    /** Gets the pointer of the VAO that this model is based on. VAO stores information about the vertices, texture coordinates,
     * and other information
     *
     * @return the pointer to VAO id
     */
    public int getVaoID() {
        return this.vaoID;
    }

    /** Number of vertices in this model
     *
     * @return number of vertices in this model
     */
    public int getVertexCount() {
        return this.vertexCount;
    }


    /** Gets the index buffer, which basically tells the order of vertices to draw in, and saves data
     *
     * @returnan IntBuffer of indexes. Each element should correspond to a vertex, that is
     *           if the first element of the index array is 2, it is corresponding to vertices[2]
     */
    public IntBuffer getElementArray() {
        return this.elementArray;
    }
}
