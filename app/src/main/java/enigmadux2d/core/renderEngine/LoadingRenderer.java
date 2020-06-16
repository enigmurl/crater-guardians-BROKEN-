package enigmadux2d.core.renderEngine;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.loading.LoadingRenderable;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadMesh;
import enigmadux2d.core.shaders.LoadingShader;

public class LoadingRenderer extends Renderer<LoadingShader, LoadingRenderable> {

    private QuadMesh mesh;


    private float[] parent = new float[16];

    private float[] buffer = new float[16];


    private float[] shader = new float[] {1,1,1,1};
    public LoadingRenderer(Context context) {
        super(new LoadingShader(context));

        //create the shared mesh
        this.mesh = new QuadMesh(new float[] {
                -0.5f,0.5f,0,
                -0.5f,-0.5f,0,
                0.5f,0.5f,0,
                0.5f,-0.5f,0
        });

        Matrix.setIdentityM(parent,0);
        Matrix.scaleM(parent,0, LayoutConsts.SCALE_X,1,1);
    }

    @Override
    void flush() {
        //bind the vao
        GLES30.glBindVertexArray(this.mesh.getVaoID());
        //enable the vertices
        GLES30.glEnableVertexAttribArray(QuadMesh.VERTEX_SLOT);




        while (! renderQ.isEmpty()){
            LoadingRenderable r = renderQ.poll();
            if (r == null){
                continue;
            }


            Matrix.multiplyMM(buffer,0,parent,0,r.getLocalTransform(),0);

            shaderProgram.writeMatrix(buffer);
            shaderProgram.writeTextureCord(r.getTextureTransform());
            shaderProgram.writeShader(shader);

            shaderProgram.writeTexture(r.getTextureID());
            shaderProgram.writeRadius(r.getClipR());

            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,this.mesh.getVertexCount());
        }


        //vertices no longer needed
        GLES30.glDisableVertexAttribArray(QuadMesh.VERTEX_SLOT);
        //vao no longer needed
        GLES30.glBindVertexArray(0);
    }
}
