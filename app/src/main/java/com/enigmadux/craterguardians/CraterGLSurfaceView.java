package com.enigmadux.craterguardians;

import android.content.Context;
import android.view.MotionEvent;

import enigmadux2d.core.EnigmaduxGLSurfaceView;


/** The specific Surface View for the app. Screen Related touch events are handled here
 *
 * @Author Manu Bhat
 * @Version BETA
 */
public class CraterGLSurfaceView extends EnigmaduxGLSurfaceView {
    //just for test
    private float mPreviousX;
    private float mPreviousY;
    //The renderer that does the actual drawing
    private CraterRenderer mRenderer;

    /** Default Constructor
     *
     * @param context Context used to reference resources, and other attributes of it. Any non null Context should work.
     */
    public CraterGLSurfaceView(Context context){
        super(context);

    }

    /** Not a usual setter method as it takes no parameters, it's meant to be called once by the parent class
     *
     */
    public void setRenderer(){
        mRenderer = new CraterRenderer(this.context);
        setRenderer(mRenderer);
    }


    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        return mRenderer.onTouch(e);
    }

}
