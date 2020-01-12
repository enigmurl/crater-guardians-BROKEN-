package com.enigmadux.craterguardians;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.enigmadux.craterguardians.Characters.Kaiser;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Characters.Ryze;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.FileStreams.SettingsData;
import com.enigmadux.craterguardians.GUI.Button;
import com.enigmadux.craterguardians.GUI.CharacterSelect;
import com.enigmadux.craterguardians.GUI.HomeButton;
import com.enigmadux.craterguardians.GUI.InGameTextbox;
import com.enigmadux.craterguardians.GUI.MatieralsBar;
import com.enigmadux.craterguardians.GUI.ProgressBar;
import com.enigmadux.craterguardians.GUI.Upgradable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.EnigmaduxGLRenderer;
import enigmadux2d.core.shapes.TexturedRect;

/** The renderer used to do all the drawing
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CraterRenderer extends EnigmaduxGLRenderer {

    //says how far back the camera is from the view
    private static final float CAMERA_Z = 4f;//todo tutorial is messed up if its not 2

    //shader of off buttons in form of r g b a
    private static final float[] OFF_SHADER = new float[] {1.0f,0.5f,0.5f,1};
    //shader of on buttons in form of r g b a
    private static final float[] ON_SHADER = new float[] {0.5f,1.0f,0.5f,1};

    //cost of upgrdaing characters
    public static final int UPGRADE_COST  = 10;


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
    //private InGameTextbox xpIndicator;
    private MatieralsBar xpIndicator;

    //a sign that tells the player if they won or lost
    private TexturedRect stateIndicator;

    //visual image of the evolveButton. Backend does all the manipulation, it is only drawn here.
    private Button evolveButton;
    
    //button used in game to spawn the pauseGameLayout or to just pause the game
    private Button pauseButton;

    //whether or not music should be played, appears in the setting layout
    private Button musicOnOffButton;

    //whether or not sound effects should be played, appears in the setting layout
    private Button soundEffectsOnOffButton;

    //the player, backend does manipulation, it is only drawn here
    private Player player;

    //helps render stuff during the loading screen
    private RenderingThread renderingThread;


    //MATRICES
    private float[] orthographicM = new float[16];
    private float[] cameraTranslationM = new float[16];
    //ortho * cameraTranslation
    private float[] vPMatrix = new float[16];

    //LAYOUTS
    /** Used to change game wide settings, such as music on or off, and sound effects on or off
     *Includes: settings background,music on off button, sound effects on off button
     */
    private CraterLayout settingsLayout;

    /** Includes virtually all layouts not part of game screen.
     * It includes: Settings Button. Currency + basic stats display
     * Layouts included: homeScreenLayout + characterSelectLayout, most likely settingsLayout
     */
    private CraterLayout fullHomeScreenLayout;

    /** Default Home layout. First thing Shown after loading is complete
     *  It includes: The background. Play button. Character Select button. Level select Button.
     */
    private CraterLayout defaultHomeScreenLayout;

    /** Character Select. A 2d selection grid of which player to choose.
     * It includes the characters. And a back to home button.
     */
    //private CraterLayout characterSelectLayout;
    private CharacterSelect characterSelectLayout;

    /** Level Select. For now its just a place holder layout. In future There should be an image, where if you slide it a new one appears.
     * As of now it does not include anything
     *
     */
    private CraterLayout levelSelectLayout;

    /** The layout that is displayed while playing the game.
     * Includes the player sprite, all bots, the background (geography) Additionally trackers on score.
     * Otherwise we could have the level selector in the game, where its kind of like clash of clans war map, and player
     * can scout, then press play. Additionally the joystick controls
     */
    private CraterLayout gameScreenLayout;

    /** the layout that is displayed after the pause button
     *
     * Inludes: the resume button, the go to home layout button, and the go to level select layout, (as well as the background)
     */
    private CraterLayout pauseGameLayout;

    /** next Level. shown whenever a level is completed.
     * Includes: play button.
     * Future: (todo) : implement a back to level select layout
     *
     */
    private CraterLayout loadLevelLayout;

    /** This is the grandmaster layout for stuff that are stationary, rather than having many getters/setters this makes it so only one is needed
     *  it contains all the layouts + textboxes below this that are stationary
     */
    private CraterLayout stationaryTutorialLayout;

    /** This is the grandmaster layout for stuff that are stuck on the game map, rather than having many getters/setters this makes it so only one is needed
     *  it contains all the layouts + textboxes below this that are stuck on the game map
     */
    private CraterLayout gameMapTutorialLayout;

    /** Contains buttons having to do with upgrading characters
     *
     */
    private CraterLayout characterUpgradeLayout;


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


    long debugStartMillis = System.currentTimeMillis();

    int updateCount = 0;
    int under60 = 0;
    List<Long> under60s = new ArrayList<Long>();
    long lastMillis = System.currentTimeMillis();
    long debugGameScreenMillis = System.currentTimeMillis();


    /** Called whenever a new frame is needed to be drawn. If the render mode is dirty, then it will only be called
     * on requestRender, otherwise it's called at 60fps (I believe)
     *
     * @param gl a GL object used to communicate with OpenGl
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        // clear Screen
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.6f,0.274f,0.1764f,1.0f);


        if (! this.loadingStarted){
            renderingThread = new RenderingThread();
            this.loadingStarted = true;
            Log.d("LOADING SCREEN:","Started rendering");
        }
        if (! this.loadingCompleted){

            Matrix.setLookAtM(this.cameraTranslationM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
            Matrix.scaleM(this.cameraTranslationM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraTranslationM,0);
            this.loadingScreen.draw(vPMatrix);
            this.lastMillis = System.currentTimeMillis();
            renderingThread.step(gl);
            return;
        }

        this.renderingThread = null;





        //todo inefficient to keep on chaning the pause, should only update it at certain times
        this.craterBackendThread.setPause(!(gameScreenLayout.isVisible() && ! pauseGameLayout.isVisible()));

        //Log.d("FRAMERATE","current framerate: "  + 10000/(longs.get(9) - longs.get(0)));


        long start = System.currentTimeMillis();
        if ((this.backend.getCurrentGameState() != CraterBackend.GAME_STATE_HOMESCREEN) && this.gameScreenLayout.isVisible()) {
            Matrix.setLookAtM(this.cameraTranslationM, 0, this.player.getDeltaX(), this.player.getDeltaY(), 1f, player.getDeltaX(), player.getDeltaY(), 0, 0, 1f, 0);
            Matrix.scaleM(cameraTranslationM, 0, this.backend.getCameraZoom(), this.backend.getCameraZoom(), 0);
            Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraTranslationM,0);
            this.gameScreenLayout.draw(this.vPMatrix);

            this.drawGameMap();
        }
        this.debugGameScreenMillis += System.currentTimeMillis() - start;

        // Reset the Model view Matri

        //draws all on screen components
        Matrix.setLookAtM(this.cameraTranslationM, 0, 0, 0, 1, 0, 0, 0, 0, 1f, 0);
        Matrix.scaleM(this.cameraTranslationM, 0, CAMERA_Z, CAMERA_Z * LayoutConsts.SCREEN_HEIGHT / LayoutConsts.SCREEN_WIDTH, 0);//too offset the orthographic projection for areas where it isnt needed
        Matrix.multiplyMM(vPMatrix,0,orthographicM,0,this.cameraTranslationM,0);
        if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_LEVELSELECT)  this.levelSelectLayout.draw(this.vPMatrix);
        if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_HOMESCREEN) this.fullHomeScreenLayout.draw(this.vPMatrix);
        this.drawScreenUtils();

        if (System.currentTimeMillis() - lastMillis  >  1000/60f){
            under60++;
            under60s.add((long) (1000f/(System.currentTimeMillis() - lastMillis)));
        }
        this.lastMillis = System.currentTimeMillis();
        updateCount++;
        if (System.currentTimeMillis() - debugStartMillis > 10000){
            Log.d("FRONTENDTHREAD:","Frames per second:"  + (1000 * updateCount/(double) (System.currentTimeMillis() - debugStartMillis)));
            Log.d("FRONTENDTHREAD:","percentage under 60:"  + ((float) under60/updateCount));
            Log.d("FRONTENDTHREAD:","under 60s:"  + under60s);

            Log.d("FRONTENDTHREAD:","Game screen Time:"  + (this.debugGameScreenMillis/this.updateCount) + " percentage: " + (this.debugGameScreenMillis/(double) (System.currentTimeMillis() - debugStartMillis)));

            debugStartMillis = System.currentTimeMillis();
            updateCount = 0;
            under60 = 0;
            under60s.clear();
            this.debugGameScreenMillis = 0;
        }


    }

    /** Helps draw portions of the game map that is not covered in the game layout. Mostly stuff that have a variable amount
     * (enemies, obstacles, spawners etc)
     *
     */
    private void drawGameMap(){
        //basically this stops threads from accessing the same variables at the same time, as during the level select levels are loaded, which
        //if drawn at the same time from two threads will throw a java ConcurrmentModificationExcpetion
        if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_TUTORIAL ){
            this.gameMapTutorialLayout.draw(this.vPMatrix);
        }



    }


    /** Draws anything that is independent of the matrix. E.g. joysticks, pause button.
     *
     */
    private void drawScreenUtils(){
        if (this.gameScreenLayout.isVisible()) {
            this.loadLevelLayout.draw(this.vPMatrix);

            long tutorialMillis = backend.getTutorialCurrentMillis();
            if (tutorialMillis > CraterBackend.JOYSTICK_INTRODUCTION && ! (this.loadLevelLayout.isVisible() || this.backend.isInEndGamePausePeriod())) {
                this.attackJoyStick.draw(this.vPMatrix);
                this.movementJoyStick.draw(this.vPMatrix);
            }
            if (tutorialMillis > CraterBackend.EVOLVE_INTRODUCTION && ! (this.loadLevelLayout.isVisible() || this.backend.isInEndGamePausePeriod())) {
                this.evolveButton.draw(this.vPMatrix);
            }

            if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_TUTORIAL) {
                this.stationaryTutorialLayout.draw(this.vPMatrix);
            }


            if (tutorialMillis > CraterBackend.CHARACTER_INTRODUCTION  && backend.getCurrentGameState() != CraterBackend.GAME_STATE_LEVELSELECT) {
                this.healthDisplay.draw(this.vPMatrix);
            }

            this.stateIndicator.draw(this.vPMatrix,(this.backend.hasWonLastLevel() ? 0:1));
            this.battleStartIndicator.draw(this.vPMatrix);


            this.pauseButton.draw(this.vPMatrix);
            this.pauseGameLayout.draw(this.vPMatrix);


        }
    }

    /** Gets the Default camera position. The greater the value the farther away the camera "is";
     *
     */
    public float getDefaultCameraZ(){
        return CraterRenderer.CAMERA_Z;
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


        GLES20.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        //gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        //gl.glLoadIdentity();
        //Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        Matrix.orthoM(orthographicM,0,-CAMERA_Z,CAMERA_Z,-CAMERA_Z* height/width,CAMERA_Z * height/width,0.2f,5f);
        //gl.glOrthof(-CAMERA_Z,CAMERA_Z,-CAMERA_Z* height/width,CAMERA_Z * height/width,0.2f,5f);
        //GLU.gluOrtho2D(gl,-1.0f,1.0f,-1.0f,1.0f);
        //GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        //gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        //gl.glLoadIdentity(); 					//Reset The Modelview Matrix
        Log.d("RENDERER","dimensions " + width + " H "  + height);


        if (this.craterBackendThread != null) this.craterBackendThread.setRunning(false);

        this.craterBackendThread = new CraterBackendThread(this.backend);
        this.craterBackendThread.setRunning(true);
        this.craterBackendThread.setPause(true);
        this.craterBackendThread.start();



        Log.d("RENDERER","started backend thread");


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

        //if this is the first time
        if (! this.loadingStarted) {
            TexturedRect.loadProgram();
            this.loadingScreen.loadGLTexture(this.context, R.drawable.loading_screen);
        }
    }


    /** Sets the player of the renderer, and it passes onto the backend as well
     *
     * @param player the current palyer
     */
    public void setPlayer(Player player){
        this.player = player;
        this.backend.setPlayer(player);
    }
    /** Loads textures that aren't loading screen oriented
     *
     */
    private void loadNonBegginingTextures(GL10 gl,int step){
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
                this.backend = new CraterBackend(context, this);
                this.craterBackendThread.setBackend(this.backend);
                break;
            case 1:
                this.playerData.loadPlayerData();
                break;
            case 2:
                this.backend.loadTextures(gl);
                break;
            case 3:
                this.backend.loadLayouts(gl);
                break;
            case 4:
                this.backend.loadTutorialLayouts(gl);
                break;
            case 5:
                this.player = new Kaiser();
                this.backend.setPlayer(this.player);

                this.movementJoyStick = this.backend.getMovementJoyStick();
                this.attackJoyStick = this.backend.getAttackJoyStick();
                this.evolveButton = this.backend.getEvolveButton();

                this.gameScreenLayout = this.backend.getGameScreenLayout();
                this.levelSelectLayout = this.backend.getLevelSelectLayout();
                this.loadLevelLayout = this.backend.getLoadLevelLayout();

                this.stationaryTutorialLayout = this.backend.getStationaryTutorialLayout();
                this.gameMapTutorialLayout = this.backend.getGameMapTutorialLayout();

                this.healthDisplay = this.backend.getHealthDisplay();
                this.battleStartIndicator = this.backend.getBattleStartIndicator();
                this.stateIndicator = this.backend.getStateIndicator();


                SoundLib.loadMedia(context);

                break;
            case 6:
                this.loadLayouts(gl, this.context);
                break;
            case 7:
                //todo, this may cause it to play the music for an enigma second
                SoundLib.setStateLobbyMusic(true);
                this.settingsData.loadSettingsFile();
                //set color of music on off button
                if (SoundLib.isPlayMusic()) {
                    this.musicOnOffButton.setShader(CraterRenderer.ON_SHADER[0], CraterRenderer.ON_SHADER[1], CraterRenderer.ON_SHADER[2], CraterRenderer.ON_SHADER[3]);
                } else {
                    this.musicOnOffButton.setShader(CraterRenderer.OFF_SHADER[0], CraterRenderer.OFF_SHADER[1], CraterRenderer.OFF_SHADER[2], CraterRenderer.OFF_SHADER[3]);
                    SoundLib.muteAllMedia();
                }
                //set color of sound effect on off button
                if (SoundLib.isPlaySoundEffects()) {
                    this.soundEffectsOnOffButton.setShader(CraterRenderer.ON_SHADER[0], CraterRenderer.ON_SHADER[1], CraterRenderer.ON_SHADER[2], CraterRenderer.ON_SHADER[3]);
                } else {
                    this.soundEffectsOnOffButton.setShader(CraterRenderer.OFF_SHADER[0], CraterRenderer.OFF_SHADER[1], CraterRenderer.OFF_SHADER[2], CraterRenderer.OFF_SHADER[3]);
                }
                break;
            case 8:

                this.loadingCompleted = true;
                break;
        }
    }


    /** Goes from game to default home screen
     *
     */
    public void exitGame(){
        this.gameMapTutorialLayout.hide();
        this.stationaryTutorialLayout.hide();
        this.loadLevelLayout.hide();
        this.pauseGameLayout.hide();
        this.gameScreenLayout.hide();
        this.levelSelectLayout.hide();
        this.characterSelectLayout.hide();
        this.defaultHomeScreenLayout.show();

    }

    /** Goes from game to level selecthome screen
     *
     */
    public void exitGameLoadLevelSelect(){
        this.gameMapTutorialLayout.hide();
        this.stationaryTutorialLayout.hide();
        this.loadLevelLayout.hide();
        this.pauseGameLayout.hide();
        this.gameScreenLayout.hide();
        this.levelSelectLayout.show();
        //backend.rese

    }

    /** Sets the appropriate color of the buttons
     *
     */
    public void updateUpgradeLayouts(){
        this.xpIndicator.updateResources();
        //this.xpIndicator.setText("Experience: " + PlayerData.getExperience());

        if (PlayerData.getExperience() < CraterRenderer.UPGRADE_COST){
            for (EnigmaduxComponent enigmaduxComponent:this.characterUpgradeLayout.getComponents()) {
                ((Button) enigmaduxComponent).setText("UPGRADE: " + CraterRenderer.UPGRADE_COST);
                ((Button) enigmaduxComponent).setShader(1, 0.5f, 0.5f, 1);
            }
        } else {
            for (EnigmaduxComponent enigmaduxComponent:this.characterUpgradeLayout.getComponents()) {
                ((Button) enigmaduxComponent).setText("UPGRADE: " + CraterRenderer.UPGRADE_COST);
                ((Button) enigmaduxComponent).setShader(0.8f,1,0.5f,1);
            }
        }
    }

    /** Loads the layouts, including creating sub components, and loading their textures
     *
     * @param gl a GL10 object used to access openGL related methods
     * @param context Any non null context that is used to access resources
     */
    private void loadLayouts(GL10 gl,Context context){
        //for some components a scale factor is needed
        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;


        //Getting all the components
        //TODO MAke this image home button
        HomeButton backToHomeButton = new HomeButton(-0.75f,0f,0.4f,backend,this);


        final Button kaiserSelectButton = new Button("Kaiser Level: " + Kaiser.PLAYER_LEVEL, 0f,0.3f, 0.7f, 0.4f, 0.25f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                characterSelectLayout.hide();
                defaultHomeScreenLayout.show();
                player = new Kaiser();
                backend.setPlayer(player);
            }


        };

        final Button ryzeSelectButton = new Button("Ryze Level: " + Ryze.PLAYER_LEVEL, 0f,-0.4f, 0.7f, 0.4f, 0.25f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                characterSelectLayout.hide();
                defaultHomeScreenLayout.show();
                player = new Ryze();
                backend.setPlayer(player);
            }


        };

        Upgradable ryzeUpgradeButton = new Upgradable(new Ryze(),0.7f,-0.5f,ryzeSelectButton,playerData,this);

        Upgradable kaiserUpgradeButton = new Upgradable(new Kaiser(),0.7f,0.2f,kaiserSelectButton,playerData,this);

        final TexturedRect ryzeInfo = new TexturedRect(-scaleX/2,-0.5f,scaleX,1){
            @Override
            public boolean onTouch(MotionEvent e) {
                if (this.visible && ! this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()))){
                    if (e.getActionMasked() == MotionEvent.ACTION_UP){
                        this.hide();
                        return true;
                    }
                }
                return this.visible;
            }
        };

        final TexturedRect kaiserInfo = new TexturedRect(-scaleX/2,-0.5f,scaleX ,1){
            @Override
            public boolean onTouch(MotionEvent e) {
                if (this.visible && ! this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()))){
                    if (e.getActionMasked() == MotionEvent.ACTION_UP){
                        this.hide();
                        return true;
                    }
                }
                return this.visible;
            }
        };

        Button kaiserInfoButton = new Button("Info", 0.7f,0.45f, 0.3f, 0.15f, 0.1f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();
                kaiserInfo.show();
            }


        };

        Button ryzeInfoButton = new Button("Info", 0.7f,-0.25f, 0.3f, 0.15f, 0.1f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                ryzeInfo.show();
            }


        };

        Button characterSelectButton_homeScreen = new Button("Select Evolver", 0,0.4f, 1.2f, 0.5f, 0.2f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                defaultHomeScreenLayout.hide();
                characterSelectLayout.show();
            }


        };

        Button playButton_homeScreen = new Button("Play Game", 0,-0.2f, 1.2f, 0.5f, 0.2f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();


                levelSelectLayout.show();
                fullHomeScreenLayout.hide();
                backend.setCurrentGameState(CraterBackend.GAME_STATE_LEVELSELECT);

            }

        };

        Button tutorialButton = new Button("Tutorial",0,-0.75f,0.8f,0.2f,0.1f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                SoundLib.setStateLobbyMusic(false);
                SoundLib.setStateGameMusic(true);
                fullHomeScreenLayout.hide();
                gameScreenLayout.show();
                backend.resetJoysticksTapped();
                backend.setTutorialCurrentMillis(0);
                backend.setLevelNum(-1);//tutorial
                backend.loadLevel();
                backend.setCurrentGameState(CraterBackend.GAME_STATE_TUTORIAL);

            }
        };

        //this.xpIndicator = new InGameTextbox("Experience: " + PlayerData.getExperience(),0,0.9f,0.1f,LayoutConsts.CRATER_TEXT_COLOR,false);
        this.xpIndicator = new MatieralsBar(-1,0.8f,2,0.2f);

        this.pauseButton = new Button(new TexturedRect(-1f,0.8f,0.2f * scaleX,0.2f )) {
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                pauseGameLayout.show();
            }

        };

        Button resumeButton = new Button(new TexturedRect(- (0.3f * scaleX)/2,0.4f,0.3f*scaleX,0.3f)){

            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                pauseGameLayout.hide();
                backend.resetJoySticks();
            }


        };


        HomeButton homeButton = new HomeButton(0,0.05f,0.3f,this.backend,this);


        Button levelSelectButton_PauseMenu = new Button("Levels",0,-0.5f,0.5f,0.2f,0.1f,LayoutConsts.CRATER_TEXT_COLOR, false) {
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                exitGameLoadLevelSelect();

                backend.setCurrentGameState(CraterBackend.GAME_STATE_LEVELSELECT);
                backend.killEndGamePausePeriod();

                SoundLib.setStateLobbyMusic(true);
                SoundLib.setStateVictoryMusic(false);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateGameMusic(false);
            }
        };

        Button settingsButton = new Button (new TexturedRect(0.95f - (0.2f * scaleX),0.75f,0.2f*scaleX,0.2f)) {
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                settingsLayout.show();
                defaultHomeScreenLayout.hide();
            }

        };

         this.musicOnOffButton = new Button(new TexturedRect(-0.4f - (0.4f*scaleX)/2 ,0.1f,0.4f*scaleX,0.4f)) {
             @Override
             public boolean isSelect(MotionEvent e) {
                 return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
             }

             @Override
             public void onRelease() {
                 super.onRelease();

                 if (Arrays.equals(this.texturedRect.getShader(), CraterRenderer.OFF_SHADER)) {
                     this.texturedRect.setShader(CraterRenderer.ON_SHADER[0],CraterRenderer.ON_SHADER[1],CraterRenderer.ON_SHADER[2],CraterRenderer.ON_SHADER[3]);
                     SoundLib.unMuteAllMedia();
                     SoundLib.setPlayMusic(true);
                 } else {
                     this.texturedRect.setShader(CraterRenderer.OFF_SHADER[0],CraterRenderer.OFF_SHADER[1],CraterRenderer.OFF_SHADER[2],CraterRenderer.OFF_SHADER[3]);
                     SoundLib.muteAllMedia();
                     SoundLib.setPlayMusic(false);
                 }

                 settingsData.writeSettingsFile();
             }

         };

         this.soundEffectsOnOffButton = new Button(new TexturedRect(0.4f - (0.4f*scaleX)/2,0.1f,0.4f*scaleX,0.4f)) {
             @Override
             public boolean isSelect(MotionEvent e) {
                 return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
             }

             @Override
             public void onRelease() {
                 super.onRelease();

                 if (Arrays.equals(this.texturedRect.getShader(), CraterRenderer.OFF_SHADER)) {
                     this.texturedRect.setShader(CraterRenderer.ON_SHADER[0],CraterRenderer.ON_SHADER[1],CraterRenderer.ON_SHADER[2],CraterRenderer.ON_SHADER[3]);
                     SoundLib.setPlaySoundEffects(true);
                 } else {
                     this.texturedRect.setShader(CraterRenderer.OFF_SHADER[0],CraterRenderer.OFF_SHADER[1],CraterRenderer.OFF_SHADER[2],CraterRenderer.OFF_SHADER[3]);
                     SoundLib.setPlaySoundEffects(false);
                 }

                 settingsData.writeSettingsFile();
             }

        };

        InGameTextbox soundEffectCaption = new InGameTextbox("Sound \nEffects",0.4f,0.65f,0.2f,LayoutConsts.CRATER_TEXT_COLOR,false);

        InGameTextbox musicCaption = new InGameTextbox("Music",-0.4f,0.75f,0.2f,LayoutConsts.CRATER_TEXT_COLOR,false);


        Button settingsDoneButton = new Button("Back to home", 0,-0.5f, 0.5f, 0.2f, 0.4f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                settingsLayout.hide();
                defaultHomeScreenLayout.show();
            }

        };

        //TexturedRect characterDisplayDefaultHomeScreen = new TexturedRect(-0.8f,-0.2f,0.3f,0.2f);
        this.characterUpgradeLayout = new CraterLayout(new EnigmaduxComponent[] {
                kaiserUpgradeButton,
                ryzeUpgradeButton
        },-0.8f,-0.8f,1.6f,1.6f);

        //includes characters

        this.characterSelectLayout = new CharacterSelect(this.backend,this,playerData,this.xpIndicator);
//        this.characterSelectLayout = new CraterLayout(new EnigmaduxComponent[] {
//                xpIndicator,
//                this.characterUpgradeLayout,
//                ryzeInfoButton,
//                kaiserInfoButton,
//                backToHomeButton,
//                kaiserSelectButton,
//                ryzeSelectButton,
//                ryzeInfo,
//                kaiserInfo,
//        },-0.8f,-0.8f,1.6f,1.6f){
//
//            @Override
//            public void show() {
//                for (int x = 0;x<this.components.length-2;x++){
//                    this.components[x].show();
//                }
//            }
//
//            @Override
//            public boolean onTouch(MotionEvent e) {
//                boolean interested = false;
//                for (int i = this.components.length - 1;i>= 0;i--){
//                    interested |= this.components[i].onTouch(e);
//                }
//                return interested;
//            }
//        };
        
        this.defaultHomeScreenLayout = new CraterLayout(new EnigmaduxComponent[] {
                tutorialButton,
                characterSelectButton_homeScreen,
                playButton_homeScreen,
                settingsButton,
                xpIndicator
                },-1.0f,-1.0f,2.0f,1.8f){

        };


        this.pauseGameLayout = new CraterLayout(new EnigmaduxComponent[]{
                resumeButton,
                homeButton,
                levelSelectButton_PauseMenu
        },-0.4f,-0.4f,0.8f,0.8f);

        this.settingsLayout = new CraterLayout(new EnigmaduxComponent[]{
                this.musicOnOffButton,
                this.soundEffectsOnOffButton,
                soundEffectCaption,
                musicCaption,
                settingsDoneButton
        },0,0,0,0);

        this.fullHomeScreenLayout = new CraterLayout(new EnigmaduxComponent[] {
                this.defaultHomeScreenLayout,
                this.characterSelectLayout,
                this.settingsLayout,
                                         },-1.0f,-1.0f,2.0f,2.0f){

        };



        this.fullHomeScreenLayout.show();
        this.settingsLayout.hide();
        this.characterSelectLayout.hide();
        this.gameScreenLayout.hide();
        this.pauseGameLayout.hide();
        this.defaultHomeScreenLayout.show();



        soundEffectCaption.loadGLTexture();
        musicCaption.loadGLTexture();
        settingsButton.loadGLTexture(this.context,R.drawable.settings_button);
        this.musicOnOffButton.loadGLTexture(this.context,R.drawable.music_on_off_button);
        this.soundEffectsOnOffButton.loadGLTexture(this.context,R.drawable.sound_effect_on_off_button);
        settingsDoneButton.loadGLTexture();

        this.pauseButton.loadGLTexture(context,R.drawable.pause_button);
        resumeButton.loadGLTexture(context,R.drawable.resume_button);
        homeButton.loadGLTexture(context,R.drawable.home_button);
        levelSelectButton_PauseMenu.loadGLTexture();

        xpIndicator.loadGLTexture(context);
        characterSelectButton_homeScreen.loadGLTexture();
        playButton_homeScreen.loadGLTexture();
        tutorialButton.loadGLTexture();

        backToHomeButton.loadGLTexture(context,R.drawable.home_button);

        this.characterSelectLayout.loadGLTexture(context);
        kaiserUpgradeButton.loadGLTexture();
        ryzeUpgradeButton.loadGLTexture();
        kaiserSelectButton.loadGLTexture();
        ryzeSelectButton.loadGLTexture();
        kaiserInfo.loadGLTexture(context,R.drawable.kaiser_info);
        ryzeInfo.loadGLTexture(context,R.drawable.ryze_info);
        kaiserInfoButton.loadGLTexture();
        ryzeInfoButton.loadGLTexture();

        this.updateUpgradeLayouts();

        //characterDisplayDefaultHomeScreen.loadGLTexture(gl,context,R.drawable.test);



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
            if (!this.fullHomeScreenLayout.onTouch(e) &&
                    !pauseButton.onTouch(e) &&
                    !pauseGameLayout.onTouch(e) &&
                    !pauseGameLayout.isVisible() &&
                    (gameScreenLayout.isVisible() || levelSelectLayout.isVisible())) {
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
            this.craterBackendThread.setPause(true);
        }


    }

    /** Called on the resuming of the app
     *
     */
    public void onResume(){
        if (this.craterBackendThread != null) {
            this.craterBackendThread.setPause(false);
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
        void step(GL10 gl10) {
            loadNonBegginingTextures(gl10,this.step);
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

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }



}