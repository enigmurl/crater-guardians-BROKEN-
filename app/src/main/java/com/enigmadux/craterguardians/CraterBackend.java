package com.enigmadux.craterguardians;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.AngleAimers.TriRectAimer;
import com.enigmadux.craterguardians.AngleAimers.TriangleAimer;
import com.enigmadux.craterguardians.Animations.Animation;
import com.enigmadux.craterguardians.Animations.DeathAnim;
import com.enigmadux.craterguardians.Animations.ToxicBubble;
import com.enigmadux.craterguardians.Attacks.Enemy1Attack;
import com.enigmadux.craterguardians.Attacks.Enemy2Attack;
import com.enigmadux.craterguardians.Attacks.KaiserE1Attack;
import com.enigmadux.craterguardians.Attacks.KaiserE2Attack;
import com.enigmadux.craterguardians.Attacks.RyzeAttack;
import com.enigmadux.craterguardians.Characters.Kaiser;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Characters.Ryze;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.Enemies.Enemy1;
import com.enigmadux.craterguardians.Enemies.Enemy2;
import com.enigmadux.craterguardians.Spawners.Enemy1Spawner;
import com.enigmadux.craterguardians.Spawners.Enemy2Spawner;
import com.enigmadux.craterguardians.Spawners.Spawner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.microedition.khronos.opengles.GL10;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** Is in charge of all the backend activities
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class CraterBackend {
    //the amount of levels
    public static final int NUM_LEVELS = 19;
    //the path to the level file
    private static final String LEVEL_FILE_PATH = "level_data";

    //how long the pause after a win or loss is for smooth transitions
    private static final long PAUSE_MILLIS  = 3000;


    //Integer which represents that the game is still on the on the home screen
    public static final int GAME_STATE_HOMESCREEN = 0;
    //Integer which represents that the game is on level select
    public static final int GAME_STATE_LEVELSELECT = 1;
    //Integer which represents that the game is being played
    public static final int GAME_STATE_INGAME = 2;
    //Integer which represnts the tutorial is being played
    public static final int GAME_STATE_TUTORIAL = 3;



    //the center coordinate of the movement joy stick (openGL coordinates)
    private static final float[] MOVEMENT_JOY_STICK_CENTER = {-0.4f,-0.6f};
    //the center coordinate of attack joy stick (openGL coordinates)
    private static final float[] ATTACK_JOY_STICK_CENTER = {0.6f,-0.4f};
    //the center coordinate of the defense joy stick (openGL coordinates)
    private static final float[] DEFENSE_JOY_STICK_CENTER = {0.4f,-0.6f};
    //the diameter of the movement and attack joysticks
    private static final float JOY_STICK_IMAGE_WIDTH = 0.2f;
    //the maximum length they can extend too
    private static final float JOY_STICK_MAX_RADIUS = 0.3f;

    //the center coordinate of the evolve button
    private static final float[] EVOLVE_BUTTON_CENTER = {0.6f,ATTACK_JOY_STICK_CENTER[1] + JOY_STICK_IMAGE_WIDTH + JOY_STICK_MAX_RADIUS + 0.1f};
    //the diameter of the evolve button
    private static final float EVOLVE_BUTTON_WIDTH = 0.4f;

    //constants about tutorial: first introduce character + healthbar + attacks + attack charge up, then joysticks,then evolve button then supplies, then plateaus,, then spawners/enemies,
    //the millis at which characters health bar and attacks bar are shown
    public static final long CHARACTER_INTRODUCTION = 0;
    //the millis at which joysticks are shown + activated (it pauses until all three have been used)
    public static final long JOYSTICK_INTRODUCTION = 10000;
    //the millis at which the evolve button is showed
    public static final long EVOLVE_INTRODUCTION = 20000;
    //the millis at which the supplies are introduced
    public static final long SUPPLIES_INTRODUCTION = 35000;
    //the millis at which the plateaus and toxic lakes are introduced
    public static final long PLATEAUS_TOXIC_LAKE_INTRODUCITON = 40000;
    //the millis at which the enemies and spawners are introduced, tutorial does not finish until spawner is killed, if the player loses there are sent back to enemies introduction
    public static final long ENEMIES_INTRODUCTION = 50000;
    //the amount of millis a tutorial is
    private static final long TUTORIAL_MILLIS = 60000;

    //used to lock threads
    public static final Object lock = new Object();


    //used for getting more information about device
    private Context context;
    //used for going back to homescreen and such
    private CraterRenderer renderer;


    //health display of the player
    private ProgressBar healthDisplay;

    //used to tell user how much of evolve has been charged as well as a way to activate the evolve
    private Button evolveButton;
    //joystick information

    //visual joystick
    private TexturedRect defenseJoyStick;

    //openGl x coordinate of the defense joystick
    private float defenseJoyStickX;
    //openGL y coordinate of the defense joystick
    private float defenseJoyStickY;
    //whether the defense joystick is being activated
    private boolean defenseJoyStickDown = false;
    //how to identify which pointer corresponds to the defense joystick
    private int defenseJoyStickPointer;


    //visual joystick
    private TexturedRect attackJoyStick;

    //openGl x coordinate of the attack joystick
    private float attackJoyStickX;
    //openGL y coordinate of the attack joystick
    private float attackJoyStickY;
    //whether the attack joystick is being activated
    private boolean attackJoyStickDown = false;
    //how to identify which pointer corresponds to the attack joystick
    private int attackJoyStickPointer;

    //visual joystick
    private TexturedRect movementJoyStick;
    //openGl x coordinate of the movement joystick
    private float movementJoyStickX;
    //openGL y coordinate of the movement joystick
    private float movementJoyStickY;
    //whether the movement joystick is being activated
    private boolean movementJoyStickDown = false;
    //how to identify which pointer corresponds to the movement joystick
    private int movementJoyStickPointer;

    //instance game state info

    //describes what the user is doing  (e.g. home screen, level select, and in game)
    private int currentGameState;

    //what levels are unlocked
    private boolean[] unlockedLevels = new boolean[CraterBackend.NUM_LEVELS];
    //what levels have been completed
    private boolean[] completedLevels = new boolean[CraterBackend.NUM_LEVELS];
    //what level number
    private int levelNum = 0;

    //radius of the crater
    private float craterRadius;
    //spawn location
    private float[] spawnLocation = new float[2];
    //the current player on the map
    private Player player;
    //all enemies on the map
    private final List<Enemy> enemies = new ArrayList<>();
    //all spawner on the map
    private final List<Spawner> spawners = new ArrayList<>();
    //all plateaus on the map
    private final List<Plateau> plateaus = new ArrayList<>();
    //all active toxic lakes on the map
    private final List<ToxicLake> toxicLakes = new ArrayList<>();
    //all active supplies on the map
    private final List<Supply> supplies = new ArrayList<>();
    //all active animations
    private final List<Animation> animations = new ArrayList<>();

    //after the game is lost or won there is small pause
    private boolean inEndGamePausePeriod;
    //the millis till the end of gamePausePeriod
    private long endGamePauseMillis = 0;

    //the crater
    private TexturedRect craterVisual;
    /** The layout that is displayed while playing the game.
     * Includes the player sprite, all bots, the craterVisual (geography) Additionally trackers on score.
     * Otherwise we could have the level selector in the game, where its kind of like clash of clans war map, and player
     * can scout, then press play. Additionally the joystick controls
     */
    private CraterLayout gameScreenLayout;

    /** Level Select. For now its just a place holder layout. In future There should be an image, where if you slide it a new one appears.
     * Includes: level select background,level 1 play button,level 2 play button
     *IMPORTANT INFO, WHEN INITIALIZING THE ARRAY INTO THE CONSTRUCTOR, THE BUTTONS SHOULD BE IN ORDER (1,2,..) AND THE LAST ITEMS, TODO SHOULD FIND BETTER METHOD
     */
    private CraterLayout levelSelectLayout;

    //this says for each joystick, if it has been tapped
    private boolean[] joysticksTapped = new boolean[3];
    /** next Level. shown whenever a level is completed or needs to be redone (loss or victory)
     * Includes: play button.
     * Future: (todo) : implement a back to level select layout
     *
     */
    private CraterLayout loadLevelLayout;

    //following are for the tutorial

    private Button exitButton;
    /** This is the grandmaster layout for stuff that are stationary, rather than having many getters/setters this makes it so only one is needed
     *  it contains all the layouts + textboxes below this that are stationary
     */
    private CraterLayout stationaryTutorialLayout;
    /** This is the grandmaster layout for stuff that are stuck on the game map, rather than having many getters/setters this makes it so only one is needed
     *  it contains all the layouts + textboxes below this that are stuck on the game map
     */
    private CraterLayout gameMapTutorialLayout;

    /** Shows the hints when playing the tutorial, the actual text that describes what the character and the bars are
     *
     *
     */
    private CraterLayout characterTutorialLayout;

    /** Shows the hints when laying the tutorial for what each joystick is, and how to use it
     *
     */
    private CraterLayout joysticksTutorialLayout;

    /** There is only one textured rect needed to tell what the evolve button is
     *
     */
    private InGameTextbox evolveTutorialInfo;


    /** There is only one textured rect needed to tell what the supplies are
     *
     */
    private InGameTextbox suppliesTutorialInfo;

    /** Shows the hints when laying the tutorial for what plateaus and toxic lakes are
     *
     */
    private CraterLayout plateausToxicLakeTutorialLayout;

    /** Shows the hints when playing the tutorial for what spawners and enemies are
     *
     */
    private CraterLayout enemiesTutorialLayout;





    /** In the tutorial, it is set up sort of as a video, so this tells where we are in the video
     *
     */
    private long tutorialCurrentMillis = 0;



    /** Default Constructor
     *
     * @param context any non null Context, used to access resources
     */
    public CraterBackend(Context context,CraterRenderer renderer){
        this.context = context;
        this.renderer = renderer;

        //scale it so its circles
        float scaleX = 1;
        float scaleY = 1;
        if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT){
            scaleX = (float) (LayoutConsts.SCREEN_HEIGHT )/ (LayoutConsts.SCREEN_WIDTH);
        } else {
            scaleY = (float) (LayoutConsts.SCREEN_WIDTH)/LayoutConsts.SCREEN_HEIGHT;

        }


        attackJoyStick = new TexturedRect(ATTACK_JOY_STICK_CENTER[0]-JOY_STICK_IMAGE_WIDTH/2, ATTACK_JOY_STICK_CENTER[1]-JOY_STICK_IMAGE_WIDTH/2,scaleX * JOY_STICK_IMAGE_WIDTH,scaleY * JOY_STICK_IMAGE_WIDTH);
        defenseJoyStick = new TexturedRect(DEFENSE_JOY_STICK_CENTER[0] - JOY_STICK_IMAGE_WIDTH/2,DEFENSE_JOY_STICK_CENTER[1]-JOY_STICK_IMAGE_WIDTH/2,scaleX * JOY_STICK_IMAGE_WIDTH,scaleY * JOY_STICK_IMAGE_WIDTH);
        movementJoyStick = new TexturedRect(MOVEMENT_JOY_STICK_CENTER[0]-JOY_STICK_IMAGE_WIDTH/2, MOVEMENT_JOY_STICK_CENTER[1]-JOY_STICK_IMAGE_WIDTH/2,scaleX * JOY_STICK_IMAGE_WIDTH,scaleY* JOY_STICK_IMAGE_WIDTH);




        evolveButton = new Button(new TexturedRect(EVOLVE_BUTTON_CENTER[0]-EVOLVE_BUTTON_WIDTH/2,EVOLVE_BUTTON_CENTER[1]-EVOLVE_BUTTON_WIDTH/2,EVOLVE_BUTTON_WIDTH,EVOLVE_BUTTON_WIDTH)) {
            //complete override of on touch because this is sensitive to pointer events as well
            @Override
            public boolean onTouch(MotionEvent e) {
                if (this.isSelect(e)) {
                    if ((e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_POINTER_UP) && this.down) {
                        this.onRelease();
                    } else if ((e.getActionMasked() == MotionEvent.ACTION_DOWN || e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) && !this.down ) {
                        this.onSelect();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getX(e.getActionIndex())),MathOps.getOpenGLY(e.getY(e.getActionIndex())))
                        && player.getEvolutionCharge() > 1
                        && (e.getPointerId(e.getActionIndex()) != attackJoyStickPointer || ! attackJoyStickDown);
            }

            @Override
            public void onRelease() {
                super.onRelease();

                player.attemptEvolve();
                Log.d("EVOLVE", "Attempting evolve");
                player.hideAngleAimer();
            }
        };


        this.reset();
    }



    /** Sets the player that the player will use
     *
     * @param player the new player that is going to be played
     */
    public void setPlayer(Player player){
        this.player = player;
        this.healthDisplay.setMaxHitPoints(player.getMaxHealth());
    }


    /** Initializes all layouts and their sub components;
     *
     */
    public void loadLayouts(GL10 gl){
        this.loadTextures(gl);

        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;
        craterVisual = new TexturedRect(-1f,-1f,2f,2f);


        final EnigmaduxComponent[] levelButtons = new EnigmaduxComponent[CraterBackend.NUM_LEVELS+2];

        TexturedRect levelBackground = new TexturedRect(-1,-1,2,2) {
            //the previous y of the touch event
            private float prevY;

            //whether we're in a scrolling session
            private boolean inScroll = false;

            //the id of the current pointer
            private int pointerID;

            @Override
            public boolean onTouch(MotionEvent e) {
                if (! this.visible){
                    return false;
                }

                if (e.getActionMasked() == MotionEvent.ACTION_DOWN){
                    this.pointerID = e.getPointerId(e.getActionIndex());
                    this.prevY = MathOps.getOpenGLY(e.getY());
                    this.inScroll = true;
                    return true;
                } else if (e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_CANCEL){
                    this.inScroll = false;
                    return true;
                }
                if (e.getActionMasked() == MotionEvent.ACTION_MOVE && this.inScroll && e.getPointerId(e.getActionIndex()) == this.pointerID){

                    float deltaY = MathOps.getOpenGLY(e.getY()) - prevY;
                    for (int i = 0;i<NUM_LEVELS;i++){
                        int offset = levelButtons.length - NUM_LEVELS;
                        EnigmaduxComponent comp = levelButtons[i+offset];
                        comp.setPos(comp.getX(),comp.getY() + deltaY);

                        float a = getLevelButtonAlpha(comp.getY() + comp.getHeight()/2);

                        if (! unlockedLevels[i]) {
                            ((Button) comp).setShader(1, 0.25f, 0.25f, a);
                        } else if (completedLevels[i]){
                            ((Button) comp).setShader(0.75f, 1, 0.75f, a);
                        } else {
                            ((Button) comp).setShader(1, 1, 1, a);
                        }

                        if (a <= 0){
                            comp.hide();
                        } else {
                            comp.show();
                        }
                    }

                    this.prevY = MathOps.getOpenGLY(e.getY());
                    return true;
                }
                return false;
            }
        };

        Button levelSelect_homeButton = new Button(new TexturedRect(-0.8f,-0.25f,0.5f * scaleX,0.5f )){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                resetLevelButtons();


                renderer.exitGame();
                killEndGamePausePeriod();

                setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);

                SoundLib.setStateLobbyMusic(true);
                SoundLib.setStateVictoryMusic(false);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateGameMusic(false);
            }

        };



        levelButtons[0] = levelBackground;
        levelButtons[1] = levelSelect_homeButton;

        float h = 0.5f;
        for (int i = 1;i<CraterBackend.NUM_LEVELS+1;i++){

            final int currentLevelNumber = i;

            Button levelPlayButton = new Button("Level " + i,0,1-h*(i) ,0.5f*scaleX,0.5f,0.1f,LayoutConsts.LEVEL_TEXT_COLOR, true) {
                @Override
                public boolean isSelect(MotionEvent e) {
                    return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY())) && unlockedLevels[currentLevelNumber - 1];
                }

                @Override
                public void onRelease() {
                    super.onRelease();

                    levelNum = currentLevelNumber;
                    loadLevel();
                    levelSelectLayout.hide();
                    gameScreenLayout.show();
                    setCurrentGameState(CraterBackend.GAME_STATE_INGAME);

                    SoundLib.setStateLobbyMusic(false);
                    SoundLib.setStateGameMusic(true);
                }

                @Override
                public void show() {
                    if (this.texturedRect.getShader()[3] > 0) {
                        super.show();
                    }
                }
            };

            levelPlayButton.loadGLTexture(gl);

            levelButtons[i+1] = levelPlayButton;
        }



          //todo make this "replay" if lost, "next level" if won
        Button playButton = new Button("Play", 0f,0.5f, 1f, 0.2f, 0.3f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                loadLevel();

                setCurrentGameState(CraterBackend.GAME_STATE_INGAME);
                loadLevelLayout.hide();

                SoundLib.setStateGameMusic(true);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateVictoryMusic(false);
            }

        };

        Button levelSelectButton = new Button("Levels",0,-0.1f,0.75f,0.2f,0.2f,LayoutConsts.CRATER_TEXT_COLOR, false) {

            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                //resetLevelButtons();
                renderer.exitGameLoadLevelSelect();

                setCurrentGameState(CraterBackend.GAME_STATE_LEVELSELECT);
                killEndGamePausePeriod();

                SoundLib.setStateLobbyMusic(true);
                SoundLib.setStateVictoryMusic(false);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateGameMusic(false);
            }
        };

        Button homeButton = new Button("Home",0,-0.65f,0.75f,0.2f,0.2f,LayoutConsts.CRATER_TEXT_COLOR, false) {

            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();
                renderer.exitGame();

                setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);
                killEndGamePausePeriod();

                SoundLib.setStateLobbyMusic(true);
                SoundLib.setStateVictoryMusic(false);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateGameMusic(false);
            }
        };



        //max health is initialized later in setPlayer() method
        this.healthDisplay = new ProgressBar(-1,0.3f,0.2f, true, false);


        this.gameScreenLayout = new CraterLayout(new EnigmaduxComponent[] {
                craterVisual,
        },-1.0f,-1.0f,2.0f,2.0f);

        this.levelSelectLayout = new CraterLayout(levelButtons,-1.0f,1.0f,2.0f,2.0f) {
            @Override
            public void show() {
                resetLevelButtons();
                super.show();
            }
        };

        this.loadLevelLayout = new CraterLayout(new EnigmaduxComponent[]{
                levelSelectButton,
                homeButton,
                playButton,

        },-0.5f,-0.5f,1.0f,1.0f);

        this.resetLevelButtons();






        this.levelSelectLayout.hide();
        this.gameScreenLayout.hide();
        this.loadLevelLayout.hide();

        homeButton.loadGLTexture(gl);
        playButton.loadGLTexture(gl);
        levelSelectButton.loadGLTexture(gl);

        levelBackground.loadGLTexture(gl,this.context,R.drawable.level_select_example);
        levelSelect_homeButton.loadGLTexture(gl,this.context,R.drawable.home_button);


        craterVisual.loadGLTexture(gl,this.context,R.drawable.level_background_crater);


        //todo bad solution to whats happening (the text starts at one place, but after being moved it gets offset)
        this.loadTutorialLayouts(gl);
        this.loadLevelData();

    }
    /** Initializes all tutorial layouts and their sub components;
     *
     */
    private void loadTutorialLayouts(GL10 gl){
        exitButton = new Button("Exit",0,0.75f,0.4f,0.1f,0.1f,LayoutConsts.CRATER_TEXT_COLOR, false){
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }
            @Override
            public void onRelease() {
                super.onRelease();
                renderer.exitGame();

                setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);
                killEndGamePausePeriod();

                SoundLib.setStateLobbyMusic(true);
                SoundLib.setStateVictoryMusic(false);
                SoundLib.setStateLossMusic(false);
                SoundLib.setStateGameMusic(false);
            }
        };

        InGameTextbox playerCaption = new InGameTextbox("This is your controllable character",0,-0.3f,0.05f,LayoutConsts.CRATER_TEXT_COLOR,false);
        InGameTextbox attackBarCaption = new InGameTextbox("This shows how many attacks you have, \n after all are used it will automatically reload", 0.35f,0.2f,0.05f,LayoutConsts.CRATER_TEXT_COLOR,false);
        InGameTextbox attackChargeCaption = new InGameTextbox("The combo charge that adds to your attack damage, \n it's advanced and you need not worry for now", -0.4f,0.3f,0.05f,LayoutConsts.CRATER_TEXT_COLOR,false);

        InGameTextbox joystickCaption = new InGameTextbox("Try all three joysticks to move on!",0,0.5f,0.1f,LayoutConsts.CRATER_TEXT_COLOR,false);
        InGameTextbox movementCaption = new InGameTextbox("This is the movement joystick\n hold it to move in a direction",MOVEMENT_JOY_STICK_CENTER[0],MOVEMENT_JOY_STICK_CENTER[1]-0.2f,0.05f,LayoutConsts.CRATER_TEXT_COLOR,false);
        InGameTextbox attackCaption = new InGameTextbox("This is the attack joystick->\n drag and release to attack",ATTACK_JOY_STICK_CENTER[0]-0.3f,ATTACK_JOY_STICK_CENTER[1],0.05f,LayoutConsts.CRATER_TEXT_COLOR,false);
        InGameTextbox defenseCaption = new InGameTextbox("<-This is the shield joystick\n drag and release to block enemies",DEFENSE_JOY_STICK_CENTER[0]+0.25f,DEFENSE_JOY_STICK_CENTER[1],0.05f,LayoutConsts.CRATER_TEXT_COLOR,false);

        this.evolveTutorialInfo = new InGameTextbox("This is the evolve button"  + /*(char) (193) + */ "\n charge it up by attacking enemies \n Activate it to evolve to a \n more powerful form  and gain health",EVOLVE_BUTTON_CENTER[0],EVOLVE_BUTTON_CENTER[1] + 0.35f,0.05f, LayoutConsts.CRATER_TEXT_COLOR,false);
        this.suppliesTutorialInfo = new InGameTextbox("These are your supplies. \n defend them from enemies, \n if all are destroyed you lose",0,-0.3f,0.05f, LayoutConsts.CRATER_TEXT_COLOR, true);

        //todo These are based of the levels file so adjust accordingly, and we should make the whole map 1 and some point
        InGameTextbox plateauCaption = new InGameTextbox("This is a plateau, both enemies and you cannot walk on it",0.1f,0.75f,0.05f,LayoutConsts.CRATER_TEXT_COLOR, true);
        InGameTextbox lakeCaption = new InGameTextbox("This is a toxic lake, it slows enemies and players. Avoid Them!",1.0f,-1f,0.05f,LayoutConsts.CRATER_TEXT_COLOR, true);

        //these too need to be adjusted
        InGameTextbox spawnerCaption = new InGameTextbox("This is a spawner that periodically summons enemies \n there are a few different types, to win a game \n you must kill all the spawners without dieing",
                                                                1.35f,1.1f,0.05f,LayoutConsts.CRATER_TEXT_COLOR, true);




        this.characterTutorialLayout = new CraterLayout(new EnigmaduxComponent[]{
                playerCaption,
                attackBarCaption,
                attackChargeCaption

        },-1.0f,-1.0f,2,2);
        this.joysticksTutorialLayout = new CraterLayout(new EnigmaduxComponent[]{
                joystickCaption,
                movementCaption,
                attackCaption,
                defenseCaption
        },-1.0f,-1.0f,2,2);

        this.plateausToxicLakeTutorialLayout = new CraterLayout(new EnigmaduxComponent[]{
                plateauCaption,
                lakeCaption

        },-1.0f,-1.0f,2,2);
        this.enemiesTutorialLayout = new CraterLayout(new EnigmaduxComponent[]{
                spawnerCaption,

        },-1.0f,-1.0f,2,2);

        this.stationaryTutorialLayout = new CraterLayout(new EnigmaduxComponent[]{
                this.evolveTutorialInfo,
                this.characterTutorialLayout,
                this.joysticksTutorialLayout,
                exitButton

        },-1.0f,-1.0f,2,2);
        this.gameMapTutorialLayout = new CraterLayout(new EnigmaduxComponent[]{
                this.suppliesTutorialInfo,
                this.plateausToxicLakeTutorialLayout,
                this.enemiesTutorialLayout

        },-1.0f,-1.0f,2,2);

        this.stationaryTutorialLayout.hide();
        this.gameMapTutorialLayout.hide();

        exitButton.loadGLTexture(gl);

        playerCaption.loadGLTexture(gl);
        attackBarCaption.loadGLTexture(gl);
        attackChargeCaption.loadGLTexture(gl);

        joystickCaption.loadGLTexture(gl);
        movementCaption.loadGLTexture(gl);
        attackCaption.loadGLTexture(gl);
        defenseCaption.loadGLTexture(gl);

        this.evolveTutorialInfo.loadGLTexture(gl);
        this.suppliesTutorialInfo.loadGLTexture(gl);

        plateauCaption.loadGLTexture(gl);
        lakeCaption.loadGLTexture(gl);

        spawnerCaption.loadGLTexture(gl);

    }

    /** Gets the gameScreenLayout, which holds all in game components
     *
     * @return the gameScreenLayout
     */
    public CraterLayout getGameScreenLayout(){
        return this.gameScreenLayout;
    }

    /** Gets the levelSelectLayout, which holds all level select components
     *
     * @return the levelSelectLayout
     */
    public CraterLayout getLevelSelectLayout(){
        return this.levelSelectLayout;
    }

    /** Gets the loadLevelLayout which is called on death and on level completion
     *
     * @return the loadLevelLayout contains the layout background and the play button
     */
    public CraterLayout getLoadLevelLayout() {
        return this.loadLevelLayout;
    }

    /** Gets the health display layout, which holds the status of the player's health visually
     *
     * @return the health display layout, which holds the status of the player's health visually
     */
    public ProgressBar getHealthDisplay(){
        return this.healthDisplay;
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
     * @param currentGameState the new game state. Recommended to use CraterBackend.GAME_STATE_* rather than hard coded literals, as for code readability.
     */
    public void setCurrentGameState(int currentGameState){
        this.currentGameState = currentGameState;
    }

    /** loads textures for in game components, also loads fonts, but main purpose is to load textures
     *
     * @param gl GL10 object used to access openGL
     */
    private void loadTextures(GL10 gl){
        //inputs
        this.defenseJoyStick.loadGLTexture(gl,this.context,R.drawable.test);
        this.attackJoyStick.loadGLTexture(gl,this.context,R.drawable.test);
        this.movementJoyStick.loadGLTexture(gl,this.context,R.drawable.test);
        this.evolveButton.loadGLTexture(gl,this.context,R.drawable.evolve_button);

        //characters
        Player.loadGLTexture(gl,this.context);

        Kaiser.loadGLTexture(gl,this.context);
        Ryze.loadGLTexture(gl,this.context);
        Enemy1.loadGLTexture(gl,this.context);
        Enemy2.loadGLTexture(gl,this.context);
        //aimers
        TriangleAimer.loadGLTexture(gl,this.context);
        TriRectAimer.loadGLTexture(gl,this.context);
        //spawners
        Enemy1Spawner.loadGLTexture(gl,this.context);
        Enemy2Spawner.loadGLTexture(gl,this.context);
        //attacks
        Enemy1Attack.loadGLTexture(gl,this.context);
        Enemy2Attack.loadGLTexture(gl,this.context);
        KaiserE1Attack.loadGLTexture(gl,this.context);
        KaiserE2Attack.loadGLTexture(gl,this.context);
        RyzeAttack.loadGLTexture(gl,this.context);

        //animations
        DeathAnim.loadGLTexture(gl,this.context);
        ToxicBubble.loadGLTexture(gl,this.context);


        //others (lakes + plateaus)
        ToxicLake.loadGLTexture(gl,this.context);
        Plateau.loadGLTexture(gl,this.context);
        ProgressBar.loadGLTexture(gl,this.context);
        Supply.loadGLTexture(gl,this.context);
        Shield.loadGLTexture(gl,this.context);
        Button.loadButtonGLTexture(gl,this.context);
        InGameTextbox.loadFont(this.context);
        InGameTextbox.loadFont(this.context);

    }

    /** Gets the alpha value of a level button given the y coordinate
     *
     * @param y the openGL y coordinate fo the button
     * @return the alpha value from 0 to 1, 0 being transparent
     *
     */
    private float getLevelButtonAlpha(float y){
        return Math.min(1,4 - 4 * Math.abs(y));
    }

    /** Resets level buttons to their original positions
     *
     */
    private void resetLevelButtons(){

        EnigmaduxComponent[] levelButtons = this.levelSelectLayout.getComponents();
        float h = 0.5f;

        for (int i = 0;i<NUM_LEVELS;i++){
            int offset = levelButtons.length - NUM_LEVELS;
            EnigmaduxComponent comp = levelButtons[i+offset];
            comp.setPos(-comp.getWidth()/2,1-h*(i+1) - comp.getHeight()/2);

            float a = this.getLevelButtonAlpha(comp.getY() + comp.getHeight()/2);

            if (! unlockedLevels[i]) {
                ((Button) comp).setShader(1, 0.25f, 0.25f, a);
            } else if (completedLevels[i]){
                ((Button) comp).setShader(0.75f, 1, 0.75f, a);
            } else {
                ((Button) comp).setShader(1, 1, 1, a);
            }

            if (a <= 0){
                comp.hide();
            }

        }
    }


    /** Loads the data from level
     *
     */
    public void loadLevelData(){
        try {
            Scanner stdin = new Scanner(this.context.openFileInput(CraterBackend.LEVEL_FILE_PATH));
            for (int i = this.levelSelectLayout.getComponents().length - CraterBackend.NUM_LEVELS;i<this.levelSelectLayout.getComponents().length;i++){
                this.unlockedLevels[i-  (this.levelSelectLayout.getComponents().length - CraterBackend.NUM_LEVELS)] = stdin.nextBoolean();
                this.completedLevels[i-  (this.levelSelectLayout.getComponents().length - CraterBackend.NUM_LEVELS)] = stdin.nextBoolean();
            }
            this.resetLevelButtons();
            stdin.close();
        } catch (FileNotFoundException e){
            Log.d("BACKEND","Error loading data file " ,e);
            this.createLevelFiles();
        } catch (NoSuchElementException e){
            Log.d("BACKEND","Incorrect file format " ,e);
            this.createLevelFiles();
        }

    }

    /** If the first time loading the game, create all the level files
     *
     */
    private void createLevelFiles(){
        this.unlockedLevels[0] = true;
        try {
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(this.context.openFileOutput (CraterBackend.LEVEL_FILE_PATH, Context.MODE_PRIVATE)));

            for (int i = 0;i<CraterBackend.NUM_LEVELS;i++){
                stdout.print(this.unlockedLevels[i] + " ");
                stdout.println(this.completedLevels[i] + " ");

                //making sure there is always one available level
                if (this.completedLevels[i] && i < CraterBackend.NUM_LEVELS-1){
                    Log.d("BACKEND","Unlocked level:" + (i+2));
                    this.unlockedLevels[i+1] = true;
                }
            }
            stdout.close();

        } catch (IOException e){
            Log.d("BACKEND","File write failed",e);
        }

        Log.d("BACKEND","completedLEVELs: " + Arrays.toString(this.completedLevels));

    }

    /** Sets the level num, mainly used to initiate tutorial
     *
     * @param levelNum what level it is, a non positive means tutorial
     */
    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    /** Initializes a level
     *
     */
    public void loadLevel(){


        this.createLevelFiles();



        this.reset();

        int fileName;
        switch (levelNum){
            case 1:
                fileName = R.raw.level_1;
                break;
            case 2:
                fileName = R.raw.level_2;
                break;
            case 3:
                fileName = R.raw.level_3;
                break;
            case 4:
                fileName = R.raw.level_4;
                break;
            case 5:
                fileName = R.raw.level_5;
                break;
            case 6:
                fileName = R.raw.level_6;
                break;
            case 7:
                fileName = R.raw.level_7;
                break;
            case 8:
                fileName = R.raw.level_8;
                break;
            case 9:
                fileName = R.raw.level_9;
                break;
            case 10:
                fileName = R.raw.level_10;
                break;
            case 11:
                fileName = R.raw.level_11;
                break;
            case 12:
                fileName = R.raw.level_12;
                break;
            case 13:
                fileName = R.raw.level_13;
                break;
            case 14:
                fileName = R.raw.level_14;
                break;
            case 15:
                fileName = R.raw.level_15;
                break;
            case 16:
                fileName = R.raw.level_16;
                break;
            case 17:
                fileName = R.raw.level_17;
                break;
            case 18:
                fileName = R.raw.level_18;
                break;
            case 19:
                fileName = R.raw.level_19;
                break;
            default:
                fileName = R.raw.level_tutorial;
                this.levelNum = 0;
        }


        Scanner level_data = new Scanner(context.getResources().openRawResource(fileName));

        spawnLocation[0] = level_data.nextFloat();
        spawnLocation[1] = level_data.nextFloat();
        this.player.setTranslate(spawnLocation[0],spawnLocation[1]);



        craterRadius = level_data.nextFloat();


        craterVisual.setScale(craterRadius,craterRadius);

        int numSupplies = level_data.nextInt();
        for (int i =0;i<numSupplies;i++){
            float x = level_data.nextFloat();
            float y = level_data.nextFloat();
            float r = level_data.nextFloat();
            int health = level_data.nextInt();

            supplies.add(new Supply(x,y,r,health));
        }

        int numToxicLakes = level_data.nextInt();

        for (int i = 0;i<numToxicLakes;i++){
            float x = level_data.nextFloat();
            float y = level_data.nextFloat();
            float a = level_data.nextFloat();
            float b = level_data.nextFloat();

            toxicLakes.add(new ToxicLake(x,y,a,b));
        }

        int numSpawners = level_data.nextInt();

        for (int i = 0;i<numSpawners;i++){
            float x = level_data.nextFloat();
            float y = level_data.nextFloat();
            float w = level_data.nextFloat();
            float h = level_data.nextFloat();
            String type = level_data.next();
            long spawnTime = level_data.nextLong();
            int hitPoints = level_data.nextInt();

            switch (type){
                case "ENEMY_TYPE_1":
                    spawners.add(new Enemy1Spawner(x,y,w,h,spawnTime,hitPoints));
                    break;
                case "ENEMY_TYPE_2":
                    spawners.add(new Enemy2Spawner(x,y,w,h,spawnTime,hitPoints));
                    break;
            }
        }

        int numPlateaus = level_data.nextInt();

        for (int i = 0;i<numPlateaus;i++){
            float x1 = level_data.nextFloat();
            float y1 = level_data.nextFloat();
            float x2 = level_data.nextFloat();
            float y2 = level_data.nextFloat();
            float x3 = level_data.nextFloat();
            float y3 = level_data.nextFloat();
            float x4 = level_data.nextFloat();
            float y4 = level_data.nextFloat();

            plateaus.add(new Plateau(
                    x1,y1,
                    x2,y2,
                    x3,y3,
                    x4,y4
            ));
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

    /** Gets the supplier for drawing
     *
     * @return the list of supplier on the map
     */
    public List<Supply> getSupplies() {
        return supplies;
    }

    /** Gets the animations
     *
     * @return the list of animations on the map
     */
    public List<Animation> getAnimations(){
        return this.animations;
    }

    /** Sees if it's in the pause period between the end of a game and the load level layout showing
     *
     * @return if it's in the pause period at the end of the game
     */
    public boolean isInEndGamePausePeriod() {
        return inEndGamePausePeriod;
    }


    /** Called when the period is cancelled by most likely the home button
     *
     */
    public void killEndGamePausePeriod(){
        this.endGamePauseMillis = 0;
        this.inEndGamePausePeriod = false;
        this.player.show();
    }
    /** Called when the period is finished, as in the whole period lived out
     *
     */
    private void finishEndGamePausePeriod(){
        this.endGamePauseMillis = 0;
        this.inEndGamePausePeriod = false;
        setCurrentGameState(GAME_STATE_LEVELSELECT);
        loadLevelLayout.show();
        this.player.show();
    }

    /** Kills all enemies; all enemies are removed from memory; reloads the game map
     *
     */
    private void reset() {
        this.enemies.clear();
        this.spawners.clear();
        this.plateaus.clear();
        this.toxicLakes.clear();
        this.supplies.clear();
        this.animations.clear();

        if (this.player != null) {
            this.player.setTranslate(this.spawnLocation[0],this.spawnLocation[1]);
            this.player.spawn();
            this.player.hideAngleAimer();
        }
        this.resetJoySticks();

    }

    /** When the level is cleared, game is paused, or other instances, the joysticks have to be reset, regardless or not if
     * the fingers are lifted up. This resets the defense,movement, and attack joysticks to their default position
     *
     */
    public void resetJoySticks(){
        this.movementJoyStickX = 0;
        this.movementJoyStickY = 0;
        this.attackJoyStickX = 0;
        this.attackJoyStickY = 0;
        this.defenseJoyStickX = 0;
        this.defenseJoyStickY = 0;
        this.attackJoyStickDown = false;
        this.defenseJoyStickDown = false;
        this.movementJoyStickDown = false;
    }

    /** Gets the current millis in the tutorial, which indicates where it is in the tutorial, if it's greater than the tutorial length it means
     * its in game not in tutorial
     *
     * @return the current millis in the tutoria
     */
    public long getTutorialCurrentMillis() {
        return this.tutorialCurrentMillis;
    }

    /** sets the current millis of the tutorial, should only be used to set it back to 0
     *
     * @param tutorialCurrentMillis the new location of where it should be in the tutorial
     */
    public void setTutorialCurrentMillis(long tutorialCurrentMillis) {
        this.tutorialCurrentMillis = tutorialCurrentMillis;
    }

    /** Shows the appropriate tutorial message based on where it is in the sequence
     *
     */
    private void displayTutorial(){
        this.stationaryTutorialLayout.hide();
        this.gameMapTutorialLayout.hide();

        this.exitButton.show();

        //makes sure it doesnt skip the joystick section
        boolean joySticksFinished = this.joysticksTapped[0] && this.joysticksTapped[1] && this.joysticksTapped[2];
        if (! joySticksFinished && this.tutorialCurrentMillis > CraterBackend.EVOLVE_INTRODUCTION){
            this.tutorialCurrentMillis = CraterBackend.EVOLVE_INTRODUCTION;
        }


        if (this.tutorialCurrentMillis > CraterBackend.ENEMIES_INTRODUCTION){
            this.enemiesTutorialLayout.show();
            return;
        }
        if (this.tutorialCurrentMillis > CraterBackend.PLATEAUS_TOXIC_LAKE_INTRODUCITON){
            this.plateausToxicLakeTutorialLayout.show();
            return;
        }
        if (this.tutorialCurrentMillis > CraterBackend.SUPPLIES_INTRODUCTION){
            this.suppliesTutorialInfo.show();
            return;
        }
        if (this.tutorialCurrentMillis > CraterBackend.EVOLVE_INTRODUCTION){
            this.evolveTutorialInfo.show();
            return;
        }
        if (this.tutorialCurrentMillis > CraterBackend.JOYSTICK_INTRODUCTION){
            this.joysticksTutorialLayout.show();
            return;
        }

        if (this.tutorialCurrentMillis > CraterBackend.CHARACTER_INTRODUCTION){
            this.characterTutorialLayout.show();
        }
    }

    /** Resets the joysticks to a blank false array
     *

     */
    public void resetJoysticksTapped() {
        this.joysticksTapped = new boolean[3];
    }

    /** Updates the game state
     *
     * @param dt milliseconds since last call
     */
    public void update(long dt){
        if (inEndGamePausePeriod){
            this.endGamePauseMillis -= dt;
            if (this.endGamePauseMillis < 0){
                this.finishEndGamePausePeriod();
            }
        }

        synchronized (CraterBackend.lock) {
            Iterator itr;

            itr = this.animations.iterator();
            while (itr.hasNext()) {
                Animation anim = (Animation) itr.next();
                anim.update(dt);
                if (anim.isFinished()) {
                    itr.remove();
                }
            }


            if (this.currentGameState == CraterBackend.GAME_STATE_INGAME) {
                this.tutorialCurrentMillis = CraterBackend.TUTORIAL_MILLIS + 1;//this essentially is a clever way of enabling all items
            } else if (this.currentGameState == CraterBackend.GAME_STATE_TUTORIAL) {
                this.tutorialCurrentMillis += dt;
                this.displayTutorial();
            }

            float scaleX = 1;
            float scaleY = 1;
            if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT) {
                scaleX = (float) (LayoutConsts.SCREEN_HEIGHT) / (LayoutConsts.SCREEN_WIDTH);
            } else {
                scaleY = (float) (LayoutConsts.SCREEN_WIDTH) / LayoutConsts.SCREEN_HEIGHT;

            }

            this.attackJoyStick.setTranslate(this.attackJoyStickX, this.attackJoyStickY);
            this.defenseJoyStick.setTranslate(this.defenseJoyStickX, this.defenseJoyStickY);
            this.movementJoyStick.setTranslate(this.movementJoyStickX, this.movementJoyStickY);


            if (this.currentGameState == CraterBackend.GAME_STATE_INGAME || this.currentGameState == CraterBackend.GAME_STATE_TUTORIAL) {

                //setting color of evolve button based on charge, -1 means that there are no evolve left
                if (this.tutorialCurrentMillis > CraterBackend.EVOLVE_INTRODUCTION) {
                    if (player.getEvolutionCharge() == -1) {
                        evolveButton.setShader(0, 0, 0, 0);
                    }
                    //its available but not fully charge
                    else if (player.getEvolutionCharge() <= 1) {
                        evolveButton.setShader(player.getEvolutionCharge(), player.getEvolutionCharge(), player.getEvolutionCharge(), 1);
                    }
                    //its fully charge
                    else {
                        evolveButton.setShader(0, 1, 0, 1);
                    }
                }

                //player has lost the game
                if (!this.inEndGamePausePeriod && this.tutorialCurrentMillis > CraterBackend.SUPPLIES_INTRODUCTION && (!this.player.isAlive() || this.supplies.size() == 0)) {

                    if (!player.isAlive()) {
                        this.animations.add(new DeathAnim(player.getDeltaX(), player.getDeltaY(), player.getW(), player.getH()));
                        this.player.hide();
                    }

                    this.player.hideAngleAimer();


                    SoundLib.setStateGameMusic(false);
                    SoundLib.setStateLossMusic(true);
                    this.resetJoySticks();
                    this.inEndGamePausePeriod = true;
                    this.endGamePauseMillis = CraterBackend.PAUSE_MILLIS;
                    return;
                }

                //translate player based on inputs from movement stick
                if (this.tutorialCurrentMillis > CraterBackend.CHARACTER_INTRODUCTION) {
                    this.player.translateFromPos(dt * this.movementJoyStickX / (1000 * scaleX) * this.player.getCharacterSpeed(), dt * this.movementJoyStickY / (scaleY * 1000) * this.player.getCharacterSpeed());
                }
                //see if the player or enemies intersect with plateaus,there is not a tutorial time cap because then the player might get stuck
                for (Plateau plateau : this.plateaus) {
                    plateau.clipCharacterPos(player);
                    for (Enemy enemy : this.enemies) {
                        plateau.clipCharacterPos(enemy);
                    }
                }

                //if the player is outside the crater, put them back
                if (this.tutorialCurrentMillis > CraterBackend.CHARACTER_INTRODUCTION) {
                    float hypotenuse = (float) Math.hypot(player.getDeltaX(), player.getDeltaY());
                    if (hypotenuse > craterRadius) {
                        player.setTranslate(player.getDeltaX() * craterRadius / hypotenuse, player.getDeltaY() * craterRadius / hypotenuse);
                    }
                    //based on player's health update the health bar
                    this.healthDisplay.update(player.getCurrentHealth(), -0.15f, -0.8f);
                    //finds the angle at which the player is aiming movement stick
                    hypotenuse = (float) Math.hypot(this.movementJoyStickX / scaleX, this.movementJoyStickY / scaleY);
                    if (hypotenuse > 0) {
                        this.player.update(dt, 180f / (float) Math.PI * MathOps.getAngle(this.movementJoyStickX / (scaleX * hypotenuse), this.movementJoyStickY / (scaleX * hypotenuse)), this.enemies, spawners);//todo make the rotation the previous frame's rotation
                    } else {
                        this.player.update(dt, this.player.getRotation(), this.enemies, spawners);//todo make the rotation the previous frame's rotation

                    }

                }


                //removes dead enemies
                if (this.tutorialCurrentMillis > CraterBackend.ENEMIES_INTRODUCTION) {
                    itr = enemies.iterator();
                    while (itr.hasNext()) {
                        Enemy enemy = (Enemy) itr.next();
                        if (!this.inEndGamePausePeriod) {
                            enemy.update(dt, this.player, this.supplies, this.plateaus);
                        }
                        if (!enemy.isAlive()) {
                            this.animations.add(new DeathAnim(enemy.getDeltaX(), enemy.getDeltaY(), enemy.getWidth(), enemy.getH()));
                            itr.remove();
                            SoundLib.playPlayerKillSoundEffect();
                        }
                    }
                }

                if (this.tutorialCurrentMillis > CraterBackend.PLATEAUS_TOXIC_LAKE_INTRODUCITON) {
                    //see if the players or enemies are in the toxic lakes, if so it damages them
                    for (ToxicLake toxicLake : this.toxicLakes) {
                        toxicLake.update(dt, this.player, this.enemies);
                    }
                }

                if (!this.inEndGamePausePeriod && this.tutorialCurrentMillis > CraterBackend.ENEMIES_INTRODUCTION) {
                    //remove dead spawners otherwise update them as to not draw dead spawners
                    itr = this.spawners.iterator();
                    while (itr.hasNext()) {
                        Spawner spawner = (Spawner) itr.next();
                        if (!spawner.isAlive()) {
                            itr.remove();
                        }

                        Enemy e = spawner.trySpawnEnemy(dt);
                        if (e != null) {
                            this.enemies.add(e);
                        }
                    }
                }
                if (this.tutorialCurrentMillis > CraterBackend.SUPPLIES_INTRODUCTION) {
                    //remove dead supplies
                    itr = this.supplies.iterator();
                    while (itr.hasNext()) {
                        Supply supply = (Supply) itr.next();
                        if (!supply.isAlive()) {
                            this.animations.add(new DeathAnim(supply.getX(), supply.getY(), supply.getWidth(), supply.getHeight()));
                            itr.remove();
                        }
                    }
                }


                if (!this.inEndGamePausePeriod && this.tutorialCurrentMillis > CraterBackend.ENEMIES_INTRODUCTION) {
                    //level is complete as all spawners have been killed
                    if (spawners.size() == 0) {
                        Log.i("BACKEND", "Level " + levelNum + " completed. Loading level " + (levelNum + 1));
                        if (levelNum != 0) {
                            this.completedLevels[levelNum - 1] = true;
                            if (levelNum < CraterBackend.NUM_LEVELS) {
                                this.unlockedLevels[levelNum] = true;
                            }
                        }
                        this.createLevelFiles();

                        this.levelNum++;


                        SoundLib.setStateGameMusic(false);
                        SoundLib.setStateVictoryMusic(true);

                        this.resetJoySticks();
                        this.inEndGamePausePeriod = true;
                        this.endGamePauseMillis = CraterBackend.PAUSE_MILLIS;
                    }
                }
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

    /** Gets the attack joystick textured rect, so the frontend can draw it
     *
     * @return the visual representation of the attack joystick
     */
    public TexturedRect getAttackJoyStick() {
        return this.attackJoyStick;
    }

    /** Gets the movement joystick textured rect, so the frontend can draw it
     *
     * @return the visual representation of the movement joystick
     */
    public TexturedRect getMovementJoyStick(){
        return this.movementJoyStick;
    }

    /** Gets the defense joystick textured rect, so the frontend can draw it
     *
     * @return the visual representation of the defense joystick
     */
    public TexturedRect getDefenseJoyStick() {
        return this.defenseJoyStick;
    }

    /** Gets the evolve button textured rect so the frontend can draw it.
     *
     * @return the visual representation of the evolve button
     */
    public Button getEvolveButton(){
        return this.evolveButton;
    }

    /** Getter for the stationary tutorial layout
     *
     * @return components of the the tutorial layout that stay on the screen
     */
    public CraterLayout getStationaryTutorialLayout(){
        return this.stationaryTutorialLayout;
    }

    /** Getter for gamemap tutorial layout
     *
     * @return components of the tutorial that move with the game map
     */
    public CraterLayout getGameMapTutorialLayout(){
        return this.gameMapTutorialLayout;
    }
    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public boolean onTouch(MotionEvent e){
        if (this.currentGameState == CraterBackend.GAME_STATE_HOMESCREEN){
            return false;
        } else if (this.currentGameState == CraterBackend.GAME_STATE_LEVELSELECT){
            this.levelSelectLayout.onTouch(e);
            this.loadLevelLayout.onTouch(e);
        } else if (this.currentGameState == CraterBackend.GAME_STATE_INGAME || this.currentGameState == CraterBackend.GAME_STATE_TUTORIAL){
            if (! this.inEndGamePausePeriod && this.tutorialCurrentMillis > CraterBackend.JOYSTICK_INTRODUCTION) {
                this.updateJoySticks(e);
            }
            if (this.currentGameState == CraterBackend.GAME_STATE_TUTORIAL){
                this.exitButton.onTouch(e);
            }
        }
        return true;
    }




    /** Based on a touch event, updates positions for the joySticks
     *
     * @param e the motion event describing how the user interacted with the screen
     */
    private void updateJoySticks(MotionEvent e){
        //scale it so its circles todo make this not initiated each frame
        float scaleX = 1;
        float scaleY = 1;
        if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT){
            scaleX = (float) (LayoutConsts.SCREEN_HEIGHT )/ (LayoutConsts.SCREEN_WIDTH );
        } else {
            scaleY = (float) (LayoutConsts.SCREEN_WIDTH)/LayoutConsts.SCREEN_HEIGHT;

        }

        int pointerInd  = e.getActionIndex();
        float x = MathOps.getOpenGLX(e.getX(pointerInd));
        float y = MathOps.getOpenGLY(e.getY(pointerInd));
        if (e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || e.getActionMasked() == MotionEvent.ACTION_DOWN){
            //assign joystick pointers
            if (x < 0 ){
                //assign left
                this.movementJoyStickPointer = e.getPointerId(pointerInd);
                this.movementJoyStickDown = true;

            } else if (! evolveButton.onTouch(e)){
                if (Math.hypot(x-ATTACK_JOY_STICK_CENTER[0],y - ATTACK_JOY_STICK_CENTER[1]) <Math.hypot(x-DEFENSE_JOY_STICK_CENTER[0],y - DEFENSE_JOY_STICK_CENTER[1])) {
                    //assign attack
                    this.attackJoyStickPointer = e.getPointerId(pointerInd);
                    this.attackJoyStickDown = true;
                    this.player.showAngleAimer();
                } else {
                    //assign defense
                    this.defenseJoyStickPointer = e.getPointerId(pointerInd);
                    this.defenseJoyStickDown = true;
                    this.player.showDefenseAngleAimer();
                }
            }

        } else if (e.getActionMasked() == MotionEvent.ACTION_POINTER_UP || e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_CANCEL){
            //de assign joystick pointers
            evolveButton.onTouch(e);

            if (e.getPointerId(pointerInd) == this.movementJoyStickPointer && this.movementJoyStickDown){
                this.movementJoyStickDown = false;
            }
            if  (e.getPointerId(pointerInd) == this.attackJoyStickPointer && this.attackJoyStickDown){
                this.attackJoyStickDown = false;
                float hypotenuse = (float) Math.hypot(this.attackJoyStickX/scaleX,this.attackJoyStickY/scaleY);
                this.player.attack(MathOps.getAngle(attackJoyStickX/(scaleX * hypotenuse), attackJoyStickY/(scaleY*hypotenuse)));
                this.joysticksTapped[2] = true;
            }
            if (e.getPointerId(pointerInd) == this.defenseJoyStickPointer && this.defenseJoyStickDown){
                this.defenseJoyStickDown = false;
                float hypotenuse = (float) Math.hypot(this.defenseJoyStickX/scaleX,this.defenseJoyStickY/scaleX);
                this.player.spawnShield(1000,MathOps.getAngle(defenseJoyStickX/(hypotenuse*scaleX), defenseJoyStickY/(scaleY * hypotenuse)));
                this.joysticksTapped[1] = true;

            }


        }

        if (this.movementJoyStickDown) {
            this.movementJoyStickX = (MathOps.getOpenGLX(e.getX(e.findPointerIndex(this.movementJoyStickPointer))) - MOVEMENT_JOY_STICK_CENTER[0]);
            this.movementJoyStickY = (MathOps.getOpenGLY(e.getY(e.findPointerIndex(this.movementJoyStickPointer))) - MOVEMENT_JOY_STICK_CENTER[1]);

            float hypotenuse = (float) Math.hypot(this.movementJoyStickX/scaleX,this.movementJoyStickY/scaleY);

            if (hypotenuse > CraterBackend.JOY_STICK_MAX_RADIUS){
                this.movementJoyStickX *= CraterBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                this.movementJoyStickY *= CraterBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
            }
            this.joysticksTapped[0] = true;
        }
        else {
            this.movementJoyStickX = 0;
            this.movementJoyStickY = 0;
        }


        if (this.attackJoyStickDown) {
            this.attackJoyStickX = (MathOps.getOpenGLX(e.getX(e.findPointerIndex(this.attackJoyStickPointer))) - ATTACK_JOY_STICK_CENTER[0]);
            this.attackJoyStickY = (MathOps.getOpenGLY(e.getY(e.findPointerIndex(this.attackJoyStickPointer))) - ATTACK_JOY_STICK_CENTER[1]);



            float hypotenuse = (float) Math.hypot(this.attackJoyStickX/scaleX,this.attackJoyStickY/scaleY);

            this.player.setAngleAimerAngle((float) (180/Math.PI) * MathOps.getAngle(this.attackJoyStickX/( scaleX * hypotenuse),  this.attackJoyStickY/(scaleY * hypotenuse)));

            if (hypotenuse > CraterBackend.JOY_STICK_MAX_RADIUS){
                this.attackJoyStickX *= CraterBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                this.attackJoyStickY *= CraterBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
            }
        } else {
            this.attackJoyStickX = 0;
            this.attackJoyStickY = 0;
        }

        if (this.defenseJoyStickDown) {
            this.defenseJoyStickX = (MathOps.getOpenGLX(e.getX(e.findPointerIndex(this.defenseJoyStickPointer))) -  DEFENSE_JOY_STICK_CENTER[0]);
            this.defenseJoyStickY = (MathOps.getOpenGLY(e.getY(e.findPointerIndex(this.defenseJoyStickPointer))) -  DEFENSE_JOY_STICK_CENTER[1]);


            float hypotenuse = (float) Math.hypot(this.defenseJoyStickX/scaleX,this.defenseJoyStickY/scaleY);

            this.player.setDefenseAngleAimerAngle((float) (180/Math.PI) * MathOps.getAngle(defenseJoyStickX/(scaleX * hypotenuse),  defenseJoyStickY/(scaleY *hypotenuse)));

            if (hypotenuse > CraterBackend.JOY_STICK_MAX_RADIUS){
                this.defenseJoyStickX *= CraterBackend.JOY_STICK_MAX_RADIUS/(hypotenuse);
                this.defenseJoyStickY *= CraterBackend.JOY_STICK_MAX_RADIUS/(hypotenuse);
            }
        } else {
            this.defenseJoyStickX = 0;
            this.defenseJoyStickY = 0;
        }


    }


}
