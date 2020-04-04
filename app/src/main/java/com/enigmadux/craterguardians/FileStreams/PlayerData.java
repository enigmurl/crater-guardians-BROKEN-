package com.enigmadux.craterguardians.FileStreams;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.GUILib.MatieralBar;
import com.enigmadux.craterguardians.players.Kaiser;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.players.Ryze;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class PlayerData {
    //all character classes
    public static final Player[] CHARACTERS = new Player[] {new Kaiser(0,0),new Ryze(0,0)};

    //the path to the player levels + xp file
    private static final String PLAYER_DATA = "player_data";


    //the amount of experience the player has
    private static int experience;

    //context used for resource opening
    private Context context;

    /** Default Constructor
     *
     * @param context any context that can provide access to files
     */
    public PlayerData(Context context){
        this.context = context;
    }



    /** Writes player data to a file. Note the actual classes are hard coded, so anytime a player is added it must also be added to this
     *
     */
    public synchronized void writePlayerData() {
        try {
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(this.context.openFileOutput (PlayerData.PLAYER_DATA, Context.MODE_PRIVATE)));

            stdout.println(PlayerData.experience);
            stdout.println(PlayerData.CHARACTERS.length);
            for (Player playerClass: PlayerData.CHARACTERS){
                String className = String.valueOf(playerClass.getClass()).split(" ")[1];

                stdout.print(className + " ");
                stdout.println(playerClass.getPlayerLevel());
            }
            stdout.close();
            Log.d("BACKEND","wrote player data");

        } catch (IOException e){
            Log.e("BACKEND","File write failed",e);
        }
    }


    /** Loads data about the player from a file,
     * todo it may be possibly be bettter to hardcode this as opposed to using the deprecated newInstance method
     *
     *
     */
    public synchronized void loadPlayerData() {
        try (Scanner stdin = new Scanner(this.context.openFileInput(PlayerData.PLAYER_DATA))) {
            PlayerData.experience = stdin.nextInt();
            int numLines = stdin.nextInt();
            for (int i = 0;i<numLines;i++){
                Class cls = Class.forName(stdin.next());
                int level = stdin.nextInt();

                Player player = (Player) cls.newInstance();
                player.setPlayerLevel(level);
            }

        } catch (IOException e) {
            Log.e("FRONTEND", "Error loading player data file ", e);
            this.writePlayerData();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e){
            Log.e("FRONTEND","PlayerData file read failed, most likely corrupted file structure",e);
            this.writePlayerData();
        }
    }


    /** Updates experience and writes the file
     *
     * @param experience the updated amount of experience
     */
    public void updateXP(int experience){
        PlayerData.experience = experience;
        MatieralBar.update();
        this.writePlayerData();
    }

    /** Gets the experience of this player
     *
     * @return the amount of experience the user has
     */
    public static int getExperience(){
        return PlayerData.experience;
    }
}
