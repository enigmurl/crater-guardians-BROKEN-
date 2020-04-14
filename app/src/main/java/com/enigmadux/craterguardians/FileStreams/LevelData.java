package com.enigmadux.craterguardians.FileStreams;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.GameMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LevelData {
    //the path to the level file containing the data
    private static final String LEVEL_FILE_PATH = "level_data";

    //which levels have been completed
    private static boolean[] completedLevels = new boolean[GameMap.NUM_LEVELS];
    //which levels are unlocked
    private static boolean[] unlockedLevels = new boolean[GameMap.NUM_LEVELS];


    private Context context;

    /** Default Constructor
     *
     * @param context any context that can access files
     */
    public LevelData(Context context){
        this.context = context;
    }

    /** Loads the data from level
     *
     */
    public void loadLevelData(){
        try (Scanner stdin = new Scanner(this.context.openFileInput(LevelData.LEVEL_FILE_PATH))) {
            for (int i =0;i<GameMap.NUM_LEVELS;i++){
                LevelData.unlockedLevels[i] = stdin.nextBoolean();
                LevelData.completedLevels[i] = stdin.nextBoolean();
            }
        } catch (FileNotFoundException e){
            Log.e("LEVEL DATA","Error loading data file " ,e);
            this.writeLevelFiles();
        } catch (NoSuchElementException e){
            Log.e("LEVEL DATA","Incorrect file format " ,e);
            this.writeLevelFiles();
        }

    }


    /** If the first time loading the game, create all the level files
     *
     */
    public void writeLevelFiles(){
        LevelData.unlockedLevels[0] = true;
        try {
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(this.context.openFileOutput (LevelData.LEVEL_FILE_PATH, Context.MODE_PRIVATE)));

            for (int i = 0;i<GameMap.NUM_LEVELS;i++){
                stdout.print(LevelData.unlockedLevels[i] + " ");
                stdout.println(LevelData.completedLevels[i] + " ");

                //making sure there is always one available level
                if (LevelData.completedLevels[i] && i < GameMap.NUM_LEVELS-1){
                    Log.d("LEVEL DATA","Unlocked level:" + (i+2));
                    LevelData.unlockedLevels[i+1] = true;
                }
            }
            stdout.close();

        } catch (IOException e){
            Log.e("LEVEL DATA","File write failed",e);
        }

        Log.d("LEVEL DATA","completedLEVELs: " + Arrays.toString(LevelData.completedLevels));

    }

    /** Gets which levels are completed
     *
     * @return a boolean[] of true or false, true means the level number is completed
     */
    public static boolean[] getCompletedLevels() {
        return completedLevels;
    }

    /** Gets which levels are unlocked
     *
     * @return which levels are unlocked, but may not have been completed
     */
    public static boolean[] getUnlockedLevels() {
        return unlockedLevels;
    }
}
