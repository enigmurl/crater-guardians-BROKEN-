package enigmadux2d.core.quadRendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

/** This is where the actual texture of the quad is stored. As well as instance specfic info
 *
 * @author Manu Bhat
 * @version BETA
 */
public class QuadTexture {
    //this is where the texture pointer is stored
    private int[] texture = new int[1];

    //scales and translates the mesh appropriately
    private final float[] scalarTranslationM = new float[16];

    /** Default Constructor, most likely will only work in a GL THREAD
     *
     * @param context any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     */
    public QuadTexture(Context context,int texturePointer,float x,float y,float w,float h){

        //set identity
        Matrix.setIdentityM(scalarTranslationM,0);
        //translate the matrix, I really don't understand why translation comes first, but it works
        Matrix.translateM(scalarTranslationM,0,x,y,0);
        //now scale it, which again should come first, but for some reason this works, other one doesn't
        Matrix.scaleM(scalarTranslationM,0,w,h,1);
        //and finally translate it
        //now actually load the texture

        //first convert the image file into a bitmap
        Bitmap texture = BitmapFactory.decodeResource(context.getResources(),texturePointer);

        //create a texture id at the specified location in the array
        GLES30.glGenTextures(1,this.texture,0);

        //now bind it with the array
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.texture[0]);

        // create nearest filtered texture
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);


        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, texture, 0);


        //bitmap no longer needed
        texture.recycle();
    }




    /** Gets the texture pointer
     *
     * @return the id of the texture
     */
    public int getTexture(){
        return this.texture[0];
    }

    /** This gives the output matrix given the input matrix, the output matrix describes how to transform the mesh to
     * the desired position and width
     *
     * @param dumpMatrix where the output matrix will be placed
     * @param mvpMatrix the input matrix 4 by 4
     */
    public void dumpOutputMatrix(float[] dumpMatrix,float[] mvpMatrix){
        Matrix.multiplyMM(dumpMatrix,0,mvpMatrix,0,this.scalarTranslationM,0);
    }
}
