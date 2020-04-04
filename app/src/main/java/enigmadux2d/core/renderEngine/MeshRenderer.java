package enigmadux2d.core.renderEngine;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.IntBuffer;

import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.models.InGameModel;
import enigmadux2d.core.models.TexturedModel;
import enigmadux2d.core.shaders.DefaultShader;
import enigmadux2d.core.shaders.ShaderProgram;


/** This draws a mesh to the screen. Todo need to upgrade this
 *
 * @author Manu Bhat
 * @version BETA
 */
public class MeshRenderer {


    /** This is the current shader program that is used to manipulate vertices and fragments (pixels)
     *
     */
    private DefaultShader shaderProgram;

    /** This is the final matrix
     *
     */
    private final float[] finalMatrix = new float[16];



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

    /** Starts the rendering process, by attaching our shader
     *
     */
    public void startRendering(){
        //bind the program we made earlier that has our shaders on it
        this.shaderProgram.useProgram();

    }


    /** Renders a collection of the same object using instancing
     *
     * @param instances the instances to be rendered
     */
    public void renderCollection(VaoCollection instances){
        ShaderProgram.NUM_DRAW_CALLS++;

        //first bind the vertex array
        GLES30.glBindVertexArray(instances.getVaoID());

        //enable all slots (not using static references, because it wouldn't make sense to create 4 for the matrices
        //but what we are enabling is the four columns of the matrix, the ARGB shader, and then the delta texture coords
        //see commment below about youtube comment for reason of disabling these line

//        GLES30.glEnableVertexAttribArray(VaoCollection.VERTEX_ATTRIBUTE_SLOT);
//        GLES30.glEnableVertexAttribArray(VaoCollection.TEXTURE_COORDINATES_ATTRIBUTE_SLOT);
//        GLES30.glEnableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT);
//        GLES30.glEnableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT+1);
//        GLES30.glEnableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT+2);
//        GLES30.glEnableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT+3);
//        GLES30.glEnableVertexAttribArray(VaoCollection.ARGB_SHADER_ATTRIBUTE_SLOT);
//        GLES30.glEnableVertexAttribArray(VaoCollection.DELTA_TEXTURE_COORDINATES_ATTRIBUTE_SLOT);

        //upload the texture to V RAM
        this.shaderProgram.writeTexture(instances.getTextureId());

        //draw triangles, second parameter is the amount of vertices
        IntBuffer elementArray = instances.getElementArrayBuffer();
        GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLES,elementArray.capacity(),GLES30.GL_UNSIGNED_INT,elementArray,instances.getNumInstances());


        //according to a youtube comment if you enable it once, you never have to re enable it again, so I'm moving that stuff to the VaoCollection class and seeing if it works

//        //de bind the vertex VBO as it's no longer needed
//        GLES30.glDisableVertexAttribArray(ModelLoader.VERTEX_ATTRIBUTE_SLOT);
//        //de bind the texture cord VBO as it's no longer needed
//        GLES30.glDisableVertexAttribArray(ModelLoader.TEXTURE_COORDINATES_ATTRIBUTE_SLOT);
//        //de bind instance attributes as well
//        GLES30.glDisableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT);
//        GLES30.glDisableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT+1);
//        GLES30.glDisableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT+2);
//        GLES30.glDisableVertexAttribArray(VaoCollection.VIEW_MATRIX_ATTRIBUTE_SLOT+3);
//        GLES30.glDisableVertexAttribArray(VaoCollection.ARGB_SHADER_ATTRIBUTE_SLOT);
//        GLES30.glDisableVertexAttribArray(VaoCollection.DELTA_TEXTURE_COORDINATES_ATTRIBUTE_SLOT);


        //unbind the VAO as it's no longer needed, use the id 0 to tell openGL to disable the current VAO
        GLES30.glBindVertexArray(0);

    }


    /** Given a TexturedModel, it draws it to the screen using the VAO, the mesh is based on.
     *
     * @param modelToBeRendered A InGameModel that has information about vertices and a texture, and has non corrupt data,
     * @param uMVPMatrix a float[16] that represents a Mat4x4 that will transform the model
     */
    public void renderMesh(InGameModel modelToBeRendered,float[] uMVPMatrix){

        //first update all the variables of the shader using the VAO from the mesh
        this.passInShaderParameters(modelToBeRendered);

        //now update the uMVPMatrix
        Matrix.multiplyMM(this.finalMatrix,0,uMVPMatrix,0,modelToBeRendered.getTransform(),0);
        this.shaderProgram.writeUmvpMatrix( uMVPMatrix);


        //draw the actual data, using triangles, the other arguments after basically say to render all vertices stored in
        //the vbo, using the indices provided by the mesh
        int numVertices = modelToBeRendered.getTexturedModel().getModelMesh().getVertexCount();

        GLES30.glDrawElements(GLES30.GL_TRIANGLES,numVertices, GLES30.GL_UNSIGNED_INT,
                modelToBeRendered.getTexturedModel().getModelMesh().getElementArray());

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
    private void passInShaderParameters(InGameModel modelToBeRendered){
        //bind the program we made earlier that has our shaders on it
        //we don't actually need to though it's done outside
        //this.shaderProgram.useProgram();

        TexturedModel texturedModel = modelToBeRendered.getTexturedModel();

        //First thing we want to do  for drawing is get the data stored in the VAO
        GLES30.glBindVertexArray(texturedModel.getModelMesh().getVaoID());
        //now get the vertex data from the VAO (index the VAO, to get a VBO)
        GLES30.glEnableVertexAttribArray(ModelLoader.VERTEX_ATTRIBUTE_SLOT);
        //now get the texture coord data from the vao (index the VAO, to get a VBO)
        GLES30.glEnableVertexAttribArray(ModelLoader.TEXTURE_COORDINATES_ATTRIBUTE_SLOT);

        //bind the texture
        this.shaderProgram.writeTexture(texturedModel.getTexture().getTextureID());

        //write the shader
        float[] shader = texturedModel.getShader();
        this.shaderProgram.writeShader(shader[0],shader[1],shader[2],shader[3]);
        //write the delta texture coordinates
        this.shaderProgram.writeDeltaTexture(texturedModel.getDeltaTextureX(),texturedModel.getDeltaTextureY());

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
