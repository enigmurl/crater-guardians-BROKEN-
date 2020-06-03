package enigmadux2d.core.renderEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.Display;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import enigmadux2d.core.models.Mesh;

/** Creates a VAO that you can use to store large amounts of data into
 *
 */
public class ModelLoader {
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


    /** MAX amount of VAOs allowed, going past this amount will most likely result in an ArrayIndexOutOfBounds
     *  exception
     *
     */
    private static final int MAX_VAOS_ALLOWED = 8;
    /** MAX amount of VBOs allowed, going past this amount will most likely result in an ArrayIndexOutOfBounds
     *  exception
     *
     */
    private static final int MAX_VBOS_ALLOWED = 32;
    /** MAX amount of textures allowed, going past this will most likely result in an ArrayIndexOutOfBounds
     *
     */
    private static final int MAX_TEXTURES_ALLOWED = 64;



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


    /** for a quad of size 1 by 1 these are the coordinates of the vertices
     *
     */
    private static final float[] QUAD_VERTICES = new float[] {
            -0.5f, 0.5f,0,
            -0.5f,-0.5f,0,
            0.5f,0.5f,0,
            0.5f,-0.5f,0,
    };
    /** For a quad of size 1 by 1 these are the coordinates for the texture coordinates
     *
     */
    private static final float[] QUAD_TEXTURE_CORDS = new float[]{
            0,0,
            0,1,
            1,0,
            1,1,
    };
    /** For a quad of size 1 by 1 these are the indices needed in the correct order
     *
     */
    private static final int[] QUAD_INDICES = new int[]{
            0,1,2,
            1,2,3
    };


    //These 3 are not lists because it makes it easier to deAssign;
    //anyways when de assigning we have to make them into int[]s for openGL to be able to de allocate, so it makes
    //more sense to just keep them as int[]s to start;
    /**keep a list of all assigned VAOs, so that we don't leak memory, they will all be de - assigned at the end*/
    private int[] vaoList = new int[ModelLoader.MAX_VAOS_ALLOWED];
    /**keep a list of all assigned VBOs, so that we don't leak memory, they will all be de - assigned at the end*/
    private int[] vboList = new int[ModelLoader.MAX_VBOS_ALLOWED];
    /**keep a list of all assigned textures, so that we don't leak memory, the will all be de assigned at the end */
    private int[] textureList = new int[ModelLoader.MAX_TEXTURES_ALLOWED];

    //because the three are arrays, we need to keep a pointer to see how many elements are in each
    //the amount of VAOs assigned
    private int numVaoAssigned = 0;
    //the amount of VBOs assigned
    private int numVboAssigned = 0;
    //the amount of textures assigned
    private int numTexturesAssigned = 0;

    /** Creates a quad with width and height of 1, center around the coordinate 0,0
     *
     * @return a Mesh with width and height of 1, centered around  (0,0)
     */
    public Mesh createQuadMesh(){
        return this.createVaoMesh(ModelLoader.QUAD_VERTICES,ModelLoader.QUAD_TEXTURE_CORDS,ModelLoader.QUAD_INDICES);
    }

    /** Takes in a list of positions, and spits out a raw mesh
     *
     * @param positions an array of positions in the form of {x1,y1,z1,x2,y2,z2...,xn,yn,zn}. Rather than repeating
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
     * @return a raw mesh that has the data of the positions stored in a VAO (inside of the Mesh)
     */
    public Mesh createVaoMesh(float[] positions,float[] textureCords, int[] indices){


        //first we create an empty vaoID, and get the pointer to that VAO so it can be accessed later
        int vaoID = this.createEmptyVAO();


        //add the positional data into one of the slots (0 by default). Now the VAO has actual information inside it
        this.pushDataInAttributeList(ModelLoader.VERTEX_ATTRIBUTE_SLOT,
                positions,
                ModelLoader.VERTEX_DATA_DEFAULT_USAGE,
                ModelLoader.FLOATS_PER_3D_VERTEX);

        //add the texture coordinate data int the first slot, now the vao has positional and texture coordinate data inside it
        this.pushDataInAttributeList(ModelLoader.TEXTURE_COORDINATES_ATTRIBUTE_SLOT,
                textureCords,
                ModelLoader.TEXTURE_COORDINATE_DATA_DEFAULT_USAGE,
                ModelLoader.FLOATS_PER_2D_VERTEX);

        //VAO is done being handled and can be unbound
        this.unBindVAO();

        //convert the int[] of indices to IntBuffer which openGL understands better
        IntBuffer indexBuffer = this.pushDataInIntBuffer(indices);


        //the data has been gathered so now we can return an object that is based on the VAO
        //the second argument is saying how many vertices there are, which is the total amount of floats, divided
        //by the amount of floats needed to represent one vertex
        //last one is the the index buffer, which has been explained lots of times, but is basically
        //an array that tells the order of vertices, by mentioning the vertex index rather than the actual
        //vertex which takes more memory
        return new Mesh(vaoID,indices.length,indexBuffer);

    }

    /** Loads the texture provided to V-RAM of the gpu
     *
     * @param context any android context that can be used to load images from the R.drawable directory
     * @param imagePointer an integer that points to the requested file. Should be in the form of R.drawable.__
     *                     please make the image width and height (pixels) powers of two, i.e. 64,128,1024...
     * @return the id of the texture, so that it can be be written to shader variables later on
     */
    public int loadTexture(Context context,int imagePointer){
        //first convert the image file into a bitmap
        Bitmap texture = BitmapFactory.decodeResource(context.getResources(),imagePointer);

        //create a texture id at the specified location in the array
        GLES30.glGenTextures(1,this.textureList,this.numTexturesAssigned);

        //now bind it with the array
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.textureList[this.numTexturesAssigned]);

        // create nearest filtered texture
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);


        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, texture, 0);


        //bitmap no longer needed
        texture.recycle();

        //assigned one more texture so need to keep track
        this.numTexturesAssigned++;


        //return the id that openGL gave us
        //its numTexturesAssigned - 1 because we just post incremented the line before, technically we could just post
        //increment inside the the actual indexing portion, but this is more understandable
        return this.textureList[this.numTexturesAssigned - 1];
    }



    /** Creates an empty VAO and returns the ID. It also binds the VAO by default, so you don't have to worry about
     * binding it after calling this method.
     *
     *
     * @return returns the id of the created VAO
     */
    private int createEmptyVAO(){

        //the n parameter refers to the amount we want created, where we only want 1 so it's 1
        //the second parameter (vaoList) is where openGL "returns" the pointer open GL requires we pass in a int[], where it will put the pointer into
        // the corresponding element of the passed in array.
        //the third parameter is always the amount of bytes we that are already in the array
        GLES30.glGenVertexArrays(1,this.vaoList,this.numVaoAssigned);


        //bind it for future use. Binding is like saying we need it right now, and load it up
        //again bind the vaoID that was given to us by openGL, so we're just binding the VAO that we just made
        GLES30.glBindVertexArray(this.vaoList[this.numVaoAssigned]);


        //add the id to the vao list so that we can keep track of the memory we're allocating,
        this.numVaoAssigned++;


        //return the id that openGL gave for us, which is only 1, so no point in returning the whole array
        //its this.numVaoAssigned minus 1 because we post incremented it the line before
        return this.vaoList[this.numVaoAssigned - 1];
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

        //one more vbo has been assigned
        this.numVboAssigned++;
    }

    /** Once a VAO is done being used, it should be unbound, as it's not needed any more
     *
     */
    private void unBindVAO(){

        //to unbind arrays you specify the ID as 0. This always unbinds the currently bound array
        GLES30.glBindVertexArray(0);
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



    /** After the game has finished, the VAOs and VBOs are no longer needed and should be de allocated from memory.
     * This method de assigns all of that
     *
     */
    public void recycle(){
        //go through each VAO and deallocate each one

        //delete the amount of VAOs assigned from the vaoList starting at the beginning
        GLES30.glDeleteVertexArrays(this.numVaoAssigned,this.vaoList,0);

        //delete the amount of VBOs assigned from the vboList starting at the beginning
        GLES30.glDeleteVertexArrays(this.numVaoAssigned,this.vboList,0);

        //delete the amount of textures assigned from the texture list starting at the beginning
        GLES30.glDeleteTextures(this.numTexturesAssigned,this.textureList,0);
    }




}
