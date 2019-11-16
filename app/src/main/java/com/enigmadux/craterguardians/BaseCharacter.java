package com.enigmadux.craterguardians;

import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Attacks.Attack;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;

/** All characters and enemies derive from here. Provides functionality for rendering like sprite sheets.
 * @author Manu Bhat
 * @version BETA
 */
public abstract class BaseCharacter extends EnigmaduxComponent {
    /**
     * TAG USED FOR LOGGING
     */
    private static final String TAG = "CHARACTER";

    /** The width in openGL terms of any character
     *
     */
    public static final float CHARACTER_WIDTH = 0.3f;
    /** The height in openGL terms of any character
     *
     */
    public static final float CHARACTER_HEIGHT = 0.3f;


    //the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
    protected int numRotationOrientations;
    //in each orientation, how many frames is the animations
    protected int framesPerRotation;


    //the amount of frames displayed in a single second
    protected float fps;
    //the current frame number of the animation
    protected int frameNum = 0;
    //the amount of milliseconds since last frame change
    protected long millisSinceLastFrameChange;


    //all alive attacks
    protected List<Attack> attacks = new ArrayList<>();

    //health of player
    protected int health;
    //whether player is alive or not
    private boolean isAlive = true;

    //position of center of the character
    private float deltaX;
    private float deltaY;
    //the angle at which the player needs to be rotated. This is because there is some granuality of the number of rotations, so we interpelate between the two by rotating it (degrees)
    protected float offsetDegrees;

    /**
     *
     * @param numRotationOrientations the amount of angles that the character is rendered at e.g 4 would mean 0,90,180,270
     * @param framesPerRotation in each orientation, how many frames is the animations
     * @param fps the amount of frames displayed in a single second
     */
    public BaseCharacter(int numRotationOrientations, int framesPerRotation,float fps){
        super(-BaseCharacter.CHARACTER_WIDTH/2,-BaseCharacter.CHARACTER_HEIGHT/2,BaseCharacter.CHARACTER_WIDTH,BaseCharacter.CHARACTER_HEIGHT);
        this.numRotationOrientations = numRotationOrientations;
        this.framesPerRotation = framesPerRotation;
        this.fps = fps;


        this.health = getMaxHealth();
    }

    /** Draws the character and elements related to it. Sub classes are responsible for drawing all elements
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public abstract void draw(GL10 gl, float[] parentMatrix);

    /** based on the current state, which frame should it be?
     *
     * @param rotation the angle at which the character is in degrees
     * @param frameNum the frame# to display in the animation
     */
    public abstract void setFrame(float rotation,int frameNum);


    /** Updates based on game state todo this won't work for more than 1 character because of updating not being in the same time frame as drawing
     *
     * @param dt amount of milliseconds since last call
     */
    public void update(long dt,float rotation){
        this.millisSinceLastFrameChange += dt;
        if (this.millisSinceLastFrameChange > 1000/this.fps){
            int numFrames = (int) this.millisSinceLastFrameChange / (int) (1000/this.fps);
            this.frameNum += numFrames;
            this.millisSinceLastFrameChange -= numFrames * (int) (1000/this.fps);
            this.frameNum = this.frameNum%this.framesPerRotation;
        }
        this.setFrame(rotation,this.frameNum);

    }

    /**In process of how its gonna work
     *
     * @param angle the angle at which to attack (in radians)
     */
    public abstract void attack(float angle);

    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public boolean onTouch(MotionEvent e){
        return true;
    }

    /** Tells how fast the character is
     *
     * @return how fast the object is (as of now there are no units) todo add units
     */
    public abstract float getCharacterSpeed();

    /** Used to damage players. Or heal if the value is negative.
     *
     * @param damage the amount to reduce the health by
     */
    public void damage(int damage){
        this.health -= damage;
        //this.visualRepresentation.setScale(5f,5f);
        if (this.health <= 0){
            Log.d(TAG,"DEATH: class: " + this.getClass()  + " took " + damage + " damage");
            this.isAlive = false;
        }
    }

    /** Sees if it collides with a line, but does not indicate where
     *
     * @param x1 openGL x of first point
     * @param y1 openGL y of first point
     * @param x2 openGL x of second point
     * @param y2 openGL y of second point
     * @return whether or not they collide
     */
    public boolean collidesWithLine(float x1, float y1, float x2, float y2){
        float cx = this.getDeltaX();
        float cy = this.getDeltaY();

        float pt1X = x1 - cx;
        float pt1Y = y1 - cy;
        float pt2X = x2 - cx;
        float pt2Y = y2 - cy;

        // Get the semi major and semi minor axes.
        float a = CHARACTER_WIDTH / 2;
        float b = CHARACTER_HEIGHT / 2;

        // Calculate the quadratic parameters.
        float A = (pt2X - pt1X) * (pt2X - pt1X) / (a*a) +
                (pt2Y - pt1Y) * (pt2Y - pt1Y) /(b* b);
        float B = 2 * pt1X * (pt2X - pt1X) /(a* a) +
                2 * pt1Y * (pt2Y - pt1Y) /(b*b);
        float C = pt1X * pt1X /(a*a)+ pt1Y * pt1Y /(b*b)- 1;


        // Calculate the discriminant.
        float discriminant = B * B - 4 * A * C;

        if (discriminant >= 0){
            float tValue1 = (float) (-B + Math.sqrt(discriminant)) /(2*A); //||
            float tValue2 = (float) (-B - Math.sqrt(discriminant))/(2*A);

            return  (tValue1 >= 0 && tValue1 <= 1) || (tValue2 >= 0 && tValue2 <= 1);
        }
        return false;
    }

    /** gets all the points where the hitbox and the line collide
     *
     * @param x1 openGL x of first point
     * @param y1 openGL y of first point
     * @param x2 openGL x of second point
     * @param y2 openGL y of second point
     * @return all the points where the hitbox and the line collide
     */
    public List<float[]> getCollisionsWithLine(float x1, float y1, float x2, float y2) {
        float cx = this.getDeltaX();
        float cy = this.getDeltaY();

        float pt1X = x1 - cx;
        float pt1Y = y1 - cy;
        float pt2X = x2 - cx;
        float pt2Y = y2 - cy;

        // Get the semi major and semi minor axes.
        float a = CHARACTER_WIDTH / 2;
        float b = CHARACTER_HEIGHT / 2;

        // Calculate the quadratic parameters.
        float A = (pt2X - pt1X) * (pt2X - pt1X) / (a * a) +
                (pt2Y - pt1Y) * (pt2Y - pt1Y) / (b * b);
        float B = 2 * pt1X * (pt2X - pt1X) / (a * a) +
                2 * pt1Y * (pt2Y - pt1Y) / (b * b);
        float C = pt1X * pt1X / (a * a) + pt1Y * pt1Y / (b * b) - 1;


        // Calculate the discriminant.
        float discriminant = B * B - 4 * A * C;

        List<float[]> returnList = new ArrayList<>();

        if (discriminant >= 0) {
            float tValue1 = (float) (-B + Math.sqrt(discriminant)) / (2 * A); //||
            float tValue2 = (float) (-B - Math.sqrt(discriminant)) / (2 * A);


            if (tValue1 >= 0 && tValue1 <= 1) {
                returnList.add(new float[]{x1 + tValue1 * (x2-x1),y1 + tValue1 * (y2-y1)});
            }
            if (tValue2 >= 0 && tValue2 <= 1 && discriminant != 0) {
                returnList.add(new float[] {x1 + tValue2 * (x2-x1),y1 + tValue2 * (y2-y1)});
            }


            //((-B - Math.sqrt(discriminant)) /(2*A) >= 0 && (-B - Math.sqrt(discriminant))/(2*A) <= 1);
        }
        return returnList;
    }

    /** Respawn the character so it is alive again
     *
     */
    public void spawn(){
        this.attacks.clear();
        this.health = getMaxHealth();
        this.isAlive = true;
    }

    /** sees if its alive
     *
     * @return if its alive
     */
    public boolean isAlive() {
        return isAlive;
    }


    /** Gets x position
     *
     * @return the x distance of center of character from y axis
     */
    public float getDeltaX(){
        return this.deltaX;
    }

    /** Gets y pos
     *
     * @return the y distance of center of character from x axis
     */
    public float getDeltaY(){
        return this.deltaY;
    }


    /** translates the TexturedRect
     *
     * @param x how much to translate in the x direction
     * @param y how much to translate in the y direction
     */
    public void setTranslate(float x,float y){
        this.deltaX = x;
        this.deltaY = y;
    }

    /** Unlike setTranslate, this moves the character from it's current position
     *
     * @param deltaX how much to translate in the x direction from the current position
     * @param deltaY how much to translate in the y direction from the current position
     */
    public void translateFromPos(float deltaX,float deltaY){
        this.setTranslate(this.deltaX +deltaX,this.deltaY + deltaY);
    }

    /** This tells the maximum health of any character; what to initialize the health to
     *
     * @return the maximum health of the character
     */
    public abstract int getMaxHealth();
}
