package enigmadux2d.core.shaders;

import android.content.Context;
import android.opengl.GLES30;

import enigmadux2d.core.quadRendering.QuadMesh;

/** This a shader for GUIs, as opposed to in game objects
 *
 * @author Manu Bhat
 * @version BETA
 *
 * BACKUP
 * //uniform mat4 mvpMatrix;
 * //uniform float cornerSize;
 * //uniform float aspectRatio;
 *
 * //attribute vec3 position;
 *
 * //varying vec2 pass_textureCord;
 *
 * //void main() {
 *     //pass_textureCord = vec2(position.x+0.5,0.5 - position.y);
 *     //if (pass_textureCord.x < cornerSize){
 *         //pass_textureCord.x /= aspectRatio;
 *     //}
 *     //if (pass_textureCord.x > 1. - cornerSize){
 *     //    pass_textureCord.x = 1. - (1. -pass_textureCord.x)/aspectRatio;
 *     //}
 *     //gl_Position = mvpMatrix * vec4(position,1);
 * //}
 */
public class GUIShader extends ShaderProgram {

    /** The keyword used for vertex information, located in the vertex shader file
     *
     */
    private static final String VERTEX_KEYWORD = "position";

    /** The keyword used for the mvpMatrix, located in the vertex shader file
     *
     */
    private static final String MATRIX_KEYWORD = "mvpMatrix";

    /** The keyword used for the texture,located in the fragment shader file
     *
     */
    private static final String TEXTURE_KEYWORD = "Texture";

    /** The keyword used for the shader (channel filter), located in the fragment shader file
     *
     */
    private static final String SHADER_KEYWORD = "shader";

    /** The keyword used for the corner size located in the vertex shader file
     *
     */
    private static final String CORNER_KEYWORD = "cornerSize";

    /** The keyword used for the aspect ratio variable locate in the vertex shader file
     *
     */
    private static final String ASPECT_KEYWORD = "aspectRatio";


    //pointer to the texture variable in the fragment shader file
    private int textureLocation;
    //pointer to the matrix in the vertex shader file
    private int matrixLocation;
    //pointer to the "shader" variable in the fragment shader file, which filters channels
    private int shaderLocation;
    //pointer to the "cornerSize" variable in the vertex shader file
    private int cornerSizeLocation;
    //pointer to "aspectRatio" variable in the vertex shader
    private int aspectRatio;


    /** Default constructor
     *
     * @param context any context that can access resources
     * @param vertexShaderFile the pointer to the vertex shader file using R.raw.*;
     * @param fragmentShaderFile the pointer to the fragment shader file using R.raw.*;
     */
    public GUIShader(Context context, int vertexShaderFile, int fragmentShaderFile){
        super(context,vertexShaderFile,fragmentShaderFile);

    }

    /** Only attribute that needs to bound are the vertices
     *
     */
    @Override
    protected void bindAttributes() {
        //only the vertices need to be bound
        this.bindAttribute(QuadMesh.VERTEX_SLOT,GUIShader.VERTEX_KEYWORD);
    }

    /** Gets the variable locations
     *
     */
    @Override
    protected void getVariableLocations() {
        //get the texture location
        this.textureLocation = this.getUniformLocation(GUIShader.TEXTURE_KEYWORD);
        //get the matrix location
        this.matrixLocation = this.getUniformLocation(GUIShader.MATRIX_KEYWORD);
        //get the shader location
        this.shaderLocation = this.getUniformLocation(GUIShader.SHADER_KEYWORD);
        //get the corner location
        this.cornerSizeLocation = this.getUniformLocation(GUIShader.CORNER_KEYWORD);
        //get the aspect ratio location
        this.aspectRatio = this.getUniformLocation(GUIShader.ASPECT_KEYWORD);
    }

    /** Writes the texture, and binds it to the shader
     *
     * @param textureID the id of the texture that openGL gave, (it will dump it to an int[])
     */
    public void writeTexture(int textureID) {
        super.writeTexture(textureID);
    }

    /** Writes the mvpMatrix,which transforms the vertices
     *
     * @param matrix a float[16], that represents the model view projection matrix
     */
    public void writeMatrix(float[] matrix){
        super.writeMatrix(this.matrixLocation,matrix);
    }

    /** Writes the shader uniform variable, which filters RGBA channels
     *
     * @param shader a four float vector that represents the actual shader
     */
    public void writeShader(float[] shader){
        GLES30.glUniform4fv(this.shaderLocation,1,shader,0);
    }

    /** Writes info about corners, for most quads it's not neccesary
     *
     * @param cornerSize the size of a single corner/ width (in square form)
     * @param aspectRatio w/h of the rectangle * w/h of the screen
     */
    public void writeCornerInfo(float cornerSize,float aspectRatio){
        super.writeUniformFloat(this.cornerSizeLocation,cornerSize);
        super.writeUniformFloat(this.aspectRatio,aspectRatio);
    }
}
