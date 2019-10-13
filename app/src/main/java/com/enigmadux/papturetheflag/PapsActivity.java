package com.enigmadux.papturetheflag;


import android.os.Bundle;

import enigmadux2d.core.EnigmaduxActivity;

/** The Main Activity for this app. It's the entry point to the app.
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class PapsActivity extends EnigmaduxActivity {
    /** Entry point to the app. The Surface view is set up here.
     *
     * @param savedInstanceState  Data that can be used to restore the activity to it's previous state. Should only really be used for super
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        enigmaduxGLSurfaceView = new PapsGLSurfaceView(this);
        setContentView(enigmaduxGLSurfaceView);

    }

}