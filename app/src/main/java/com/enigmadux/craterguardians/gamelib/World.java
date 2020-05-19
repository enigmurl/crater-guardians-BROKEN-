package com.enigmadux.craterguardians.gamelib;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.enigmadux.craterguardians.animations.Animation;
import com.enigmadux.craterguardians.animations.TransitionAnim;
import com.enigmadux.craterguardians.attacks.BaseAttack;
import com.enigmadux.craterguardians.attacks.EnemyAttack;
import com.enigmadux.craterguardians.attacks.PlayerAttack;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.EnemyMap;
import com.enigmadux.craterguardians.filestreams.LevelData;
import com.enigmadux.craterguardians.filestreams.PlayerData;
import com.enigmadux.craterguardians.filestreams.TutorialData;
import com.enigmadux.craterguardians.guilib.GUILayout;
import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.guis.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.guis.pauseGameScreen.PauseGameLayout;
import com.enigmadux.craterguardians.guis.postGameLayout.PostGameLayout;
import com.enigmadux.craterguardians.GameMap;
import com.enigmadux.craterguardians.gameobjects.Plateau;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.gameobjects.ToxicLake;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.enemies.Enemy1;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;
import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;
import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;
import enigmadux2d.core.renderEngine.MeshRenderer;

/** Upper level class that contains data about the game state
 *
 * @author Manu Bhat
 * @version BETA
 */
public class World {
    //how long the pause after a win or loss is for smooth transitions
    private static final long PAUSE_MILLIS  = 3000;
    //how long the pre game period lasts
    public static final long PRE_GAME_MILLIES = 2000;

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

    //how far the camera can max travel in a frame;
    private static final float MAX_CAMERA_TRAVEL_FRAME = 0.05f;
    //minimum distance traveled in a frame (unless target is shorter distance)
    private static final float MIN_CAMERA_TRAVEL_FRAME = 0.01f;




    //the amount of xp gained for clearing a level
    private static final int XP_GAIN_PER_LEVEL = 10;


    //DIFFERENT STATES
    public static final int STATE_GUI = 1;
    public static final int STATE_INGAME = 2;
    public static final int STATE_PREGAME = 3;
    public static final int STATE_POSTGAMEPAUSE = 4;

    //Locks
    public static final Object blueEnemyLock = new Object();
    public static final Object orangeEnemyLock = new Object();
    public static final Object enemyAttackLock = new Object();
    public static final Object playerAttackLock = new Object();
    public static final Object supplyLock = new Object();
    public static final Object spawnerLock = new Object();
    public static final Object plateauLock = new Object();
    public static final Object toxicLakeLock = new Object();
    private static final Object animationLock = new Object();
    public static final Object playerLock = new Object();



    /** Current main player in the world
     *
     */
    private Player player;

    /** List of all active blue enemies
     *
     */
    private CraterCollection<Enemy> blueEnemies;

    /** List of all active orange enemies
     *
     */
    private CraterCollection<Enemy> orangeEnemies;

    /** List of all active attacks from the enemies
     *
     */
    private CraterCollection<BaseAttack> enemyAttacks;

    /** List of all active attacks from the player
     *
     */
    private CraterCollection<BaseAttack> playerAttacks;

    /** List of all active supplies
     *
     */
    private CraterCollection<Supply> supplies;

    /** List of all active spawners
     *
     */
    private CraterCollection<Spawner> spawners;

    /** List of all plateaus
     *
     */
    private CraterCollection<Plateau> plateaus;

    /** List of all toxic lakes
     *
     */
    private CraterCollection<ToxicLake> toxicLakes;

    private QuadTexture craterVisual;


    private ArrayList<Animation> animations = new ArrayList<>();
    //basically in game components will not be updated
    private boolean enableInGameUpdating = true;


    private MeshRenderer meshRenderer;
    private GuiRenderer guiRenderer;
    private DynamicText dynamicText;
    private QuadRenderer quadRenderer;

    private HashMap<String, GUILayout> guiLayouts;


    private int levelNum;


    //file streams
    private PlayerData playerData;
    private LevelData levelData;


    private int mState = World.STATE_GUI;

    private GameMap gameMap;
    private EnemyMap enemyMap;

    private boolean wonLastLevel = false;
    private int amntXpLastLevel;


    private float cameraX, cameraY;
    //where the camera is moved by screen shakes
    private float cameraDeltaX,cameraDeltaY;

    //avg loc of the supplies
    private float supplyX, supplyY;

    private float movementX, movementY;

    private float attackX, attackY;

    private float defenseX, defenseY;


    //mvp matrix for in game stuff
    private final float[] mvpMatrix = new float[16];
    //camera
    private final float[] camMatrix = new float[16];

    private long endGamePauseMillis;
    private long preGameZoomMillis;

    private InGameScreen inGameScreen;

    private Context context;

    //stuff to be rendered
    private ArrayList<QuadTexture> renderables = new ArrayList<>();

    public World(Context context,HashMap<String, GUILayout> guilayouts){
        this.context = context;
        this.blueEnemies = new CraterCollection<>(256,World.QUAD_VERTICES,Enemy1.TEXTURE_MAP,World.QUAD_INDICES);

        this.orangeEnemies = new CraterCollection<>(256,World.QUAD_VERTICES,Enemy1.TEXTURE_MAP,World.QUAD_INDICES);

        this.toxicLakes = new CraterCollection<>(32,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.toxicLakes.loadTexture(context,R.drawable.toxic_lake_texture);

        this.plateaus = new CraterCollection<>(32,Plateau.VERTICES,Plateau.TEX_CORDS,World.QUAD_INDICES);
        this.plateaus.loadTexture(context,R.drawable.plateau);

        this.spawners = new CraterCollection<>(16,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.spawners.loadTexture(context,R.drawable.enemy_spawner);

        this.supplies = new CraterCollection<>(16,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.supplies.loadTexture(context,R.drawable.supply_top_view);

        this.craterVisual = new QuadTexture(context,R.drawable.level_background_crater,0,0,1,1);

        this.enemyAttacks = new CraterCollection<>(128,World.QUAD_VERTICES, EnemyAttack.TEXTURE_MAP,World.QUAD_INDICES);
        this.enemyAttacks.loadTexture(context,R.drawable.enemy_attack_spritesheet);
        this.playerAttacks = new CraterCollection<>(128,World.QUAD_VERTICES, PlayerAttack.TEXTURE_MAP,World.QUAD_INDICES);
        this.playerAttacks.loadTexture(context,R.drawable.kaiser_attack_spritesheet);


        this.meshRenderer = new MeshRenderer();
        this.meshRenderer.loadShaders(context,R.raw.basic_vertex_shader,R.raw.basic_frag_shader);
        this.guiRenderer = new GuiRenderer(context,R.raw.gui_vertex_shader,R.raw.gui_fragment_shader);
        this.quadRenderer = new QuadRenderer(context,R.raw.quad_vertex_shader,R.raw.quad_frag_shader);
        this.dynamicText = new DynamicText(context,R.drawable.baloo_bhaina_texture_atlas,R.raw.baloo_bhaina_atlas);


        this.playerData = new PlayerData(context);
        this.levelData = new LevelData(context);

        this.guiLayouts = guilayouts;

        this.gameMap = new GameMap(context,this.craterVisual,supplies,toxicLakes,spawners,plateaus);

    }

    /** loads textures for in game components, but main purpose is to load textures
     *
     */
    public static void loadTextures(Context context){
        //inputs
        //characters
        Player.loadTexture(context);
        //animations
        //others (lakes + plateaus)

    }

    public void loadLayouts(){

        this.inGameScreen = (InGameScreen) this.guiLayouts.get(InGameScreen.ID);
        this.inGameScreen.setBattleStartIndicatorVisibility(false);
        this.inGameScreen.setWinLossVisibility(false);

        this.levelData.loadLevelData();
    }


    public void update(long dt){
        if (this.mState == World.STATE_GUI){
            return;
        }
        this.updateInGameScreen(dt);

        if (animations.size() > 0){
            synchronized (animationLock){
                //not using micro optimized because the list can reduce while updating
                for (int i = 0; i < this.animations.size();i++){
                    this.animations.get(i).update(this,dt);
                }
            }
        }

        if (this.mState == (World.STATE_PREGAME)){
            this.preGameZoomMillis -= dt;
            if (this.preGameZoomMillis < 0){
                //it will auto hide through an animation
                this.inGameScreen.setBattleStartIndicatorVisibility(true);
                this.mState = World.STATE_INGAME;
            }
            return;
        } else if (this.mState == (World.STATE_POSTGAMEPAUSE)){
            //so it's only called once
            if (this.endGamePauseMillis == World.PAUSE_MILLIS) {
                this.inGameScreen.setWinLossVisibility(true);
            }
            this.endGamePauseMillis -= dt;
            if (this.endGamePauseMillis < 0){
                this.finishEndGamePausePeriod();
            }
            return;
        }

        if (this.enableInGameUpdating) {
            synchronized (blueEnemyLock) {
                this.blueEnemies.update(dt, this);
            }
            synchronized (orangeEnemyLock) {
                this.orangeEnemies.update(dt, this);
            }
            synchronized (toxicLakeLock) {
                this.toxicLakes.update(dt, this);
            }
            synchronized (plateauLock) {
                this.plateaus.update(dt, this);
            }
            if (spawners.size() == 0) {
                this.completeLevelBeaten();
            } else {
                synchronized (spawnerLock) {
                    this.spawners.update(dt, this);
                }
            }

            if (supplies.size() == 0) {
                this.completeLevelLost();
            } else {
                synchronized (supplyLock) {
                    this.supplies.update(dt, this);
                }
            }

            synchronized (playerAttackLock) {
                this.playerAttacks.update(dt, this);
            }
            synchronized (enemyAttackLock) {
                this.enemyAttacks.update(dt, this);
            }

            synchronized (playerLock) {
                this.updatePlayer(dt);
            }
        }

    }

    private void updateInGameScreen(long dt){
        this.inGameScreen.update(this,dt);

    }

    private void updatePlayer(long dt){
        float scaleX = 1;
        float scaleY = 1;
        if (LayoutConsts.SCREEN_WIDTH > LayoutConsts.SCREEN_HEIGHT) {
            scaleX = (float) (LayoutConsts.SCREEN_HEIGHT) / (LayoutConsts.SCREEN_WIDTH);
        } else {
            scaleY = (float) (LayoutConsts.SCREEN_WIDTH) / LayoutConsts.SCREEN_HEIGHT;

        }
        float hypotenuse = (float) Math.hypot(this.movementX / scaleX, this.movementY / scaleY);
        if (hypotenuse > 0) {
            this.player.setRotation(180f / (float) Math.PI * MathOps.getAngle(this.movementX / (scaleX * hypotenuse), this.movementY / (scaleX * hypotenuse)));
        }

        this.player.translateFromPos(dt * this.movementX / (1000 * scaleX) * this.player.getCharacterSpeed(), dt * this.movementY / (scaleY * 1000) * this.player.getCharacterSpeed());
        this.player.setVelocity(this.movementX / (scaleX) * this.player.getCharacterSpeed(), this.movementY / (scaleY) * this.player.getCharacterSpeed());
        hypotenuse = (float) Math.hypot(this.attackX / scaleX, this.attackY / scaleY);
        if (hypotenuse > 0){
            this.player.attemptAttack(this,this.attackX/ (scaleX * hypotenuse),(this.attackY) / (scaleY *hypotenuse));
        }
        this.player.setIsAttacking(hypotenuse > 0);

        //making it sure it doesn't go out of bounds
        hypotenuse = (float) Math.hypot(player.getDeltaX(), player.getDeltaY());
        if (hypotenuse > this.gameMap.getCraterRadius()) {
            player.setTranslate(player.getDeltaX() * this.gameMap.getCraterRadius() / hypotenuse, player.getDeltaY() * this.gameMap.getCraterRadius() / hypotenuse);
        }

        hypotenuse = (float) Math.hypot(this.defenseX / scaleX, this.defenseY / scaleY);
        if (hypotenuse > 0){
            this.player.getShield().setState(true);
            this.player.getShield().setMidAngle(180f / (float) Math.PI * MathOps.getAngle(this.defenseX / (scaleX * hypotenuse), this.defenseY / (scaleX * hypotenuse)));
        } else {
            this.player.getShield().setState(false);
        }
        this.player.update(dt,this);



    }

    public void draw(float[] scaleMatrix,float[] orthoMatrix){
        boolean cameraLockedOnPlayer = true;
        if (this.mState == World.STATE_INGAME ||
                this.mState == World.STATE_POSTGAMEPAUSE ||
                this.mState == World.STATE_PREGAME) {
            this.quadRenderer.startRendering();
            this.renderables.clear();
            this.renderables.add(this.craterVisual);

            cameraLockedOnPlayer = this.updateCamera();

            float deltaX = cameraLockedOnPlayer ? this.cameraDeltaX : 0;
            float deltaY = cameraLockedOnPlayer ? this.cameraDeltaY : 0;

            Matrix.setLookAtM(this.camMatrix,0,cameraX + deltaX,cameraY + deltaY, 1,cameraX + deltaX,cameraY + deltaY,0,0,1,0);
            Matrix.multiplyMM(this.mvpMatrix,0,scaleMatrix,0,this.camMatrix,0);


            //temporary renering this is very slow
            for (int i = 0;i<spawners.size();i++){
                this.renderables.addAll(this.spawners.getInstanceData().get(i).getRenderables());
            }

            this.quadRenderer.renderQuads(this.renderables,mvpMatrix);


            this.updateVertexData(mvpMatrix);
            this.meshRenderer.startRendering();

            this.meshRenderer.renderCollection(this.spawners.getVertexData());
            this.meshRenderer.renderCollection(this.supplies.getVertexData());
            this.meshRenderer.renderCollection(this.toxicLakes.getVertexData());
            this.meshRenderer.renderCollection(this.plateaus.getVertexData());

            this.meshRenderer.renderCollection(this.playerAttacks.getVertexData());

            this.meshRenderer.renderCollection(this.blueEnemies.getVertexData());
            this.meshRenderer.renderCollection(this.orangeEnemies.getVertexData());

            this.meshRenderer.renderCollection(this.enemyAttacks.getVertexData());

        }

        this.quadRenderer.startRendering();

        if (this.mState == World.STATE_INGAME ||
                this.mState == World.STATE_POSTGAMEPAUSE ||
                this.mState == World.STATE_PREGAME) {
            //position in so it's always in the center of the screen
            float playerX = this.player.getDeltaX();
            float playerY = this.player.getDeltaY();
            synchronized (playerLock) {
                if (cameraLockedOnPlayer) {
                    this.player.setTranslate(cameraX, cameraY);
                }
                this.player.draw(mvpMatrix, this.quadRenderer);
                this.player.setTranslate(playerX, playerY);
            }
            renderables.clear();
            for (int i = 0;i<supplies.size();i++){
                this.renderables.addAll(this.supplies.getInstanceData().get(i).getRenderables());
            }
            synchronized (animationLock) {
                renderables.addAll(animations);
            }

            this.quadRenderer.renderQuads(renderables,mvpMatrix);
            this.inGameScreen.render(orthoMatrix,quadRenderer,dynamicText);
        }


        //the only gui layouts that can be shown in game, are pause game layouts, and post game layouts, otherwise
        //we don't need to render them
        if (this.mState == World.STATE_GUI || this.guiLayouts.get(PauseGameLayout.ID).isVisible()) {
            this.guiRenderer.startRendering();
            for (GUILayout layout : this.guiLayouts.values()) {
                layout.render(orthoMatrix, this.guiRenderer, this.dynamicText);
            }
        }
    }

    //if target is player =true, else false
    private boolean updateCamera(){

        if (this.mState == World.STATE_POSTGAMEPAUSE && supplies.size() == 0){
            float targetX = supplyX;
            float targetY = supplyY;
            float dist = (float) Math.hypot(cameraX - targetX,cameraY - targetY);
            if (dist < MIN_CAMERA_TRAVEL_FRAME){
                cameraX = targetX;
                cameraY = targetY;
            } else {
                dist = MAX_CAMERA_TRAVEL_FRAME;
                cameraX += (targetX - cameraX) * dist;
                cameraY += (targetY - cameraY) * dist;
            }
            return false;
        } else {
            cameraX = player.getDeltaX();
            cameraY = player.getDeltaY();
            return true;
        }
    }

    public void loadLevel(){
        Log.d("WORLD","Loading Level : " + levelNum);
        this.reset();
        blueEnemies.loadTexture(context,Enemy.STRENGTH_TEXTURES[Enemy.STRENGTHS[levelNum]]);
        orangeEnemies.loadTexture(context,Enemy.STRENGTH_TEXTURES[Enemy.STRENGTHS[levelNum]]);

        this.gameMap.loadLevel(levelNum);
        this.mState = World.STATE_INGAME;
        if (this.levelNum > GameMap.NUM_LEVELS){
            this.levelNum = 0;
        }
        supplyX = 0;
        supplyY = 0;
        for (Supply s: supplies){
            supplyX += s.getDeltaX()/supplies.size();
            supplyY += s.getDeltaY()/supplies.size();
        }

        TransitionAnim.clear();
        this.enemyMap = this.gameMap.getEnemyMap();
    }

    public void reset(){
        synchronized (blueEnemyLock){
            this.blueEnemies.clear();
        }
        synchronized (orangeEnemyLock){
            this.orangeEnemies.clear();
        }
        synchronized (enemyAttackLock){
            this.enemyAttacks.clear();
        }
        synchronized (playerAttackLock){
            this.playerAttacks.clear();
        }
        synchronized (supplyLock){
            this.supplies.clear();
        }
        synchronized (spawnerLock){
            this.spawners.clear();
        }
        synchronized (plateauLock){
            this.plateaus.clear();
        }
        synchronized (toxicLakeLock){
            this.toxicLakes.clear();
        }
        synchronized (animationLock){
            this.animations.clear();
        }

        if (this.player != null) {
            this.player.setTranslate(this.gameMap.getSpawnLocX(), this.gameMap.getSpawnLocY());
            this.player.spawn();
            //this.player.hideAngleAimer();
        }

    }

    //semi normalized values (divide by hypotenuse, but it should be an ellipse where X axis is smaller in relation to screen
    public void updateJoysticks(float mX,float mY,float dX,float dY,float aX,float aY){
        this.movementX = mX;
        this.movementY = mY;

        this.defenseX = dX;
        this.defenseY = dY;

        this.attackX = aX;
        this.attackY = aY;
    }
    /** When the level is cleared, game is paused, or other instances, the joysticks have to be reset, regardless or not if
     * the fingers are lifted up. This resets the defense,movement, and attack joysticks to their default position
     *
     */
    public void resetJoySticks(){
        this.inGameScreen.resetJoySticks();

    }


    private void updateVertexData(float[] parentMatrix){
        synchronized (blueEnemyLock){
            this.blueEnemies.prepareFrame(parentMatrix);
        }
        synchronized (orangeEnemyLock){
            this.orangeEnemies.prepareFrame(parentMatrix);
        }
        synchronized (enemyAttackLock){
            this.enemyAttacks.prepareFrame(parentMatrix);
        }
        synchronized (playerAttackLock){
            this.playerAttacks.prepareFrame(parentMatrix);
        }
        synchronized (supplyLock){
            this.supplies.prepareFrame(parentMatrix);
        }
        synchronized (spawnerLock){
            this.spawners.prepareFrame(parentMatrix);
        }
        synchronized (plateauLock){
            this.plateaus.prepareFrame(parentMatrix);
        }
        synchronized (toxicLakeLock){
            this.toxicLakes.prepareFrame(parentMatrix);
        }

        this.blueEnemies.updateVBO();
        this.orangeEnemies.updateVBO();
        this.toxicLakes.updateVBO();
        this.plateaus.updateVBO();
        this.spawners.updateVBO();
        this.supplies.updateVBO();
        this.playerAttacks.updateVBO();
        this.enemyAttacks.updateVBO();
    }

    /** Sets the level num, mainly used to initiate tutorial
     *
     * @param levelNum what level it is, a negative means tutorial
     */
    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    private void completeLevelBeaten(){
        Log.i("BACKEND", "Level " + levelNum + " completed. Loading level " + (levelNum + 1));

        this.wonLastLevel = true;
        this.mState = World.STATE_POSTGAMEPAUSE;
        this.endGamePauseMillis = World.PAUSE_MILLIS;

        boolean alreadyCompleted = false;
        if (levelNum > 0) {
            alreadyCompleted = LevelData.getCompletedLevels()[levelNum-1];
            LevelData.getCompletedLevels()[levelNum - 1] = true;
            LevelData.updateUnlocked();
        }

        this.amntXpLastLevel = getXpGainPerLevel(levelNum,alreadyCompleted);
        this.playerData.updateXP(PlayerData.getExperience() + amntXpLastLevel);
        this.levelData.writeLevelFiles();

        this.levelNum++;

        SoundLib.setStateGameMusic(false);
        SoundLib.setStateVictoryMusic(true);


        Log.d("BACKEND","started End game");
        TutorialData.TUTORIAL_ENABLED = false;

        this.resetJoySticks();
    }

    public void completeLevelLost(){
        //this.player.hideAngleAimer();
        TutorialData.TUTORIAL_ENABLED = false;

        this.mState = World.STATE_POSTGAMEPAUSE;
        this.wonLastLevel = false;
        this.endGamePauseMillis = World.PAUSE_MILLIS;
        amntXpLastLevel = 0;

        SoundLib.setStateGameMusic(false);
        SoundLib.setStateLossMusic(true);
        this.resetJoySticks();
    }

    /** During the PRe game zoom period there is some extended zoom that has to be applied
     *
     * @return the amount the camera has to additionally zoom
     */
    public float getCameraZoom(){
        if (this.mState != World.STATE_PREGAME){
            return 1;
        } else {
            //interpolate between the value needed to show the whole map, and the default camera zoom
            float startTarget = this.gameMap.getCraterRadius() * GameMap.CRATER_VISUAL_SCALE;
            float endTarget = CraterRenderer.CAMERA_Z * LayoutConsts.SCREEN_HEIGHT/LayoutConsts.SCREEN_WIDTH;

            //because at the beggining pre game zoom millis is at the max rather than at 0 it's inverted the squared is for acceleration
            float currentRad = (float) (Math.pow((double) this.preGameZoomMillis/World.PRE_GAME_MILLIES,2) * (startTarget - endTarget) + endTarget);
            return endTarget/currentRad;
        }
    }

    public void setState(int state){
        //exiting the game
        if (mState != World.STATE_GUI && state == World.STATE_GUI){
            this.getEnemyMap().endProcess();
        }

        this.mState = state;
        if (mState == World.STATE_PREGAME){
            this.preGameZoomMillis = World.PRE_GAME_MILLIES;
        } else if (mState == World.STATE_POSTGAMEPAUSE){
            this.endGamePauseMillis = World.PRE_GAME_MILLIES;
        }
    }

    public int getCurrentGameState(){
        return this.mState;
    }


    /** Called when the period is cancelled by most likely the home button
     *
     */
    public void killEndGamePausePeriod(){
        Log.d("BACKEND","Killed End game");

        this.endGamePauseMillis = 0;
        //this.player.show();
        this.inGameScreen.setWinLossVisibility(false);
        this.inGameScreen.setVisibility(false);
    }

    /** Called when the period is finished, as in the whole period lived out
     *
     */
    private void finishEndGamePausePeriod(){
        Log.d("BACKEND","Finished End game");
        this.endGamePauseMillis = 0;
        this.setState(World.STATE_GUI);
        this.guiLayouts.get(PostGameLayout.ID).setVisibility(true);

        this.inGameScreen.setWinLossVisibility(false);
        this.inGameScreen.setVisibility(false);
    }


    public void setEnableInGameUpdating(boolean enableInGameUpdating){
        this.enableInGameUpdating = enableInGameUpdating;
    }


    public Player getPlayer() {
        return player;
    }

    public CraterCollection<Enemy> getBlueEnemies() {
        return blueEnemies;
    }

    public CraterCollection<Enemy> getOrangeEnemies() {
        return orangeEnemies;
    }

    public CraterCollection<BaseAttack> getEnemyAttacks() {
        return enemyAttacks;
    }

    public CraterCollection<BaseAttack> getPlayerAttacks() {
        return playerAttacks;
    }

    public CraterCollection<Supply> getSupplies() {
        return supplies;
    }

    public CraterCollection<Spawner> getSpawners() {
        return spawners;
    }

    public CraterCollection<Plateau> getPlateaus() {
        return plateaus;
    }

    public CraterCollection<ToxicLake> getToxicLakes() {
        return toxicLakes;
    }

    public boolean hasWonLastLevel(){
        return this.wonLastLevel;
    }

    public int getAmntXpLastLevel(){
        return this.amntXpLastLevel;
    }

    public void setPlayer(Player player) {
        this.player = player;
        //todo loading components mid game, need to find better way
        this.player.loadComponents(context);
        this.gameMap.setPlayer(player);
        this.playerAttacks.loadTexture(context,player.getAttackSpritesheetPointer());
    }

    public int getLevelNum(){
        return levelNum;
    }


    public void addAnim(Animation animation){
        synchronized (animationLock){
            this.animations.add(animation);
        }
    }
    public void removeAnim(Animation animation){
        synchronized (animationLock){
            this.animations.remove(animation);
        }
    }

    public EnemyMap getEnemyMap(){return  this.enemyMap; }

    public void setEnemyMap(EnemyMap enemyMap){
        this.enemyMap = enemyMap;
        this.gameMap.setEnemyMap(enemyMap);
    }

    public float getCraterRadius(){
        return gameMap.getCraterRadius();
    }

    public void setCameraDelta(float dX,float dY){
        this.cameraDeltaX = dX;
        this.cameraDeltaY = dY;
    }
    private static int getXpGainPerLevel(int level, boolean alreadyCompleted){
        double exp = alreadyCompleted ? 0.4 : 1.45;
        return (int) (World.XP_GAIN_PER_LEVEL * Math.pow(level,exp));
    }


    public void onDestroy(){
        synchronized (blueEnemyLock){
            this.blueEnemies.clear();
            this.blueEnemies.getVertexData().recycle();
        }
        synchronized (orangeEnemyLock){
            this.orangeEnemies.clear();
            this.orangeEnemies.getVertexData().recycle();
        }
        synchronized (enemyAttackLock){
            this.enemyAttacks.clear();
            this.enemyAttacks.getVertexData().recycle();
        }
        synchronized (playerAttackLock){
            this.playerAttacks.clear();
            this.playerAttacks.getVertexData().recycle();
        }
        synchronized (supplyLock){
            this.supplies.clear();
            this.supplies.getVertexData().recycle();
        }
        synchronized (spawnerLock){
            this.spawners.clear();
            this.spawners.getVertexData().recycle();
        }
        synchronized (plateauLock){
            this.plateaus.clear();
            this.plateaus.getVertexData().recycle();
        }
        synchronized (toxicLakeLock){
            this.toxicLakes.clear();
            this.toxicLakes.getVertexData().recycle();
        }
        synchronized (animationLock){
            this.animations.clear();
        }
    }


}
