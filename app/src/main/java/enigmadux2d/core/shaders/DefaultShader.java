package enigmadux2d.core.shaders;

import android.content.Context;

import enigmadux2d.core.renderEngine.ModelLoader;

/** This is the default shader, that has texture coordinates and vertex positioning capabilities
 *
 * @author Manu Bhat
 * @version BETA
 */
public class DefaultShader extends ShaderProgram {
    /** The name of the in variable of the vertex shader that corresponds to the current vertex
     *
     */
    private static final String IN_VERTEX_POSITION_KEYWORD = "position";

    /** The name of the in variable of the vertex shader that corresponds to the current texture coordinates
     *
     */
    private static final String IN_TEXTURE_CORD_KEYWORD = "textureCord";


    /** This is where the uniform variable "texture" location (found in the fragment shader) is stored, so we can edit the value
     * of the texture later
     *
     */
    private int textureLocation;
    /** This is where the uniform variable "shader" location (found in the fragment shader) is stored, so we can write to this
     * value  later
     *
     */
    private int shaderLocation;
    /** This is where the uniform variable "deltaTextureCord" location (found in vertex shader) is stored, so we
     * can write to the value later
     *
     */
    private int deltaTextureLocation;
    /** This is where the uniform variable "uMVPmatrix" location (found in vertex shader) is stored, so we
     * can write to the value later
     *
     */
    private int uMVPMatrixLocation;




    /**
     * Loads the specified shader, as well as creates an openGL program. The shader are bound to the program
     *
     * @param context               a context that we use to load in the specified files
     * @param vertexFileReference   the pointer to the vertex shader file (should be R.raw.__)
     * @param fragmentFileReference the pointer to the fragment shader file (should be R.raw__)
     */
    public DefaultShader(Context context, int vertexFileReference, int fragmentFileReference) {
        super(context, vertexFileReference, fragmentFileReference);
    }

    /**bind all necessary attributes in this method, so that information from the VAO is automatically transferred
     * to the vertex shader
     *
     *
     */
    @Override
    public void bindAttributes() {
        //binds vertex attribute with corresponding name in shader program
        this.bindAttribute(ModelLoader.VERTEX_ATTRIBUTE_SLOT, DefaultShader.IN_VERTEX_POSITION_KEYWORD);
        //binds texture coordinate attribute with corresponding name in shader program
        this.bindAttribute(ModelLoader.TEXTURE_COORDINATES_ATTRIBUTE_SLOT, DefaultShader.IN_TEXTURE_CORD_KEYWORD);
    }


    /** Loads up the location of all uniform variables
     *
     */
    @Override
    protected void getVariableLocations() {
        this.textureLocation = this.getUniformLocation("Texture");
        this.shaderLocation = this.getUniformLocation("shader");
        this.deltaTextureLocation = this.getUniformLocation("deltaTextureCord");
        this.uMVPMatrixLocation = this.getUniformLocation("uMVPmatrix");

    }


    /** Writes the texture to the "Texture" location in the fragment shader
     *
     * @param textureID the id of the texture to write
     */
    public void writeTexture(int textureID){
        super.writeTexture(this.textureLocation,textureID);
    }

    /** Updates the uniform shader variable
     *
     * @param r the updated red filter value (0 to 1)
     * @param g the updated green filter value (0 to 1)
     * @param b the updated blue filter value (0 to 1)
     * @param a the updated alpha filter value (0 to 1) 0 = transparent, 1 fully opaque
     */
    public void writeShader(float r,float g,float b,float a){
        super.writeUniformVec4(this.shaderLocation,r,g,b,a);
    }

    /** Updates the delta texture coordinates variable, really only useful for animations
     *
     * @param deltaX the amount the texture coordinates should be translated to the right
     * @param deltaY the amount the texture coordinates should be translated down
     *
     */
    public void writeDeltaTexture(float deltaX,float deltaY){
        super.writeUniformVec2(this.deltaTextureLocation,deltaX,deltaY);
    }


    /** Updates the matrix that is used to modify the model to eye coordinates
     *
     * @param matrix a 4 by 4 matrix represented by a float[16];
     */
    public void writeUmvpMatrix(float[] matrix){
        super.writeMatrix(this.uMVPMatrixLocation,matrix);
    }


}
