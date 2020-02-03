package enigmadux2d.core.renderEngine;

import android.content.Context;
import android.opengl.GLES30;

import enigmadux2d.core.models.TexturedModel;
import enigmadux2d.core.shaders.DefaultShader;


/** This draws a mesh to the screen.
 *
 * @author Manu Bhat
 * @version BETA
 */
public class MeshRenderer {


    /** This is the current shader program that is used to manipulate vertices and fragments (pixels)
     *
     */
    private DefaultShader shaderProgram;



    /** Loads the specified shader, as well as creates an openGL program. The shader are bound to the program
     * It does this by creating A new ShaderProgram object.
     *
     * @param context a context that we use to load in the specified files
     * @param vertexShaderFile the pointer to the vertex shader file (should be R.raw.__)
     * @param fragmentShaderFile the pointer to the fragment shader file (should be R.raw__)
     */
    public void loadShaders(Context context, int vertexShaderFile, int fragmentShaderFile){
        this.shaderProgram = new DefaultShader(context,vertexShaderFile,fragmentShaderFile);

    }



    /** Given a TexturedModel, it draws it to the screen using the VAO, the mesh is based on.
     *
     * @param modelToBeRendered A TexturedModel that has information about vertices and a texture, and has non corrupt data,
     * @param uMVPMatrix a float[16] that represents a Mat4x4 that will transform the model
     */
    public void renderMesh(TexturedModel modelToBeRendered,float[] uMVPMatrix){

        //first update all the variables of the shader using the VAO from the mesh
        this.passInShaderParameters(modelToBeRendered);

        //now update the uMVPMatrix
        this.shaderProgram.writeUmvpMatrix(uMVPMatrix);


        //draw the actual data, using triangles, the other arguments after basically say to render all vertices stored in
        //the vbo, using the indices provided by the mesh
        int numVertices = modelToBeRendered.getModelMesh().getVertexCount();

        GLES30.glDrawElements(GLES30.GL_TRIANGLES,numVertices, GLES30.GL_UNSIGNED_INT,modelToBeRendered.getModelMesh().getElementArray());

        //unbind the VBOs and VAOs as we're done with them
        this.finishRendering();



    }

    /** Updates Shader Variables, as in the data based on the mesh is passed onto the shader. Not all variables
     * are passed in, such as the uMVPMatrix, however all variables from the VAO will be passed on, as well
     * as some other attributes from the modelToBeRendered
     *
     * @param modelToBeRendered the model that stores the VAO with the information that will be passed onto the shaders,
     *                          as well as information about the Texture
     */
    private void passInShaderParameters(TexturedModel modelToBeRendered){
        //bind the program we made earlier that has our shaders on it
        GLES30.glUseProgram(this.shaderProgram.getProgramID());


        //First thing we want to do  for drawing is get the data stored in the VAO
        GLES30.glBindVertexArray(modelToBeRendered.getModelMesh().getVaoID());
        //now get the vertex data from the VAO (index the VAO, to get a VBO)
        GLES30.glEnableVertexAttribArray(ModelLoader.VERTEX_ATTRIBUTE_SLOT);
        //now get the texture coord data from the vao (index the VAO, to get a VBO)
        GLES30.glEnableVertexAttribArray(ModelLoader.TEXTURE_COORDINATES_ATTRIBUTE_SLOT);

        //bind the texture
        this.shaderProgram.writeTexture(modelToBeRendered.getTexture().getTextureID());

        //write the shader
        float[] shader = modelToBeRendered.getShader();
        this.shaderProgram.writeShader(shader[0],shader[1],shader[2],shader[3]);
        //write the delta texture coordinates
        this.shaderProgram.writeDeltaTexture(modelToBeRendered.getDeltaTextureX(),modelToBeRendered.getDeltaTextureY());

    }

    /** De assigns the current bounded objects (VBOs, and VAOs)
     *
     */
    private void finishRendering(){
        //de bind the vertex VBO as it's no longer needed
        GLES30.glDisableVertexAttribArray(ModelLoader.VERTEX_ATTRIBUTE_SLOT);
        //de bind the texture cord VBO as it's no longer needed
        GLES30.glDisableVertexAttribArray(ModelLoader.TEXTURE_COORDINATES_ATTRIBUTE_SLOT);


        //unbind the VAO as it's no longer needed, use the id 0 to tell openGL to disable the current VAO
        GLES30.glBindVertexArray(0);
    }





}
