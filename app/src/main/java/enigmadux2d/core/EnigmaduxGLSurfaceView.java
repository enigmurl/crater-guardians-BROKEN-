package enigmadux2d.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/** Abstract class used for the Surface Views of apps
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public abstract class EnigmaduxGLSurfaceView extends GLSurfaceView {

    /** Context that is mainly used for sub classes that extend this class. Any non null context should work.
     *
     */
    protected Context context;


    /** Default Constructor
     *
     * @param context A Context object that is mainly used for subclasses that extend this class. Any non null context should work.
     */
    public EnigmaduxGLSurfaceView(Context context) {
        super(context);
        this.context = context;

        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3);
        //fix for error No Config chosen, but I don't know what this does.
        //super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        this.setRenderer();

    }

    /** Sets the renderer by initializing it. A method may not really be necessary, but for now.
     * The reason this isn't done in the default constructor is you cant initialize a direct EnigmaduxGlRenderer object.
     */
    public abstract void setRenderer();

    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public abstract boolean onTouchEvent(MotionEvent e);

    public abstract void onStop();

    public abstract void onStart();

    public abstract void onDestroy();




}