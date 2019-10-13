package enigmadux2d.core;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;


/** An abstract base class that should be extended inside the app. The renderer is where the actual draw code should be done.
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public abstract class EnigmaduxGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "ENIGMADUX_GL_RENDERER";
    /** Context that can be used in a variety of ways, for instance, getting resources*/
    protected Context context;

    /** Default constructor
     *
     * @param context A Context Object that is mainly of use to child class. Any non-null Context should work.
     */
    public EnigmaduxGLRenderer(Context context){
        this.context = context;
    }


    /** Used whenever the surface is created(see android documentation for more details)
     *
     * @param gl a Gl object used to communicate with open gl
     * @param config config of open gl (check android doc)
     */
    public void onSurfaceCreated(GL10 gl,EGLConfig config){
        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
        gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        //Really Nice Perspective Calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    /** Used whenever the surface is changed(e.g rotated screen from landscape to portrait) (see android documentation for more details)
     *
     * @param gl a GL object used to communicate with OpenGL
     * @param width the new width of the surface (in pixels I believe, but could be open gl width)
     * @param height the new height of the surface (in pixels I believe, but could be open gl height)
     */
    public abstract void onSurfaceChanged(GL10 gl,int width,int height);

    /** Called whenever a new frame is needed to be drawn. If the render mode is dirty, then it will only be called
     * on requestRender, otherwise it's called at 60fps (I believe)
     *
     * @param gl a GL object used to communicate with OpenGl
     */
    public abstract void onDrawFrame(GL10 gl);






}