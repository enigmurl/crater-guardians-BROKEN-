package enigmadux2d.core.quadRendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.SparseIntArray;

import com.enigmadux.craterguardians.values.LayoutConsts;

/** This is where the actual texture of the quad is stored. As well as instance specfic info
 *
 * @author Manu Bhat
 * @version BETA
 */
public class QuadTexture {


    /** Rather than having multiple quads with the same texture, this maps Android texture pointers ( R.drawable.x), to
     * openGL ones. This way we don't assign the duplicate memory
     *
     */
    private static SparseIntArray androidToGLTextureMap = new SparseIntArray();

    //this is where the texture pointer is stored
    protected int[] texture = new int[1];

    //scales and translates the mesh appropriately
    private final float[] scalarTranslationM = new float[16];

    /** Center X
     *
     */
    protected float x;
    /** Center Y
     *
     */
    protected float y;
    /** Width
     *
     */
    protected float w;
    /** Height
     *
     */
    protected float h;

    /** This shader limits specific channels regarding RGBA, from 0 to 1, where 0 is fully transparent, 1 opaque
     *
     */
    protected float[] shader = new float[] {1,1,1,1};

    /**float 0 to 1, represenitng radius of corner/ widht of texture in square form
     *
     */
    protected float cornerSize;


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
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;


        //set identity
        Matrix.setIdentityM(scalarTranslationM,0);
        //translate the matrix, I really don't understand why translation comes first, but it works
        Matrix.translateM(scalarTranslationM,0,x,y,0);
        //now scale it, which again should come first, but for some reason this works, other one doesn't
        Matrix.scaleM(scalarTranslationM,0,w,h,1);
        //and finally translate it
        //now actually load the texture

        this.loadAndroidTexturePointer(context,texturePointer);
    }


    /** Default Constructor, most likely will only work in a GL THREAD
     *
     * @param texturePointer an OPEN GL texture pointer, this is different from R.drawable.*, as menntioned in first constructor
     * @param x the center x position of the texture
     * @param y the center y position of the texture
     * @param w the width of the texture
     * @param h the height of the texture
     */
    public QuadTexture(int texturePointer,float x,float y,float w,float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;


        //set identity
        Matrix.setIdentityM(scalarTranslationM,0);
        //translate the matrix, I really don't understand why translation comes first, but it works
        Matrix.translateM(scalarTranslationM,0,x,y,0);
        //now scale it, which again should come first, but for some reason this works, other one doesn't
        Matrix.scaleM(scalarTranslationM,0,w,h,1);
        //and finally translate it

        this.texture[0] = texturePointer;
    }
//    /** Default Constructor, most likely will only work in a GL THREAD
//     *
//     * @param texture a bitmap representing the image
//     * @param x the center x position of the texture
//     * @param y the center y position of the texture
//     * @param w the width of the texture
//     * @param h the height of the texture
//     */
//    public QuadTexture(Bitmap texture,float x,float y,float w,float h){
//        //create a texture id at the specified location in the array
//        GLES30.glGenTextures(1, this.texture, 0);
//
//        //now bind it with the array
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.texture[0]);
//
//        // create nearest filtered texture
//        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
//        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
//
//
//        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
//        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, texture, 0);
//
//
//        //bitmap no longer needed
//        texture.recycle();
//    }

    /** load an ANDROID texture pointer (R.drawable.*)
     *
     * @param context any context that can get resources
     * @param texturePointer the ANDROID pointer to the image
     */
    public void loadAndroidTexturePointer(Context context,int texturePointer){
        int indexOfPointer = QuadTexture.androidToGLTextureMap.indexOfKey(texturePointer);
        if (indexOfPointer < 0) {
            //first convert the image file into a bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inPremultiplied = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap texture = BitmapFactory.decodeResource(context.getResources(), texturePointer,options);
            texture.setHasAlpha(true);

            texture = Bitmap.createBitmap(texture);


            //create a texture id at the specified location in the array
            GLES30.glGenTextures(1, this.texture, 0);

            //now bind it with the array
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.texture[0]);

            // create nearest filtered texture
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);


            // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, texture, 0);


            //bitmap no longer needed
            texture.recycle();

            //add it to our banking
            QuadTexture.androidToGLTextureMap.put(texturePointer,this.texture[0]);

        } else {
            this.texture[0] = QuadTexture.androidToGLTextureMap.get(texturePointer);
        }
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

    /** Gets the shader which limits channels regarding RGBA
     *
     * @return a 4 float vector, which specifies how much to multiply the RGBA channels
     */
    public float[] getShader(){
        return this.shader;
    }

    /** Set the shader which limits channels regarding RGBA
     *
     * @param r the filter on red (0 to 1)
     * @param g the filter on green (0 to 1)
     * @param b the filter on blue (0 to 1)
     * @param a the filter on alpha (0 to 1)
     */
    public void setShader(float r,float g,float b, float a){
        this.shader[0] = r;
        this.shader[1] = g;
        this.shader[2] = b;
        this.shader[3] = a;
    }

    /** Gets the corner size
     *
     * @return float 0 to 1, represenitng radius of corner/ widht of texture in square form
     */
    public float getCornerSize(){
        return this.cornerSize;
    }

    /** Gets the aspect ratio when finally displayed to the screen;
     *
     * @return the aspect ratio when finally displayed to the screen
     *
     */
    public float getAspectRatio(){
        return this.w/this.h * LayoutConsts.SCREEN_WIDTH/LayoutConsts.SCREEN_HEIGHT;
    }


    /** Recycles the components by telling open gl to delete the texture
     *
     *
     */
    public void recycle(){
       GLES30.glDeleteTextures(1,this.texture,0);
    }


    /** Deletes the sparse int array, in the event that textures need to be reloaded
     *
     */
    public static void resetTextures(){
        androidToGLTextureMap = new SparseIntArray();
    }
}
