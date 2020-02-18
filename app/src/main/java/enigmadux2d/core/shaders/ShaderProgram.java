package enigmadux2d.core.shaders;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.util.Scanner;

import enigmadux2d.core.renderEngine.ModelLoader;

/** This tells openGL how to render fragments and vertices
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class ShaderProgram {
    /** Tag used for logging,
     *
     */
    private static final String TAG = "SHADER PROGRAM";




    //this is the ID that the program we create has. We can use it to actually render stuff with our shaders
    private int programID;

    /** Loads the specified shader, as well as creates an openGL program. The shader are bound to the program
     *
     * @param context a context that we use to load in the specified files
     * @param vertexFileReference the pointer to the vertex shader file (should be R.raw.__)
     * @param fragmentFileReference the pointer to the fragment shader file (should be R.raw__)
     */
    public ShaderProgram(Context context, int vertexFileReference,int fragmentFileReference) {
        this.loadShaders(context, vertexFileReference, fragmentFileReference);
        this.getVariableLocations();
    }

    /** Should bind all necessary attributes in this method, use  bindAttribute(int attributeSlot,String variableName) for help
     *
     */
    protected abstract void bindAttributes();

    /** Gets the location of all variables, mainly uniforms of a shader. It is called in the constructor once,
     * so shouldn't need to be called after that
     *
     */
    protected abstract void getVariableLocations();

    /** Gets the program ID. Use GLES30.glUseProgram(<id>) to actually use the shaders here
     *
     * @return thd ID of the current program.
     */
    public int getProgramID() {
        return this.programID;
    }

    /** Gets a uniform location so that the uniform value can be changed later on
     *
     * @param uniformName the string value in the code that the variable is named
     * @return the location of the uniform variable (well the int represents the location, use this number for other methods
     *         in this class when it asks for the location)
     */
    protected int getUniformLocation(String uniformName){
        //just ask openGL for the uniform location
        return GLES30.glGetUniformLocation(this.programID,uniformName);
    }

    /**  Writes a float value to a uniform variable
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param value the value that should be written to the variable
     */
    protected void writeUniformFloat(int location,float value){
        //ask openGL to write the value
        GLES30.glUniform1f(location,value);
    }

    /** Writes a vector of two values to a uniform variable, Note that while the parameters are named
     * deltX y z,it doesn't have to be position vector, could also be color
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param x the float value that should be written to at position 0
     * @param y the float value that should be written to at position 1
     */
    protected void writeUniformVec2(int location,float x,float y) {
        //ask openGL to write the two values
        GLES30.glUniform2f(location,x,y);
    }
    /** Writes a vector of three values to a uniform variable, Note that while the parameters are named
     * deltX y z,it doesn't have to be position vector, could also be color
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param x the float value that should be written to at position 0
     * @param y the float value that should be written to at position 1
     * @param z the float value that should be written to at position 2
     */
    protected void writeUniformVec3(int location,float x,float y,float z){
        //ask openGL to write the three values
        GLES30.glUniform3f(location,x,y,z);
    }
    /** Writes a vector of four values to a uniform variable, Note that while the parameters are named
     * deltX y z w,it doesn't have to be position vector, could also be color
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param x the float value that should be written to at position 0
     * @param y the float value that should be written to at position 1
     * @param z the float value that should be written to at position 2
     * @param w the float value that should be written to at position 3
     */
    protected void writeUniformVec4(int location,float x,float y,float z,float w){
        //ask openGL to write the four values
        GLES30.glUniform4f(location,x,y,z,w);

    }

    /** Writes a boolean value to a uniform value, but since it's written in C, it first makes it 1 or 0 based on
     * the true or false
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param value the value that should be written to the variable
     */
    protected void writeBoolean(int location,boolean value){
        //first convert it to an int
        int iValue = (value) ? 1:0;
        GLES30.glUniform1ui(location,iValue);
    }

    /** Writes a matrix value to the specified uniform value
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param matrix a float[] representing a matrix, it assumed it's a 4 by 4 matrix (16 elements), that should not
     *               be transposed
     */
    protected void writeMatrix(int location,float[] matrix){
        //ask openGL to write the 16 values
        GLES30.glUniformMatrix4fv(location,1,false,matrix,0);
    }

    /** Writes a texture to a uniform variable
     *
     * @param location this is where the value will be written to (use getUniformLocation to find the int)
     * @param textureID this is the id of texture, not the actual texture itself. OpenGL should've given you
     *                  the textureID
     */
    protected void writeTexture(int location,int textureID){
        //first set the texture port to 0
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        //bind the texture with the port
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureID);
        //not completely sure what this does (or even if it works?)
        GLES30.glUniform1i(location, 0);


    }




    /** Loads the specified shader, as well as creates an openGL program. The shader are bound to the program
     *
     * @param context a context that we use to load in the specified files
     * @param vertexShaderFile the pointer to the vertex shader file (should be R.raw.__)
     * @param fragmentShaderFile the pointer to the fragment shader file (should be R.raw__)
     */
    private void loadShaders(Context context, int vertexShaderFile, int fragmentShaderFile){
        //first scan the vertexShader file and make it into a giant string
        Scanner vertexScanner = new Scanner(context.getResources().openRawResource(vertexShaderFile));
        String vertexShaderString = "";

        //add the lines of the file to the string
        while (vertexScanner.hasNextLine()){
            vertexShaderString += vertexScanner.nextLine();
        }
        vertexScanner.close();

        //now do the same thing for the fragment shader
        Scanner fragmentScanner = new Scanner(context.getResources().openRawResource(fragmentShaderFile));
        String fragmentShaderString = "";

        //add the lines of the file to the string
        while (fragmentScanner.hasNextLine()){
            fragmentShaderString += fragmentScanner.nextLine() + "\n";
        }
        fragmentScanner.close();

        //now we have strings that are the vertex and fragment shader files, they must now be compile
        //the ints that are returned are references to the shaders
        int vertexShaderReference = this.compileShader(GLES30.GL_VERTEX_SHADER,vertexShaderString);
        int fragmentShaderReference = this.compileShader(GLES30.GL_FRAGMENT_SHADER,fragmentShaderString);

        //now that we have our shaders we can create a program, and catch the reference of it
        this.programID = GLES30.glCreateProgram();



        //however it is empty so must add the shaders we created earlier to it
        //adding vertex shader
        GLES30.glAttachShader(this.programID,vertexShaderReference);
        //adding fragment shader
        GLES30.glAttachShader(this.programID,fragmentShaderReference);

        //now we make sure that the variables of the shader should be bound with it's corresponding counterpart
        this.bindAttributes();

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(this.programID);
    }


    /** Binds an attribute
     *
     * @param attributeSlot this is essentially the slot in which the VBO takes place. For example, if you have your
     *                      vertices in the slot 0, you would put this as 0
     * @param variableName this is the variable name in the shader program, e.g. position
     */
    protected void bindAttribute(int attributeSlot,String variableName){
        GLES30.glBindAttribLocation(this.programID,attributeSlot,variableName);
    }



    /** This compiles shaders and returns a reference to the created shader
     *
     * @param shaderType This is whether it's a vertex shader or fragment shader that needs to be compiled
     *                   For vertex use: GLES30.GL_VERTEX_SHADER, for fragment use GLES30.GL_FRAGMENT_SHADER
     * @param shaderCode this is the actual code itself, represented as a string. It should be
     *                   written in GLSL (gl shading language)
     * @return A reference to the created shader
     */
    private int compileShader(int shaderType, String shaderCode){
        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(shaderType);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        this.validateShader(shaderType,shader);

        return shader;
    }


    /** Validates shaders to make sure that they are actually compile properly
     * If the shader is proper nothing happens, otherwise a
     *
     * @param shaderType This is whether it's a vertex shader or fragment shader that needs to be compiled
     *                   For vertex use: GLES30.GL_VERTEX_SHADER, for fragment use GLES30.GL_FRAGMENT_SHADER
     * @param shaderID a pointer to the shader that needs to be checked
     */
    private void validateShader(int shaderType, int shaderID){
        //this is where openGL tells us the result
        final int[] openGLDump = new int[1];

        //see if it has been compiled
        GLES30.glGetShaderiv(shaderID,GLES30.GL_COMPILE_STATUS,openGLDump,0);

        //if it hasn't been compiled
        if (openGLDump[0] == GLES30.GL_FALSE) {

            String shaderTypeString = (shaderType == GLES30.GL_VERTEX_SHADER) ? "VERTEX SHADER " : "FRAGMENT SHADER ";
            Log.e(ShaderProgram.TAG,shaderTypeString + "NOT COMPILED: error message: " + GLES30.glGetShaderInfoLog(shaderID));
            throw new InvalidShaderCodeException(shaderTypeString + "NOT COMPILED: error message: " + GLES30.glGetShaderInfoLog(shaderID));
        }


    }





    /** Exception called when the Shader is not compiled
     *
     */
    private static class InvalidShaderCodeException extends RuntimeException {
        /** Default Constructor
         *
         * @param message the message that should be displayed to the output log
         */
        public InvalidShaderCodeException(String message){
            super(message);
        }
    }
}
