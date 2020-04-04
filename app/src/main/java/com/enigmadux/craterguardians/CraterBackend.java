package com.enigmadux.craterguardians;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.FileStreams.LevelData;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.GUILib.GUILayout;
import com.enigmadux.craterguardians.GUIs.inGameScreen.InGameScreen;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.worlds.World;

import java.util.HashMap;

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
    public static final int GAME_STATE_PREGAME_ZOOM = 3;
    public static final int GAME_STATE_POST_GAME_PAUSE = 4;


    //the amount of xp gained for clearing a level todo in future make this part of the level data
    private static final int XP_GAIN_PER_LEVEL = 10;


    //used for getting more information about device
    private Context context;
    //used for going back to homescreen and such
    private CraterRenderer renderer;


    //instance game state info

    //describes what the user is doing  (e.g. home screen, level select, and in game)
    private int currentGameState;

    //what level number
    private int levelNum = 0;


    //a map of everything in the game
    private World gameMap;
    //a map of where the enemy should go
    //private EnemyMap enemyMap;


    //after the game is lost or won there is small pause
    //the millis till the end of gamePausePeriod
    private long endGamePauseMillis = 0;

    //before the game is loaded it's zoomed out
    //the millies till the end of the preGameZoom is over;
    private long preGameZoomMillis;

    //the amount of experience has
    //public static int experience;

    //tells how much xp there is
    private PlayerData playerData;
    //tells what levels the user has completed or unlocked
    private LevelData levelData;


    //a textbox that is shown at the begginig of the game

    //todo not a good solution
    //Tells the user if they won or not it contains inside the win texture
    //the loss texture the state indicator may need to access


    private HashMap<String,GUILayout> guiLayoutHashMap;
    private InGameScreen inGameScreen;

    /** Default Constructor
     *
     * @param context any non null Context, used to access resources
     * @param renderer the renderer that has the openGL context
     */
    public CraterBackend(Context context,
                         CraterRenderer renderer, HashMap<String, GUILayout> guiLayoutHashMap){
        this.context = context;
        this.renderer = renderer;
        this.guiLayoutHashMap = guiLayoutHashMap;

        this.playerData = new PlayerData(context);
        this.levelData = new LevelData(context);

        //this.gameMap = new World(context,this,guiLayoutHashMap);
//        this.gameMap.reset();
    }

    /** Sets the player that the player will use
     *
     * @param player the new player that is going to be played
     */
    public void setPlayer(com.enigmadux.craterguardians.players.Player player){
        Log.d("BACKEND","Player; " + player);
        this.gameMap.setPlayer(player);
    }

    /** Initializes all layouts and their sub components;
     *
     */
    public void loadLayouts(){
        //this.loadTextures(gl);

        this.inGameScreen = (InGameScreen) guiLayoutHashMap.get(InGameScreen.ID);

        Log.d("BACKEND","Null check: " + this.inGameScreen);
        this.inGameScreen.setBattleStartIndicatorVisibility(false);
        this.inGameScreen.setWinLossVisibility(false);

        this.levelData.loadLevelData();
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
        if (this.currentGameState == CraterBackend.GAME_STATE_PREGAME_ZOOM){
            this.gameMap.setState(World.STATE_PREGAME);
            this.preGameZoomMillis = CraterBackend.PRE_GAME_MILLIES;
        }
        else if (this.currentGameState == CraterBackend.GAME_STATE_POST_GAME_PAUSE){
            this.gameMap.setState(World.STATE_POSTGAMEPAUSE);
            this.endGamePauseMillis = CraterBackend.PAUSE_MILLIS;
        }
        else if (this.currentGameState == CraterBackend.GAME_STATE_INGAME){
            this.gameMap.setState(World.STATE_INGAME);
        } else if (currentGameState == CraterBackend.GAME_STATE_HOMESCREEN || currentGameState == CraterBackend.GAME_STATE_LEVELSELECT){
            this.gameMap.setState(World.STATE_GUI);
        }
    }


    /** Called when the period is cancelled by most likely the home button
     *
     */
    public void killEndGamePausePeriod(){
        Log.d("BACKEND","Killed End game");

        this.endGamePauseMillis = 0;
        //this.player.show();
        this.inGameScreen.setWinLossVisibility(false);
    }


    /** Updates the game state
     *
     * @param dt milliseconds since last call
     */
    public void update(long dt){
//        if (this.currentGameState == CraterBackend.GAME_STATE_POST_GAME_PAUSE){
//            this.inGameScreen.setWinLossVisibility(true);
//            this.endGamePauseMillis -= dt;
//            if (this.endGamePauseMillis < 0){
//                this.finishEndGamePausePeriod();
//            }
//        }
//
//        if (this.currentGameState == CraterBackend.GAME_STATE_PREGAME_ZOOM){
//            this.preGameZoomMillis -= dt;
//            if (this.preGameZoomMillis < 0){
//                this.inGameScreen.setBattleStartIndicatorVisibility(true);
//                this.setCurrentGameState(CraterBackend.GAME_STATE_INGAME);
//                //todo hardcoded value of 1000;
//                new DelayedHide(this.inGameScreen.getBattleStartIndicator(),1000);
//            }
//        }
//        QuadTexture evolveButton = this.inGameScreen.getEvolveButton();
//        if (this.getPlayer().getEvolveCharge() == 1) {
//            evolveButton.setVisibility(true);
//            evolveButton.setShader(0,1,0,1);
//
//        } else if (this.getPlayer().getEvolveCharge() < 0){
//            evolveButton.setVisibility(false);
//        } else {
//            evolveButton.setVisibility(true);
//            float charge = this.getPlayer().getEvolveCharge();
//            evolveButton.setShader(charge,charge,charge,1);
//        }
        this.gameMap.update(dt);


    }




    /** Gets the current player
     *
     * @return the current player
     */
    public Player getPlayer(){
        return this.gameMap.getPlayer();
    }

    public World getWorld(){
        return this.gameMap;
    }
}
