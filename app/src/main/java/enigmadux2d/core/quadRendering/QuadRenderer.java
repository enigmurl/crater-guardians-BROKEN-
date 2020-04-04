package enigmadux2d.core.quadRendering;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.util.ArrayList;

import enigmadux2d.core.shaders.QuadShader;
import enigmadux2d.core.shaders.ShaderProgram;

/** Renders quads, and because buttons share a lot of the same texture, renders those as well
 * The difference between this and the gui renderer is that gui renderer is slower as it handles corners
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
    private final QuadShader quadShader;


    //see below, but basically a buffered list for efficient rendering and limited state changing
    private ArrayList<QuadTexture> bufferedQuads = new ArrayList<>();

    /** Default constructor
     *
     * @param context any context that can load resources; used for shader program
     * @param vertexShader the vertex shader, the pointer that is in the form of R.raw._;
     * @param fragmentShader the fragment shader, the pointer that is in the form R.raw._;
     */
    public QuadRenderer(Context context, int vertexShader, int fragmentShader){
        //create the shared mesh
        this.mesh = new QuadMesh(new float[] {
                -0.5f,0.5f,0,
                -0.5f,-0.5f,0,
                0.5f,0.5f,0,
                0.5f,-0.5f,0
        });

        Log.d("TEXTURE","id: " + this.mesh.getVaoID());

        //initialize the shader
        this.quadShader = new QuadShader(context,vertexShader,fragmentShader);

    }

    /** Starts the rendering process, by attaching our shader
     *
     */
    public void startRendering(){
        //use our shader program
        this.quadShader.useProgram();

    }

    /** Renders a single quad. Should only be used if you only want to render a single quad, otherwise, this is inefficient
     *
     * @param quad the texture that needs to be rendered
     * @param uMVPmatrix a 4x4 matrix that represents the model view projection matrix
     */
    public void renderQuad(QuadTexture quad,float[] uMVPmatrix){
        ShaderProgram.NUM_DRAW_CALLS++;
        if (! quad.isVisible()) return;;

        //in case it's not currently
        this.startRendering();
        //bind the vao
        GLES30.glBindVertexArray(this.mesh.getVaoID());
        //enable the vertices
        GLES30.glEnableVertexAttribArray(QuadMesh.VERTEX_SLOT);

        //create the instance matrix
        quad.dumpOutputMatrix(this.instanceTransformation,uMVPmatrix);

        //write that matrix to the shader
        this.quadShader.writeMatrix(this.instanceTransformation);
        //then write the texture
        this.quadShader.writeTexture(quad.getTexture());
        //then write the shader variable
        this.quadShader.writeShader(quad.getShader());
        //write texture cord date
        this.quadShader.writeTextureCord(quad.getTextureCord());
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
        //in case it's not currently
        this.startRendering();
        //bind the vao
        GLES30.glBindVertexArray(this.mesh.getVaoID());
        //enable the vertices
        GLES30.glEnableVertexAttribArray(QuadMesh.VERTEX_SLOT);
        //now draw all the quads
        for (int i = 0,size = quads.size();i<size;i++){
            ShaderProgram.NUM_DRAW_CALLS++;

            if (! quads.get(i).isVisible()) continue;
            quads.get(i).dumpOutputMatrix(this.instanceTransformation,uMVPmatrix);

            //first write the instance transform matrix
            this.quadShader.writeMatrix(this.instanceTransformation);
            //then write the texture
            this.quadShader.writeTexture(quads.get(i).getTexture());
            //then write the shader variable
            this.quadShader.writeShader(quads.get(i).getShader());
            //write texture cord date
            this.quadShader.writeTextureCord(quads.get(i).getTextureCord());

            //finally draw the quad

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,this.mesh.getVertexCount());
        }
        //vertices no longer needed
        GLES30.glDisableVertexAttribArray(QuadMesh.VERTEX_SLOT);
        //vao no longer needed
        GLES30.glBindVertexArray(0);

    }



    //this is basically adding components to a list and then rendering them all at once for effecieny and limiting state changes
    public void bufferQuad(QuadTexture q){
        this.bufferedQuads.add(q);
    }

    public void bufferQuads(ArrayList<QuadTexture> quads){
        this.bufferedQuads.addAll(quads);
    }

    public void bufferQuads(QuadTexture... quadTextures){
        for (int i = 0,size = quadTextures.length;i<size;i++) {
            this.bufferedQuads.add(quadTextures[i]);
        }
    }

    public void flush(float[] mvpMatrix){
        this.renderQuads(this.bufferedQuads,mvpMatrix);
        this.bufferedQuads.clear();
    }
}
