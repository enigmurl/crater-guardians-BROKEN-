package com.enigmadux.craterguardians;


import android.os.Bundle;
import android.util.Log;

import com.enigmadux.craterguardians.util.SoundLib;

import enigmadux2d.core.EnigmaduxActivity;

/** The Main Activity for this app. It's the entry point to the app.
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class CraterActivity extends EnigmaduxActivity {
    /** Entry point to the app. The Surface view is set up here.
     *
     * @param savedInstanceState  Data that can be used to restore the activity to it's previous state. Should only really be used for super
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            // Create a GLSurfaceView instance and set it
            // as the ContentView for this Activity
            enigmaduxGLSurfaceView = new CraterGLSurfaceView(this);
            setContentView(enigmaduxGLSurfaceView);
        } catch (Exception e){
            Log.d("Exception","On Create Failed:" + e);
        }


    }

    /** on pause
     *
     */
    @Override
    public void onPause(){
        try {
            super.onPause();
            this.enigmaduxGLSurfaceView.onPause();
            SoundLib.pauseAllMedia();
        } catch (Exception e){
            Log.d("Exception","Pause Failed",e);
        }
    }


    /** on resume
     *
     */
    @Override
    public void onResume(){
        try {
            super.onResume();
            this.enigmaduxGLSurfaceView.onResume();
            SoundLib.resumeAllMedia();
        } catch (Exception e){
            Log.d("Exception","Resume Failed",e);
        }
    }
    /** Loads all media
     *
     */
    @Override
    public void onStart(){
        try {
            super.onStart();
            this.enigmaduxGLSurfaceView.onStart();
        } catch (Exception e){
            Log.d("Exception","Start Failed",e);
        }
    }

    /** called whenever app is exited, but still in memory
     *
     */
    @Override
    public void onStop(){
        try {
            super.onStop();
            this.enigmaduxGLSurfaceView.onStop();
        } catch (Exception e){
            Log.d("Exception","Stop Failed",e);
        }
//        Log.d("CRATER","Stopping Activity");
    }


    @Override
    protected void onDestroy() {
        try {
            SoundLib.stopAllMedia();
            this.enigmaduxGLSurfaceView.onDestroy();
            super.onDestroy();
        } catch (Exception e){
            Log.d("Exception","Destroy Failed",e);
        }
    }

}