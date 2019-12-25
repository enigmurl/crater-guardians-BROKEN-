package com.enigmadux.craterguardians;

import android.util.Log;

import java.util.ArrayList;

public class CraterBackendThread extends Thread {

    //the amount of update calls in a second
    private static final float UPDATE_RATE = 60;

    //whether the thread is running, if its ever set to false after started it will be finished
    private boolean running = false;

    //whether the thread is temporarily paused. It's still in the loop just not making calls to backend
    private boolean paused = false;

    //the backend object
    private CraterBackend backend;
    //the milliseconds of the last frame
    private long lastMillis;




    public CraterBackendThread(CraterBackend backend){
        super();
        this.backend = backend;

        this.lastMillis = System.currentTimeMillis();
    }


    /** Called whenever screen is changed, constantly calls update
     *
     */
    @Override
    public void run() {
        super.run();
        this.running = true;
        while (this.running){

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMillis < 1000/CraterBackendThread.UPDATE_RATE)
            try {
                sleep((long) (1000/CraterBackendThread.UPDATE_RATE) - (currentTime - lastMillis));
            } catch (InterruptedException e){
                //pass
            }

            if (! paused) {
                this.backend.update(System.currentTimeMillis() - lastMillis);//todo update this value
            }

            //Log.d("BACKEND_THREAD","framerate: " + (1000/( System.currentTimeMillis() - lastMillis)));


            this.lastMillis = System.currentTimeMillis();

        }
    }

    /** This is the overall running, if running is ever set to false after the thread starts.
     * If you want to temporarly stop the thread, use setPause
     *
     * @param running the new running state
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /** If you want to temporarily pause the game use this
     *
     * @param pausedState whether to pause the thread or not
     */
    public void setPause(boolean pausedState){
        this.paused = pausedState;
    }
}
