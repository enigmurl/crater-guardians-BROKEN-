package com.enigmadux.craterguardians;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.FileStreams.SettingsData;
import com.enigmadux.craterguardians.FileStreams.TutorialData;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUIs.characterSelect.CharacterSelectLayout;
import com.enigmadux.craterguardians.GUIs.homeScreen.HomeScreen;
import com.enigmadux.craterguardians.GUIs.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.GUIs.levelSelect.LevelSelectLayout;
import com.enigmadux.craterguardians.GUIs.pauseGameScreen.PauseGameLayout;
import com.enigmadux.craterguardians.GUIs.postGameLayout.PostGameLayout;
import com.enigmadux.craterguardians.GUIs.settingsScreen.SettingsScreen;
import com.enigmadux.craterguardians.gameLib.GUIDataWrapper;
import com.enigmadux.craterguardians.players.Kaiser;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.players.TutorialPlayer;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.worlds.World;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.EnigmaduxGLRenderer;
import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

import enigmadux2d.core.shaders.ShaderProgram;

/** The renderer used to do all the drawing
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CraterRenderer extends EnigmaduxGLRenderer {

    //says how far back the camera is from the view
    private static final float CAMERA_Z = 3f;


    private DisplayMetrics displayMetrics;//used to get information about screen;

    private CraterBackendThread craterBackendThread;//used to call update on the backend object


    //streams that allow us to access data about files
    //tells experience of the player
    private PlayerData playerData;
    //loads and writes settings of the player, for now just having to do with sound effects and music
    private SettingsData settingsData;
    //tells if this is the players first game
    private TutorialData tutorialData;


    //whether or not all componnets have been loaded to memory
    private boolean loadingCompleted = false;
    //whether or not loading has been started of non loading screen elements
    private boolean loadingStarted = false;
    //The first thing the user sees, the loading screen
    private QuadTexture loadingScreen;
    //IN GAME COMPONENTS

    //helps render stuff during the loading screen
    private RenderingThread renderingThread;


    //MATRICES
    //orthographic projection
    private float[] orthographicM = new float[16];
    //camera matrix
    private float[] cameraM = new float[16];
    //ortho * cameraTranslation
    private float[] vPMatrix = new float[16];
    private float[] guiMatrix = new float[16];


    /** This performs the openGL calls
     *
     */

    //todo these are all debug varaibles delete them before releaes

    long debugStartMillis = System.currentTimeMillis();

    int updateCount = 0;
    int under60 = 0;
    long lastMillis = System.currentTimeMillis();


    private World world;


    /** Single, non instanced quads, are rendered using this, only used for the loading screen
     *
     */
    private GuiRenderer guiRenderer;


    /** Makes it easier to render lots of GUIs
     *
     */
    private GUIDataWrapper guiData;

    private HashMap<String,GUILayout> layoutHashMap = new HashMap<>();


    /** Constructor to set the handed over context
     *
     * @param context The context used for loading the square's texture to it
     */
    public CraterRenderer(Context context) {
        super(context);


        final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        LayoutConsts.SCREEN_WIDTH = displayMetrics.widthPixels + this.getNavigationBarHeight();
        LayoutConsts.SCREEN_HEIGHT = displayMetrics.heightPixels;

        this.playerData = new PlayerData(context);
        this.settingsData = new SettingsData(context);
        this.tutorialData = new TutorialData(context);


    }

    /** Used whenever the surface is created(see android documentation for more details)
     *
     * @param gl a Gl object used to communicate with open gl
     * @param config config of open gl (check android doc)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config){
        Log.d("LOADING SCREEN:","Started loading from source ");
        Log.d("RENDERER","onSurfaceCreated");
        super.onSurfaceCreated(gl,config);


        final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        LayoutConsts.SCREEN_WIDTH = displayMetrics.widthPixels + this.getNavigationBarHeight();
        LayoutConsts.SCREEN_HEIGHT = displayMetrics.heightPixels;

        LayoutConsts.SCALE_X = 1;
        LayoutConsts.SCALE_Y = 1;
        if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT){
            LayoutConsts.SCALE_X = (float) (LayoutConsts.SCREEN_HEIGHT )/ (LayoutConsts.SCREEN_WIDTH);
        } else {
            LayoutConsts.SCALE_Y = (float) (LayoutConsts.SCREEN_WIDTH)/LayoutConsts.SCREEN_HEIGHT;

        }

        Log.d("GL ERRORS ", "1) Error code; " + GLES30.glGetError());

        this.guiRenderer = new GuiRenderer(this.context,R.raw.gui_vertex_shader,R.raw.gui_fragment_shader);
        this.guiRenderer.startRendering();

        if (! this.loadingStarted) {
            this.loadingScreen = new QuadTexture(context,R.drawable.loading_screen,0,0,2,2);
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

        Log.d("RENDERER_","dm w " + (this.displayMetrics.widthPixels + this.getNavigationBarHeight()) + " dm h " + this.displayMetrics.heightPixels
                + " lc w "  +LayoutConsts.SCREEN_WIDTH + " lc h " + LayoutConsts.SCREEN_HEIGHT);


        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }


        GLES30.glViewport(0, 0, width, height); 	//Reset The Current Viewport


        //Calculate The Aspect Ratio Of The Window
        Matrix.orthoM(orthographicM,0,-CAMERA_Z,CAMERA_Z,-CAMERA_Z* height/width,CAMERA_Z * height/width,0.2f,5f);
        Log.d("RENDERER","dimensions " + width + " H "  + height);


        if (this.craterBackendThread == null) {
            this.craterBackendThread = new CraterBackendThread(this.world);
            this.craterBackendThread.setRunning(true);
            this.craterBackendThread.setPause(true);
            this.craterBackendThread.start();
        }



        Log.d("RENDERER","started backend thread");


    }


    /** Resets the screen to a brown screen
     *
     */
    private void clearScreen(){
        //reset the color array
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //fill it with a brownish color
        GLES30.glClearColor(1,0.125f,0.125f,1.0f);
    }

    /** Resets the camera Translation M to the origin and pointing towards there
     *
     */
    private void resetCamera(){
        Matrix.setLookAtM(this.cameraM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
    }


    /** Renders the loading screen
     *
     * @return true if loading screen is visible, false if it is not needed to be rendered
     */
    private boolean renderLoadingScreen(){
        //if loading hasn't started start it
        if (! this.loadingStarted){
            renderingThread = new RenderingThread();
            this.loadingStarted = true;
            Log.d("LOADING SCREEN:","Started rendering");
        }
        //if it hasn't completed, draw the loading screen
        if (! this.loadingCompleted){
            this.guiRenderer.startRendering();
            this.resetCamera();
            Matrix.scaleM(this.cameraM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraM,0);

            this.guiRenderer.renderQuad(this.loadingScreen,this.vPMatrix);
            this.lastMillis = System.currentTimeMillis();
            renderingThread.step();
            return true;
        }

        //if we haven't finished it, finish it
        if (this.renderingThread != null) {
            this.loadingScreen.recycle();
            this.renderingThread = null;
        }

        return false;
    }

    /** Called whenever a new frame is needed to be drawn. If the render mode is dirty, then it will only be called
      * on requestRender, otherwise it's called at 60fps (I believe)
      *
      * @param gl a GL object used to communicate with OpenGl
      */
    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.d("NumDraws: "," "+ TexturedRect.numDraws);
        ShaderProgram.NUM_STATE_CHANGES = 0;
        ShaderProgram.NUM_DRAW_CALLS = 0;

        // clear Scree
        this.clearScreen();

        //if loading hasn't rendered return
        if (this.renderLoadingScreen()) return;


        if ((this.world.getCurrentGameState() != World.STATE_GUI)) {
            Matrix.setIdentityM(this.cameraM,0);
            Matrix.scaleM(this.cameraM,0,this.world.getCameraZoom(),this.world.getCameraZoom(),1);
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraM,0);   //this.world.draw(vPMatrix);
        }
        //draws all on screen components
        Matrix.setLookAtM(this.cameraM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
        Matrix.scaleM(this.cameraM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
        Matrix.multiplyMM(this.guiMatrix,0,orthographicM,0,this.cameraM,0);

        this.world.draw(vPMatrix,this.guiMatrix);

        if (System.currentTimeMillis() - lastMillis  >  1000/60f){
            under60++;
        }
        this.lastMillis = System.currentTimeMillis();
        updateCount++;



        if (System.currentTimeMillis() - debugStartMillis > 10000){
            Log.d("FRONTENDTHREAD:","Frames per second:"  + (1000 * updateCount/(double) (System.currentTimeMillis() - debugStartMillis)));
            Log.d("FRONTENDTHREAD:","percentage under 60:"  + ((float) under60/updateCount));

            Log.d("FRONTENDTHREAD:","Total Time:"  + ((System.currentTimeMillis() - debugStartMillis)/this.updateCount));

            debugStartMillis = System.currentTimeMillis();
            updateCount = 0;
            under60 = 0;
        }


        //Log.d("CRATER RENDERER:","State changes: " + ShaderProgram.NUM_STATE_CHANGES + " draw calls: " + ShaderProgram.NUM_DRAW_CALLS);
    }


    /** Sets the player of the renderer, and it passes onto the backend as well
     *
     * @param player the current player
     */
    public void setPlayer(Player player){
        this.world.setPlayer(player);
    }


    /** Loads textures that aren't loading screen oriented
     *
     */
    private void loadNonBeginningTextures(int step){
        //this is in the case of a resumed screen, we don't need to re init all the stuff then
        if (this.loadingCompleted){
            return;
        }

        //padding frame so that the loading scren will display
        if (step < 0){
            return;
        }
        switch (step) {
            case 0:
                Log.d("RENDERER","Loading step: 0");
                QuadTexture.resetTextures();

                break;
            case 1:
                Log.d("RENDERER","Loading step: 1");
                this.tutorialData.loadTutorialFile();
                break;
            case 2:
                this.world = new World(context,this.layoutHashMap);
                this.craterBackendThread.setBackend(this.world);
                Log.d("RENDERER","Loading step: 2");
                break;
            case 3:
                this.playerData.loadPlayerData();
                Log.d("RENDERER","Loading step: 3");
                break;
            case 4:
                Log.d("RENDERER","Loading step: 4");
                break;
            case 5:
                World.loadTextures(context);
                Log.d("RENDERER","Loading step: 5");
                break;
            case 6:
                Log.d("RENDERER","Loading step: 6");

                SoundLib.loadMedia(context);

                break;
            case 7:
                this.world.setPlayer(new Kaiser(0,0));
                Log.d("RENDERER","Loading step: 7");
                break;
            case 8:
                Log.d("RENDERER","Loading step: 8");
                //todo, this may cause it to play the music for an enigma second
                this.settingsData.loadSettingsFile();
                break;
            case 9:
                Log.d("RENDERER","Loading step: 9");
                ArrayList<GUILayout> layouts = new ArrayList<>();
                HomeScreen homeScreen = new HomeScreen(this);
                SettingsScreen settingsScreen = new SettingsScreen(this.settingsData);
                CharacterSelectLayout characterSelectLayout = new CharacterSelectLayout(this,playerData);
                LevelSelectLayout levelSelectLayout = new LevelSelectLayout(this);

                InGameScreen inGameScreen = new InGameScreen(this);
                PostGameLayout postGameLayout = new PostGameLayout(this);
                PauseGameLayout pauseGameLayout = new PauseGameLayout(this);


                this.layoutHashMap.put(HomeScreen.ID,homeScreen);
                this.layoutHashMap.put(SettingsScreen.ID,settingsScreen);
                this.layoutHashMap.put(CharacterSelectLayout.ID,characterSelectLayout);
                this.layoutHashMap.put(LevelSelectLayout.ID,levelSelectLayout);
                this.layoutHashMap.put(InGameScreen.ID,inGameScreen);
                this.layoutHashMap.put(PostGameLayout.ID,postGameLayout);
                this.layoutHashMap.put(PauseGameLayout.ID,pauseGameLayout);


                layouts.add(homeScreen);
                layouts.add(settingsScreen);
                layouts.add(characterSelectLayout);
                layouts.add(levelSelectLayout);
                layouts.add(inGameScreen);
                layouts.add(postGameLayout);
                layouts.add(pauseGameLayout);



                this.guiData = new GUIDataWrapper(layouts);
                this.guiData.loadComponents(this.context,this.layoutHashMap);
                this.world.loadLayouts();
                if (TutorialData.TUTORIAL_ENABLED){
                    this.world.setPlayer(new TutorialPlayer());
                    //important this is first, because internally, it may be set
                    //to false later on
                    this.craterBackendThread.setPause(false);
                    inGameScreen.setVisibility(true);
                    //non positives = tutorial
                    this.world.setLevelNum(0);
                    this.world.loadLevel();
                    this.world.setState(World.STATE_PREGAME);
                    SoundLib.setStateGameMusic(true);
                } else {
                    homeScreen.setVisibility(true);
                }
            case 10:
                this.loadingCompleted = true;
                break;
        }
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
            this.guiData.onTouch(e);
        } catch (NullPointerException ev) {
            Log.i("null Pointer", "touch event before loaded",ev);
        }
        return true;

    }

    /** Called on the pausing of the app
     *
     */
     void onPause(){
        if (this.craterBackendThread != null && this.world != null) {
            if (this.world.getCurrentGameState() == World.STATE_PREGAME ||
                    this.world.getCurrentGameState() == World.STATE_INGAME ||
                    this.world.getCurrentGameState() == World.STATE_POSTGAMEPAUSE) {

                this.layoutHashMap.get(PauseGameLayout.ID).setVisibility(true);
            }
            this.craterBackendThread.setPause(true);
        }


    }

    /** Called on the resuming of the app
     *
     */
    void onResume(){
    }

    void onStop(){
        try {
            this.craterBackendThread.setRunning(false);
            this.craterBackendThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void onStart(){
        if (this.craterBackendThread == null || ! this.craterBackendThread.isAlive()) {
            this.craterBackendThread = new CraterBackendThread(this.world);
            this.craterBackendThread.setRunning(true);
            this.craterBackendThread.setPause(true);
            this.craterBackendThread.start();
        }
    }

    public CraterBackendThread getCraterBackendThread(){
        return craterBackendThread;
    }

    public World getWorld(){
        return this.world;
    }

    private class RenderingThread {


        //put some blank steps before so gl can keep the screen blank
        private int PADDING_STEPS = 5;
        //step number
        private int step = -PADDING_STEPS;

        /**
         *
         */
        void step() {
            loadNonBeginningTextures(this.step);
            this.step++;
        }
    }


}