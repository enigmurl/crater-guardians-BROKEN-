package enigmadux2d.core.renderEngine;

import java.util.Arrays;
import java.util.LinkedList;

import enigmadux2d.core.shaders.ShaderProgram;

public abstract class Renderer<T extends ShaderProgram,R extends Renderable> {


    LinkedList<R> renderQ = new LinkedList<>();

    T shaderProgram;
    public Renderer(T shaderProgram){
        this.shaderProgram = shaderProgram;
    }


    public void buffer(R r){
        renderQ.add(r);
    }

    @SafeVarargs
    public final void buffer(R... r){
        renderQ.addAll(Arrays.asList(r));
    }



    abstract void flush();

    public void render(){
        shaderProgram.useProgram();
        this.flush();
        renderQ.clear();
    }

    public void recycle(){
        shaderProgram.recycle();
    }
}
