package com.enigmadux.craterguardians;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.enemies.Enemy;
import com.enigmadux.craterguardians.filestreams.LevelData;
import com.enigmadux.craterguardians.gameobjects.Plateau;
import com.enigmadux.craterguardians.gameobjects.Supply;
import com.enigmadux.craterguardians.gameobjects.ToxicLake;
import com.enigmadux.craterguardians.spawners.Enemy1Spawner;
import com.enigmadux.craterguardians.spawners.Enemy2Spawner;
import com.enigmadux.craterguardians.spawners.Enemy3Spawner;
import com.enigmadux.craterguardians.spawners.Spawner;
import com.enigmadux.craterguardians.gamelib.CraterCollection;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.Scanner;

import enigmadux2d.core.quadRendering.QuadTexture;

public class GameMap {
    //the amount of levels in the game
    public static final int NUM_LEVELS = 20;


    //because there is stuff a little outside the actual crater, we draw it slightly bigger
    public static final float CRATER_VISUAL_SCALE = 1.85f;



    //radius of the crater
    private float craterRadius;
    //spawn location
    private float[] spawnLocation = new float[2];
    //the current player on the map
    private Player player;


    //a map of where the enemy should go
    private EnemyMap enemyMap;

    //used to write what levels are unlocked/cleared
    private LevelData levelData;
    //used to open resources
    private Context context;


    //renders the supplies
    private QuadTexture craterVisual;
    private CraterCollection<Supply> suppliesVao;
    private CraterCollection<ToxicLake> toxicLakeCollection;
    private CraterCollection<Spawner> spawnerCollection;
    private CraterCollection<Plateau> plateausCollection;
    /** Creates a GameMap Instance
     *
     * @param context any non null context that can acess resources
     */
    public GameMap(Context context,
                   QuadTexture craterVisual,
                   CraterCollection<Supply> suppliesCollection,
                   CraterCollection<ToxicLake> toxicLakeCollection,
                   CraterCollection<Spawner> spawnerCollection,
                   CraterCollection<Plateau> plateausCollection) {
        this.context = context;

        this.levelData = new LevelData(context);

        this.craterVisual = craterVisual;
        this.suppliesVao = suppliesCollection;
        this.toxicLakeCollection = toxicLakeCollection;
        this.spawnerCollection = spawnerCollection;
        this.plateausCollection = plateausCollection;

    }


    /** The current player object
     *
     * @param player the current player object e.g. Ryze
     */
    public void setPlayer(Player player){
        this.player = player;
    }





    /**
     * Initializes a level, tries to do in same order as drawing as too make it best aligned for the viewer
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
        craterRadius = level_data.nextFloat();


        synchronized (World.supplyLock) {
            int numSupplies = level_data.nextInt();
            for (int i = 0; i < numSupplies; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                float r = level_data.nextFloat();
                int health = level_data.nextInt();

                int id = this.suppliesVao.createVertexInstance();
                suppliesVao.addInstance(new Supply(context,x, y, r, health,id));
            }
        }


        int numToxicLakes = level_data.nextInt();
        synchronized (World.toxicLakeLock) {
            for (int i = 0; i < numToxicLakes; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                // here w is the width, not the radius, so we divide by two
                float w = level_data.nextFloat();

                int id = this.toxicLakeCollection.createVertexInstance();
                toxicLakeCollection.addInstance(new ToxicLake(id,x, y, w / 2));
            }
        }

        int numSpawners = level_data.nextInt();
        synchronized (World.spawnerLock) {
            for (int i = 0; i < numSpawners; i++) {
                Log.d("SPAWNER","Iteration " + i);
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                float w = level_data.nextFloat();
                float h = level_data.nextFloat();
                String type = level_data.next();
                long blue1 = level_data.nextLong();
                long orange = level_data.nextLong();
                long blue2 = level_data.nextLong();

                int maxHealth = level_data.nextInt();
                int endBlue1Health = level_data.nextInt();
                int endOrangeHealth = level_data.nextInt();

                int numOrangeWaves = level_data.nextInt();
                short[] numOrangeSpawns = new short[numOrangeWaves];
                long[] orangeSpawnTimes = new long[numOrangeWaves];
                for (int j=0;j<numOrangeWaves;j++){
                    numOrangeSpawns[j] = level_data.nextShort();
                    orangeSpawnTimes[j] = level_data.nextLong();
                }

                int numBlueWaves = level_data.nextInt();
                short[] numBlueSpawns = new short[numBlueWaves];
                long[] blueSpawnTimes = new long[numBlueWaves];
                for (int j=0;j<numBlueWaves;j++){
                    numBlueSpawns[j] = level_data.nextShort();
                    blueSpawnTimes[j] = level_data.nextLong();
                }





                int instanceID = this.spawnerCollection.createVertexInstance();
                int strength = Enemy.STRENGTHS[levelNum];

                switch (type) {
                    case "ENEMY_TYPE_1":
                        spawnerCollection.addInstance(new Enemy1Spawner(context,instanceID, x, y, w, h,endOrangeHealth,endBlue1Health,maxHealth,blue1,orange,blue2,
                                numBlueSpawns,blueSpawnTimes,numOrangeSpawns,orangeSpawnTimes,strength));
                        break;
                    case "ENEMY_TYPE_2":
                        spawnerCollection.addInstance(new Enemy2Spawner(context,instanceID, x, y, w, h,endOrangeHealth,endBlue1Health,maxHealth,blue1,orange,blue2,
                                numBlueSpawns,blueSpawnTimes,numOrangeSpawns,orangeSpawnTimes,strength));
                        break;
                    case "ENEMY_TYPE_3":
                        spawnerCollection.addInstance(new Enemy3Spawner(context,instanceID, x, y, w, h,endOrangeHealth,endBlue1Health,maxHealth,blue1,orange,blue2,
                                numBlueSpawns,blueSpawnTimes,numOrangeSpawns,orangeSpawnTimes,strength));
                        break;
                    default:
                        Log.d("GAME MAP","Level: " + levelNum + " incorrect spawner Type of " + type);
                }
            }
        }

        int numPlateaus = level_data.nextInt();

        synchronized (World.plateauLock) {
            for (int i = 0; i < numPlateaus; i++) {
                float x1 = level_data.nextFloat();
                float y1 = level_data.nextFloat();
                float x2 = level_data.nextFloat();
                float y2 = level_data.nextFloat();
                float x3 = level_data.nextFloat();
                float y3 = level_data.nextFloat();
                float x4 = level_data.nextFloat();
                float y4 = level_data.nextFloat();

                int instanceID = this.plateausCollection.createVertexInstance();
                plateausCollection.addInstance(new Plateau(instanceID,
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
            float r = level_data.nextFloat();
            nodes[i] = new EnemyMap.Node(x, y,r);
        }

        int numConnections = level_data.nextInt();
        for (int i = 0; i < numConnections; i++) {
            int i1 = level_data.nextInt();
            int i2 = level_data.nextInt();
            float w = level_data.nextFloat();//HARD CODED
            nodes[i1].addNeighbour(nodes[i2], w);
            nodes[i2].addNeighbour(nodes[i1], w);
        }


        this.enemyMap = new EnemyMap(this.plateausCollection.getInstanceData(), this.toxicLakeCollection.getInstanceData(), nodes);
        //this.enemyMap.start();
        level_data.close();

        this.craterVisual.setTransform(0,0,this.craterRadius * 2 * CRATER_VISUAL_SCALE,this.craterRadius * 2 * CRATER_VISUAL_SCALE);

        Log.d("GAMEMAP:","finished loading level");
        this.player.setTranslate(spawnLocation[0], spawnLocation[1]);


        //Log.d("GAMEMAP", "ENEMYMAP " + enemyMap);

    }



    public EnemyMap getEnemyMap(){
        return this.enemyMap;
    }
    public void setEnemyMap(EnemyMap enemyMap){
        this.enemyMap = enemyMap;
    }

    public float getSpawnLocX(){
        return this.spawnLocation[0];
    }

    public float getSpawnLocY(){
        return this.spawnLocation[1];
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

}
