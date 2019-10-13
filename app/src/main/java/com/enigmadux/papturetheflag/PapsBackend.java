package com.enigmadux.papturetheflag;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.enigmadux.papturetheflag.Characters.Player;
import com.enigmadux.papturetheflag.Enemies.Enemy;
import com.enigmadux.papturetheflag.Spawners.Enemy1Spawner;
import com.enigmadux.papturetheflag.Spawners.Spawner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Is in charge of all the backend activities
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class PapsBackend {
    //Integer which represents that the game is still on the on the home screen
    public static final int GAME_STATE_HOMESCREEN = 0;
    //Integer which represents that the game is on level select
    public static final int GAME_STATE_LEVELSELECT = 1;
    //Integer which represents that the game is being played
    public static final int GAME_STATE_INGAME = 2;



    //the center coordinate of the left joy stick (openGL coordinates)
    private static final float[] LEFT_JOY_STICK_CENTER = {-0.6f,-0.4f};
    //the center coordinate of right joy stick (openGL coordinates)
    private static final float[] RIGHT_JOY_STICK_CENTER = {0.6f,-0.4f};
    //the diameter of the left and right joysticks
    private static final float JOY_STICK_IMAGE_WIDTH = 0.2f;

    private static final float JOY_STICK_MAX_RADIUS = 0.3f;


    //used to get information about the screen todo: optimization: make the display metric stored in single static class, so the methods can be shared
    private DisplayMetrics displayMetrics;
    //used for getting more information about device
    private Context context;

    //joystick information

    //visual joystick
    private TexturedRect rightJoyStick;

    //openGl x coordinate of the right joystick
    private float rightJoyStickX;
    //openGL y coordinate of the right joystick
    private float rightJoyStickY;
    //whether the right joystick is being activated
    private boolean rightJoyStickDown = false;
    //how to identify which pointer corresponds to the right joystick
    private int rightJoyStickPointer;

    //visual joystick
    private TexturedRect leftJoyStick;
    //openGl x coordinate of the left joystick
    private float leftJoyStickX;
    //openGL y coordinate of the left joystick
    private float leftJoyStickY;
    //whether the left joystick is being activated
    private boolean leftJoyStickDown = false;
    //how to identify which pointer corresponds to the left joystick
    private int leftJoyStickPointer;

    //instance game state info

    //describes what the user is doing  (e.g. home screen, level select, and in game)
    private int currentGameState;

    //what level number
    private int levelNum;

    //the current player on the map
    private Player player;
    //all enemies on the map
    private List<Enemy> enemies = new ArrayList<>();
    //all spawner on the map
    private List<Spawner> spawners = new ArrayList<>();
    //all plateaus on the map
    private List<Plateau> plateaus = new ArrayList<>();
    //all active toxic lakes on the map
    private List<ToxicLake> toxicLakes = new ArrayList<>();


    /** The layout that is displayed while playing the game.
     * Includes the player sprite, all bots, the background (geography) Additionally trackers on score.
     * Otherwise we could have the level selector in the game, where its kind of like clash of clans war map, and player
     * can scout, then press play. Additionally the joystick controls
     */
    private PapsLayout gameScreenLayout;

    /** Level Select. For now its just a place holder layout. In future There should be an image, where if you slide it a new one appears.
     * As of now it does not include anything
     *
     */
    private PapsLayout levelSelectLayout;


    /** Default Constructor
     *
     * @param displayMetrics a configured DisplayMetrics object which
     * @param context any non null Context, used to access resources
     */
    public PapsBackend(DisplayMetrics displayMetrics, Context context){
        this.displayMetrics = displayMetrics;
        this.context = context;

        rightJoyStick = new TexturedRect(RIGHT_JOY_STICK_CENTER[0]-JOY_STICK_IMAGE_WIDTH/2,RIGHT_JOY_STICK_CENTER[1]-JOY_STICK_IMAGE_WIDTH/2,JOY_STICK_IMAGE_WIDTH,JOY_STICK_IMAGE_WIDTH);
        leftJoyStick = new TexturedRect(LEFT_JOY_STICK_CENTER[0]-JOY_STICK_IMAGE_WIDTH/2,LEFT_JOY_STICK_CENTER[1]-JOY_STICK_IMAGE_WIDTH/2,JOY_STICK_IMAGE_WIDTH,JOY_STICK_IMAGE_WIDTH);

        rightJoyStick.show();
        leftJoyStick.show();

        this.reset();

    }

    /** Sets the player that the player will use
     *
     * @param player the new player that is going to be played
     */
    public void setPlayer(Player player){
        this.player = player;
    }


    /** Initializes all layouts and their sub components;
     *
     */
    public void loadLayouts(GL10 gl){
        TexturedRect levelBackground = new TexturedRect(-1.0f,-1.0f,2.0f,2.0f);

        Button level1PlayButton = new Button("PLAY GAME",R.font.baloobhaina,-0.4f,0.2f,0.8f,0.4f, Color.BLUE){
            @Override
            public boolean onTouch(MotionEvent e) {

                if (this.visible && this.isInside(getOpenGLX(e.getRawX()),getOpenGLY(e.getRawY())) && e.getActionMasked() == MotionEvent.ACTION_UP) {
                    levelSelectLayout.hide();
                    gameScreenLayout.show();
                    levelNum = 1;//todo make it so it must be unlocked
                    setCurrentGameState(PapsBackend.GAME_STATE_INGAME);
                    loadLevel();
                    return true;
                }
                return false;
            }
        };

        this.gameScreenLayout = new PapsLayout(new EnigmaduxComponent[] {
                levelBackground,
        },-1.0f,-1.0f,2.0f,2.0f);

        this.levelSelectLayout = new PapsLayout(new EnigmaduxComponent[]{
                level1PlayButton,

            },-1.0f,1.0f,2.0f,2.0f);

        this.levelSelectLayout.hide();
        this.gameScreenLayout.hide();

        level1PlayButton.loadGLTexture(gl,this.context);
        levelBackground.loadGLTexture(gl,this.context,R.drawable.level_background_crater);


        this.loadTextures(gl);
    }

    /** Gets the gameScreenLayout, which holds all in game components
     *
     * @return the gameScreenLayout
     */
    public PapsLayout getGameScreenLayout(){
        return this.gameScreenLayout;
    }

    /** Gets the levelSelectLayout, which holds all level select components
     *
     * @return the levelSelectLayout
     */
    public PapsLayout getLevelSelectLayout(){
        return this.levelSelectLayout;
    }


    /** Returns the int describing the game state. Compare to class constants to get english representation.
     *
     * @return the int describing the game state
     */
    public int getCurrentGameState(){
        return this.currentGameState;
    }

    /** Sets the current game state.
     *
     * @param currentGameState the new game state. Recommended to use PapsBackend.GAME_STATE_* rather than hard coded literals, as for code readability.
     */
    public void setCurrentGameState(int currentGameState){
        this.currentGameState = currentGameState;
    }

    /** loads textures for in game components
     *
     * @param gl GL10 object used to access openGL
     */
    private void loadTextures(GL10 gl){

        this.rightJoyStick.loadGLTexture(gl,this.context,R.drawable.test);
        this.leftJoyStick.loadGLTexture(gl,this.context,R.drawable.test);

        this.player.loadGLTexture(gl,this.context);
    }


    /** Converts android canvas x coordinate into openGL coordinate
     *
     * @param x android canvas x coordinate
     * @return openGL x coordinate equivalent of (x)
     */
    private float getOpenGLX(float x){
        return  2* (x-(float) (displayMetrics.widthPixels+this.getNavigationBarHeight())/2) /(this.getNavigationBarHeight()+ displayMetrics.widthPixels);
    }

    /** Converts android canvas y coordinate into openGL coordinate
     *
     * @param y android canvas y coordinate
     * @return openGL y coordinate equivalent of (y)
     */
    private float getOpenGLY(float y){

        return  2* (-y+(float) displayMetrics.heightPixels/2) /( displayMetrics.heightPixels);
    }

    /** Gets the action bar height, used for finding screen width
     *
     * @return the amount of pixels in the action bar
     */
    private int getNavigationBarHeight() {
        int resourceId = this.context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return  this.context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /** Initializes a level
     *
     */
    private void loadLevel(){
        this.reset();
        //todo don't make it hardcoded but rather read from file
        if (this.levelNum == 1){
            this.spawners.add(new Enemy1Spawner(-1.0f,1.0f,0.5f,0.5f,4000));
            this.plateaus.add(new Plateau(new float[][]{
                    {1.0f,-1.0f},
                    {1.2f,-0.5f},
                    {1.5f,-1.3f},
                    {1.4f,-0.4f}
            }));
            this.toxicLakes.add(new ToxicLake(-1.0f,-1.0f,0.5f));
        }
    }

    /** Gets all spawners as to be used for drawing
     *
     * @return the list of spawners of the map
     */
    public List<Spawner> getSpawners() {
        return spawners;
    }

    /** Gets the enemies as to be used for drawing
     *
     * @return the list of enemies on the map
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /** Gets the plateaus as to be used for drawing
     *
     * @return the list of plateaus on the map;
     */
    public List<Plateau> getPlateaus() {
        return plateaus;
    }

    /** Gets the toxic lakes for drawing
     *
     * @return the list of toxic lakes on the map
     */
    public List<ToxicLake> getToxicLakes() {
        return this.toxicLakes;
    }

    /** Kills all enemies; all enemies are removed from memory
     *
     */
    private void reset() {
        this.enemies.clear();
        this.spawners.clear();
        this.plateaus.clear();
        this.toxicLakes.clear();
        if (this.player != null) {
            this.player.setTranslate(0, 0);
            this.player.spawn();
        }
    }

    /** Updates the game state
     *
     * @param gl a GL10 object used to access open gl to load textures (no drawing in the backend)
     * @param dt milliseconds since last call
     */
    public void update(GL10 gl,long dt){
        this.rightJoyStick.setTranslate(this.rightJoyStickX,this.rightJoyStickY);

        this.leftJoyStick.setTranslate(this.leftJoyStickX,this.leftJoyStickY);

        if (this.currentGameState == PapsBackend.GAME_STATE_INGAME){
            if (! this.player.isAlive()){
                this.reset();
                this.loadLevel();
            }

            this.player.translateFromPos(dt*this.leftJoyStickX/1000 * this.player.getCharacterSpeed(),dt*this.leftJoyStickY/1000 * this.player.getCharacterSpeed());
            for (Plateau plateau: this.plateaus){
                if (! plateau.isTextureLoaded()){
                    plateau.loadGLTexture(gl,context);

                }
                plateau.clipCharacterPos(player);
                for (Enemy enemy: this.enemies){
                    plateau.clipCharacterPos(enemy);
                }

            }


            float hypotenuse = (float) Math.hypot(this.leftJoyStickX,this.leftJoyStickY);
            this.player.update(dt,
                    (hypotenuse != 0) ? 180f/(float) Math.PI * PapsBackend.getAngle(this.leftJoyStickX/hypotenuse,this.leftJoyStickY/hypotenuse) : 0,this.enemies,gl,this.context);//todo make the rotation the previous frame's rotation
            Iterator itr = enemies.iterator();
            while (itr.hasNext()){
                Enemy enemy = (Enemy) itr.next();
                enemy.update(dt,this.player,gl,context);
                if (! enemy.isAlive()){
                    itr.remove();
                }
            }

        }

        for (ToxicLake toxicLake:this.toxicLakes){
            if (! toxicLake.isTextureLoaded()){
                toxicLake.loadGLTexture(gl,context);
            }
            toxicLake.update(dt,this.player,this.enemies);
        }



        for (Spawner spawner: this.spawners){
            if (! spawner.isTextureLoaded()){//todo bad way to load gl textures and is expensive
                spawner.loadGLTexture(gl,this.context);
            }
            Enemy e = spawner.trySpawnEnemy(gl,this.context,dt);
            if (e != null){
                this.enemies.add(e);
            }
        }


    }

    /** Returns the player; mainly used to get coordinates as to reposition the camera
     *
     * @return the current player object e.g Kaiser
     */
    public BaseCharacter getPlayer(){
        return this.player;
    }

    /** Gets the right joystick textured rect, so the frontend can draw it
     *
     * @return the visual representation of the right joystick
     */
    public TexturedRect getRightJoyStick(){
        return this.rightJoyStick;
    }

    /** Gets the left joystick textured rect, so the frontend can draw it
     *
     * @return the visual representation of the left joystick
     */
    public TexturedRect getLeftJoyStick(){
        return this.leftJoyStick;
    }

    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public boolean onTouch(MotionEvent e){
        if (this.currentGameState == PapsBackend.GAME_STATE_HOMESCREEN){
            return false;
        } else if (this.currentGameState == PapsBackend.GAME_STATE_LEVELSELECT){
            this.levelSelectLayout.onTouch(e);
        } else if (this.currentGameState == PapsBackend.GAME_STATE_INGAME){
            this.updateJoySticks(e);
        }
        return true;
    }



    /** Based on a touch event, updates positions for the joySticks
     *
     * @param e the motion event describing how the user interacted with the screen
     */
    private void updateJoySticks(MotionEvent e){

        int pointerInd  = e.getActionIndex();
        float x = getOpenGLX(e.getX(pointerInd));
        if (e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || e.getActionMasked() == MotionEvent.ACTION_DOWN){
            //assign joystick pointers
            if (x < 0 ){
                //assign left
                this.leftJoyStickPointer = e.getPointerId(pointerInd);
                this.leftJoyStickDown = true;

            } else {
                //analyze right
                this.rightJoyStickPointer = e.getPointerId(pointerInd);
                this.rightJoyStickDown = true;
                this.player.showAngleAimer();
            }

        } else if (e.getActionMasked() == MotionEvent.ACTION_POINTER_UP || e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_CANCEL){
            //de assign joystick pointers

            if (e.getPointerId(pointerInd) == this.leftJoyStickPointer && this.leftJoyStickDown){
                this.leftJoyStickDown = false;
            }
            if (e.getPointerId(pointerInd) == this.rightJoyStickPointer && this.rightJoyStickDown){
                this.rightJoyStickDown = false;
                float hypotenuse = (float) Math.hypot(this.rightJoyStickX,this.rightJoyStickY);
                this.player.attack(getAngle(rightJoyStickY/hypotenuse,rightJoyStickX/hypotenuse));
            }
        }
        try {
            if (this.leftJoyStickDown) {
                this.leftJoyStickX = this.getOpenGLX(e.getX(this.leftJoyStickPointer)) - LEFT_JOY_STICK_CENTER[0];
                this.leftJoyStickY = this.getOpenGLY(e.getY(this.leftJoyStickPointer)) - LEFT_JOY_STICK_CENTER[1];
                double hypotenuse = Math.hypot(this.leftJoyStickX,this.leftJoyStickY);
                if (hypotenuse > PapsBackend.JOY_STICK_MAX_RADIUS){
                    this.leftJoyStickX *= PapsBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                    this.leftJoyStickY *= PapsBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                }
            }
            else {
                this.leftJoyStickX = 0;
                this.leftJoyStickY = 0;
            }
        } catch (IllegalArgumentException exp){
            this.leftJoyStickX = 0;
            this.leftJoyStickY = 0;
        }

        try {
            if (this.rightJoyStickDown) {
                this.rightJoyStickX = this.getOpenGLX(e.getX(this.rightJoyStickPointer)) - RIGHT_JOY_STICK_CENTER[0];
                this.rightJoyStickY = this.getOpenGLY(e.getY(this.rightJoyStickPointer)) - RIGHT_JOY_STICK_CENTER[1];
                float hypotenuse = (float) Math.hypot(this.rightJoyStickX,this.rightJoyStickY);
                if (hypotenuse > PapsBackend.JOY_STICK_MAX_RADIUS){
                    this.rightJoyStickX *= PapsBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                    this.rightJoyStickY *= PapsBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                }
                this.player.setAngleAimerAngle((float) (180/Math.PI) * getAngle(rightJoyStickY/hypotenuse,rightJoyStickX/hypotenuse));
            } else {
                this.rightJoyStickX = 0;
                this.rightJoyStickY = 0;
            }
        } catch (IllegalArgumentException exp){
            this.leftJoyStickX = 0;
            this.leftJoyStickY = 0;
        }

    }

    /** Given the sin and cosine, it can compute the angle, not limited to any quadrant
     *
     * @param sin the sine (value between -1,1)
     * @param cos cosine (value between -1,1) Sin^2 + cos^2 should be equal to 1
     * @return the angle at which both the sin and cosine values are satisfied in radians
     */
    public static float getAngle(float sin,float cos){
        if (sin > 0 ){
            return (float) Math.acos(cos);
        }
        return (float) (2 * Math.PI - Math.acos(cos));
    }
}
