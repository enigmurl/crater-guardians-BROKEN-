package com.enigmadux.craterguardians;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.AngleAimers.TriRectAimer;
import com.enigmadux.craterguardians.AngleAimers.TriangleAimer;
import com.enigmadux.craterguardians.Animations.Animation;
import com.enigmadux.craterguardians.Animations.DeathAnim;
import com.enigmadux.craterguardians.Animations.DelayedHide;
import com.enigmadux.craterguardians.Animations.EvolveAnimation;
import com.enigmadux.craterguardians.Animations.ToxicBubble;
import com.enigmadux.craterguardians.Attacks.Enemy1Attack;
import com.enigmadux.craterguardians.Attacks.Enemy2Attack;
import com.enigmadux.craterguardians.Attacks.Enemy3Attack;
import com.enigmadux.craterguardians.Attacks.KaiserAttack;
import com.enigmadux.craterguardians.Attacks.RyzeAttack;
import com.enigmadux.craterguardians.Characters.Kaiser;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Characters.Ryze;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.Enemies.Enemy1;
import com.enigmadux.craterguardians.Enemies.Enemy2;
import com.enigmadux.craterguardians.Enemies.Enemy3;
import com.enigmadux.craterguardians.FileStreams.LevelData;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.GUILib.Button;
import com.enigmadux.craterguardians.GUILib.HealthBar;
import com.enigmadux.craterguardians.GUILib.InGameTextbox;
import com.enigmadux.craterguardians.GUILib.ProgressBar;
import com.enigmadux.craterguardians.GameObjects.Plateau;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.GameObjects.ToxicLake;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.Iterator;
import java.util.List;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.shapes.TexturedRect;

/** Is in charge of all the backend activities
 *
 * lolfat
 *
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class CraterBackend {
    /*the amount of levels
     */
    public static final int NUM_LEVELS = 20;

    //because there is stuff a little outside the actual crater, we draw it slightly bigger
    private static final float CRATER_VISUAL_SCALE = 1.25f;

    //how long the pause after a win or loss is for smooth transitions
    private static final long PAUSE_MILLIS  = 3000;
    //how long the pre game period lasts
    private static final long PRE_GAME_MILLIES = 1000;


    //Integer which represents that the game is still on the on the home screen
    public static final int GAME_STATE_HOMESCREEN = 0;
    //Integer which represents that the game is on level select
    public static final int GAME_STATE_LEVELSELECT = 1;
    //Integer which represents that the game is being played
    public static final int GAME_STATE_INGAME = 2;




    //the center coordinate of the movement joy stick (openGL coordinates)
    private static final float[] MOVEMENT_JOY_STICK_CENTER = {-0.4f,-0.6f};
    //the center coordinate of attack joy stick (openGL coordinates)
    private static final float[] ATTACK_JOY_STICK_CENTER = {0.6f,-0.4f};
    //the diameter of the movement and attack joysticks
    private static final float JOY_STICK_IMAGE_WIDTH = 0.2f;
    //the maximum length they can extend too
    private static final float JOY_STICK_MAX_RADIUS = 0.3f;

    //the center coordinate of the evolve button
    private static final float[] EVOLVE_BUTTON_CENTER = {0.6f,ATTACK_JOY_STICK_CENTER[1] + JOY_STICK_IMAGE_WIDTH + JOY_STICK_MAX_RADIUS + 0.1f};
    //the diameter of the evolve button
    private static final float EVOLVE_BUTTON_WIDTH = 0.4f;



    //the amount of xp gained for clearing a level todo in future make this part of the level data
    private static final int XP_GAIN_PER_LEVEL = 10;

    //used to lock threads as to prevent concurrent modification
    //public static final Object lock = new Object();
    //LOCKS
    //plateau locks
    //toxic lake
    //supplies
    //enemies
    //animations
    public static final Object PLATEAU_LOCK = new Object();
    public static final Object TOXICLAKE_LOCK = new Object();
    public static final Object SUPPLIES_LOCK = new Object();
    public static final Object SPAWNER_LOCK = new Object();
    public static final Object ENEMIES_LOCK = new Object();
    public static final Object ANIMATIONS_LOCK = new Object();
    public static final Object PLAYER_LOCK = new Object();

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
    private TexturedRect attackJoyStick;

    //openGl deltX coordinate of the attack joystick
    private float attackJoyStickX;
    //openGL y coordinate of the attack joystick
    private float attackJoyStickY;
    //whether the attack joystick is being activated
    private boolean attackJoyStickDown = false;
    //how to identify which pointer corresponds to the attack joystick
    private int attackJoyStickPointer;

    //visual joystick
    private TexturedRect movementJoyStick;
    //openGl deltX coordinate of the movement joystick
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

    //what level number
    private int levelNum = 0;

    //the current player
    private Player player;

    //a map of everything in the game
    private GameMap gameMap;
    //a map of where the enemy should go
    //private EnemyMap enemyMap;


    //after the game is lost or won there is small pause
    private boolean inEndGamePausePeriod;
    //the millis till the end of gamePausePeriod
    private long endGamePauseMillis = 0;

    //before the game is loaded it's zoomed out
    private boolean inPreGameZoomPeriod;
    //the millies till the end of the preGameZoom is over;
    private long preGameZoomMillis;

    //if the last level was won
    private boolean wonLastLevel = false;

    //the amount of experience has
    //public static int experience;

    //tells how much xp there is
    private PlayerData playerData;
    //tells what levels the user has completed or unlocked
    private LevelData levelData;

    //the crater
    private TexturedRect craterVisual;

    //a textbox that is shown at the begginig of the game
    private InGameTextbox battleStartIndicator;

    //Tells the user if they won or not
    private TexturedRect stateIndicator;


    /** The layout that is displayed while playing the game.
     * Includes the player sprite, all bots, the craterVisual (geography) Additionally trackers on score.
     * Otherwise we could have the level selector in the game, where its kind of like clash of clans war map, and player
     * can scout, then press play. Additionally the joystick controls
     */
    private CraterLayout gameScreenLayout;


    /** Default Constructor
     *
     * @param context any non null Context, used to access resources
     * @param renderer the renderer that has the openGL context
     * @param suppliesCollection a Vao where supplies data is written too
     * @param
     */
    public CraterBackend(Context context,
                         CraterRenderer renderer,
                         VaoCollection suppliesCollection,
                         VaoCollection toxicLakeCollection,
                         VaoCollection enemiesCollection,
                         VaoCollection spawnerCollection,
                         VaoCollection plateauCollection){
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

        this.playerData = new PlayerData(context);
        this.levelData = new LevelData(context);

        this.gameMap = new GameMap(context,this,suppliesCollection,toxicLakeCollection,enemiesCollection,spawnerCollection,plateauCollection,this.renderer.collectionsRenderer);


        attackJoyStick = new TexturedRect(ATTACK_JOY_STICK_CENTER[0]-JOY_STICK_IMAGE_WIDTH/2, ATTACK_JOY_STICK_CENTER[1]-JOY_STICK_IMAGE_WIDTH/2,scaleX * JOY_STICK_IMAGE_WIDTH,scaleY * JOY_STICK_IMAGE_WIDTH);
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


        this.gameMap.reset();
    }

    /** Sets the player that the player will use
     *
     * @param player the new player that is going to be played
     */
    public void setPlayer(Player player){
        Log.d("BACKEND","Player; " + player);
        this.gameMap.setPlayer(player);
        this.player = player;
        this.healthDisplay.setMaxHitPoints(player.getMaxHealth());
    }

    /** Initializes all layouts and their sub components;
     *
     */
    public void loadLayouts(){
        //this.loadTextures(gl);

        float scaleX = (float) LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;


        this.craterVisual = new TexturedRect(-1f,-1f,2f,2f);

        this.battleStartIndicator = new InGameTextbox("BATTLE!",0,0.5f,0.5f,LayoutConsts.CRATER_TEXT_COLOR,false);

        this.stateIndicator = new TexturedRect(-scaleX/2,0.2f,scaleX,0.6f,2);


        //max health is initialized later in setPlayer() method
        this.healthDisplay = new ProgressBar(-1,0.3f,0.2f);


        this.gameMap.setCraterVisual(this.craterVisual);
        this.gameScreenLayout = new CraterLayout(new EnigmaduxComponent[] {
                this.gameMap
        },-1.0f,-1.0f,2.0f,2.0f) {
            @Override
            public void draw(float[] parentMatrix) {
                gameMap.setCameraPos(player.getDeltaX(),player.getDeltaY());

                super.draw(parentMatrix);
            }
        };


        this.gameScreenLayout.hide();




        craterVisual.loadGLTexture(this.context,R.drawable.level_background_crater);
        battleStartIndicator.loadGLTexture();
        battleStartIndicator.hide();

        this.stateIndicator.loadGLTexture(this.context,R.drawable.victory_sign,0);
        this.stateIndicator.loadGLTexture(this.context,R.drawable.loss_sign,1);
        this.stateIndicator.hide();



        this.levelData.loadLevelData();
    }

    /** Gets the gameScreenLayout, which holds all in game components
     *
     * @return the gameScreenLayout
     */
    public CraterLayout getGameScreenLayout(){
        return this.gameScreenLayout;
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
        if (this.currentGameState == CraterBackend.GAME_STATE_INGAME){
            this.inPreGameZoomPeriod = true;
            this.preGameZoomMillis = CraterBackend.PRE_GAME_MILLIES;
        }
    }

    /** loads textures for in game components, also loads fonts, but main purpose is to load textures
     *
     */
    public void loadTextures(){
        //inputs
        this.attackJoyStick.loadGLTexture(this.context,R.drawable.test);
        this.movementJoyStick.loadGLTexture(this.context,R.drawable.test);
        this.evolveButton.loadGLTexture(this.context,R.drawable.evolve_button);

        //characters
        Player.loadGLTexture(this.context);

        Kaiser.loadGLTexture(this.context);
        Ryze.loadGLTexture(this.context);
        Enemy1.loadGLTexture(this.context);
        Enemy2.loadGLTexture(this.context);
        Enemy3.loadGLTexture(this.context);

        //aimers
        TriangleAimer.loadGLTexture(this.context);
        TriRectAimer.loadGLTexture(this.context);
        //attacks
        Enemy1Attack.loadGLTexture(this.context);
        Enemy2Attack.loadGLTexture(this.context);
        Enemy3Attack.loadGLTexture(this.context);
        KaiserAttack.loadGLTexture(this.context);
        RyzeAttack.loadGLTexture(this.context);

        //animations
        DeathAnim.loadGLTexture(this.context);
        EvolveAnimation.loadGLTexture(this.context);
        ToxicBubble.loadGLTexture(this.context);


        //others (lakes + plateaus)
        Plateau.loadGLTexture(this.context);
        ProgressBar.loadGLTexture(this.context);
        HealthBar.loadGLTexture(this.context);

        InGameTextbox.loadFont(this.context);
        InGameTextbox.loadFont(this.context);

        Button.loadButtonGLTexture(this.context);

    }


    /** Sets the level num, mainly used to initiate tutorial
     *
     * @param levelNum what level it is, a non positive means tutorial
     */
    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    /** Initializes a level, most of the work is just calling the gamemap
     *
     */
    public void loadLevel(){

        this.resetJoySticks();
        this.gameMap.loadLevel(this.levelNum);
        this.gameScreenLayout.show();
        if (this.levelNum > GameMap.NUM_LEVELS){
            this.levelNum = 0;
        }

        float scale = CRATER_VISUAL_SCALE * this.gameMap.getCraterRadius();
        this.craterVisual.setScale(scale,scale);

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

    /** Gets the battle start Indicator textbox
     *
     * @return the battle start indicator textbox that is displayed at the start of the battle
     */
    public InGameTextbox getBattleStartIndicator(){
        return this.battleStartIndicator;
    }

    /** Gets the evolve button textured rect so the frontend can draw it.
     *
     * @return the visual representation of the evolve button
     */
    public Button getEvolveButton(){
        return this.evolveButton;
    }



    /** Returns the state indicator that tells the user whether they won or loss
     *
     * @return the state indicator that tells the user whether they won or loss
     */
    public TexturedRect getStateIndicator(){
        return this.stateIndicator;
    }


    /** During the PRe game zoom period there is some extended zoom that has to be applied
     *
     * @return the amount the camera has to additionally zoom
     */
    public float getCameraZoom(){
        if (! inPreGameZoomPeriod){
            return 1;
        } else {
            //interpolate between the value needed to show the whole map, and the default camera zoom
            float startTarget = this.gameMap.getCraterRadius() * CraterBackend.CRATER_VISUAL_SCALE;
            float endTarget = this.renderer.getDefaultCameraZ() * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;

            //because at the beggining pre game zoom millies is at the max rather than at 0 it's inverted the squared is for acceleration
            float currentRad = (float) (Math.pow((double) this.preGameZoomMillis/CraterBackend.PRE_GAME_MILLIES,2) * (startTarget - endTarget) + endTarget);
            Log.d("CAMERA:","CAlled");
            return endTarget/currentRad;

        }
    }



    /** Tells whether or not he player won their last game
     *
     * @return whether or not he player won their last game
     */
    public boolean hasWonLastLevel(){
        return this.wonLastLevel;
    }


    /** Called when the period is cancelled by most likely the home button
     *
     */
    public void killEndGamePausePeriod(){
        Log.d("BACKEND","Killed End game");

        this.endGamePauseMillis = 0;
        this.inEndGamePausePeriod = false;
        this.player.show();
        this.stateIndicator.hide();
    }

    /** Called when the period is finished, as in the whole period lived out
     *
     */
    private void finishEndGamePausePeriod(){
        Log.d("BACKEND","Finished End game");
        this.endGamePauseMillis = 0;
        this.inEndGamePausePeriod = false;
        setCurrentGameState(GAME_STATE_LEVELSELECT);
        this.renderer.showPostGameLayout();
        this.player.show();
        this.stateIndicator.hide();

        //we need to show the load level layout somehow

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
        this.attackJoyStickDown = false;
        this.movementJoyStickDown = false;
    }



    //todo remove these
    private long DEBUGtotalTime;
    private long DEBUGsynchroTime;
    private long DEBUGanimationsTime;
    private long DEBUGplayerTime;
    private long DEBUGplateauTime;
    private long DEBUGenemyTime;
    private long DEBUGtoxicLakeTime;
    private long DEBUGspawnerTime;

    /** Updates the game state
     *
     * @param dt milliseconds since last call
     */
    public void update(long dt){
        long totalStart = System.currentTimeMillis();
        if (inEndGamePausePeriod){
            this.stateIndicator.show();
            this.endGamePauseMillis -= dt;
            if (this.endGamePauseMillis < 0){
                this.finishEndGamePausePeriod();
            }
        }

        if (inPreGameZoomPeriod){
            this.preGameZoomMillis -= dt;
            if (this.preGameZoomMillis < 0){
                this.battleStartIndicator.show();
                this.inPreGameZoomPeriod = false;

                //todo hardcoded value of 1000;
                new DelayedHide(this.battleStartIndicator,1000);
            }
            //return;
        }

        //during the evolve period
        if (this.player.isEvolving()) {
            this.player.update(dt, this.player.getRotation(), this.gameMap.getEnemies(), this.gameMap.getSpawners());
            Log.d("FRONT:","EVOLVING:");
            return;
        }
        long start = System.currentTimeMillis();

        Iterator itr;

        this.DEBUGsynchroTime += System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        synchronized (CraterBackend.ANIMATIONS_LOCK) {
            itr = this.gameMap.getAnimations().iterator();
            while (itr.hasNext()) {
                Animation anim = (Animation) itr.next();
                anim.update(dt);
                if (anim.isFinished()) {
                    itr.remove();
                }
            }
        }
        this.DEBUGanimationsTime += System.currentTimeMillis() - start;


        float scaleX = 1;
        float scaleY = 1;
        if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT) {
            scaleX = (float) (LayoutConsts.SCREEN_HEIGHT) / (LayoutConsts.SCREEN_WIDTH);
        } else {
            scaleY = (float) (LayoutConsts.SCREEN_WIDTH) / LayoutConsts.SCREEN_HEIGHT;

        }

        this.attackJoyStick.setTranslate(this.attackJoyStickX, this.attackJoyStickY);
        this.movementJoyStick.setTranslate(this.movementJoyStickX, this.movementJoyStickY);


        if (this.currentGameState == CraterBackend.GAME_STATE_INGAME) {

            //setting color of evolve button based on charge, -1 means that there are no evolve left
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


            //player has lost the game
            if (!this.inEndGamePausePeriod && (!this.player.isAlive() || this.gameMap.getSupplies().size() == 0)) {

                if (!player.isAlive()) {
                    synchronized (CraterBackend.ANIMATIONS_LOCK) {
                        this.gameMap.getAnimations().add(new DeathAnim(player.getDeltaX(), player.getDeltaY(), player.getRadius() * 2, player.getRadius() * 2));
                        this.player.hide();
                    }
                }

                this.player.hideAngleAimer();


                SoundLib.setStateGameMusic(false);
                SoundLib.setStateLossMusic(true);
                this.resetJoySticks();
                Log.d("BACKEND","started End game");
                this.inEndGamePausePeriod = true;
                this.endGamePauseMillis = CraterBackend.PAUSE_MILLIS;
                this.wonLastLevel = false;
                return;
            }





            start = System.currentTimeMillis();

            if (EnemyMap.LOCK.tryLock()) {
                try {
                    this.gameMap.getEnemyMap().updatePlayerPosition(this.player);
                } finally {
                    EnemyMap.LOCK.unlock();
                }
            }
            synchronized (CraterBackend.ENEMIES_LOCK) {
                //removes dead enemies
                itr = this.gameMap.getEnemies().iterator();
                while (itr.hasNext()) {
                    Enemy enemy = (Enemy) itr.next();
                    if (!this.inEndGamePausePeriod) {
                        enemy.update(dt, this.player, this.gameMap.getSupplies(), this.gameMap.getEnemyMap());
                    }
                    if (!enemy.isAlive()) {
                        synchronized (CraterBackend.ANIMATIONS_LOCK) {
                            this.gameMap.getAnimations().add(new DeathAnim(enemy.getDeltaX(), enemy.getDeltaY(), enemy.getWidth(), enemy.getHeight()));
                            itr.remove();

                            this.gameMap.getEnemiesVao().deleteInstance(enemy.getInstanceID());
                            SoundLib.playPlayerKillSoundEffect();
                        }
                    }
                }

                this.DEBUGenemyTime += System.currentTimeMillis() - start;
            }


            this.DEBUGenemyTime += System.currentTimeMillis() - start;

            start = System.currentTimeMillis();
            synchronized (CraterBackend.TOXICLAKE_LOCK) {
                //see if the players or enemies are in the toxic lakes, if so it damages them
                for (ToxicLake toxicLake : this.gameMap.getToxicLakes()) {
                    toxicLake.update(dt, this.player, this.gameMap.getEnemies());
                }

            }
            this.DEBUGtoxicLakeTime += System.currentTimeMillis() - start;

            synchronized (CraterBackend.SPAWNER_LOCK) {
                start = System.currentTimeMillis();
                if (!this.inEndGamePausePeriod ) {
                    //remove dead spawners otherwise update them as to not draw dead spawners
                    itr = this.gameMap.getSpawners().iterator();
                    while (itr.hasNext()) {
                        Spawner spawner = (Spawner) itr.next();
                        if (!spawner.isAlive()) {
                            this.gameMap.getSpawnerVao().deleteInstance(spawner.getInstanceID());
                            itr.remove();
                        }

                        //Enemy e = spawner.trySpawnEnemy(dt);
                        List<Enemy> enemies = spawner.update(dt,this.gameMap.getEnemiesVao());
                        if (enemies != null) {
                            Log.d("BACKEND","Adding enemy");
                            synchronized (CraterBackend.ENEMIES_LOCK){
                                this.gameMap.getEnemies().addAll(enemies);
                                //this.gameMap.getEnemies().add(e);
                            }
                        }
                    }
                }
            }
            this.DEBUGspawnerTime += System.currentTimeMillis() - start;
            synchronized (CraterBackend.SUPPLIES_LOCK) {
                //remove dead supplies
                itr = this.gameMap.getSupplies().iterator();
                while (itr.hasNext()) {
                    Supply supply = (Supply) itr.next();
                    if (!supply.isAlive()) {
                        synchronized (CraterBackend.ANIMATIONS_LOCK) {
                            this.gameMap.getAnimations().add(new DeathAnim(supply.getDeltaX(), supply.getDeltaY(), supply.getWidth(), supply.getHeight()));
                        }
                        this.gameMap.getSuppliesVao().deleteInstance(supply.getInstanceID());
                        itr.remove();
                    }
                }

            }


            if (! this.inEndGamePausePeriod) {
                //level is complete as all spawners have been killed
                if (gameMap.getSpawners().size() == 0) {
                    this.completeLevelBeaten();

                }
            }

            start = System.currentTimeMillis();
            //if the player is outside the crater, put them back
            //lock is needed for the added attacks
            synchronized (CraterBackend.PLAYER_LOCK) {
                start = System.currentTimeMillis();
                //translate player based on inputs from movement stick
                this.player.translateFromPos(dt * this.movementJoyStickX / (1000 * scaleX) * this.player.getCharacterSpeed(), dt * this.movementJoyStickY / (scaleY * 1000) * this.player.getCharacterSpeed());

                float hypotenuse = (float) Math.hypot(player.getDeltaX(), player.getDeltaY());
                if (hypotenuse > this.gameMap.getCraterRadius()) {
                    player.setTranslate(player.getDeltaX() * this.gameMap.getCraterRadius() / hypotenuse, player.getDeltaY() * this.gameMap.getCraterRadius() / hypotenuse);
                }
                //based on player's health update the health bar
                this.healthDisplay.update(player.getCurrentHealth(), -0.15f, -0.8f);
                //finds the angle at which the player is aiming movement stick
                hypotenuse = (float) Math.hypot(this.movementJoyStickX / scaleX, this.movementJoyStickY / scaleY);
                if (hypotenuse > 0) {
                    this.player.update(dt, 180f / (float) Math.PI * MathOps.getAngle(this.movementJoyStickX / (scaleX * hypotenuse), this.movementJoyStickY / (scaleX * hypotenuse)), this.gameMap.getEnemies(), this.gameMap.getSpawners());
                } else {
                    this.player.update(dt, this.player.getRotation(), this.gameMap.getEnemies(), this.gameMap.getSpawners());

                }

                //see if the player or enemies intersect with plateaus,there is not a tutorial time cap because then the player might get stuck
                for (Plateau plateau : this.gameMap.getPlateaus()) {
                    plateau.clipCharacterPos(player);
                    for (Enemy enemy : this.gameMap.getEnemies()) {
                        plateau.clipCharacterPos(enemy);
                    }
                }

            }
            this.DEBUGplayerTime += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();




            this.DEBUGplayerTime += System.currentTimeMillis() - start;
        }


        this.DEBUGtotalTime += System.currentTimeMillis() - totalStart;

        if (this.DEBUGtotalTime > 10000){
            Log.d("TIMECONSUMPTION:","ANIMATION PERCENTAGE:"  + ((float) this.DEBUGanimationsTime)/this.DEBUGtotalTime);
            Log.d("TIMECONSUMPTION:","PLAYER PERCENTAGE:"  + ((float) this.DEBUGplayerTime)/this.DEBUGtotalTime);
            Log.d("TIMECONSUMPTION:","PLATEAU PERCENTAGE:"  + ((float) this.DEBUGplateauTime)/this.DEBUGtotalTime);
            Log.d("TIMECONSUMPTION:","ENEMY PERCENTAGE:"  + ((float) this.DEBUGenemyTime)/this.DEBUGtotalTime);
            Log.d("TIMECONSUMPTION:","TOXIC LAKE PERCENTAGE:"  + ((float) this.DEBUGtoxicLakeTime)/this.DEBUGtotalTime);
            Log.d("TIMECONSUMPTION:","SPAWNER PERCENTAGE:"  + ((float) this.DEBUGspawnerTime)/this.DEBUGtotalTime);
            Log.d("TIMECONSUMPTION:","SYNCHRO PERCENTAGE:"  + ((float) this.DEBUGsynchroTime)/this.DEBUGtotalTime);

            this.DEBUGtotalTime = 0;
            this.DEBUGanimationsTime = 0;
            this.DEBUGplayerTime = 0;
            this.DEBUGenemyTime = 0;
            this.DEBUGtoxicLakeTime = 0;
            this.DEBUGspawnerTime = 0;
            this.DEBUGsynchroTime = 0;
        }


    }

    /** Called whenever a level is beaten
     *
     */
    private void completeLevelBeaten(){
        Log.i("BACKEND", "Level " + levelNum + " completed. Loading level " + (levelNum + 1));

        this.wonLastLevel = true;

        if (levelNum > 0) {
            LevelData.getCompletedLevels()[levelNum - 1] = true;
            if (levelNum < CraterBackend.NUM_LEVELS) {

                LevelData.getUnlockedLevels()[levelNum] = true;
            }
        }
        this.levelNum++;

        this.playerData.updateXP(PlayerData.getExperience() + CraterBackend.XP_GAIN_PER_LEVEL);
        //this.experience += CraterBackend.XP_GAIN_PER_LEVEL;

        this.levelData.writeLevelFiles();
        //this.createLevelFiles();





        SoundLib.setStateGameMusic(false);
        SoundLib.setStateVictoryMusic(true);


        Log.d("BACKEND","started End game");

        this.resetJoySticks();
        this.inEndGamePausePeriod = true;
        this.endGamePauseMillis = CraterBackend.PAUSE_MILLIS;
    }

    /** Called every time there is a touch event
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not you are interested in the rest of that event (everything from ACTION_DOWN to ACTION_UP or ACTION_CANCEL) (true means interested, false means not, other views get to read the event)
     */
    public boolean onTouch(MotionEvent e){
        if (this.currentGameState == CraterBackend.GAME_STATE_HOMESCREEN){
            return false;
        } else if (this.currentGameState == CraterBackend.GAME_STATE_INGAME){
            if (! (this.inEndGamePausePeriod || this.inPreGameZoomPeriod )) {
                this.updateJoySticks(e);
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

        if (e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || e.getActionMasked() == MotionEvent.ACTION_DOWN){
            //assign joystick pointers
            if (x < 0 ){
                //assign left
                this.movementJoyStickPointer = e.getPointerId(pointerInd);
                this.movementJoyStickDown = true;

            } else if (! evolveButton.onTouch(e)){
                //assign attack
                this.attackJoyStickPointer = e.getPointerId(pointerInd);
                this.attackJoyStickDown = true;
                this.player.showAngleAimer();
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
                //can't attack while evolving
                if (! this.player.isEvolving()) {
                    synchronized (CraterBackend.PLAYER_LOCK) {
                        this.player.attack(MathOps.getAngle(attackJoyStickX / (scaleX * hypotenuse), attackJoyStickY / (scaleY * hypotenuse)));
                    }
                }
            }

        }

        //don't move in pre game period
        if (this.inPreGameZoomPeriod){
            return;
        }

        if (this.movementJoyStickDown) {
            this.movementJoyStickX = (MathOps.getOpenGLX(e.getX(e.findPointerIndex(this.movementJoyStickPointer))) - MOVEMENT_JOY_STICK_CENTER[0]);
            this.movementJoyStickY = (MathOps.getOpenGLY(e.getY(e.findPointerIndex(this.movementJoyStickPointer))) - MOVEMENT_JOY_STICK_CENTER[1]);

            float hypotenuse = (float) Math.hypot(this.movementJoyStickX/scaleX,this.movementJoyStickY/scaleY);

            if (hypotenuse > CraterBackend.JOY_STICK_MAX_RADIUS){
                this.movementJoyStickX *= CraterBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
                this.movementJoyStickY *= CraterBackend.JOY_STICK_MAX_RADIUS/hypotenuse;
            }
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

        //if evolving no need to hide
        if (this.player.isEvolving()) this.player.hideAngleAimer();



    }

    /** Gets the current player
     *
     * @return the current player
     */
    public Player getPlayer(){
        Log.d("BACKEND","Player: " + this.player);
        return this.player;
    }

    public GameMap getGameMap(){
        return this.gameMap;
    }


}
