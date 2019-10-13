package com.enigmadux.papturetheflag;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.enigmadux.papturetheflag.Characters.Kaiser;
import com.enigmadux.papturetheflag.Characters.Player;
import com.enigmadux.papturetheflag.Enemies.Enemy;
import com.enigmadux.papturetheflag.Spawners.Spawner;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.EnigmaduxGLRenderer;
import enigmadux2d.core.shapes.TexturedRect;

/** The renderer used to do all the drawing
 *
 * @author Manu Bhat
 * @version BETA
 */
public class PapsRenderer extends EnigmaduxGLRenderer {


    private DisplayMetrics displayMetrics;//used to get information about screen;

    private PapsBackend backend;//used to perform backend operations

    //IN GAME COMPONENTS
    //visual image of the left joystick. Backend does all the manipulation, it is only drawn here
    private TexturedRect leftJoyStick;

    //visual image of the right joystick Backend does all the manipulation, it is only draw here
    private TexturedRect rightJoyStick;

    //the player, backend does manipulation, it is only drawn here
    private Player player;


    //MATRICES
    private float[] cameraTranslationM = new float[16];

    //LAYOUTS
    /** Includes virtually all layouts not part of game screen.
     * It includes: Settings Button. Currency + basic stats display
     * Layouts included: homeScreenLayout + characterSelectLayout, most likely settingsLayout
     */
    private PapsLayout fullHomeScreenLayout;

    /** Default Home layout. First thing Shown after loading is complete
     *  It includes: The background. Play button. Character Select button. Level select Button.
     */
    private PapsLayout defaultHomeScreenLayout;

    /** Character Select. A 2d selection grid of which player to choose.
     * It includes the characters. And a back to home button.
     */
    private PapsLayout characterSelectLayout;

    /** Level Select. For now its just a place holder layout. In future There should be an image, where if you slide it a new one appears.
     * As of now it does not include anything
     *
     */
    private PapsLayout levelSelectLayout;

    /** The layout that is displayed while playing the game.
     * Includes the player sprite, all bots, the background (geography) Additionally trackers on score.
     * Otherwise we could have the level selector in the game, where its kind of like clash of clans war map, and player
     * can scout, then press play. Additionally the joystick controls
     */
    private PapsLayout gameScreenLayout;

    /** Constructor to set the handed over context
     *
     * @param context The context used for loading the square's texture to it
     */
    public PapsRenderer(Context context) {
        super(context);
    }

    /** Called whenever a new frame is needed to be drawn. If the render mode is dirty, then it will only be called
     * on requestRender, otherwise it's called at 60fps (I believe)
     *
     * @param gl a GL object used to communicate with OpenGl
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        this.backend.update(gl,1000/60);//todo make separate backend thread and update the 1000/60 value
        // clear Screen and Depth Buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glClearColor(0,1.0f,1.0f,1.0f);

        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();


        float yOffset = (this.backend.getCurrentGameState() == PapsBackend.GAME_STATE_INGAME) ? -0f : 0;
        Matrix.setLookAtM(this.cameraTranslationM,0,this.player.getDeltaX(), player.getDeltaY()+yOffset,1f, player.getDeltaX(), player.getDeltaY(),0,0,1f,0);

        this.fullHomeScreenLayout.draw(gl,this.cameraTranslationM);
        this.gameScreenLayout.draw(gl,this.cameraTranslationM);
        this.drawGameMap(gl);
        this.levelSelectLayout.draw(gl,this.cameraTranslationM);

        // Reset the Model view Matrix
        gl.glLoadIdentity();


        //draws all on screen components
        Matrix.setLookAtM(this.cameraTranslationM,0,0,0,1,0,0,0,0,1f,0);

        this.drawScreenUtils(gl);

    }

    /** Helps draw portions of the game map that is not covered in the game layout. Mostly stuff that have a variable amount
     * (enemies, obstacles, spawners etc)
     *
     * @param gl the GL10 object used to communicate with open gl
     */
    public void drawGameMap(GL10 gl){
        if (this.gameScreenLayout.isVisible()){
            for (Plateau plateau: this.backend.getPlateaus()){
                plateau.draw(gl,this.cameraTranslationM);
            }

            for (ToxicLake toxicLake:this.backend.getToxicLakes()){
                toxicLake.draw(gl,this.cameraTranslationM);
            }
            for (Spawner spawner: this.backend.getSpawners()){
                if (spawner.isTextureLoaded())
                    spawner.draw(gl,this.cameraTranslationM);
            }

            this.player.draw(gl,this.cameraTranslationM);

            for (Enemy enemy: this.backend.getEnemies()){
                enemy.draw(gl,this.cameraTranslationM);
            }


        }
    }


    /** Draws anything that is independent of the matrix. E.g. joysticks, pause button.
     *
     * @param gl the GL10 object used to communicate with open gl
     */
    public void drawScreenUtils(GL10 gl){
        if (this.gameScreenLayout.isVisible()){
            this.rightJoyStick.draw(gl,this.cameraTranslationM);
            this.leftJoyStick.draw(gl,this.cameraTranslationM);
        }
    }


    /** Used whenever the surface is changed(e.g rotated screen from landscape to portrait) (see android documentation for more details)
     *
     * @param gl a GL object used to communicate with OpenGL
     * @param width the new width of the surface (in pixels I believe, but could be open gl width)
     * @param height the new height of the surface (in pixels I believe, but could be open gl height)
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }

        gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        gl.glLoadIdentity(); 					//Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        gl.glOrthof(-1.0f,1.0f,-1.0f,1.0f,0.2f,5f);
        //GLU.gluOrtho2D(gl,-1.0f,1.0f,-1.0f,1.0f);
        //GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        gl.glLoadIdentity(); 					//Reset The Modelview Matrix


    }

    /** Used whenever the surface is created(see android documentation for more details)
     *
     * @param gl a Gl object used to communicate with open gl
     * @param config config of open gl (check android doc)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl,config);



        final WindowManager w = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);


        this.backend = new PapsBackend(displayMetrics,context);


        this.player = new Kaiser();

        this.backend.setPlayer(this.player);
        this.backend.loadLayouts(gl);

        this.leftJoyStick  = this.backend.getLeftJoyStick();
        this.rightJoyStick = this.backend.getRightJoyStick();

        this.gameScreenLayout = this.backend.getGameScreenLayout();
        this.levelSelectLayout = this.backend.getLevelSelectLayout();




        this.loadLayouts(gl,this.context);

    }

    /** Loads the layouts, including creating sub components, and loading their textures
     *
     * @param gl a GL10 object used to access openGL related methods
     * @param context Any non null context that is used to access resources
     */
    private void loadLayouts(GL10 gl,Context context){
        //Getting all the components
        Button backToHomeButton = new Button("BACK TO HOME",R.font.baloobhaina,-0.8f,0.3f,0.3f,0.2f,0xFFFFFFFF){

            @Override
            public boolean onTouch(MotionEvent e) {
                if (this.visible && this.isInside(getOpenGLX(e.getRawX()),getOpenGLY(e.getRawY())) && e.getActionMasked() == MotionEvent.ACTION_UP) {
                    characterSelectLayout.hide();
                    defaultHomeScreenLayout.show();
                    backend.setCurrentGameState(PapsBackend.GAME_STATE_HOMESCREEN);
                    return true;

                }
                return false;
            }//BACK TO HOME BUTTON
        };

        Button characterSelectButton_homeScreen = new Button("SELECT CHARACTER",R.font.baloobhaina,-0.3f,0.4f,0.6f,0.3f,Color.BLUE){
            @Override
            public boolean onTouch(MotionEvent e) {
                if (this.visible && this.isInside(getOpenGLX(e.getRawX()),getOpenGLY(e.getRawY())) && e.getActionMasked() == MotionEvent.ACTION_UP) {
                    characterSelectLayout.show();
                    defaultHomeScreenLayout.hide();
                    return true;

                }
                return false;
            }
        };


        Button playButton_homeScreen = new Button("SELECT LEVEL",R.font.baloobhaina,-0.3f,0,0.6f,0.3f,Color.BLUE){
            @Override
            public boolean onTouch(MotionEvent e) {
                if (this.visible && this.isInside(getOpenGLX(e.getRawX()),getOpenGLY(e.getRawY())) && e.getActionMasked() == MotionEvent.ACTION_UP) {
                    levelSelectLayout.show();
                    fullHomeScreenLayout.hide();
                    backend.setCurrentGameState(PapsBackend.GAME_STATE_LEVELSELECT);
                    return true;

                }
                return false;
            }
        };



        TexturedRect characterDisplayDefaultHomeScreen = new TexturedRect(-0.8f,-0.2f,0.3f,0.2f);


        //includes characters
        this.characterSelectLayout = new PapsLayout(new EnigmaduxComponent[] {
                backToHomeButton
                                        },-0.8f,-0.8f,1.6f,1.6f){

        };
        
        this.defaultHomeScreenLayout = new PapsLayout(new EnigmaduxComponent[] {
                characterSelectButton_homeScreen,
                playButton_homeScreen,




                                         },-1.0f,-1.0f,2.0f,1.8f){

        };



        this.fullHomeScreenLayout = new PapsLayout(new EnigmaduxComponent[] {
                this.defaultHomeScreenLayout,
                this.characterSelectLayout,
                                         },-1.0f,-1.0f,2.0f,2.0f){

        };

        this.fullHomeScreenLayout.show();
        this.characterSelectLayout.hide();
        this.gameScreenLayout.hide();


        characterSelectButton_homeScreen.loadGLTexture(gl,context);
        playButton_homeScreen.loadGLTexture(gl,context);

        backToHomeButton.loadGLTexture(gl,context);

        characterDisplayDefaultHomeScreen.loadGLTexture(gl,context,R.drawable.test);



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

    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public boolean onTouch(MotionEvent e){
        try {
            this.fullHomeScreenLayout.onTouch(e);
            backend.onTouch(e);
        } catch (NullPointerException ev){
            Log.i("null Pointer", "touch event before loaded");
        }
        return true;
    }




}