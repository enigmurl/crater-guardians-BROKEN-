package enigmadux2d.core.shaders;

import android.content.Context;
import android.opengl.GLES30;

import com.enigmadux.craterguardians.R;

import enigmadux2d.core.quadRendering.QuadMesh;

public class LoadingShader extends ShaderProgram {

    /** The keyword used for vertex information, located in the vertex shader file
     *
     */
    private static final String VERTEX_KEYWORD = "position";

    /** The keyword used for the mvpMatrix, located in the vertex shader file
     *
     */
    private static final String MATRIX_KEYWORD = "mvpMatrix";


    /** The keyword used for the shader (channel filter), located in the fragment shader file
     *
     */
    private static final String SHADER_KEYWORD = "shader";

    /** The keyword used for the texture cord data located in the vertex shader file
     *
     */
    private static final String TEXTURE_CORD_KEYWORD = "textureCord";

    private static final String RADIUS_KEYWORD = "r";



    //pointer to the texture cord data (dX,dY,w,h);
    private int textureCordLocation;
    //pointer to the matrix in the vertex shader file
    private int matrixLocation;
    //pointer to the "shader" variable in the fragment shader file, which filters channels
    private int shaderLocation;

    private int rClipLocation;


    /** Default constructor
     *
     * @param context any context that can access resources
     */
    public LoadingShader(Context context){
        super(context, R.raw.loader_vertex_shader,R.raw.loader_frag_shader);

    }

    /** Only attribute that needs to bound are the vertices
     *
     */
    @Override
    protected void bindAttributes() {
        //only the vertices need to be bound
        this.bindAttribute(QuadMesh.VERTEX_SLOT, LoadingShader.VERTEX_KEYWORD);
    }

    /** Gets the variable locations
     *
     */
    @Override
    protected void getVariableLocations() {
        //get the texture location
        //get the matrix location
        this.matrixLocation = this.getUniformLocation(LoadingShader.MATRIX_KEYWORD);
        //get the shader location
        this.shaderLocation = this.getUniformLocation(LoadingShader.SHADER_KEYWORD);
        //get location
        this.textureCordLocation = this.getUniformLocation(LoadingShader.TEXTURE_CORD_KEYWORD);

        this.rClipLocation = this.getUniformLocation(LoadingShader.RADIUS_KEYWORD);
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

    public void writeRadius(float clipRadius){
        super.writeUniformFloat(this.rClipLocation,clipRadius);
    }


    //dX,dY,w,h
    public void writeTextureCord(float[] textureCord){
        GLES30.glUniform4fv(textureCordLocation,1,textureCord,0);
    }


}
