package enigmadux2d.core;

import android.view.MotionEvent;

/** Parent Class of all Enigmadux Components including Rectangles, TextBoxes, and Layouts. All components are capable of being
 * visible to the end user.
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class EnigmaduxComponent {
    //open gl x coordinate (read constructor javadoc for more details)
    protected float x;
    //open gl y coordinate (read constructor javadoc for more details)
    protected float y;
    //open gl width (read constructor javadoc for more details)
    protected float w;
    //open gl height (read constructor javadoc for more details)
    protected float h;
    //whether the component should be drawn or not
    protected boolean visible = true;


    /**
     * Default Constructor
     *
     * @param x the open gl coordinate of the component, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the component, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     * @param w the width of the rect (distance from left edge to right edge) in open gl coordinate terms e.g (1.0f, 1.5f) Should be positive
     * @param h the height of the rect (distance from top edge to bottom edge) in open gl coordinate terms e.g (1.0f, 1.5f) should be positive
     */
    public EnigmaduxComponent(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /** Gets the current w value, this might not be the true width
     *
     * @return the current w value
     */
    public float getWidth(){
        return this.w;
    }
    /** Gets the current h value, this might not be the true height
     *
     * @return the current h value
     */
    public float getHeight(){
        return this.h;
    }

    /** Gets the bottom edge y
     *
     * @return bottom edge openGL
     */
    public float getY() {
        return y;
    }

    /** Gets the left edge x
     *
     * @return left edge openGL
     */
    public float getX() {
        return x;
    }

    /** Sets the position of the x and y in open gl coordinates. NOTE DOES NOT DO ANYTHING VISUALLY UNLESS TEXTURE IS RELOADED
     *
     * @param x the open gl coordinate of the component, left most edge x coordinate e.g. (1.0f, -0.5f, 0.0f ,0.1f)
     * @param y the open gl coordinate of the component, bottom most y coordinate e.g. (1.0f,-0.5f, 0.0f, 0.1f)
     */
    public void setPos(float x,float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Draws the component onto the screen
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    public abstract void draw(float[] parentMatrix);


    /**
     * Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public abstract boolean onTouch(MotionEvent e);

    /**
     * Shows the component, and it will be no longer be exempt from drawing operations
     * This will be overridden for layouts as they have to call it for sub components
     */
    public void show() {
        this.visible = true;
    }

    /**
     * Hides the component, and it will be exempt from drawing operations
     * This will be overridden for layouts as they have to call it for sub components
     */
    public void hide() {
        this.visible = false;
    }

    /**
     * Sees if an object is visible or not
     *
     * @return the boolean visible, indicating whether the component is visible or not
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sees whether a point is contained within the rectangle bounding the component
     *
     * @param x the openGL x coordinate of the point that is being tested to see if it is inside the component
     * @param y the openGL y coordinate of the point that is being tested to see if it is inside the component
     * @return whether the point represented by the open gl coordinate (x,y) is inside the bounds of this component
     */
    public boolean isInside(float x, float y) {
        return (x > this.x &&
                x < this.x + this.w &&
                y > this.y &&
                y < this.y + this.h);

    }
}
