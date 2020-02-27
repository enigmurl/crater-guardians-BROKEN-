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

import com.enigmadux.craterguardians.Characters.Kaiser;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy1;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.FileStreams.SettingsData;
import com.enigmadux.craterguardians.GUILib.Button;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUILib.InGameTextbox;
import com.enigmadux.craterguardians.GUILib.ProgressBar;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.GUIs.characterSelect.CharacterSelectLayout;
import com.enigmadux.craterguardians.GUIs.homeScreen.HomeScreen;
import com.enigmadux.craterguardians.GUIs.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.GUIs.levelSelect.LevelSelectLayout;
import com.enigmadux.craterguardians.GUIs.pauseGameScreen.PauseGameLayout;
import com.enigmadux.craterguardians.GUIs.postGameLayout.PostGameLayout;
import com.enigmadux.craterguardians.GUIs.settingsScreen.SettingsScreen;
import com.enigmadux.craterguardians.GameObjects.Plateau;
import com.enigmadux.craterguardians.gameLib.CraterVaoCollection;
import com.enigmadux.craterguardians.gameLib.GUIDataWrapper;
import com.enigmadux.craterguardians.gameLib.InstancedDataWrapper;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.EnigmaduxGLRenderer;
import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;
import enigmadux2d.core.renderEngine.MeshRenderer;

import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.shapes.TexturedRect;

/** The renderer used to do all the drawing
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CraterRenderer extends EnigmaduxGLRenderer {

    //says how far back the camera is from the view
    private static final float CAMERA_Z = 3f;//todo tutorial is messed up if its not 2

    //the vertices of a quad of size 1 by 1, centered around the origin
    private static final float[] QUAD_VERTICES = new float[] {
            -0.5f, 0.5f,0,
            -0.5f,-0.5f,0,
            0.5f,0.5f,0,
            0.5f,-0.5f,0,

    };
    //the texture coordinates of a quad of size 1 by 1 centered around the origin
    private static final float[] QUAD_TEXTURE_CORDS = new float[] {
                0,0,
                0,1,
                1,0,
                1,1
    };
    //the indices of which vertex to use for a quad
    private static final int[] QUAD_INDICES = new int[] {
                0,1,2,
                1,2,3
    };

    private DisplayMetrics displayMetrics;//used to get information about screen;

    private CraterBackend backend;//used to perform backend operations

    private CraterBackendThread craterBackendThread;//used to call update on the backend object


    //streams that allow us to access data about files
    //tells experience of the player
    private PlayerData playerData;
    //loads and writes settings of the player, for now just having to do with sound effects and music
    private SettingsData settingsData;


    //whether or not all componnets have been loaded to memory
    private boolean loadingCompleted = false;
    //whether or not loading has been started of non loading screen elements
    private boolean loadingStarted = false;
    //The first thing the user sees, the loading screen
    private TexturedRect loadingScreen = new TexturedRect(-1f,-1f,2f,2f);
    //IN GAME COMPONENTS
    //health bar of the player
    private ProgressBar healthDisplay;

    //visual image of the movement joystick. Backend does all the manipulation, it is only drawn here
    private TexturedRect movementJoyStick;

    //visual image of the attack JoyStick Backend does all the manipulation, it is only draw here
    private TexturedRect attackJoyStick;

    //textbox that says "Battle!" at the start of each level
    private InGameTextbox battleStartIndicator;

    //tells the user how much xp they have;
    //a sign that tells the player if they won or lost
    private TexturedRect stateIndicator;

    //visual image of the evolveButton. Backend does all the manipulation, it is only drawn here.
    private Button evolveButton;

    //the player, backend does manipulation, it is only drawn here
    private Player player;

    //helps render stuff during the loading screen
    private RenderingThread renderingThread;


    //MATRICES
    //orthographic projection
    private float[] orthographicM = new float[16];
    //camera matrix
    private float[] cameraTranslationM = new float[16];
    //ortho * cameraTranslation
    private float[] vPMatrix = new float[16];

    //LAYOUTS




    /** The layout that is displayed while playing the game.
     * Includes the player sprite, all bots, the background (geography) Additionally trackers on score.
     * Otherwise we could have the level selector in the game, where its kind of like clash of clans war map, and player
     * can scout, then press play. Additionally the joystick controls
     */
    private CraterLayout gameScreenLayout;

    /** This performs the openGL calls
     *
     */

    //todo these are all debug varaibles delete them before releaes

    long debugStartMillis = System.currentTimeMillis();

    int updateCount = 0;
    int under60 = 0;
    long lastMillis = System.currentTimeMillis();




    /** This does the openGL work on collections
     *
     */
    public MeshRenderer collectionsRenderer;

    /** This is a vao that contains data about the supplies graphically wise
     *
     */
    private VaoCollection suppliesVao;

    /** This is a vao that contains data about the toxic lakes graphically wise
     *
     */
    private VaoCollection toxicLakeVao;
    /** This is a vao that contains data about the spawners graphically wise
     *
     */
    private VaoCollection spawnersVao;
    /** This is a vao that contains data about the enemies graphically wise
     *
     */
    private VaoCollection enemiesVao;
    /** This is a vao that contains data about the plateaus graphically wise
     *
     */
    private VaoCollection plateausVao;


    /** Single, non instanced quads, are rendered using this
     *
     */
    private QuadRenderer quadRenderer;



    /** Makes it easier to render many VAOs
     *
     */
    private InstancedDataWrapper instancedData;


    /** Makes it easier to render lots of GUIs
     *
     */
    private GUIDataWrapper guiData;

    private HashMap<String,GUILayout> layoutHashMap;

    /** Dynamic textRenderer that renders text
     *
     */
    private DynamicText textRenderer;

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

        Log.d("GL ERRORS ", "1) Error code; " + GLES30.glGetError());


        this.collectionsRenderer = new MeshRenderer();
        this.collectionsRenderer.loadShaders(this.context,R.raw.basic_vertex_shader,R.raw.basic_frag_shader);



        suppliesVao = new CraterVaoCollection(3,CraterRenderer.QUAD_VERTICES,CraterRenderer.QUAD_TEXTURE_CORDS,CraterRenderer.QUAD_INDICES);
        suppliesVao.loadTexture(this.context,R.drawable.supply_top_view);

        enemiesVao = new CraterVaoCollection(500,CraterRenderer.QUAD_VERTICES,
                new float[] {

                    0,(Enemy1.NUM_ROTATION_ORIENTATIONS-1f)/Enemy1.NUM_ROTATION_ORIENTATIONS,
                    0,1,
                    1/(float) Enemy1.FRAMES_PER_ROTATION,(Enemy1.NUM_ROTATION_ORIENTATIONS-1f)/Enemy1.NUM_ROTATION_ORIENTATIONS,
                    1/(float) Enemy1.FRAMES_PER_ROTATION,1,
                },CraterRenderer.QUAD_INDICES);
        enemiesVao.loadTexture(this.context,R.drawable.enemy1_sprite_sheet);

        toxicLakeVao = new CraterVaoCollection(10,CraterRenderer.QUAD_VERTICES,CraterRenderer.QUAD_TEXTURE_CORDS,CraterRenderer.QUAD_INDICES);
        toxicLakeVao.loadTexture(this.context,R.drawable.toxic_lake_texture);

        spawnersVao = new CraterVaoCollection(6,CraterRenderer.QUAD_VERTICES,CraterRenderer.QUAD_TEXTURE_CORDS,CraterRenderer.QUAD_INDICES);
        spawnersVao.loadTexture(this.context,R.drawable.enemy1_spawner);

        plateausVao = new CraterVaoCollection(20, Plateau.VERTICES,Plateau.TEX_CORDS,CraterRenderer.QUAD_INDICES);
        plateausVao.loadTexture(this.context,R.drawable.plateau);


        if (! this.loadingStarted) {
            TexturedRect.loadProgram();
            this.loadingScreen.loadGLTexture(this.context, R.drawable.loading_screen);
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
            this.craterBackendThread = new CraterBackendThread(this.backend);
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
        GLES30.glClearColor(0.6f,0.274f,0.1764f,1.0f);
    }

    /** Resets the camera Translation M to the origin and pointing towards there
     *
     */
    private void resetCamera(){
        Matrix.setLookAtM(this.cameraTranslationM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
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
            this.resetCamera();
            Matrix.scaleM(this.cameraTranslationM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraTranslationM,0);

            this.loadingScreen.draw(vPMatrix);
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
        TexturedRect.numDraws = 0;
        // clear Screen


        this.clearScreen();

        //if loading hasn't rendered return
        if (this.renderLoadingScreen()) return;

        if ((this.backend.getCurrentGameState() != CraterBackend.GAME_STATE_HOMESCREEN) && this.gameScreenLayout.isVisible()) {

                float deltaX = this.player.getDeltaX();
                float deltaY = this.player.getDeltaY();
            Matrix.setLookAtM(this.cameraTranslationM, 0, deltaX,deltaY, 1f,deltaX, deltaY, 0, 0, 1f, 0);
            Matrix.scaleM(cameraTranslationM, 0, this.backend.getCameraZoom(), this.backend.getCameraZoom(), 0);
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraTranslationM,0);
            this.gameScreenLayout.draw(this.vPMatrix);
        }
        //draws all on screen components
        Matrix.setLookAtM(this.cameraTranslationM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
        Matrix.scaleM(this.cameraTranslationM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
        Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraTranslationM,0);

        this.drawScreenUtils();

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


    }



    /** Draws anything that is independent of the matrix. E.g. joysticks, pause button.
     *
     */
    private void drawScreenUtils(){

        if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_INGAME) {
//
            this.attackJoyStick.draw(this.vPMatrix);
            this.movementJoyStick.draw(this.vPMatrix);

            this.evolveButton.draw(this.vPMatrix);

            this.healthDisplay.draw(this.vPMatrix);
//
            this.stateIndicator.draw(this.vPMatrix,(this.backend.hasWonLastLevel() ? 0:1));
            this.battleStartIndicator.draw(this.vPMatrix);

        }
        this.guiData.renderData(this.vPMatrix,this.quadRenderer,this.textRenderer);
        Matrix.scaleM(this.vPMatrix,0,0.125f,0.25f,0);
    }

    /** Gets the Default camera position. The greater the value the farther away the camera "is";
     *
     */
    public float getDefaultCameraZ(){
        return CraterRenderer.CAMERA_Z;
    }

    /** Shows the post game layout
     *
     */
    public void showPostGameLayout(){
        this.layoutHashMap.get(PostGameLayout.ID).setVisibility(true);
    }


    /** Sets the player of the renderer, and it passes onto the backend as well
     *
     * @param player the current player
     */
    public void setPlayer(Player player){
        this.player = player;
        this.backend.setPlayer(player);
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
                this.backend = new CraterBackend(this.context,this,
                        suppliesVao,toxicLakeVao,enemiesVao,spawnersVao,plateausVao);
                this.craterBackendThread.setBackend(this.backend);
                break;
            case 1:
                Log.d("RENDERER","Loading step: 1");
                this.playerData.loadPlayerData();
                break;
            case 2:
                Log.d("RENDERER","Loading step: 2");
                this.backend.loadTextures();
                break;
            case 3:
                Log.d("RENDERER","Loading step: 3");
                this.backend.loadLayouts();
                break;
            case 4:
                Log.d("RENDERER","Loading step: 4");
                break;
            case 5:
                Log.d("RENDERER","Loading step: 5");
                this.suppliesVao.loadTexture(this.context,R.drawable.supply_top_view);
                break;
            case 6:
                Log.d("RENDERER","Loading step: 6");

                this.player = new Kaiser();
                this.backend.setPlayer(this.player);

                this.movementJoyStick = this.backend.getMovementJoyStick();
                this.attackJoyStick = this.backend.getAttackJoyStick();
                this.evolveButton = this.backend.getEvolveButton();

                this.gameScreenLayout = this.backend.getGameScreenLayout();

                this.healthDisplay = this.backend.getHealthDisplay();
                this.battleStartIndicator = this.backend.getBattleStartIndicator();
                this.stateIndicator = this.backend.getStateIndicator();


                SoundLib.loadMedia(context);

                break;
            case 7:
                Log.d("RENDERER","Loading step: 7");
                this.loadLayouts();
                break;
            case 8:
                Log.d("RENDERER","Loading step: 8");
                //todo, this may cause it to play the music for an enigma second
                this.settingsData.loadSettingsFile();
                break;
            case 9:
                Log.d("RENDERER","Loading step: 9");
                QuadTexture.resetTextures();
                ArrayList<GUILayout> layouts = new ArrayList<>();
                this.layoutHashMap = new HashMap<>();
                HomeScreen homeScreen = new HomeScreen(this.backend);
                SettingsScreen settingsScreen = new SettingsScreen(this.settingsData);
                CharacterSelectLayout characterSelectLayout = new CharacterSelectLayout(this );
                LevelSelectLayout levelSelectLayout = new LevelSelectLayout(this.craterBackendThread);
                InGameScreen inGameScreen = new InGameScreen();
                PostGameLayout postGameLayout = new PostGameLayout(this.craterBackendThread);
                PauseGameLayout pauseGameLayout = new PauseGameLayout(this.craterBackendThread);


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


                this.quadRenderer = new QuadRenderer(this.context,R.raw.gui_vertex_shader,R.raw.gui_fragment_shader);

                this.guiData = new GUIDataWrapper(layouts);
                this.guiData.loadComponents(this.context,this.layoutHashMap);


                homeScreen.setVisibility(true);
            case 10:
                this.textRenderer = new DynamicText(this.context,R.drawable.baloo_bhaina_texture_atlas,R.raw.baloo_bhaina_atlas);
            case 11:
                this.loadingCompleted = true;
                break;
        }
    }


    /** Goes from game to default home screen
     *
     */
    public void exitGame(){
        this.gameScreenLayout.hide();
    }


    /** Loads the layouts, including creating sub components, and loading their textures
     *
     */
    private void loadLayouts(){
        this.gameScreenLayout.hide();
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
            if (!this.guiData.onTouch(e) &&
                    (gameScreenLayout.isVisible())) {
                backend.onTouch(e);
            }
        } catch (NullPointerException ev) {
            Log.i("null Pointer", "touch event before loaded",ev);
        }
        return true;

    }

    /** Called on the pausing of the app
     *
     */
    public void onPause(){
        if (this.craterBackendThread != null) {
            if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_INGAME) {
                this.layoutHashMap.get(PauseGameLayout.ID).setVisibility(true);
            }
            this.craterBackendThread.setPause(true);
        }


    }

    /** Called on the resuming of the app
     *
     */
    public void onResume(){
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

    /** Compiles the shader code
     *
     * @param type vertex or fragment
     * @param shaderCode the actual code
     * @return an integer that tells openGL how to write the code
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }



}