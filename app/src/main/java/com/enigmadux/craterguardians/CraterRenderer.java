package com.enigmadux.craterguardians;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.enigmadux.craterguardians.Animations.Animation;
import com.enigmadux.craterguardians.Characters.Kaiser;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.Spawners.Spawner;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Scanner;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.EnigmaduxGLRenderer;
import enigmadux2d.core.Text;
import enigmadux2d.core.shapes.TexturedRect;

/** The renderer used to do all the drawing
 *
 * @author Manu Bhat
 * @version BETA
 */
public class CraterRenderer extends EnigmaduxGLRenderer {
    //the path to the settings file
    private static final String SETTINGS = "settings";
    //says how far back the camera is from the view
    private static final float CAMERA_Z = 2f;//todo tutorial is messed up if its not 2

    //shader of off buttons in form of r g b a
    private static final float[] OFF_SHADER = new float[] {1.0f,0.5f,0.5f,1};
    //shader of on buttons in form of r g b a
    private static final float[] ON_SHADER = new float[] {0.5f,1.0f,0.5f,1};


    private DisplayMetrics displayMetrics;//used to get information about screen;

    private CraterBackend backend;//used to perform backend operations

    //IN GAME COMPONENTS
    //health bar of the player
    private ProgressBar healthDisplay;

    //visual image of the movement joystick. Backend does all the manipulation, it is only drawn here
    private TexturedRect movementJoyStick;

    //visual image of the attack JoyStick Backend does all the manipulation, it is only draw here
    private TexturedRect attackJoyStick;

    //visual image of the defense JoyStick Backend does all the manipulation, it is only draw here
    private TexturedRect defenseJoyStick;

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


    //MATRICES
    private float[] cameraTranslationM = new float[16];

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
    private CraterLayout characterSelectLayout;

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

    /** Constructor to set the handed over context
     *
     * @param context The context used for loading the square's texture to it
     */
    public CraterRenderer(Context context) {
        super(context);
    }


    //todo debuging
    private static ArrayList<Long> longs = new ArrayList<>(10);
    static {
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
        longs.add(0L);
    }

    /** Loads the data from the settings file into the SoundLib settings (play music/sound effects)
     *
     */
    private void loadSettingsFile(){
        try {
            Scanner stdin = new Scanner(this.context.openFileInput(CraterRenderer.SETTINGS));

            //first get music on or off
            SoundLib.setPlayMusic(stdin.nextBoolean());


            //second get sound effects on or off
            SoundLib.setPlaySoundEffects(stdin.nextBoolean());

            stdin.close();
        } catch (Exception e){
            Log.d("FRONTEND","Error loading settings file ",e);
            this.writeSettingsFile();
        } finally {
            //set color of music on off button
            if (SoundLib.isPlayMusic()){
                this.musicOnOffButton.setShader(CraterRenderer.ON_SHADER[0],CraterRenderer.ON_SHADER[1],CraterRenderer.ON_SHADER[2],CraterRenderer.ON_SHADER[3]);
            } else {
                this.musicOnOffButton.setShader(CraterRenderer.OFF_SHADER[0],CraterRenderer.OFF_SHADER[1],CraterRenderer.OFF_SHADER[2],CraterRenderer.OFF_SHADER[3]);
                SoundLib.muteAllMedia();
            }
            //set color of sound effect on off button
            if (SoundLib.isPlaySoundEffects()){
                this.soundEffectsOnOffButton.setShader(CraterRenderer.ON_SHADER[0],CraterRenderer.ON_SHADER[1],CraterRenderer.ON_SHADER[2],CraterRenderer.ON_SHADER[3]);
            } else {
                this.soundEffectsOnOffButton.setShader(CraterRenderer.OFF_SHADER[0],CraterRenderer.OFF_SHADER[1],CraterRenderer.OFF_SHADER[2],CraterRenderer.OFF_SHADER[3]);
            }
        }
    }

    /** Writes the data from SoundLib settings (play music/sound effects) into the settings file
     *
     */
    private void writeSettingsFile(){
        try {
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(this.context.openFileOutput (CraterRenderer.SETTINGS, Context.MODE_PRIVATE)));

            //first do the music on or off
            stdout.println(SoundLib.isPlayMusic());
            //then do the sound effects on or off
            stdout.print(SoundLib.isPlaySoundEffects());

            stdout.close();

        } catch (IOException e){
            Log.d("BACKEND","File write failed",e);
        }
    }

    /** Called whenever a new frame is needed to be drawn. If the render mode is dirty, then it will only be called
     * on requestRender, otherwise it's called at 60fps (I believe)
     *
     * @param gl a GL object used to communicate with OpenGl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        if (gameScreenLayout.isVisible() && ! pauseGameLayout.isVisible()) {
            this.backend.update(longs.get(9)-longs.get(8));//todo make separate backend thread and update the 1000/60 value
        }
        longs.remove(0);
        longs.add(System.currentTimeMillis());
        //Log.d("FRAMERATE","current framerate: "  + 10000/(longs.get(9) - longs.get(0)));

        // clear Screen and Depth Buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glClearColor(0.6f,0.274f,0.1764f,1.0f);

        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();


        float yOffset = (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_INGAME) ? -0f : 0;
        Matrix.setLookAtM(this.cameraTranslationM,0,this.player.getDeltaX(), player.getDeltaY()+yOffset,1f, player.getDeltaX(), player.getDeltaY(),0,0,1f,0);

        this.gameScreenLayout.draw(gl,this.cameraTranslationM);

        this.drawGameMap(gl);

        // Reset the Model view Matrix
        gl.glLoadIdentity();


        //draws all on screen components
        Matrix.setLookAtM(this.cameraTranslationM,0,0,0,1,0,0,0,0,1f,0);
        Matrix.scaleM(this.cameraTranslationM,0,CAMERA_Z,CAMERA_Z* LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH,0);//too offset the orthographic projection for areas where it isnt needed
        this.levelSelectLayout.draw(gl,this.cameraTranslationM);
        this.fullHomeScreenLayout.draw(gl,this.cameraTranslationM);
        this.drawScreenUtils(gl);

    }

    /** Helps draw portions of the game map that is not covered in the game layout. Mostly stuff that have a variable amount
     * (enemies, obstacles, spawners etc)
     *
     * @param gl the GL10 object used to communicate with open gl
     */
    private void drawGameMap(GL10 gl){
        if (this.gameScreenLayout.isVisible()){


            List<Plateau> p =  this.backend.getPlateaus();
            try {
                //plateaus don't need the timing as otherwise the player might get stuck
                for (Plateau plateau : this.backend.getPlateaus()) {
                    plateau.draw(gl, this.cameraTranslationM);
                }
            } catch (ConcurrentModificationException e){
                Log.d("RENDERER","BUG ALERT : plateaus:"  + this.backend.getPlateaus() + " num tries " + p,e);
            }

            List<ToxicLake> tl = this.backend.getToxicLakes();
            try {
                if (this.backend.getTutorialCurrentMillis() > CraterBackend.PLATEAUS_TOXIC_LAKE_INTRODUCITON) {
                    for (ToxicLake toxicLake : this.backend.getToxicLakes()) {
                        toxicLake.draw(gl, this.cameraTranslationM);
                    }
                }
            } catch (ConcurrentModificationException e){
                Log.d("RENDERER","BUG ALERT : enemies:"  + this.backend.getToxicLakes() + " num tries " + tl,e);
            }
            List<Animation> an = this.backend.getAnimations();
            try {
                for (Animation animation: this.backend.getAnimations()){
                    animation.draw(gl,this.cameraTranslationM);
                }
            }catch (ConcurrentModificationException e){
                Log.d("RENDERER","BUG ALERT : animations:"  + this.backend.getAnimations() + " num tries " + an,e);
            }


            List<Supply> supplies = this.backend.getSupplies();
            try {
                if (this.backend.getTutorialCurrentMillis() > CraterBackend.SUPPLIES_INTRODUCTION) {
                    for (Supply supply : this.backend.getSupplies()) {
                        supply.draw(gl, this.cameraTranslationM);
                    }
                }
            } catch (ConcurrentModificationException e){
                Log.d("RENDERER","BUG ALERT : enemies:"  + this.backend.getSupplies() + " num tries " + supplies,e);
            }

            //todo seems to be crashing when the play button is pressed
            List<Spawner> spawners=this.backend.getSpawners();
            List<Enemy> enemies = this.backend.getEnemies();
            try {
                if (this.backend.getTutorialCurrentMillis() > CraterBackend.ENEMIES_INTRODUCTION) {
                    for (Spawner spawner : this.backend.getSpawners()) {
                        spawner.draw(gl, this.cameraTranslationM);
                    }
                    for (Enemy enemy : this.backend.getEnemies()) {

                        enemy.draw(gl, this.cameraTranslationM);
                    }
                }
            } catch (ConcurrentModificationException e){
                Log.d("RENDERER","BUG ALERT : enemies:"  + this.backend.getEnemies() + " spawners" + this.backend.getSpawners() + " e1 " + enemies + " spawbers1 " + spawners,e);
            }

            if (this.backend.getTutorialCurrentMillis() > CraterBackend.CHARACTER_INTRODUCTION) {
                this.player.draw(gl, this.cameraTranslationM);
            }

            if (this.backend.getCurrentGameState() == CraterBackend.GAME_STATE_TUTORIAL){
                this.gameMapTutorialLayout.draw(gl,this.cameraTranslationM);
            }


        }
    }


    /** Draws anything that is independent of the matrix. E.g. joysticks, pause button.
     *
     * @param gl the GL10 object used to communicate with open gl
     */
    private void drawScreenUtils(GL10 gl){
        if (this.gameScreenLayout.isVisible()) {
            this.loadLevelLayout.draw(gl, this.cameraTranslationM);

            if (backend.getTutorialCurrentMillis() > CraterBackend.JOYSTICK_INTRODUCTION && ! (this.loadLevelLayout.isVisible() || this.backend.isInEndGamePausePeriod())) {
                this.attackJoyStick.draw(gl, this.cameraTranslationM);
                this.movementJoyStick.draw(gl, this.cameraTranslationM);
                this.defenseJoyStick.draw(gl, this.cameraTranslationM);
            }
            if (backend.getTutorialCurrentMillis() > CraterBackend.EVOLVE_INTRODUCTION && ! (this.loadLevelLayout.isVisible() || this.backend.isInEndGamePausePeriod())) {
                this.evolveButton.draw(gl, this.cameraTranslationM);
            }

            this.stationaryTutorialLayout.draw(gl,this.cameraTranslationM);

            this.pauseButton.draw(gl,this.cameraTranslationM);
            this.pauseGameLayout.draw(gl,this.cameraTranslationM);

            if (this.backend.getTutorialCurrentMillis() > CraterBackend.CHARACTER_INTRODUCTION) {
                this.healthDisplay.draw(gl, this.cameraTranslationM);
            }
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
        LayoutConsts.SCREEN_WIDTH = width;
        LayoutConsts.SCREEN_HEIGHT = height;

        Log.d("RENDERER_","dm w " + (this.displayMetrics.widthPixels + this.getNavigationBarHeight()) + " dm h " + this.displayMetrics.heightPixels
             + " lc w "  +LayoutConsts.SCREEN_WIDTH + " lc h " + LayoutConsts.SCREEN_HEIGHT);


        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }

        gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        gl.glLoadIdentity();
        //Reset The Projection Matrix


        //Calculate The Aspect Ratio Of The Window
        gl.glOrthof(-CAMERA_Z,CAMERA_Z,-CAMERA_Z* height/width,CAMERA_Z * height/width,0.2f,5f);
        //GLU.gluOrtho2D(gl,-1.0f,1.0f,-1.0f,1.0f);
        //GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        gl.glLoadIdentity(); 					//Reset The Modelview Matrix

        Text.setDimensions(width,height);
        Log.d("RENDERER","dimensions " + width + " H "  + height);

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

        LayoutConsts.SCREEN_WIDTH = displayMetrics.widthPixels + this.getNavigationBarHeight();
        LayoutConsts.SCREEN_HEIGHT = displayMetrics.heightPixels;


        this.backend = new CraterBackend(context);


        this.player = new Kaiser();

        this.backend.loadLayouts(gl);
        this.backend.setPlayer(this.player);


        this.movementJoyStick = this.backend.getMovementJoyStick();
        this.attackJoyStick = this.backend.getAttackJoyStick();
        this.defenseJoyStick = this.backend.getDefenseJoyStick();
        this.evolveButton = this.backend.getEvolveButton();

        this.gameScreenLayout = this.backend.getGameScreenLayout();
        this.levelSelectLayout = this.backend.getLevelSelectLayout();
        this.loadLevelLayout = this.backend.getLoadLevelLayout();

        this.stationaryTutorialLayout = this.backend.getStationaryTutorialLayout();
        this.gameMapTutorialLayout = this.backend.getGameMapTutorialLayout();

        this.healthDisplay = this.backend.getHealthDisplay();



        this.loadLayouts(gl,this.context);


        SoundLib.loadMedia(this.context);
        SoundLib.setStateLobbyMusic(true);

        this.loadSettingsFile();

    }

    /** Loads the layouts, including creating sub components, and loading their textures
     *
     * @param gl a GL10 object used to access openGL related methods
     * @param context Any non null context that is used to access resources
     */
    private void loadLayouts(GL10 gl,Context context){
        //for some components a scale factor is needed
        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;
        float scaleY = 1;//don't need to scale both


        //Getting all the components
        Button backToHomeButton = new Button("Back to home", -0.75f,0.7f, 0.4f, 0.1f, 0.05f,LayoutConsts.CRATER_TEXT_COLOR){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                characterSelectLayout.hide();
                defaultHomeScreenLayout.show();
                backend.setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);
            }


        };

        Button kaiserSelectButton = new Button("Kaiser", 0.1f,0, 0.5f, 0.2f, 0.6f,LayoutConsts.CRATER_TEXT_COLOR){
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

        Button characterSelectButton_homeScreen = new Button("Select Evolver", 0,0.4f, 1.2f, 0.5f, 0.2f,LayoutConsts.CRATER_TEXT_COLOR){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                characterSelectLayout.show();
                defaultHomeScreenLayout.hide();
            }


        };




        Button playButton_homeScreen = new Button("Play Game", 0,-0.2f, 1.2f, 0.5f, 0.2f,LayoutConsts.CRATER_TEXT_COLOR){
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
        Button tutorialButton = new Button("Tutorial",0,-0.75f,0.8f,0.2f,0.1f,LayoutConsts.CRATER_TEXT_COLOR){
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

        Button resumeButton = new Button(new TexturedRect(- (0.5f * scaleX)/2,0.25f,0.5f*scaleX,0.5f)){

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

        Button homeButton = new Button(new TexturedRect(- (0.5f * scaleX)/2,-0.5f,0.5f*scaleX,0.5f)){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                gameMapTutorialLayout.hide();
                stationaryTutorialLayout.hide();
                loadLevelLayout.hide();
                pauseGameLayout.hide();
                gameScreenLayout.hide();
                defaultHomeScreenLayout.show();

                backend.setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);
                backend.killEndGamePausePeriod();

                SoundLib.setStateLobbyMusic(true);
                SoundLib.setStateVictoryMusic(false);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateGameMusic(false);
            }

        };

        Button settingsButton = new Button (new TexturedRect(1 - (0.2f * scaleX),0.8f,0.2f*scaleX,0.2f)) {
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

                 writeSettingsFile();
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
                 writeSettingsFile();
             }

        };

        Button settingsDoneButton = new Button("Back to home", 0,-0.5f, 0.5f, 0.2f, 0.4f,LayoutConsts.CRATER_TEXT_COLOR){
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


        //includes characters
        this.characterSelectLayout = new CraterLayout(new EnigmaduxComponent[] {
                backToHomeButton,
                kaiserSelectButton
                                        },-0.8f,-0.8f,1.6f,1.6f){

        };
        
        this.defaultHomeScreenLayout = new CraterLayout(new EnigmaduxComponent[] {
                tutorialButton,
                characterSelectButton_homeScreen,
                playButton_homeScreen,
                settingsButton
                                         },-1.0f,-1.0f,2.0f,1.8f){

        };


        this.pauseGameLayout = new CraterLayout(new EnigmaduxComponent[]{
                resumeButton,
                homeButton
        },-0.4f,-0.4f,0.8f,0.8f);

        this.settingsLayout = new CraterLayout(new EnigmaduxComponent[]{
                this.musicOnOffButton,
                this.soundEffectsOnOffButton,
                settingsDoneButton
        },0,0,0,0);

        this.fullHomeScreenLayout = new CraterLayout(new EnigmaduxComponent[] {
                this.defaultHomeScreenLayout,
                this.characterSelectLayout,
                this.settingsLayout
                                         },-1.0f,-1.0f,2.0f,2.0f){

        };



        this.fullHomeScreenLayout.show();
        this.settingsLayout.hide();
        this.characterSelectLayout.hide();
        this.gameScreenLayout.hide();
        this.pauseGameLayout.hide();

        settingsButton.loadGLTexture(gl,this.context,R.drawable.settings_button);
        this.musicOnOffButton.loadGLTexture(gl,this.context,R.drawable.music_on_off_button);
        this.soundEffectsOnOffButton.loadGLTexture(gl,this.context,R.drawable.sound_effect_on_off_button);
        settingsDoneButton.loadGLTexture(gl);

        this.pauseButton.loadGLTexture(gl,context,R.drawable.pause_button);
        resumeButton.loadGLTexture(gl,context,R.drawable.resume_button);
        homeButton.loadGLTexture(gl,context,R.drawable.home_button);

        characterSelectButton_homeScreen.loadGLTexture(gl);
        playButton_homeScreen.loadGLTexture(gl);
        tutorialButton.loadGLTexture(gl);

        backToHomeButton.loadGLTexture(gl);
        kaiserSelectButton.loadGLTexture(gl);

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
            if (! this.fullHomeScreenLayout.onTouch(e) &&
                    ! pauseButton.onTouch(e) &&
                    ! pauseGameLayout.onTouch(e) &&
                    ! pauseGameLayout.isVisible() &&
                    (gameScreenLayout.isVisible() || levelSelectLayout.isVisible())) {
                backend.onTouch(e);
            }
        } catch (NullPointerException ev){
            Log.i("null Pointer", "touch event before loaded");
        }
         return true;
    }




}