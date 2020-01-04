package enigmadux2d.core.shapes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;
import android.view.MotionEvent;


import com.enigmadux.craterguardians.MathOps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;
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
    private int[] textures ;
    //whether or not textures has been initialized
    private boolean texturesInitialized = false;

    //how much to translate it by in the x direction
    private float deltaX = 0;
    //how much to translate it by in the y direction
    private float deltaY = 0;

    //how much to scale it by in the x direction
    private float scaleX = 1;
    //how much to scale it by in the y direction;
    private float scaleY = 1;
    //how much to rotate it along the 0,0,1 axis
    private float angle = 0;


    //a value of 1,1,1,1 would mean draw the object directly, the shader alters the color of the texture, by multiplying each
    private float[] shader = new float[] {1,1,1,1};

    //how much to translate the texture coordinate x direction
    private float deltaU;
    //how much to translate the texture coordinate y direction
    private float deltaV;



    /** Default Constructor, defaults to 1 texture
     *
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public TexturedRect( float x, float y, float w, float h) {
        this(x,y,w,h,1);
    }

    /** Default Constructor
     *
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     * @param numTextures the amount of textures that this can be bind too, think of it as the number of frames in an animation
     */
    public TexturedRect( float x, float y, float w, float h,int numTextures) {
        super(x, y, w, h);

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.textures = new int[numTextures];

        vertices = new float[]{
                x, y, 0,
                x, y + h, 0,
                x + w, y, 0,
                x + w, y + h, 0
        };


        //floats are 4 bytes
        //used as a buffer for the vertex and texture buffers
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


        byteBuffer = ByteBuffer.allocateDirect(vertices.length * 8/3);
        byteBuffer.order(ByteOrder.nativeOrder());

        textureBuffer = byteBuffer.asFloatBuffer();


    }


    /** Loads the vertices from a float[] to a buffer
     *
     * @param vertices the vertices, should be in the form of {x1,y1,z1,x2,y2,z2 ...}, bottom right, top right, bottom left, top left
     */
    public void loadVertexBuffer(float[] vertices){

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }


    /** Loads the texture from a float[] to a buffer
     *
     * @param texture the texture represented as a float[]. All sub values should be 0 to 1f
     */
    public void loadTextureBuffer(float[] texture){

        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    /** Binds the given image to the rect. Defaults to save at the 0th position in the texture array
     *
     * @param gl an instance of GL10 used to access open gl
     * @param context any android context use to get the resources (this is subject to change)
     * @param textureID the android reference to the R.drawable.* image
     */
    public void loadGLTexture(@NonNull GL10 gl, Context context,int textureID) {
        this.loadGLTexture(gl,context,textureID,0);
    }

    /** Binds the given image to the rect
     *
     * @param gl an instance of GL10 used to access open gl
     * @param context any android context use to get the resources (this is subject to change)
     * @param textureID the android reference to the R.drawable.* image
     * @param index corresponds to the position in the textures array
     */
    public void loadGLTexture(@NonNull GL10 gl, Context context,int textureID, int index) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),textureID);

        this.loadGLTexture(gl,bitmap,index);

    }



    /** Binds the given bitmap image to the rect. Defaults to save at the first position in the texture array
     *
     * (possible optimizations: don't recreate the bitmap if it's already fine. And make it so you can pass in the texture possibly separate method
     *
     * @param gl an instance of GL10 used to access open gl
     * @param bitmap The bitmap that defines the texture of the rect
     */
    public void loadGLTexture(@NonNull GL10 gl,Bitmap bitmap){
        this.loadGLTexture(gl,bitmap,0);
    }

    /** Binds the given bitmap image to the rect.
     *
     * (possible optimizations: don't recreate the bitmap if it's already fine. And make it so you can pass in the texture possibly separate method
     *
     * @param gl an instance of GL10 used to access open gl
     * @param bitmap The bitmap that defines the texture of the rect
     * @param index corresponds to the position in the textures array
     */
    public void loadGLTexture(@NonNull GL10 gl,Bitmap bitmap,int index){
        int afterW = MathOps.nextPowerTwo(bitmap.getWidth());
        int afterH = MathOps.nextPowerTwo(bitmap.getHeight());
        if (afterH != bitmap.getHeight() || afterW != bitmap.getWidth()) {
            bitmap = Bitmap.createScaledBitmap(bitmap, afterW, afterH, false);
        }

        loadTextureBuffer(new float[]{
                0,1,
                0,0,
                1,1,
                1,0
        });


        // generate one texture pointer
        if (! this.texturesInitialized) {
            gl.glGenTextures(textures.length, textures, 0);
            this.texturesInitialized = true;
        }

        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[index]);

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


    /** The draw method for the TexturedRect with the GL context. Draws the TexturedRect with the texture given at pos 0
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void draw(GL10 gl,float[] parentMatrix) {
        this.draw(gl,parentMatrix,0);
    }

    /** The draw method for the TexturedRect with the GL context. Draws the TexturedRect with the texture given.
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     * @param frameNum refers to which texture to bind too
     */
    public void draw(GL10 gl,float[] parentMatrix,int frameNum) {
        if (! this.visible){
            return;
        }

        gl.glLoadMatrixf(parentMatrix,0);


        this.prepareDraw(gl,frameNum);

        gl.glMatrixMode(GL10.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glTranslatef(this.deltaU,this.deltaV,0);
        // Draw the vertices as triangle strip
        gl.glColor4f(shader[0],shader[1],shader[2],shader[3]); // This is where the magic does happen
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        this.endDraw(gl);

        gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    /** Draw without the overhead of loading the textures
     *
     * @param gl the gl object used so we can send commands
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void intermediateDraw(GL10 gl,float[] parentMatrix){
        if (! this.visible){
            return;
        }

        gl.glLoadMatrixf(parentMatrix,0);
        gl.glMatrixMode(GL10.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glTranslatef(this.deltaU,this.deltaV,0);
        // Draw the vertices as triangle strip
        gl.glColor4f(shader[0],shader[1],shader[2],shader[3]); // This is where the magic does happen
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    /** Prepares drawing, But basically means u can draw the same object multiple times without the overhead
     *
     * @param gl the gl object used so we can send commands
     * @param frameNum the frameNum in the texture
     */
    public void prepareDraw(GL10 gl,int frameNum){
        gl.glTranslatef(this.deltaX,this.deltaY,0);
        gl.glScalef(this.scaleX,this.scaleY,1);
        gl.glRotatef(this.angle,0,0,1);



        // bind the previously generated texture
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[frameNum]);

        // Point to our buffers
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);


        // Point to our vertex buffer
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
    }

    /** Ends the drawing period
     *
     * @param gl gl object used so we can send commands
     */
    public void endDraw(GL10 gl){
        //Disable the client state before leaving
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

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
        return this.angle;
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

    /** Sets how much to translate the deltaU
     *
     * @param deltaU how much to translate in the x direction
     * @param deltaV how much to translate in the y direction
     */
    public void setTextureDelta(float deltaU,float deltaV){
        this.deltaU = deltaU;
        this.deltaV = deltaV;
    }

    /** Sets the shader, which modifies the color of the texture
     *
     * @param r the red value of the filter, 1.0 is full red
     * @param g the green value of the filter, 1.0 is full red
     * @param b the blue value of the filter, 1.0 is full red
     * @param a the alpha value of the filter, 1.0 is full alpha
     */
    public void setShader(float r,float g,float b,float a){
        this.shader[0] = r;
        this.shader[1] = g;
        this.shader[2] = b;
        this.shader[3] = a;
    }

    /** Gets the array representing the shader
     *
     * @return an array representing the shader in rgba, with 0 being no power, 1 being full
     */
    public float[] getShader() {
        return shader;
    }
}