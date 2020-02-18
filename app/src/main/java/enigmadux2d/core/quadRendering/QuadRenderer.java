package enigmadux2d.core.quadRendering;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;

import enigmadux2d.core.shaders.GUIShader;

/** Renders quads, and because buttons share a lot of the same texture, renders those as well
 * COMMON DEBUGS:
 *  Make sure you change the rendering methods for both single quad rendering, and multi quad rendering (this caused a big head ache before ;( )
 *
 * @author Manu Bhat
 * @version BETA
 */
public class QuadRenderer {



    //all quads share the same mesh, it's initialized in the constructor
    private final QuadMesh mesh;

    //this is where each specfic quad apply's its own transformations
    private final float[] instanceTransformation = new float[16];

    //this is our shader program
    private final GUIShader guiShader;

    /** Default constructor
     *
     * @param context any context that can load resources; used for shader program
     * @param vertexShader the vertex shader, the pointer that is in the form of R.raw._;
     * @param fragmentShader the fragment shader, the pointer that is in the form R.raw._;
     */
    public QuadRenderer(Context context,int vertexShader,int fragmentShader){
        //create the shared mesh
        this.mesh = new QuadMesh(new float[] {
                -0.5f,0.5f,0,
                -0.5f,-0.5f,0,
                0.5f,0.5f,0,
                0.5f,-0.5f,0
        });

        Log.d("TEXTURE","id: " + this.mesh.getVaoID());

        //initialize the shader
        this.guiShader = new GUIShader(context,vertexShader,fragmentShader);

    }

    /** Starts the rendering process, by attaching our shader
     *
     */
    public void startRendering(){
        //use our shader program
        GLES30.glUseProgram(this.guiShader.getProgramID());

    }

    /** Renders a single quad. Should only be used if you only want to render a single quad, otherwise, this is inefficient
     *
     * @param quad the texture that needs to be rendered
     * @param uMVPmatrix a 4x4 matrix that represents the model view projection matrix
     */
    public void renderQuad(QuadTexture quad,float[] uMVPmatrix){

        //bind the vao
        GLES30.glBindVertexArray(this.mesh.getVaoID());
        //enable the vertices
        GLES30.glEnableVertexAttribArray(QuadMesh.VERTEX_SLOT);

        //create the instance matrix
        quad.dumpOutputMatrix(this.instanceTransformation,uMVPmatrix);

        //write that matrix to the shader
        this.guiShader.writeMatrix(this.instanceTransformation);
        //then write the texture
        this.guiShader.writeTexture(quad.getTexture());
        //then write the shader variable
        this.guiShader.writeShader(quad.getShader());

        //finally draw the quad
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,this.mesh.getVertexCount());

        //vertices no longer needed
        GLES30.glDisableVertexAttribArray(QuadMesh.VERTEX_SLOT);
        //vao no longer needed
        GLES30.glBindVertexArray(0);
    }

    /** Renders multiple quads. This is slightly more efficient, because it doesn't have to resend vertex information,
     * just texture information.
     *
     * @param quads an array list of the textures to be rendered
     * @param uMVPmatrix a 4x4 matrix that represents the    model view projection matrix
     */
    public void renderQuads(ArrayList<? extends QuadTexture> quads,float[] uMVPmatrix){
        //use our shader program
        GLES30.glUseProgram(this.guiShader.getProgramID());

        //bind the vao
        GLES30.glBindVertexArray(this.mesh.getVaoID());
        //enable the vertices
        GLES30.glEnableVertexAttribArray(QuadMesh.VERTEX_SLOT);
        //now draw all the quads
        for (int i = 0,size = quads.size();i<size;i++){
            quads.get(i).dumpOutputMatrix(this.instanceTransformation,uMVPmatrix);

            //first write the instance transform matrix
            this.guiShader.writeMatrix(this.instanceTransformation);
            //then write the texture
            this.guiShader.writeTexture(quads.get(i).getTexture());
            //then write the shader variable
            this.guiShader.writeShader(quads.get(i).getShader());

            //finally draw the quad

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,this.mesh.getVertexCount());
        }
        //vertices no longer needed
        GLES30.glDisableVertexAttribArray(QuadMesh.VERTEX_SLOT);
        //vao no longer needed
        GLES30.glBindVertexArray(0);

    }
}
