package enigmadux2d.layouts;

import android.view.MotionEvent;

import enigmadux2d.core.EnigmaduxComponent;

public abstract class EnigmaduxLayout extends EnigmaduxComponent{
    // the components in the layout
    protected EnigmaduxComponent[] components;

    /** Default constructor
     *
     * @param components The components that are part of the layout. This includes TexturedRect, Text, a Layout itself and anything else that derives from EnigmaduxComponent that is part of the layout
     * @param x the open gl coordinate of the rect, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the rect, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     *
     */
    public EnigmaduxLayout(EnigmaduxComponent[] components,float x,float y,float w,float h){
        super(x,y,w,h);
        this.components = components;
    }



    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public boolean onTouch(MotionEvent e){
        for (EnigmaduxComponent cmp: components){
            if (cmp.onTouch(e)) return true;
        }
        return false;//change this later to actually match it
    }

    /** Shows the component, and all sub components are shown as well. All of them are no longer exempt from drawing operations
     *
     */
    @Override
    public void show(){
        super.show();
        for (EnigmaduxComponent cmp: components){
            cmp.show();
        }
    }

    /** Hides the component, and all sub components are hidden as well. All of them are exempt from drawing operations
     *
     */
    @Override
    public void hide(){
        super.hide();
        for (EnigmaduxComponent cmp: components){
            cmp.hide();
        }
    }
}
