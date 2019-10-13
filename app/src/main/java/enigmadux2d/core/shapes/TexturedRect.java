package enigmadux2d.core.shapes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES10;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;

/** An Enigmadux rect. Used for drawing, collision detection, and more
 *
 * @author Manu Bhat
 * @version BETA
 */
public class TexturedRect extends EnigmaduxComponent {
    // buffer holding the vertices
    private FloatBuffer vertexBuffer;
    //vertices used for open gl
    private float[] vertices;
    // buffer holding the texture coordinates
    private FloatBuffer textureBuffer;
    //the pointers to the textures used for open gl
    private int[] textures = new int[1];

    //how much to translate it by in the x direction
    private float deltaX = 0;
    //how much to translate it by in the y direction
    private float deltaY = 0;
    //how much to translate it by in the z direction
    private float deltaZ = 0;
    //how much to scale it by in the x direction
    private float scaleX = 1;
    //how much to scale it by in the y direction;
    private float scaleY = 1;
    //how much to rotate it along the 0,0,1 axis
    private float angle = 0;

    //original width and height before padding
    protected int orgW,orgH;
    //width and height of the bitmap after padding
    protected int afterW,afterH;

    //sees if the texture has been loaded or not
    protected boolean isTextureLoaded;






    /** Default Constructor
     *
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public TexturedRect( float x, float y, float w, float h) {
        super(x,y,w,h);


        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;


        vertices = new float[] {
            x,y,0,
            x,y+h,0,
            x+w,y,0,
            x+w,y+h,0
        };


        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    /** Loads the vertices from a float[] to a buffer
     *
     * @param vertices the vertices, should be in the form of {x1,y1,z1,x2,y2,z2 ...}, bottom right, top right, bottom left, top left
     */
    protected void loadVertexBuffer(float[] vertices){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }


    /** Loads the texture from a float[] to a buffer
     *
     * @param texture the texture represented as a float[]. All sub values should be 0 to 1f
     */
    protected void loadTextureBuffer(float[] texture){

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    /** Binds the given image to the rect
     *
     * @param gl an instance of GL10 used to access open gl
     * @param context any android context use to get the resources (this is subject to change)
     * @param textureID the android reference to the R.drawable.* image
     */
    public void loadGLTexture(@NonNull GL10 gl, Context context,int textureID) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),textureID);

        this.loadGLTexture(gl,bitmap);

    }



    /** Binds the given bitmap image to the rect.
     *
     * (possible optimizations: don't recreate the bitmap if it's already fine. And make it so you can pass in the texture possibly separate method
     *
     * @param gl an instance of GL10 used to access open gl
     * @param bitmap The bitmap that defines the texture of the rect
     */
    public void loadGLTexture(@NonNull GL10 gl,Bitmap bitmap){
        this.isTextureLoaded = true;

        this.orgW = bitmap.getWidth();//original width
        this.orgH = bitmap.getHeight();//original height

        //if (orgW/orgH != this.w/this.h)
        //    throw new IllegalArgumentException("Image Dimensions (" + bitmap.getWidth() + "," + bitmap.getHeight() +  ") not mappable to TexturedRect Dimensions (" + (vertices[6]-vertices[0]) +"," + (vertices[4]-vertices[1]) + ")");




        this.afterW = TexturedRect.nextPowerTwo(this.orgW);
        this.afterH = TexturedRect.nextPowerTwo(this.orgH);
        bitmap = TexturedRect.padBitmap(bitmap, 0, afterW - bitmap.getWidth(), 0,afterH - bitmap.getHeight());


        loadTextureBuffer(new float[] {
                0.0f,1,//bottom left
                0.0f,(float) (afterH - this.orgH)/afterH,//top left
                (float) this.orgW/afterW,1,//bottom right
                (float) this.orgW/afterW,(float) (afterH - orgH)/afterH//top right
                }
        );

        /*loadTextureBuffer(new float[]{
                0.0f,1f,
                0,0,
                0.5f,1f,
                0.5f,0
        });*/


        // generate one texture pointer
        gl.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // create nearest filtered texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        // loading texture as late as possible as to save memory (think)

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();
    }

    /** translates the TexturedRect
     *
     * @param x how much to translate in the x direction
     * @param y how much to translate in the y direction
     */
    public void setTranslate(float x,float y){
        this.deltaX = x;
        this.deltaY = y;
    }


    /** The draw method for the TexturedRect with the GL context. Draws the TexturedRect with the texture given.
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void draw(GL10 gl,float[] parentMatrix) {
        if (! this.visible || ! this.isTextureLoaded){
            return;
        }
        gl.glLoadMatrixf(parentMatrix,0);
        gl.glTranslatef(this.deltaX,this.deltaY,this.deltaZ);
        gl.glRotatef(this.angle,0,0,1);
        gl.glScalef(this.scaleX,this.scaleY,1);
        // bind the previously generated texture
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // Point to our buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // Set the face rotation
        gl.glFrontFace(GL10.GL_CW);

        // Point to our vertex buffer
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

        // Draw the vertices as triangle strip
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    /** Sees whether two TexturedRect objects share any space together
     *
     * @param otherRect the rect you want to compare this one too
     * @return Whether or not the two TexturedRect objects collide (true = collision, false = no collision)
     */
    public boolean collidesWith(TexturedRect otherRect){
        return this.x <  otherRect.getX() + otherRect.getW() &&
                this.x + this.w > otherRect.getX() &&
                this.y < otherRect.getY() + otherRect.getH() &&
                this.y + this.h < otherRect.getY();
    }

    /**
     * Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }

    /** Gets the open gl x coordinate
     *
     * @return the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     */
    public float getX(){
        return this.x;
    }

    /** Gets the open gl y coordinate
     *
     * @return the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     */
    public float getY(){
        return this.y;
    }

    /** Gets the open gl width
     *
     * @return the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     */
    public float getW(){
        return this.w;
    }

    /** Gets the open gl height
     *
     * @return the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public float getH(){
        return this.h;
    }

    /** Given a number input, returns the first power of two to be higher or equal to it
     * retrieved from https://www.geeksforgeeks.org/smallest-power-of-2-greater-than-or-equal-to-n/
     *
     * @param n the minimum threshold of the output
     * @return the first integer power of two to be greater than or equal to input
     */
    private static int nextPowerTwo(int n){
        int count = 0;

        // First n in the below
        // condition is for the
        // case where n is 0
        if (n > 0 && (n & (n - 1)) == 0)
            return n;

        while(n != 0)
        {
            n >>= 1;
            count += 1;
        }

        return 1 << count;
    }

    /** Slightly expensive. In essence creates a large bitmap of white space, and draws the src on top of that.
     * Therefore there will be a white margin around the outside of the image. (method retrieved from
     * https://stackoverflow.com/questions/6957032/android-padding-left-a-bitmap-with-white-color)
     *
     * @param src The source bitmap of which you want to get padded
     * @return A bitmap which has the the source at the exact same dimensions, however there is white space along the edges
     */
    private static Bitmap padBitmap(Bitmap src,int paddingL,int paddingR,int paddingB,int paddingT){
        Bitmap outputImage = Bitmap.createBitmap(src.getWidth() + paddingL + paddingR,src.getHeight() + paddingT + paddingB, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputImage);
        can.drawARGB(0,0,0,0); //This represents White color
        can.drawBitmap(src, paddingL, paddingT, null);
        return outputImage;
    }

    /** sees if a number is an integer power of two
     *
     * @param n the number to check
     * @return whether there exists an integer a such that 2^a == n.
     */
    private static boolean isPowerTwo(int n){
        if(n==0)
            return false;

        return (int)(Math.ceil((Math.log(n) / Math.log(2)))) ==
                (int)(Math.floor(((Math.log(n) / Math.log(2)))));

    }

    /** Unlike setTranslate, this moves the rectangle from it's current position
     *
     * @param deltaX how much to translate in the x direction from the current position
     * @param deltaY how much to translate in the y direction from the current position
     */
    public void translateFromPos(float deltaX,float deltaY){
        this.setTranslate(this.deltaX +deltaX,this.deltaY + deltaY);
    }

    /** Sets the change in z
     *
     * @param deltaZ how much too translate it in the z direction from 0
     */
    public void setDeltaZ(float deltaZ) {
        this.deltaZ = deltaZ;
    }

    /** Sets the angle around the axis 0,0,1
     *
     * @param angle the angle around axis 0,0,1
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /** Gets the angle
     *
     * @return the angle around axis 0,0,1
     */
    public float getAngle() {
        return angle;
    }

    /** Gets how much it is translated by in the x direction
     *
     * @return deltaX;gets how much it is translated by in the x direction
     */
    public float getDeltaX(){
        return this.deltaX;
    }

    /** Gets how much it is translated by in the y direction
     *
     * @return deltaY; how much it is translated by in the y direction
     */
    public float getDeltaY() {
        return this.deltaY;
    }

    /** Gets how much it is translated by in the z direction
     *
     * @return deltaZ; how much it is translated by in the z direction
     */
    public float getDeltaZ() {
        return this.deltaZ;
    }

    /** Sees if the texture has been loaded as too not prematurely draw it
     *
     * @return whether or not the texture has been loaded
     */
    public boolean isTextureLoaded() {
        return this.isTextureLoaded;
    }

    /** Scales the textured rect. (before rotating and before translating)
     *
     * @param scaleX how much to scale in the x direction
     * @param scaleY how much to scale in the y direction
     */
    public void setScale(float scaleX,float scaleY){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}