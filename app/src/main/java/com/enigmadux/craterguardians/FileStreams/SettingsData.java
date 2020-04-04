package com.enigmadux.craterguardians.FileStreams;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.util.SoundLib;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class SettingsData {
    //the path to the settings file
    private static final String SETTINGS = "settings";

    //context used for resource opening
    private Context context;

    /** Default constructor
     *
     * @param context any non null context that can access files
     */
    public SettingsData(Context context){
        this.context = context;
    }

    /** Writes the data from SoundLib settings (play music/sound effects) into the settings file
     *
     */
    public void writeSettingsFile(){
        try {
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(this.context.openFileOutput (SettingsData.SETTINGS, Context.MODE_PRIVATE)));

            //first do the music on or off
            stdout.println(SoundLib.isPlayMusic());
            //then do the sound effects on or off
            stdout.print(SoundLib.isPlaySoundEffects());


            stdout.println();
            stdout.close();

        } catch (IOException e){
            Log.d("BACKEND","File write failed",e);
        }
    }


    /** Loads the data from the settings file into the SoundLib settings (play music/sound effects)
     *
     */
    public void loadSettingsFile(){
        try (Scanner stdin = new Scanner(this.context.openFileInput(SettingsData.SETTINGS))) {

            //first get music on or off
            if (stdin.nextBoolean()){
                Log.d("SOUND LIB:","settings setting MUSIC STATE: " + true);
                SoundLib.unMuteAllMedia();
            } else {
                Log.d("SOUND LIB:","settings setting MUSIC STATE: " + false );
                SoundLib.muteAllMedia();
            }

            //second get sound effects on or off
            SoundLib.setPlaySoundEffects(stdin.nextBoolean());

        } catch (Exception e) {
            Log.d("FRONTEND", "Error loading settings file ", e);
            this.writeSettingsFile();
        }
    }



}
