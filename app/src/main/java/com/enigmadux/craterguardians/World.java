package com.enigmadux.craterguardians;

import com.enigmadux.craterguardians.Attacks.Attack;
import com.enigmadux.craterguardians.GameObjects.Plateau;
import com.enigmadux.craterguardians.GameObjects.Supply;
import com.enigmadux.craterguardians.GameObjects.ToxicLake;
import com.enigmadux.craterguardians.Spawners.Spawner;
import com.enigmadux.craterguardians.enemies_v1.Enemy;
import com.enigmadux.craterguardians.gameLib.CraterCollection;
import com.enigmadux.craterguardians.players.Player;

import java.util.ArrayList;
import java.util.List;

import enigmadux2d.core.gameObjects.VaoCollection;

/** Upper level class that contains data about the game state
 *
 * @author Manu Bhat
 * @version BETA
 */
public class World {
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
    private ArrayList<Attack> enemyAttacks;

    /** List of all active attacks from the player
     *
     */
    private ArrayList<Attack> playerAttacks;

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



    public World(){
        this.blueEnemies = new CraterCollection<>(0,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.orangeEnemies = new CraterCollection<>(0,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.toxicLakes = new CraterCollection<>(0,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.plateaus = new CraterCollection<>(0,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.spawners = new CraterCollection<>(0,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);
        this.supplies = new CraterCollection<>(0,World.QUAD_VERTICES,World.QUAD_TEXTURE_CORDS,World.QUAD_INDICES);

        this.enemyAttacks = new ArrayList<>();
        this.playerAttacks = new ArrayList<>();
    }


    public void update(){

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

    public ArrayList<Attack> getEnemyAttacks() {
        return enemyAttacks;
    }

    public ArrayList<Attack> getPlayerAttacks() {
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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
