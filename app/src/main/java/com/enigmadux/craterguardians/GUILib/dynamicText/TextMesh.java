package com.enigmadux.craterguardians.GUILib.dynamicText;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/** Rather than keep on expensively calculating the coordinates, if the text does not change it's better to preload the data
 *
 * @author Manu Bhat
 * @verion BETA
 */
public class TextMesh {


    /** The float buffer of positions triangle strip form
     *
     */
    private FloatBuffer vertices;

    /** The float buffer of texture cords triangle strip form
     *
     */
    private FloatBuffer textureCords;


    /** The shader in the form of rgba that dictates color
     *
     */
    private float[] shader;

    /** Stores the order in which to render the vertices
     *
     */
    private IntBuffer elementArray;

    /** A text mesh
     *
     * @param vertices the vertices, in a normal fashion
     * @param textureCords the texture cords, in a normal fashion GLES30.TRIANGLES ()
     * @param shader a shader in the form of rgba that dictates color
     */
    public TextMesh(float[] vertices,float[] textureCords,float[] shader){
        this.vertices = this.pushDataInFloatBuffer(vertices);
        this.textureCords = this.pushDataInFloatBuffer(textureCords);
        this.shader = shader;

        int[] elementArray = new int[vertices.length/2];

        //amount of quads
        for (int i = 0;i<vertices.length/12;i++){
            elementArray[6 * i] = i * 4;
            elementArray[6 * i + 1] = i * 4 + 1;
            elementArray[6 * i + 2] = i * 4 + 2;
            elementArray[6 * i + 3] = i * 4 + 1;
            elementArray[6 * i + 4] = i * 4 + 2;
            elementArray[6 * i + 5] = i * 4 + 3;

        }
        Log.d("DYNAMIC TEXT","Element Array: " + Arrays.toString(elementArray));

        this.elementArray = this.pushDataInIntBuffer(elementArray);
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



    /** Gets the texture cords
     *
     * @return the texture cords
     */
    public FloatBuffer getTextureCords() {
        return textureCords;
    }

    /** Gets the vertices
     * @return the vertices
     */
    public FloatBuffer getVertices() {
        return vertices;
    }

    /** Gets the shader
     *
     * @return the shader (RGBA form)
     */
    public float[] getShader() {
        return shader;
    }

    /** Gets the element array
     *
     * @return the element array that tells how to order the vertices
     */
    public IntBuffer getElementArray() {
        return elementArray;
    }
}