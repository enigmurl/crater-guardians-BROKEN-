package enigmadux2d.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.SoundLib;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Scanner;

import javax.microedition.khronos.opengles.GL10;

/** Not exactly part of enigmadux2d ;) But is used to make three d models from the given resource
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class Model3D extends EnigmaduxComponent {

    //the pointers to the textures used for open gl
    private int[] textures = new int[1];

    //the list of vertices in the form of {x1,y1,z1,x2,y2,z2,x3,y3,z3}
    private FloatBuffer vertices;
    //the indicices of the vertex num used to form triangles
    private float[] indices;
    //the list of the the uv coordintaes in the form of  {x1,y1,x2,y2,x3,y3}
    private FloatBuffer uvCoordinates;
    //the Bitmap given from the texture resource
    private Bitmap bitmapTexture;

    //the number of vertices stored in "vertices" variable. There may be repeats
    private int numVertices;


    /** Default constructor todo right now just put in the vertices in an inefficeint manner, indices are ignored
     *
     * @param verticesResource file name under raw that has a list of vertices in the form of numVertices,x1,y1,z1..xN,yN,ZN)
     * @param indicesResource file name under raw that defines each triangle based on the vertices
     * @param uvResource file name under raw that indicates what maps the triangles to the textures
     * @param textureResource pointer to the image in R.drawable.*;
     * @param context any context that can access resources
     */
    public Model3D(String verticesResource, String indicesResource, String uvResource, int textureResource, Context context){
        super(0,0,0,0);

        ByteBuffer dataBuffer;

        float[] vertices = Model3D.getVertices(verticesResource,context);
        dataBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        this.vertices = dataBuffer.asFloatBuffer();

        this.numVertices = vertices.length/3;

        this.indices = Model3D.getIndicies(indicesResource,context);

        float[] uvCoordinates = Model3D.flipTextureY(Model3D.getTextures(uvResource,context));
        dataBuffer = ByteBuffer.allocateDirect(uvCoordinates.length * 4);
        this.uvCoordinates = dataBuffer.asFloatBuffer();


        this.bitmapTexture = BitmapFactory.decodeResource(context.getResources(),textureResource);
    }

    private static float[] getIndicies(String resource,Context context){
        float[] indices = null;

        try {
            Scanner stdin = new Scanner(context.openFileInput(resource));

            int numTriangles = stdin.nextInt();
            indices = new float[numTriangles * 3];


            for (int i = 0;i<numTriangles;i++){
                float v1 = stdin.nextInt();
                float v2 = stdin.nextInt();
                float v3 = stdin.nextInt();
                indices[i*3] = v1;
                indices[i*3 + 1] = v2;
                indices[i*3 + 2] = v3;
            }

            stdin.close();
        } catch (IOException e){
        }
        if (indices == null){
            return new float[0];
        } else {
            return indices;
        }
    }

    /** Given the points to the file, this returns the list of vertices in the form of {x1,y1,z1,x2,y2,z2,x3,y3,z3}
     *
     * @param resource the name of the file under the raw folder;
     * @param context any context that can access resources
     * @return the list of vertices in the form of {x1,y1,z1,x2,y2,z2,x3,y3,z3}
     */
    private static float[] getVertices(String resource,Context context){
        float[] vertices = null;

        try {
            Scanner stdin = new Scanner(context.openFileInput(resource));

            int numVertices = stdin.nextInt();
            vertices = new float[numVertices * 3];


            for (int i = 0;i<numVertices;i++){
                float x = stdin.nextInt();
                float y = stdin.nextInt();
                float z = stdin.nextInt();
                vertices[i*3] = x;
                vertices[i*3 + 1] = y;
                vertices[i*3 + 2] = z;
            }

            stdin.close();
        } catch (IOException e){
        }
        if (vertices == null){
            return new float[0];
        } else {
            return vertices;
        }
    }

    /** Gets the textures from the raw folder, however y coordinates still need to be flipped
     *
     * @param resource the name of the file under the raw folder;
     * @param context any context that can access resources
     * @return  the list of the the uv coordinates in the form of  {x1,y1,x2,y2,x3,y3}
     */
    private static float[] getTextures(String resource,Context context){
        float[] uvCoordinates = null;

        try {
            Scanner stdin = new Scanner(context.openFileInput(resource));

            int numUVCoordinates = stdin.nextInt();
            uvCoordinates = new float[numUVCoordinates * 2];


            for (int i = 0;i<numUVCoordinates;i++){
                float x = stdin.nextInt();
                float y = stdin.nextInt();
                uvCoordinates[i*3] = x;
                uvCoordinates[i*3 + 1] = y;
            }

            stdin.close();
        } catch (IOException e){
        }
        if (uvCoordinates == null){
            return new float[0];
        } else {
            return uvCoordinates;
        }
    }

    /** Because android coordinates are y = 0 at the top, this flips it so y = 0 is at the bottom
     *
     * @param uvCoordinates the list of the the uv coordintaes in the form of  {x1,y1,x2,y2,x3,y3} without the y flipped
     * @return the list of the the uv coordintaes in the form of  {x1,y1,x2,y2,x3,y3} but each y = 1- orginal y
     */
    private static float[] flipTextureY(float[] uvCoordinates){
        for (int i = 1;i<uvCoordinates.length;i+=2){
            uvCoordinates[i] = 1 - uvCoordinates[i];
        }
        return uvCoordinates;
    }


    /** Binds the given bitmap image to the rect.
     *
     * (possible optimizations: don't recreate the bitmap if it's already fine. And make it so you can pass in the texture possibly separate method
     *
     * @param gl an instance of GL10 used to access open gl
     */
    public void loadGLTexture(GL10 gl){
        // generate one texture pointer
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);


        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.bitmapTexture, 0);
    }


    /** Draws the model onto the world with the parent matrix as a modifier
     *
     * @param gl           the GL10 object used to access openGL
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        //load the parent matrix
        gl.glLoadMatrixf(parentMatrix,0);


        // bind the previously generated texture
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // Point to our buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // Set the face rotation
        gl.glFrontFace(GL10.GL_CW);

        // Point to our vertex buffer
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertices);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.uvCoordinates);

        // Draw the vertices as triangle strip
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.numVertices);
        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    /** Don't do anything
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return false all the time
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
