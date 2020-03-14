package enigmadux2d.core.shapes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;


import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.MathOps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shaders.ShaderProgram;

/** An Enigmadux rect. Used for drawing, collision detection, and more
 *
 * @author Manu Bhat
 * @version BETA
 */
public class TexturedRect extends EnigmaduxComponent {
    //the maximum dimension of a bitmap allowed
    private static final int MAX_DIMENIONS = 1024;

   private static final String vertexShaderCode =
           "precision mediump float;" +
           "uniform mat4 uMVPMatrix;" +
           "uniform vec2 texCoordDelta;"+

           "attribute vec4 vPosition;" +
           "attribute vec2 TexCoordIn;" +


           "varying vec2 TexCoordOut;" +

           "void main() {" +
           //the matrix must be included as a modifier of gl_Position
           "  gl_Position = uMVPMatrix * vPosition;" +
           "  TexCoordOut = TexCoordIn + texCoordDelta;" +
           "}";
    //fragement shader code
    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D Texture;" +     // The input texture.
            "varying lowp vec2 TexCoordOut;"  + // Interpolated texture coordinate per fragment.
            "uniform vec4 shaderIn;" +
            "void main() {" +
            //"  gl_FragColor = ();" +
            "  gl_FragColor = texture2D(Texture,TexCoordOut) * shaderIn;" +
            "}";

    //an empty openGL program
    private static int mProgram;



    // buffer holding the vertices
    private FloatBuffer vertexBuffer;
    //vertices used for open gl
    protected float[] vertices;
    //buffer holding the indices
    private ShortBuffer indexBuffer;
    //the acutal indices in array form
    private short[] indices;

    // buffer holding the texture coordinates
    private FloatBuffer textureBuffer;
    //the pointers to the textures used for open gl
    private int[] textures ;
    //whether or not textures has been initialized
    private boolean texturesInitialized = false;

    //how much to translate it by in the deltX direction
    private float deltaX = 0;
    //how much to translate it by in the y direction
    private float deltaY = 0;

    //how much to scale it by in the deltX direction
    private float scaleX = 1;
    //how much to scale it by in the y direction;
    private float scaleY = 1;
    //how much to rotate it along the 0,0,1 axis
    private float angle = 0;


    //a value of 1,1,1,1 would mean draw the object directly, the shader alters the color of the texture, by multiplying each
    private float[] shader = new float[] {1,1,1,1};


    //how much to translate the texture coordinate deltX direction
    private float[] deltaTextureCoordinates = new float[2];


    //how to apply the scaling and rotating
    private final float[] finalMatrix = new float[16];

    //position handle of the vertex pointer
    private int verticesHandle;
    //position handle of the texture coordinates
    private int textureCordHandle;
    //position handle of the texture delta coordinates
    private int textureDeltaHandle;
    //position handle of the actual texture
    private int textureHandle;
    //position handle of the view projection matrix;
    private int vPMatrixHandle;
    //position handle of the shader vec
    private int shaderHandle;


    /** Default Constructor, defaults to 1 texture
     *
     * @param x the open gl coordinate of the rect, left most edge deltX coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public TexturedRect( float x, float y, float w, float h) {
        this(x,y,w,h,1);
    }

    /** Default Constructor
     *
     * @param x the open gl coordinate of the rect, left most edge deltX coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
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


        this.indices = new short[] {0,1,2,1,2,3};
        byteBuffer = ByteBuffer.allocateDirect(indices.length * Short.SIZE/8);
        byteBuffer.order(ByteOrder.nativeOrder());


        indexBuffer = byteBuffer.asShortBuffer();
        indexBuffer.put(this.indices);
        indexBuffer.position(0);

    }

    /** Loads the program
     *
     */
    public static void loadProgram() {
        int vertexShader = CraterRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = CraterRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        Log.e("TEXTURED:","vertex: " + vertexShader + " fragement: " +  fragmentShader);
        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram();

        // add the vertex shader to program
        GLES30.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES30.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(mProgram);

    }


    /** Gets integers that represent the handles
     *
     */
    private void loadHandles() {
        // get handle to vertex shader's vPosition member
        this.verticesHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");

        this.textureHandle = GLES30.glGetUniformLocation(mProgram, "Texture");

        this.textureCordHandle = GLES30.glGetAttribLocation(mProgram, "TexCoordIn");

        this.textureDeltaHandle = GLES30.glGetUniformLocation(mProgram,"texCoordDelta");

        this.vPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        this.shaderHandle = GLES30.glGetUniformLocation(mProgram,"shaderIn");

        //Log.e("TEXTURED:", mProgram + " " + this.verticesHandle + " " + this.textureHandle  +"  " + this.textureCordHandle + " " + this.vPMatrixHandle + " " + this.shaderHandle + " " + this.textureDeltaHandle);

    }

    /** Loads the indices from a short[] to a buffer
     *
     * @param indices the indices which tells how to draw, right now the mode is in GL_TRIANGLES, so note that
     */
    public void loadIndexBuffer(short[] indices){
        this.indices = indices;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(indices.length * Short.SIZE/8);
        byteBuffer.order(ByteOrder.nativeOrder());

        indexBuffer = byteBuffer.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

    }

    /** Loads the vertices from a float[] to a buffer
     *
     * @param vertices the vertices, should be in the form of {x1,y1,z1,x2,y2,z2 ...}, bottom right, top right, bottom left, top left
     */
    public void loadVertexBuffer(float[] vertices){
        this.vertices = vertices;
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
    public void loadTextureBuffer(float[] texture){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    /** Binds the given image to the rect. Defaults to save at the 0th position in the texture array
     *  @param context any android context use to get the resources (this is subject to change)
     * @param textureID the android reference to the R.drawable.* image
     */
    public void loadGLTexture(Context context, int textureID) {
        this.loadGLTexture(context,textureID,0);
    }

    /** Binds the given image to the rect
     *  @param context any android context use to get the resources (this is subject to change)
     * @param textureID the android reference to the R.drawable.* image
     * @param index corresponds to the position in the textures array
     */
    public void loadGLTexture(Context context, int textureID, int index) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),textureID);

        this.loadGLTexture(bitmap,index);

    }



    /** Binds the given bitmap image to the rect. Defaults to save at the first position in the texture array
     *
     * (possible optimizations: don't recreate the bitmap if it's already fine. And make it so you can pass in the texture possibly separate method
     *
     * @param bitmap The bitmap that defines the texture of the rect
     */
    public void loadGLTexture(Bitmap bitmap){
        this.loadGLTexture(bitmap,0);
    }


    private static long memory;
    /** Binds the given bitmap image to the rect. Additionally loads handles
     *
     * (possible optimizations: don't recreate the bitmap if it's already fine. And make it so you can pass in the texture possibly separate method
     *  @param bitmap The bitmap that defines the texture of the rect
     * @param index corresponds to the position in the textures array
     */
    public void loadGLTexture(Bitmap bitmap, int index){
        this.loadHandles();


        int afterW = Math.min(MathOps.nextPowerTwo(bitmap.getWidth()),TexturedRect.MAX_DIMENIONS);
        int afterH = Math.min(MathOps.nextPowerTwo(bitmap.getHeight()),TexturedRect.MAX_DIMENIONS);
        Log.d("TEXTURED RECT: ", "Before w " + bitmap.getWidth() + " h: " + bitmap.getHeight() + "AFTER:  w: "  + afterW  + " h: " + afterH);
        memory+= afterH * afterW  *4;
        Log.d("TEXTURED RECT,","memory: " + memory);
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
            GLES30.glGenTextures(textures.length, textures, 0);
            this.texturesInitialized = true;
        }

        // ...and bind it to our array
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[index]);

        // create nearest filtered texture
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // loading texture as late as possible as to save memory (think)

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();
    }

    /** translates the TexturedRect
     *
     * @param x how much to translate in the deltX direction
     * @param y how much to translate in the y direction
     */
    public void setTranslate(float x,float y){
        this.deltaX = x;
        this.deltaY = y;
    }


    /** The draw method for the TexturedRect with the GL context. Draws the TexturedRect with the texture given at pos 0
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void draw(float[] parentMatrix) {
        this.draw(parentMatrix,0);
    }

    /** The draw method for the TexturedRect with the GL context. Draws the TexturedRect with the texture given.
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     * @param frameNum refers to which texture to bind too
     */
    public void draw(float[] parentMatrix, int frameNum) {
        if (! this.visible){
            return;
        }

        this.prepareDraw(frameNum);




        this.intermediateDraw(parentMatrix);



        this.endDraw();

    }
    public static int numDraws = 0;

    /** Draw without the overhead of loading the textures
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public void intermediateDraw(float[] parentMatrix) {
        if (!this.visible) {
            return;
        }
        numDraws++;
        // Pass the projection and view transformation to the shader
        Matrix.translateM(finalMatrix, 0, parentMatrix, 0, this.deltaX, this.deltaY, 0);
        Matrix.scaleM(finalMatrix, 0, this.scaleX, this.scaleY, 1);
        Matrix.rotateM(finalMatrix, 0, this.angle, 0, 0, 1);

        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, finalMatrix, 0);


        GLES30.glUniform2fv(this.textureDeltaHandle, 1, this.deltaTextureCoordinates, 0);


        if (this.vertices.length/3 == 4)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, this.vertices.length / 3);
        else
            GLES30.glDrawElements(GLES30.GL_TRIANGLES,this.indices.length,GLES30.GL_UNSIGNED_SHORT,this.indexBuffer);

    }
    /** Prepares drawing, But basically means u can draw the same object multiple times without the overhead
     *
     * @param frameNum the frameNum in the texture
     */
    public void prepareDraw(int frameNum){
        if (mProgram != ShaderProgram.currentProgram) {
            GLES30.glUseProgram(mProgram);
            ShaderProgram.currentProgram = mProgram;
        }

        // Point to our buffers
        GLES30.glEnableVertexAttribArray(verticesHandle);
        // Prepare the rect coordinate data
        GLES30.glVertexAttribPointer(verticesHandle,3, GLES30.GL_FLOAT, false, 12, vertexBuffer);

        GLES30.glEnableVertexAttribArray(textureCordHandle);
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(textureCordHandle,2, GLES30.GL_FLOAT, false, 8, textureBuffer);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[frameNum]);
        GLES30.glUniform1i(textureHandle, 0);

        GLES30.glEnableVertexAttribArray(shaderHandle);
        // Prepare the triangle coordinate data
        GLES30.glUniform4fv(shaderHandle, 1,this.shader,0);


    }

    /** Ends the drawing period
     *
     */
    public void endDraw(){

        GLES30.glDisableVertexAttribArray(verticesHandle);
        GLES30.glDisableVertexAttribArray(textureCordHandle);
        GLES30.glDisableVertexAttribArray(shaderHandle);

    }

    /** De assigns the memory contents of this quad
     *
     */
    public void recycle(){
        GLES30.glDeleteTextures(this.textures.length,this.textures,0);
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

    /** Gets the open gl deltX coordinate
     *
     * @return the open gl coordinate of the rect, left most edge deltX coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
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
     * @param scaleX how much to scale in the deltX direction
     * @param scaleY how much to scale in the y direction
     */
    public void setScale(float scaleX,float scaleY){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /** Sets how much to translate the deltaU
     *
     * @param deltaU how much to translate in the deltX direction
     * @param deltaV how much to translate in the y direction
     */
    public void setTextureDelta(float deltaU,float deltaV){
        this.deltaTextureCoordinates[0] = deltaU;
        this.deltaTextureCoordinates[1] = deltaV;
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