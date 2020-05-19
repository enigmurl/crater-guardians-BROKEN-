package com.enigmadux.craterguardians;


import android.os.Bundle;
import android.util.Log;

import com.enigmadux.craterguardians.util.SoundLib;

import enigmadux2d.core.EnigmaduxActivity;
import enigmadux2d.core.quadRendering.QuadTexture;

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
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        enigmaduxGLSurfaceView = new CraterGLSurfaceView(this);
        setContentView(enigmaduxGLSurfaceView);


    }

    /** on pause
     *
     */
    @Override
    public void onPause(){
        super.onPause();
        this.enigmaduxGLSurfaceView.onPause();
        SoundLib.pauseAllMedia();
    }


    /** on resume
     *
     */
    @Override
    public void onResume(){
        super.onResume();
        this.enigmaduxGLSurfaceView.onResume();
        SoundLib.resumeAllMedia();
    }
    /** Loads all media
     *
     */
    @Override
    public void onStart(){
        super.onStart();
        this.enigmaduxGLSurfaceView.onStart();

    }

    /** called whenever app is exited, but still in memory
     *
     */
    @Override
    public void onStop(){
        super.onStop();
        this.enigmaduxGLSurfaceView.onStop();
        Log.d("CRATER","Stopping Activity");
    }


    @Override
    protected void onDestroy() {
        SoundLib.stopAllMedia();
        this.enigmaduxGLSurfaceView.onDestroy();
        super.onDestroy();
    }

}