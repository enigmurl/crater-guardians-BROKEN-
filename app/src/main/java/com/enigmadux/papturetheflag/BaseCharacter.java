package com.enigmadux.papturetheflag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.enigmadux.papturetheflag.Attacks.Attack;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.shapes.TexturedRect;
/** All characters and enemies derive from here. Provides functionality for rendering like sprite sheets.
 * @author Manu Bhat
 * @version BETA
 */
public abstract class BaseCharacter extends TexturedRect {
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
    public static final float CHARACTER_HEIGHT = 0.6f;


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
    protected int health = 10;

    private boolean isAlive = true;

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

    }

    /** Draws the character and elements related to it
     *
     * @param gl the GL10 object used to communicate with open gl
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(GL10 gl, float[] parentMatrix) {
        for (Attack attack: this.attacks){
            attack.draw(gl,parentMatrix);
        }
        super.draw(gl, parentMatrix);

        //the scale is sometimes set lower for 1 frame to indicate damage taken, after that 1 frame it needs to be reset
        this.setScale(1,1);
    }

    /** based on the current state, which frame should it be?
     *
     * @param rotation the angle at which the character is in degrees
     * @param frameNum the frame# to display in the animation
     */
    private void setFrame(float rotation,int frameNum){
        float x1 = ((float) frameNum/this.framesPerRotation) * (float) this.orgW/afterW;
        float x2 = ((float) (frameNum+1)/this.framesPerRotation) * (float) this.orgW/afterW;
        float y1 = (float) (afterH - this.orgH)/afterH + (float) ((int) rotation/(int) (360f/this.numRotationOrientations))/this.numRotationOrientations * (float) (this.orgH)/afterH;// flip because have to make it go from canvas coordinates to openGL.todo possible optimization on the arithmetic of this line
        float y2 = y1 + ((float) (this.orgH)/afterH)/numRotationOrientations;
        //Log.d(TAG,"Y1: " + y1 + " y2: " + y2 + " adder: " +  ((float) (this.orgH)/afterH));

        this.loadTextureBuffer(new float[] {
            x1,y2,
            x1,y1,
            x2,y2,
            x2,y1
        });
    }

    /** Updates based on game state
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
    /** Loads the texture of the sprite sheet
     *
     * @param gl a GL10 object used to access openGL
     * @param context context used to grab the actual image from res
     */
    public abstract void loadGLTexture(@NonNull GL10 gl, Context context);

    /**In process of how its gonna work
     *
     * @param angle the angle at which to attack
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
        this.setScale(5f,5f);
        if (this.health <= 0){
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
    //todo needs javadoc
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
        this.health = 10;//todo dont hardcode this
        this.isAlive = true;
    }

    /** sees if its alive
     *
     * @return if its alive
     */
    public boolean isAlive() {
        return isAlive;
    }
}
