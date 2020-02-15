package enigmadux2d.core.quadRendering;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/** This stores data in a  vao about the actual mesh. There is really only vertices, however from that the texture coordinates
 * can be implied from the vertex data
 *
 * @author Manu Bhat
 * @version BETA
 */
public class QuadMesh {

    /** Vertices are stored in the attribute slot 0 of the vao
     *
     */
    public static final int VERTEX_SLOT = 0;

    //this is where the vao ID will be stored
    private final int[] vao = new int[1];

    //this is where vbos will be stored, only 1 vbo is needed as of now
    private final int[] vboList = new int[1];

    //the amount of vertices this contains
    private final int numVertices;


    /** Default Constructor
     *
     * @param positions an array of positions in the form of [x1,y1,z1,x2,y2,z2]...
     */
    public QuadMesh(float[] positions){
        //create and bind an empty vao
        this.createEmptyVAO();

        //then add the buffer to the vertex slot, it won't be changed ever, and each vertex is size 3
        this.pushDataInAttributeList(QuadMesh.VERTEX_SLOT,positions,GLES30.GL_STATIC_DRAW,3);

        //unbind the vao, by putting 0 as the parameter
        GLES30.glBindVertexArray(0);

        this.numVertices = positions.length/3;
    }

    /** Creates an empty VAO and returns the ID. It also binds the VAO by default, so you don't have to worry about
     * binding it after calling this method.
     *
     *
     * @return returns the id of the created VAO
     */
    private int createEmptyVAO(){

        //the n parameter refers to the amount we want created, where we only want 1 so it's 1
        //the second parameter (vao) is where openGL "returns" the pointer open GL requires we pass in a int[], where it will put the pointer into
        // the corresponding element of the passed in array.
        //the third parameter is always the index to start of
        GLES30.glGenVertexArrays(1,this.vao,0);


        //bind it for future use. Binding is like saying we need it right now, and load it up
        //again bind the vaoID that was given to us by openGL, so we're just binding the VAO that we just made
        GLES30.glBindVertexArray(this.vao[0]);


        //return the id that openGL gave for us, which is only 1, so no point in returning the whole array
        //its this.numVaoAssigned minus 1 because we post incremented it the line before
        return this.vao[0];
    }

    /** OpenGL is written mostly in C, which understands buffers better than java arrays. So we have
     * to convert the data into float buffers
     *
     * @param data the data that needs to be stored into a float buffer
     * @return a FloatBuffer object that has the same contents as the array
     */
    private FloatBuffer pushDataInFloatBuffer(float[] data){
        //allocates memory for a byte buffer in native order
        //this is allocating memory by saying the data length * the amount of bytes in a Float
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * Float.SIZE/Byte.SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        //convert to a float buffer
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        //add the data to the float buffer
        floatBuffer.put(data);
        //it was in write mode, now make it into read mode
        floatBuffer.flip();

        return floatBuffer;
    }

    /** Stores data into an attribute list. Assumes that the target VAO is currently bound, as this will not do
     * any bounding for you.
     * Works by: Generating a VBO based on the data parameter, then it adds said VBO into the attributeSlot specified
     * by the parameters into the current bound VAO
     *
     *
     *
     * @param attributeSlot basically the index of the VAO, it goes into this slot
     * @param data the actual data that goes into the specified slot
     * @param usage Specifies the expected usage pattern of the data store. The symbolic constant must be GL_STREAM_DRAW, GL_STATIC_DRAW, or GL_DYNAMIC_DRAW.
     *              it should basically tell how often you expect to update the VBO
     * @param size the amount of floats for each data, for example if you're calling this for vertices
     *             it will be 3 because each 3d vector is represented by deltX,y,z which is 3 floats
     *
     */
    private void pushDataInAttributeList(int attributeSlot,float[] data,int usage,int size){

        //now this creates a VBO, that stores the pointer into slot 0
        //n is 1 because only VBO is being created, and offset is starts at the beginning of the untouched portion
        //of the vbos arrays
        GLES30.glGenBuffers(1,this.vboList,0);

        //now we have to bind the vbo so we can access the contents
        //first parameter tells that we're binding an array buffer, and the second one is the pointer to the
        //specific vbo we are referencing
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.vboList[0]);

        //convert the data to a float buffer
        FloatBuffer floatBuffer = this.pushDataInFloatBuffer(data);

        //now finally add the data into the buffer
        //it first says type of data, then it says the byte size of the data, the actual data, and basically
        //how often the data is updated
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,floatBuffer.capacity() * Float.SIZE/Byte.SIZE,floatBuffer,usage);

        //finally add the vbo to the vao
        //ARGS:, first is saying where we have to put in; the index, then it's the amount of floats for each data,
        //then its the type of data, I'm hardcoding this to always be floats, then the data isn't normalize (scaled
        //to unit sphere), then there is no other data between consecutive vertices so stride and offset should both
        //be 0
        GLES30.glVertexAttribPointer(attributeSlot,size,GLES30.GL_FLOAT,false,0,0);

        //finally unbind the current VBO, specifying the type of buffer, and telling the pointer 0, which means to unbind
        //whatever we currently have.
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }

    /** Gets the vao id, so you can access the vertices
     *
     * @return the id of the vao that stores the vertex information
     */
    public int getVaoID(){
        return this.vao[0];
    }

    /** Gets the amount of vertices this stores
     *
     * @return the vertex count in this vao
     */
    public int getVertexCount(){
        return this.numVertices;
    }


}
