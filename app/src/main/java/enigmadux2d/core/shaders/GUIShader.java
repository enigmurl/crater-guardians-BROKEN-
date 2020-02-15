package enigmadux2d.core.shaders;

import android.content.Context;
import android.util.Log;

import enigmadux2d.core.quadRendering.QuadMesh;

/** This a shader for GUIs, as opposed to in game objects
 *
 * @author Manu Bhat
 * @version BETA
 *
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

    //pointer to the texture variable in the fragment shader file
    private int textureLocation;
    //pointer to the matrix in the vertex shader file
    private int matrixLocation;


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
    }

    /** Writes the texture, and binds it to the shader
     *
     * @param textureID the id of the texture that openGL gave, (it will dump it to an int[])
     */
    public void writeTexture(int textureID) {
        super.writeTexture(this.textureLocation, textureID);
    }

    /** Writes the mvpMatrix,which transforms the vertices
     *
     * @param matrix a float[16], that represents the model view projection matrix
     */
    public void writeMatrix(float[] matrix){
        super.writeMatrix(this.matrixLocation,matrix);
    }
}
