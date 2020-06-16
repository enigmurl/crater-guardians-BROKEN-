package com.enigmadux.craterguardians;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.enigmadux.craterguardians.filestreams.PlayerData;
import com.enigmadux.craterguardians.filestreams.SettingsData;
import com.enigmadux.craterguardians.filestreams.TutorialData;
import com.enigmadux.craterguardians.gamelib.DrawablesLoader;
import com.enigmadux.craterguardians.gamelib.GUIDataWrapper;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guis.characterSelect.CharacterSelectLayout;
import com.enigmadux.craterguardians.guis.homeScreen.HomeScreen;
import com.enigmadux.craterguardians.guis.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.guis.levelSelect.LevelSelectLayout;
import com.enigmadux.craterguardians.guis.pauseGameScreen.PauseGameLayout;
import com.enigmadux.craterguardians.guis.postGameLayout.PostGameLayout;
import com.enigmadux.craterguardians.guis.settingsScreen.SettingsScreen;
import com.enigmadux.craterguardians.loading.Loader;
import com.enigmadux.craterguardians.players.Kaiser;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.players.TutorialPlayer;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxGLRenderer;
import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;
import enigmadux2d.core.renderEngine.LoadingRenderer;

/** The renderer used to do all the drawing
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CraterRenderer extends EnigmaduxGLRenderer {

    private static final float LOADING_SCREEN_ASPECT_RATIO = 1006f/564;
    //says how far back the camera is from the view
    public static final float CAMERA_Z = 4f;


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


    private long lastFrameSessionStart = System.currentTimeMillis();
    private int updateCountThisFrameSession = 0;


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
//        Log.d("LOADING SCREEN:","Started loading from source ");
//        Log.d("RENDERER","onSurfaceCreated");
        try {
            super.onSurfaceCreated(gl, config);


            final WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final Display d = w.getDefaultDisplay();
            displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            LayoutConsts.SCREEN_WIDTH = displayMetrics.widthPixels + this.getNavigationBarHeight();
            LayoutConsts.SCREEN_HEIGHT = displayMetrics.heightPixels;

            LayoutConsts.SCALE_X = 1;
            LayoutConsts.SCALE_Y = 1;
            if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT) {
                LayoutConsts.SCALE_X = (float) (LayoutConsts.SCREEN_HEIGHT) / (LayoutConsts.SCREEN_WIDTH);
            } else {
                LayoutConsts.SCALE_Y = (float) (LayoutConsts.SCREEN_WIDTH) / LayoutConsts.SCREEN_HEIGHT;

            }


            this.guiRenderer = new GuiRenderer(this.context, R.raw.gui_vertex_shader, R.raw.gui_fragment_shader);
            this.guiRenderer.startRendering();

            if (!this.loadingStarted) {
                final float aspectRatio = LOADING_SCREEN_ASPECT_RATIO * LayoutConsts.SCALE_X;
                float x1 = 2 * aspectRatio;
                float y1 = 2;
                float x2 = 2;
                float y2 = 2 / aspectRatio;
                float width = (x1 < 2) ? x2 : x1;
                float height = (x1 < 2) ? y2 : y1;
                this.loadingScreen = new QuadTexture(context, R.drawable.loading_screen, 0, 0, width, height);
            }
        } catch (Exception e){
            Log.d("Exception","On Surface Created Failed",e);
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
        try {
            if (height == 0) {                        //Prevent A Divide By Zero By
                height = 1;                        //Making Height Equal One
            }

            GLES30.glViewport(0, 0, width, height);    //Reset The Current Viewport


            //Calculate The Aspect Ratio Of The Window
            Matrix.orthoM(orthographicM, 0, -CAMERA_Z, CAMERA_Z, -CAMERA_Z * height / width, CAMERA_Z * height / width, 0.2f, 5f);


            if (this.craterBackendThread == null) {
                this.craterBackendThread = new CraterBackendThread(this.context, this.world);
                this.craterBackendThread.setRunning(true);
                this.craterBackendThread.setGamePaused(true);
                this.craterBackendThread.start();
            }
        } catch (Exception e){
            Log.d("Exception","On Surface Changed Failed",e);

        }
    }


    /** Resets the screen to a brown screen
     *
     */
    private void clearScreen(){
        //reset the color array
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //fill it with a brownish color
        GLES30.glClearColor(0.768f,0.6078f,0.4f,1.0f);
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
//            Log.d("LOADING SCREEN:","Started rendering");
        }
        //if it hasn't completed, draw the loading screen
        if (! this.loadingCompleted){
            this.guiRenderer.startRendering();
            this.resetCamera();
            Matrix.scaleM(this.cameraM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraM,0);

            this.guiRenderer.renderQuad(this.loadingScreen,this.vPMatrix);
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
        try {
            // clear Scree
            this.clearScreen();

            //if loading hasn't rendered return
            if (this.renderLoadingScreen()) return;


            if ((this.world.getCurrentGameState() != World.STATE_GUI)) {
                Matrix.setIdentityM(this.cameraM, 0);
                Matrix.scaleM(this.cameraM, 0, this.world.getCameraZoom(), this.world.getCameraZoom(), 1);
                Matrix.multiplyMM(vPMatrix, 0, orthographicM, 0, this.cameraM, 0);   //this.world.draw(vPMatrix);
            }
            //draws all on screen components
            Matrix.setLookAtM(this.cameraM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
            Matrix.scaleM(this.cameraM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
            Matrix.multiplyMM(this.guiMatrix, 0, orthographicM, 0, this.cameraM, 0);

            this.world.draw(vPMatrix, this.guiMatrix);

            updateCountThisFrameSession++;

            if (System.currentTimeMillis() - lastFrameSessionStart > 10000) {
//            Log.d("FRONTENDTHREAD:","Frames per second:"  + (1000 * updateCountThisFrameSession /(double) (System.currentTimeMillis() - lastFrameSessionStart)));

                lastFrameSessionStart = System.currentTimeMillis();
                updateCountThisFrameSession = 0;
            }
        } catch (Exception e){
            Log.d("Exception","On Draw Frame Failed",e);

        }
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
     * returns whether it can move onto next step or not
     */
    private boolean loadNonBeginningTextures(int step){
        //this is in the case of a resumed screen, we don't need to re init all the stuff then
        if (this.loadingCompleted){
            return false;
        }

        //padding frame so that the loading scren will display
        if (step < 0){
            return true;
        }
        switch (step) {
            case 0:
//                Log.d("RENDERER","Loading step: 0");
                QuadTexture.resetTextures();
                break;
            case 1:
//                Log.d("RENDERER","Loading step: 1");
                this.tutorialData.loadTutorialFile();
                this.craterBackendThread.reloadSounds();
                break;
            case 2:
                if (this.world != null){
                    this.world.onDestroy();
                }
                this.world = new World(context,this.layoutHashMap);
                this.craterBackendThread.setBackend(this.world);
//                Log.d("RENDERER","Loading step: 2");
                break;
            case 3:
                this.playerData.loadPlayerData();
                DrawablesLoader.reset();
//                Log.d("RENDERER","Loading step: 3");
                break;
            case 4:
                return DrawablesLoader.loadResource(context);
//                Log.d("RENDERER","Loading step: 4");
            case 5:
                World.loadTextures(context);
//                Log.d("RENDERER","Loading step: 5");
                break;
            case 6:
//                Log.d("RENDERER","Loading step: 6");
                break;
            case 7:
                this.world.setPlayer(new Kaiser(0,0));
//                Log.d("RENDERER","Loading step: 7");
                break;
            case 8:
//                Log.d("RENDERER","Loading step: 8");
                this.settingsData.loadSettingsFile();
                break;
            case 9:
//                Log.d("RENDERER","Loading step: 9");
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
                    this.craterBackendThread.setGamePaused(false);
                    //non positives = tutorial
                    this.world.setLevelNum(0);
                    this.world.loadLevel();
                    inGameScreen.setVisibility(true);
                    this.world.setState(World.STATE_PREGAME);
                    SoundLib.setStateGameMusic(true);
                } else {
                    homeScreen.setVisibility(true);
                }
            case 10:
                this.loadingCompleted = true;
                break;
        }
        return true;
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
            if (this.guiData != null) {
                this.guiData.onTouch(e);
            }
        } catch (Exception e1){
            Log.d("EXCEPTION","Motion Event Failed: " + e1);
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
            this.craterBackendThread.setAppPaused(true);
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
            if (world != null && world.getEnemyMap() != null) this.world.getEnemyMap().endProcess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void onStart(){
        if (this.craterBackendThread == null || ! this.craterBackendThread.isAlive()) {
            boolean music = this.craterBackendThread != null && this.craterBackendThread.hasLoadedSound();
            this.craterBackendThread = new CraterBackendThread(this.context,this.world);
            if (music){
                this.craterBackendThread.alertSoundsAlreadyLoaded();
            }
            this.craterBackendThread.setRunning(true);
            this.craterBackendThread.setGamePaused(true);
            this.craterBackendThread.start();
        }
    }

    public CraterBackendThread getCraterBackendThread(){
        return craterBackendThread;
    }

    public World getWorld(){
        return this.world;
    }


    public void onDestroy(){
        if (this.world != null) {
            this.world.onDestroy();
        }
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
            if (loadNonBeginningTextures(this.step)) {
                this.step++;
            }
        }
    }

    private class CraterLoader extends Loader{

        //put some blank steps before so gl can keep the screen blank
        private int PADDING_STEPS = 5;
        //step number
        private int step = -PADDING_STEPS;
        public CraterLoader(LoadingRenderer loadingRenderer) {
            super(loadingRenderer);
        }

        @Override
        public boolean load(Context context) {
            super.load(context);
            if (loadNonBeginningTextures(this.step)) {
                this.step++;
                return true;
            }
            return false;
        }
    }

}