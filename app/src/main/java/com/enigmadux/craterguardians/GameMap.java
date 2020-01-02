package com.enigmadux.craterguardians;

import android.content.Context;
import android.util.Log;

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

public class GameMap {
    /*the amount of levels in the game*/
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

    public GameMap(Context context) {
        this.context = context;

        this.levelData = new LevelData(context);

    }


    /**
     * Initializes a level
     */
    public void loadLevel(int levelNum) {
        this.levelData.writeLevelFiles();

        this.reset();

        int fileName;
        switch (levelNum) {
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
            case 20:
                fileName = R.raw.level_20;
                break;
            default:
                fileName = R.raw.level_tutorial;
                //this.levelNum = 0;
        }

        Scanner level_data = new Scanner(context.getResources().openRawResource(fileName));

        spawnLocation[0] = level_data.nextFloat();
        spawnLocation[1] = level_data.nextFloat();
        this.player.setTranslate(spawnLocation[0], spawnLocation[1]);


        craterRadius = level_data.nextFloat();



        synchronized (CraterBackend.SUPPLIES_LOCK) {
            int numSupplies = level_data.nextInt();
            for (int i = 0; i < numSupplies; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                float r = level_data.nextFloat();
                int health = level_data.nextInt();

                supplies.add(new Supply(x, y, r, health));
            }
        }


        int numToxicLakes = level_data.nextInt();
        synchronized (CraterBackend.TOXICLAKE_LOCK) {
            for (int i = 0; i < numToxicLakes; i++) {
                float x = level_data.nextFloat();
                float y = level_data.nextFloat();
                // here w is the width, not the radius, so we divide by two
                float w = level_data.nextFloat();


                toxicLakes.add(new ToxicLake(x, y, w / 2));
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
                long spawnTime = level_data.nextLong();
                int hitPoints = level_data.nextInt();

                switch (type) {
                    case "ENEMY_TYPE_1":
                        spawners.add(new Enemy1Spawner(x, y, w, h, spawnTime, hitPoints));
                        break;
                    case "ENEMY_TYPE_2":
                        spawners.add(new Enemy2Spawner(x, y, w, h, spawnTime, hitPoints));
                        break;
                    case "ENEMY_TYPE_3":
                        spawners.add(new Enemy3Spawner(x, y, w, h, spawnTime, hitPoints));
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

                plateaus.add(new Plateau(
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
    private void reset() {
        synchronized (CraterBackend.ENEMIES_LOCK) {
            this.enemies.clear();
        }
        synchronized (CraterBackend.SPAWNER_LOCK) {
            this.spawners.clear();
        }
        synchronized (CraterBackend.PLATEAU_LOCK) {
            this.plateaus.clear();
        }
        synchronized (CraterBackend.TOXICLAKE_LOCK) {
            this.toxicLakes.clear();
        }
        synchronized (CraterBackend.SUPPLIES_LOCK) {
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

}
