package enigmadux2d.core.renderEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/** Is able to draw multiple of the same model, store
 *
 *
 */
public class VaoCollection {
    /** The amount of bytes a single entity takes up
     *
     */
    private static final int FLOATS_PER_ENTITY = 22;
    /** This is the amount of VBOs needed to make a Collection
     * (Textured Cord, Vertices, Instance Info)
     */
    private static final int VBOS_NEEDED = 3;
    /**
     * in a VAO, the vertices (and any other data must go into a specified slot. This tells which slot
     * (index) the data should be put into by default.
     */
    public static final int VERTEX_ATTRIBUTE_SLOT = 0;
    /** In a VAO, the texture coordinates and other data go into specific slots. This tells which slots
     * (index) texture coordinate (also known as uv coordinates/st coordinates) fall into
     *
     */
    public static final int TEXTURE_COORDINATES_ATTRIBUTE_SLOT = 1;
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


    /** Vertex Data is assumed to be static by default, that is,
     * The data store contents will be modified once and used many times.
     */
    private static final int VERTEX_DATA_DEFAULT_USAGE = GLES30.GL_STATIC_DRAW;
    /** Texture Coordinate Data is assumed to be static by default, that is,
     * The data store contents will be modified once and used many times.
     */
    private static final int TEXTURE_COORDINATE_DATA_DEFAULT_USAGE = GLES30.GL_STATIC_DRAW;


    /** This is just to help make code more readable, but it's basically saying in a standard vector
     * that we use (3 dimensional), how many floats are needed to represent one vertex
     *
     */
    private static final int FLOATS_PER_3D_VERTEX = 3;
    /** This is just to help make code more readable, but it's basically saying in a standard  texture coordinate vector
     * that we use (2 dimensional), how many floats are needed to represent one vertex
     *
     */
    private static final int FLOATS_PER_2D_VERTEX = 2;

    /** This matrix basically hides any object, by shrinking it to 0, and then moving it offscreen, it shouldn't
     * be too expensive I believe. Note that it's transposed so it's equivalent to
     * First matrix translates, second matrix makes it all go to [0,0,0], So together they make it go to 0,0,0, and then
     * move it off the screen, which is just the first matrix.
     *
     * [0 0 0 2]     [0 0 0 0]   [0 0 0 2]
     * [0 0 0 0]     [0 0 0 0]   [0 0 0 0]
     * [0 0 0 0]  *  [0 0 0 0] = [0 0 0 0]
     * [0 0 0 1]     [0 0 0 1]   [0 0 0 1]
     * transposed this is
     *
     * 0 0 0 0
     * 0 0 0 0
     * 0 0 0 0
     * 2 0 0 1
     */
    private static final float[] HIDE_MATRIX = new float[] {
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            2, 0, 0, 1,
    };






    //this is where the pointer to the vao is stored
    private int[] vao = new int[1];


    //this is where the index array is stored
    private IntBuffer elementArrayBuffer;

    //These 2 are not lists because it makes it easier to deAssign;
    //anyways when de assigning we have to make them into int[]s for openGL to be able to de allocate, so it makes
    //more sense to just keep them as int[]s to start;
    /**keep a list of all assigned VBOs, so that we don't leak memory, they will all be de - assigned at the end*/
    private int[] vboList = new int[VBOS_NEEDED];



    /** this our main vbo where we stored instanced data */
    private int instancedVbo;
    //this is the actual float[] which we use because it's easy to write to
    private float[] instancedData;
    //this is the vbo data but as as buffer so openGL can understand
    private FloatBuffer instancedBufferedData;

    //because it are arrays, we need to keep a pointer to see how many elements are in each
    //the amount of VBOs assigned
    private int numVboAssigned = 0;

    //the amount of instances created
    private int numInstances;

    //the pointer to the texture that this class uses for rendering
    //only 1 texture will be used
    private int[] textureList = new int[1];


    /** Default Constructor
     *
     * @param numEntities the maximum amount of entities that this VAO can store
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
    public VaoCollection(int numEntities,float[] vertices,float[] textureCords,int[] indices){
        //first allocate the memory
        ByteBuffer pureByteBuffer = ByteBuffer.allocateDirect(numEntities  * VaoCollection.FLOATS_PER_ENTITY * Float.SIZE/Byte.SIZE);
        //order it in the way C scripts understands
        pureByteBuffer.order(ByteOrder.nativeOrder());

        //now make it into a float buffer
        this.instancedBufferedData = pureByteBuffer.asFloatBuffer();


        //initialize the blank instanced data
        this.instancedData = new float[numEntities * FLOATS_PER_ENTITY];


        //put in the blank data
        this.instancedBufferedData.put(this.instancedData);
        //prepare it for reading
        this.instancedBufferedData.flip();


        //created the instanced vbo, but it has no information (yet)
        this.instancedVbo = this.createEmptyVbo(numEntities * FLOATS_PER_ENTITY);




        //first we create an empty vaoID, and bind it (binding done inside the create method)
        this.createEmptyVAO();


        //add the positional data into one of the slots (0 by default). Now the VAO has actual information inside it
        this.pushDataInAttributeList(
                VaoCollection.VERTEX_ATTRIBUTE_SLOT,
                vertices,
                VaoCollection.VERTEX_DATA_DEFAULT_USAGE,
                VaoCollection.FLOATS_PER_3D_VERTEX);

        //add the texture coordinate data int the first slot, now the vao has positional and texture coordinate data inside it
        this.pushDataInAttributeList(
                VaoCollection.TEXTURE_COORDINATES_ATTRIBUTE_SLOT,
                textureCords,
                VaoCollection.TEXTURE_COORDINATE_DATA_DEFAULT_USAGE,
                VaoCollection.FLOATS_PER_2D_VERTEX);


        //VAO is done being handled and can be unbound
        this.unBindVAO();

        //convert the int[] of indices to IntBuffer which openGL understands better
        this.elementArrayBuffer = this.pushDataInIntBuffer(indices);

        //now set up all the data to instancedData, no data is being written, but the "links" are
        //also see if the attribute slots should start at 1 instead of 2, video said 1, but I think that's being used
        //for texture cords

        //matrix col 0
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT, 4, VaoCollection.FLOATS_PER_ENTITY, 0);
        //matrix col 1
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT + 1, 4, VaoCollection.FLOATS_PER_ENTITY, 4);
        //matrix col 2
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT + 2, 4, VaoCollection.FLOATS_PER_ENTITY, 8);
        //matrix col 3
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT + 3, 4, VaoCollection.FLOATS_PER_ENTITY, 12);
        //shader (a,r,g,b)
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, VaoCollection.ARGB_SHADER_ATTRIBUTE_SLOT, 4, VaoCollection.FLOATS_PER_ENTITY, 16);
        //texture cord offset
        this.pushInstancedDataInAttributeList(this.vao[0], this.instancedVbo, VaoCollection.DELTA_TEXTURE_COORDINATES_ATTRIBUTE_SLOT, 2, VaoCollection.FLOATS_PER_ENTITY, 20);
    }


    /** Loads the texture provided to V-RAM of the gpu
     *
     * @param context any android context that can be used to load images from the R.drawable directory
     * @param imagePointer an integer that points to the requested file. Should be in the form of R.drawable.__
     *                     please make the image width and height (pixels) powers of two, i.e. 64,128,1024...
     */
    public void loadTexture(Context context, int imagePointer){
        //first convert the image file into a bitmap
        Bitmap texture = BitmapFactory.decodeResource(context.getResources(),imagePointer);

        //create a texture id at the specified location in the array
        GLES30.glGenTextures(1,this.textureList,0);

        //now bind it with the array
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.textureList[0]);

        // create nearest filtered texture
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);


        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, texture, 0);


        //bitmap no longer needed
        texture.recycle();


    }


    /** This method should only be called once in the constructor, when the variable
     * "instancedBufferedData" is full of 0s
     * The method creates a VBO based on the amount of floats specified.
     * It creates the data as GL_STREAM_DRAW, meaning that it will be read and modified every frame
     *
     * @param numFloats the amount of floats in this vbo
     * @return the pointer to the vbo created
     */
    private int createEmptyVbo(int numFloats){
        //create a buffer id
        GLES30.glGenBuffers(1,this.vboList,this.numVboAssigned);

        //now we have to bind the vbo so we can access the contents
        //first parameter tells that we're binding an array buffer, and the second one is the pointer to the
        //specific vbo we are referencing
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.vboList[this.numVboAssigned]);

        //add the empty data
        Log.e("VAO COLLECTION:", "Pure size: " + this.instancedBufferedData.capacity() + " numFloats: " + numFloats);
        GLES30.glBufferData(
                this.vboList[this.numVboAssigned],
                numFloats * Float.SIZE/Byte.SIZE,
                this.instancedBufferedData,
                GLES30.GL_STREAM_DRAW);

        //unbind the current data
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        //return the created pointer, as well as tell that one more vbo has been created by using the post increment operator
        return this.vboList[this.numVboAssigned++];

    }


    /** Each frame, all instances must be updated with their data, in this method call.
     * Note that you must call updateInstancedVbo after all of the instances have been updated, for
     * the changes to take actual affect
     *
     * @param instanceId this instance id (returned from the addInstance method)
     * @param instanceData this is the data that describes the instance, It should be FLOATS_PER_ENTITY in length;
     *                     right now that would like like 16 for model view matrix, 4 for ARGB shader, and 2 for texture
     *                     cord offset, however that may have changed in the time that your are reading this so look out for that
     *
     */
    public void updateInstance(int instanceId,float[] instanceData){
        int offset = instanceId * VaoCollection.FLOATS_PER_ENTITY;

        //copy the passed in data to our global data efficiently
        System.arraycopy(instanceData, 0, this.instancedData, offset, VaoCollection.FLOATS_PER_ENTITY);

    }

    /** Hides an instance, because shifting it down, is pretty expensive and, makes lots of stuff more complicated.
     * You can undo this by updating the elements info. As the implementation, just makes the matrix all
     *
     * @param instanceId the id of the element that needs to be hidden,
     */
    public void hideInstance(int instanceId){

    }

    /** After a particular round has been played, it may be best to clear the entire vao, rather that just hiding specific
     * elements. This clears the entire array, and the amount of instances starts back at 0.
     * Note by clearing it does not mean that the data is lost, just that it wont be used, like when indexing is deleted
     * on a hard drive.
     *
     */
    public void clearInstanceData(){
        this.numInstances = 0;
    }

    /** After all the separate instances are updated, we need to push that data into the instanced vbo,
     * this method does that
     *
     */
    public void updateInstancedVbo(){
        //remove existing data
        this.instancedBufferedData.clear();
        //put in the new data
        this.instancedBufferedData.put(this.instancedData);
        //finally make it ready for reading
        this.instancedBufferedData.flip();

        //bind the VBO so operations can be performed
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.instancedVbo);
        //add in the data
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,this.instancedBufferedData.capacity() * Float.SIZE/Byte.SIZE,this.instancedBufferedData, GLES30.GL_STREAM_DRAW);
        //not entirely sure what this does, it's at https://youtu.be/Rm-By2NJsrc?t=1198, seems like it's making the vbo forget
        //about previous data, but would need to look at the api or something
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER,0,this.elementArrayBuffer.capacity() * Float.SIZE/Byte.SIZE,this.instancedBufferedData);
        //unbind the vbo as we're done
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }


    /** Creates a blank instance, and returns the id, use the id so you can change the properties of that instance later
     *
     * @return the id of the instance created
     */
    public int addInstance(){
        return this.numInstances++;
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
    /** Once a VAO is done being used, it should be unbound, as it's not needed any more
     *
     */
    private void unBindVAO(){

        //to unbind arrays you specify the ID as 0. This always unbinds the currently bound array
        GLES30.glBindVertexArray(0);
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
     *             it will be 3 because each 3d vector is represented by x,y,z which is 3 floats
     *
     */
    private void pushDataInAttributeList(int attributeSlot,float[] data,int usage,int size){

        //now this creates a VBO, that stores the pointer into vboList[this.numVboAssigned]
        //n is 1 because only VBO is being created, and offset is starts at the beginning of the untouched portion
        //of the vbos arrays
        GLES30.glGenBuffers(1,this.vboList,this.numVboAssigned);

        //now we have to bind the vbo so we can access the contents
        //first parameter tells that we're binding an array buffer, and the second one is the pointer to the
        //specific vbo we are referencing
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.vboList[this.numVboAssigned]);

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
        //GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);

        this.numVboAssigned++;
      }

    /** Pushes data into an instanced vao. Might want to rename this, as no data is technically being pushed
     *  https://www.youtube.com/watch?v=Rm-By2NJsrc&t=603s this has more info, I don't fully understand code as of now,
     *  so i'll try to look into it
     *
     * @param vao the id of the VAO that will get the data
     * @param vbo the id of the VBO that will get the data (see if vao and vbo are getting/receiving the data??)
     * @param attributeSlot the slot in the VAO that takes in the data
     * @param numFloats the amount of floats each data takes up
     * @param stride the amount of floats needed for one instance
     * @param offset the amount of floats from the beginning of a particular instance
     */
    private void pushInstancedDataInAttributeList(int vao,int vbo,int attributeSlot, int numFloats,int stride, int offset){
        //bind the Vbo so we can can use it
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,vbo);
        //bind vertex array so we can use it
        GLES30.glBindVertexArray(vao);
        //say how the data is formatted, again don't know specifics truly
        GLES30.glVertexAttribPointer(attributeSlot,
                numFloats,
                GLES30.GL_FLOAT,
                false,
                stride * Float.SIZE/Byte.SIZE,
                offset * Float.SIZE/Byte.SIZE);
        //now say that it only changes once per instance
        GLES30.glVertexAttribDivisor(attributeSlot,1);

        //unbind VAO and VBO
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
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

    /** OpenGL is written mostly in C, which understands buffers better than java arrays. So we have
     * to convert the data into int buffers
     *
     * @param data the data that needs to be stored into a int buffer
     * @return a IntBuffer object that has the same contents as the array
     */
    private IntBuffer pushDataInIntBuffer(int[] data){
        //this is allocating memory by saying the data length * the amount of bytes in a Float
        //allocates memory for a byte buffer in native order
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * Integer.SIZE/Byte.SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        //convert to an int buffer
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        //add the data to the float buffer
        intBuffer.put(data);
        //it was in write mode, now make it into read mode
        intBuffer.flip();

        return intBuffer;
    }

    /** Gets the vao ID, so a renderer can bind the VAO
     *
     * @return the VAO id that this holds
     */
    public int getVaoID(){
        return this.vao[0];
    }

    /** Gets the texture ID, so a renderer can pass that onto the shader
     *
     * @return the pointer to the texture, that this holds
     */
    public int getTextureId() {
        return this.textureList[0];
    }

    /** Gets the element array buffer, which contains the indices of what vertices to use
     *
     * @return the indices of what vertices to use
     */
    public IntBuffer getElementArrayBuffer() {
        return elementArrayBuffer;
    }

    /** Gets the amount of instances in this collection
     *
     * @return the amount of instances in this collection
     */
    public int getNumInstances(){
        return this.numInstances;
    }

    /** De assigns VAOs and VBOs associated with this Collection
     *
     */
    public void recycle(){
        //de assign our main vao
        GLES30.glDeleteVertexArrays(1,this.vao,0);

        //de assign all sub vbo
        GLES30.glDeleteBuffers(this.numVboAssigned,this.vboList,0);
    }
}
