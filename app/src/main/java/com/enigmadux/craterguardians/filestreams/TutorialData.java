package com.enigmadux.craterguardians.filestreams;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class TutorialData {
    //the path to the settings file
    private static final String TUTORIAL = "tutorial";

    public static boolean TUTORIAL_ENABLED;

    //context used for resource opening
    private Context context;

    /** Default constructor
     *
     * @param context any non null context that can access files
     */
    public TutorialData(Context context){
        this.context = context;
    }

    /** Writes the data from SoundLib settings (play music/sound effects) into the settings file
     *
     */
    public void writeTutorialFile(){
        try {
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(this.context.openFileOutput (TutorialData.TUTORIAL, Context.MODE_PRIVATE)));

            //if the file exists its always false
            stdout.println(false);

            stdout.println();
            stdout.close();

        } catch (IOException e){
//            Log.d("BACKEND","File write failed",e);
        }
    }


    /** Loads the data from the settings file into the SoundLib settings (play music/sound effects)
     *
     */
    public void loadTutorialFile(){
        try (Scanner stdin = new Scanner(this.context.openFileInput(TutorialData.TUTORIAL))) {
            TUTORIAL_ENABLED = stdin.nextBoolean();
        } catch (Exception e) {
//            Log.d("FRONTEND", "Error loading tutorial file ", e);
            this.writeTutorialFile();
            TUTORIAL_ENABLED = true;
        }
    }



}
