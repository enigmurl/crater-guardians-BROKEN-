package com.enigmadux.craterguardians;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Animations.Animation;
import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.Enemies.Enemy;
import com.enigmadux.craterguardians.FileStreams.LevelData;
import com.enigmadux.craterguardians.GameObjects.Plateau;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.GameObjects.ToxicLake;
import com.enigmadux.craterguardians.Spawners.Enemy1Spawner;
import com.enigmadux.craterguardians.Spawners.Enemy2Spawner;
import com.enigmadux.craterguardians.Spawners.Enemy3Spawner;
import com.enigmadux.craterguardians.Spawners.Spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.gameObjects.VaoCollection;
import enigmadux2d.core.renderEngine.MeshRenderer;

public class GameMap extends EnigmaduxComponent {
    //the amount of levels in the game
    public static final int NUM_LEVELS = 20;


    //radius of the crater
    private float craterRadius;
    //spawn location
    private float[] spawnLocation = new float[2];
    //the current player on the map
    private Player player;
    //all enemies on the map
    //private final Enemy[] enemies = new Enemy[CraterBackend.MAX_ENEMIES];
    private final List<Enemy> enemies = new ArrayList<>();
    //all spawner on the map
    //private final Spawner[] spawners = new Spawner[]
    private final List<Spawner> spawners = new ArrayList<>();
    //all plateaus on the map
    private final List<Plateau> plateaus = new ArrayList<>();
    //all active toxic lakes on the map
    private final List<ToxicLake> toxicLakes = new ArrayList<>();
    //all active supplies on the map
    private final List<Supply> supplies = new ArrayList<>();
    //all active animations
    private final List<Animation> animations = new ArrayList<>();

    //a map of where the enemy should go
    private EnemyMap enemyMap;

    //used to write what levels are unlocked/cleared
    private LevelData levelData;
    //used to open resources
    private Context context;
    //used to calculate tutorial millis
    private CraterBackend backend;

    //details about camera
    //the world deltX position
    private float cameraX;
    //the world y position
    private float cameraY;

    //renders the supplies
    private VaoCollection suppliesVao;
    private VaoCollection toxicLakeCollection;
    private VaoCollection enemiesCollection;
    private VaoCollection spawnerCollection;
    private VaoCollection plateausCollection;

    private MeshRenderer meshRenderer;

    /** Creates a GameMap Instance
     *
     * @param context any non null context that can acess resources
     * @param backend we really only need the backend for tutorial checking
     */
    public GameMap(Context context, CraterBackend backend, VaoCollection suppliesCollection,
                   VaoCollection toxicLakeCollection,
                   VaoCollection enemiesCollection,
                   VaoCollection spawnerCollection,
                   VaoCollection plateausColleciton,
                   MeshRenderer meshRenderer) {
        super(0,0,0,0);//deltX,y,w,h, are never really used
        this.context = context;

        this.levelData = new LevelData(context);
        this.backend = backend;

        this.suppliesVao = suppliesCollection;
        this.toxicLakeCollection = toxicLakeCollection;
        this.enemiesCollection = enemiesCollection;
        this.spawnerCollection = spawnerCollection;
        this.plateausCollection = plateausColleciton;

        this.meshRenderer = meshRenderer;

        this.show();
    }


    /** The current player object
     *
     * @param player the current player object e.g. Ryze
     */
    public void setPlayer(Player player){
        this.player = player;
    }


    /** Sets the camera coordinates
     *
     * @param cameraX the cameraX location
     * @param cameraY the cameraY location
     */
    public void setCameraPos(float cameraX,float cameraY){
        this.cameraX = cameraX;
        this.cameraY = cameraY;
    }

    //todo these are debug only
    private long TOTAL = 0;
    private long plateauTIME;
    private long toxicLakeTIME;
    private long suppliesTIME;
    private long spawnersTIME;
    private long enemiesTIME;
    private long enemiesSynchroTIME;
    private long animationTIME;
    private long playerTIME;

    /** Draws the gameMap
     *
     * @param parentMatrix describes how to transform to world coordinates
     */
    public void draw(float[] parentMatrix){

        //basically this stops threads from accessing the same variables at the same time, as during the level select levels are loaded, which
        //if drawn at the same time from two threads will throw a java ConcurrmentModificationExcpetion

        ///see if splitting it into two loops makes it slower
        long overallStart = System.currentTimeMillis();
        long start = System.currentTimeMillis();

        float[] bufferData = new float[22];


        if (plateaus.size() > 0) {
            synchronized (CraterBackend.PLATEAU_LOCK) {
                for (int i = 0, size = this.plateaus.size();i < size; i++){
                    plateaus.get(i).updateInstanceInfo(bufferData,parentMatrix);
                    plateausCollection.updateInstance(plateaus.get(i).getInstanceID(),bufferData);
                    //plateaus.get(i).draw(parentMatrix);
                }
                plateausCollection.updateInstancedVbo();
                this.meshRenderer.renderCollection(this.plateausCollection);
                //for (Plateau plateau : this.plateaus) {
                //  plateau.draw(gl, parentMatrix);
                //}
            }
        }
        plateauTIME += System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        if (this.backend.getTutorialCurrentMillis() > CraterBackend.PLATEAUS_TOXIC_LAKE_INTRODUCTION) {
            synchronized (CraterBackend.TOXICLAKE_LOCK) {
                for (int i = 0, size = this.toxicLakes.size();i < size; i++){
                    this.toxicLakes.get(i).updateInstanceInfo(bufferData,parentMatrix);

                    this.toxicLakeCollection.updateInstance(this.toxicLakes.get(i).getInstanceID(),bufferData);

                }

                this.toxicLakeCollection.updateInstancedVbo();

                this.meshRenderer.renderCollection(this.toxicLakeCollection);

                //for (ToxicLake toxicLake : this.toxicLakes) {
                //    toxicLake.draw(gl, parentMatrix);
                //}

            }
        }
        toxicLakeTIME += System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        if (supplies.size() > 0 && this.backend.getTutorialCurrentMillis() > CraterBackend.SUPPLIES_INTRODUCTION) {
            synchronized (CraterBackend.SUPPLIES_LOCK) {

                for (int i = 0, size = this.supplies.size();i < size; i++){
                    this.supplies.get(i).updateInstanceInfo(bufferData,parentMatrix);

                    this.suppliesVao.updateInstance(this.supplies.get(i).getInstanceID(),bufferData);

                }

                this.suppliesVao.updateInstancedVbo();

                this.meshRenderer.renderCollection(this.suppliesVao);

                //for (Supply supply : this.supplies) {
                //    supply.draw(gl, parentMatrix);
                //}

            }
        }
        suppliesTIME += System.currentTimeMillis() - start;

        if (this.backend.getTutorialCurrentMillis() > CraterBackend.ENEMIES_INTRODUCTION) {
            start = System.currentTimeMillis();
            synchronized (CraterBackend.SPAWNER_LOCK) {
                for (int i = 0, size = this.spawners.size();i < size; i++){
                    this.spawners.get(i).updateInstanceInfo(bufferData,parentMatrix);

                    this.spawnerCollection.updateInstance(this.spawners.get(i).getInstanceID(),bufferData);
                }
                this.spawnerCollection.updateInstancedVbo();
                this.meshRenderer.renderCollection(this.spawnerCollection);
                //for (Spawner spawner : this.spawners) {
                //    spawner.draw(gl, parentMatrix);
                //}
            }
            spawnersTIME += System.currentTimeMillis() - start;
            if (enemies.size() > 0) {
                start = System.currentTimeMillis();
                synchronized (CraterBackend.ENEMIES_LOCK) {
                    enemiesSynchroTIME += System.currentTimeMillis() - start;
                    start = System.currentTimeMillis();
//                    Enemy.prepareDraw();
                    for (int i = 0, size = this.enemies.size();i < size; i++){
                        enemies.get(i).updateInstanceInfo(bufferData,parentMatrix);
                        this.enemiesCollection.updateInstance(enemies.get(i).getInstanceID(),bufferData);
                    }
                    this.enemiesCollection.updateInstancedVbo();
                    this.meshRenderer.renderCollection(this.enemiesCollection);
//                    Enemy.endDrawing();
                    for (int i = 0, size = this.enemies.size();i < size; i++){
                        //enemies.get(i).draw(parentMatrix);
                    }
                    enemiesTIME += System.currentTimeMillis() - start;

                    //for (Enemy enemy : this.enemies) {
                    //    enemy.draw(gl, parentMatrix);
                    //}
                }

            }
        }

        start = System.currentTimeMillis();
        if (animations.size() > 0) {
            synchronized (CraterBackend.ANIMATIONS_LOCK) {
                for (int i = 0, size = this.animations.size();i < size; i++) {
                    animations.get(i).draw(parentMatrix);
                }
                //for (Animation animation : this.animations) {
                //    animation.draw(gl, parentMatrix);
                //}
            }
        }
        animationTIME += System.currentTimeMillis() - start;


        start = System.currentTimeMillis();
        synchronized (CraterBackend.PLAYER_LOCK) {
            if (this.backend.getTutorialCurrentMillis() > CraterBackend.CHARACTER_INTRODUCTION) {
                float x = this.player.getDeltaX();
                float y = this.player.getDeltaY();
                this.player.setTranslate(this.cameraX,this.cameraY);
                //this.player.draw(parentMatrix);
                this.player.setTranslate(x,y);
            }
        }
        playerTIME += System.currentTimeMillis() - start;

        this.TOTAL += System.currentTimeMillis() - overallStart;
        if (TOTAL > 10000){
            Log.d("FRONTEND:","Plateau time: " + (plateauTIME/((double)TOTAL)) );
            Log.d("FRONTEND:","Toxic L time: "+ (toxicLakeTIME/((double) TOTAL)) );
            Log.d("FRONTEND:","SUPPLY  time: "+ (suppliesTIME/((double)TOTAL)) );
            Log.d("FRONTEND:","SPAWNER time: "+ (spawnersTIME/((double) TOTAL)) );
            Log.d("FRONTEND:","ENEMY   time: "+ (enemiesTIME/((double) TOTAL)) );
            Log.d("FRONTEND:","ENMYSYN time: "+ (enemiesSynchroTIME/((double) TOTAL)) );
            Log.d("FRONTEND:","ANIMATI time: "+ (animationTIME/((double) TOTAL)) );
            Log.d("FRONTEND:","PLAYER  time: "+ (playerTIME/((double) TOTAL)) );

            TOTAL = 0;
            plateauTIME = 0;
            toxicLakeTIME = 0;
            suppliesTIME = 0;
            spawnersTIME = 0;
            enemiesTIME = 0;
            animationTIME = 0;
            playerTIME = 0;

        }


    }




    /**
     * Initializes a level
     */
    public void loadLevel(int levelNum) {
        Log.d("GAMEMAP:","Loading level:" + levelNum);
        this.levelData.writeLevelFiles();


        int fileName;
        switch (levelNum) {
            case 1:
                fileName = R.raw.level_01;
                break;
            case 2:
                fileName = R.raw.level_02;
                break;
            case 3:
                fileName = R.raw.level_03;
                break;
            case 4:
                fileName = R.raw.level_04;
                break;
            case 5:
                fileName = R.raw.level_05;
                break;
            case 6:
                fileName = R.raw.level_06;
                break;
            case 7:
                fileName = R.raw.level_07;
                break;
            case 8:
                fileName = R.raw.level_08;
                break;
            case 9:
                fileName = R.raw.level_09;
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
            case 20:
                fileName = R.raw.level_20;
                break;
            default:
                fileName = R.raw.level_tutorial;
        }

        Scanner level_data = new Scanner(context.getResources().openRawResource(fileName));

        spawnLocation[0] = level_data.nextFloat();
        spawnLocation[1] = level_data.nextFloat();
        this.player.setTranslate(spawnLocation[0], spawnLocation[1]);
        craterRadius = level_data.nextFloat();

        this.reset();


        synchronized (CraterBackend.SUPPLIES_LOCK) {
            int numSupplies = level_data.nextInt();
            for (int i = 0; i < numSupplies; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                float r = level_data.nextFloat();
                int health = level_data.nextInt();

                int id = this.suppliesVao.addInstance();
                supplies.add(new Supply(x, y, r, health,id));
            }
        }


        int numToxicLakes = level_data.nextInt();
        synchronized (CraterBackend.TOXICLAKE_LOCK) {
            for (int i = 0; i < numToxicLakes; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                // here w is the width, not the radius, so we divide by two
                float w = level_data.nextFloat();

                int id = this.toxicLakeCollection.addInstance();
                toxicLakes.add(new ToxicLake(id,x, y, w / 2));
            }
        }

        int numSpawners = level_data.nextInt();
        synchronized (CraterBackend.SPAWNER_LOCK) {
            for (int i = 0; i < numSpawners; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                float w = level_data.nextFloat();
                float h = level_data.nextFloat();
                String type = level_data.next();
                //how long a wave is
                long waveTime = level_data.nextLong();
                //the amount of spawns in a wave
                int numSpawns = level_data.nextInt();
                short[] spawnsPerSubWave = new short[numSpawns];
                long[] timesOfSubWave = new long[numSpawns];
                for (int j = 0;j<numSpawns;j++){
                    timesOfSubWave[j] = level_data.nextLong();
                    spawnsPerSubWave[j] = level_data.nextShort();
                }

                //long spawnTime = level_data.nextLong();
                long decayTime = level_data.nextInt();
                int hitPoints = level_data.nextInt();

                int instanceID = this.spawnerCollection.addInstance();

                switch (type) {
                    case "ENEMY_TYPE_1":
                        //spawners.add(new Enemy1Spawner(deltX, y, w, h, spawnTime, hitPoints));
                        spawners.add(new Enemy1Spawner(instanceID, x, y, w, h,spawnsPerSubWave,timesOfSubWave,waveTime, decayTime, hitPoints));
                        break;
                    case "ENEMY_TYPE_2":
                        //spawners.add(new Enemy2Spawner(deltX, y, w, h, spawnTime, hitPoints));
                        spawners.add(new Enemy2Spawner(instanceID,x, y, w, h,spawnsPerSubWave,timesOfSubWave,waveTime, decayTime, hitPoints));
                        break;
                    case "ENEMY_TYPE_3":
                        spawners.add(new Enemy3Spawner(instanceID,x, y, w, h,spawnsPerSubWave,timesOfSubWave,waveTime, decayTime, hitPoints));
                        //spawners.add(new Enemy3Spawner(deltX, y, w, h, spawnTime, hitPoints));
                        break;
                }
            }
        }

        int numPlateaus = level_data.nextInt();

        synchronized (CraterBackend.PLATEAU_LOCK) {
            for (int i = 0; i < numPlateaus; i++) {
                float x1 = level_data.nextFloat();
                float y1 = level_data.nextFloat();
                float x2 = level_data.nextFloat();
                float y2 = level_data.nextFloat();
                float x3 = level_data.nextFloat();
                float y3 = level_data.nextFloat();
                float x4 = level_data.nextFloat();
                float y4 = level_data.nextFloat();

                int instanceID = this.plateausCollection.addInstance();
                plateaus.add(new Plateau(instanceID,
                        x1, y1,
                        x2, y2,
                        x3, y3,
                        x4, y4
                ));
            }
        }

        int numNodes = level_data.nextInt();
        EnemyMap.Node[] nodes = new EnemyMap.Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            float x = level_data.nextFloat();
            float y = level_data.nextFloat();
            nodes[i] = new EnemyMap.Node(x, y);
        }

        int numConnections = level_data.nextInt();
        for (int i = 0; i < numConnections; i++) {
            int i1 = level_data.nextInt();
            int i2 = level_data.nextInt();
            float w = 0.1f;//HARD CODED
            nodes[i1].addNeighbour(nodes[i2], w);
            nodes[i2].addNeighbour(nodes[i1], w);
        }


        this.enemyMap = new EnemyMap(this.plateaus, this.toxicLakes, nodes);
        level_data.close();


        Log.d("GAMEMAP", "ENEMYMAP " + enemyMap);

    }

    /**
     * Kills all enemies; all enemies are removed from memory; reloads the game map
     */
    public void reset() {
        synchronized (CraterBackend.ENEMIES_LOCK) {
            this.enemiesCollection.clearInstanceData();
            this.enemies.clear();
        }
        synchronized (CraterBackend.SPAWNER_LOCK) {
            this.spawnerCollection.clearInstanceData();
            this.spawners.clear();
        }
        synchronized (CraterBackend.PLATEAU_LOCK) {
            this.plateausCollection.clearInstanceData();
            this.plateaus.clear();
        }
        synchronized (CraterBackend.TOXICLAKE_LOCK) {
            this.toxicLakeCollection.clearInstanceData();
            this.toxicLakes.clear();
        }
        synchronized (CraterBackend.SUPPLIES_LOCK) {
            this.suppliesVao.clearInstanceData();
            this.supplies.clear();
        }
        synchronized (CraterBackend.ANIMATIONS_LOCK) {
            this.animations.clear();
        }

        synchronized (CraterBackend.PLAYER_LOCK) {
            if (this.player != null) {
                this.player.setTranslate(this.spawnLocation[0], this.spawnLocation[1]);
                this.player.spawn();
                this.player.hideAngleAimer();
            }
        }
    }

    /** Gets the radius of the crater
     *
     * @return the radius of the crater
     */
    public float getCraterRadius() {
        return craterRadius;
    }

    /** Gets the player
     *
     * @return the player in the game
     */
    public Player getPlayer() {
        return player;
    }

    /** Gets a list of all alive enemies
     *
     * @return a list of all alive enemies
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /** Gets a list of all alive spawners
     *
     * @return list of all alive spawners
     *
     */
    public List<Spawner> getSpawners() {
        return spawners;
    }

    /** Gets all active plateaus
     *
     * @return all active plateaus
     */
    public List<Plateau> getPlateaus() {
        return plateaus;
    }

    /** Gets all active toxic lakes
     *
     * @return all active toxic lakes
     */
    public List<ToxicLake> getToxicLakes() {
        return toxicLakes;
    }

    /** Gts all alive supplies
     *
     * @return all alive supplies
     */
    public List<Supply> getSupplies() {
        return supplies;
    }

    /** All running animations
     *
     * @return all running animations
     */
    public List<Animation> getAnimations() {
        return animations;
    }

    /** Gets a map that tells how enemies should travel the game map
     *
     * @return a map that tells how enemies should travel the game map
     */
    public EnemyMap getEnemyMap() {
        return enemyMap;
    }

    /** Gets the vao of enemies vertex data
     *
     * @return enemies vertex data
     */
    public VaoCollection getEnemiesVao(){
        return this.enemiesCollection;
    }
    /** Gets the vao of Spawner vertex data
     *
     * @return spawner vertex data
     */
    public VaoCollection getSpawnerVao(){
        return this.spawnerCollection;
    }
    /** Gets the vao of supplies vertex data
     *
     * @return supplies vertex data
     */
    public VaoCollection getSuppliesVao(){
        return this.suppliesVao;
    }

    /** Nothing in a game map needs to respond to touch events, so this always returns false
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return Always returns false
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        return false;
    }
}
