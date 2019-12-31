package com.enigmadux.craterguardians;

import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CraterBackendThread extends Thread {

    //the amount of update calls in a second
    private static final float UPDATE_RATE = 100;

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

        //Debug.startMethodTracing("backend");

        long debugStartMillis = System.currentTimeMillis();

        int updateCount = 0;
        int under60 = 0;
        List<Long> under60s = new ArrayList<Long>();

        while (this.running){
            updateCount++;



            long currentTime = System.currentTimeMillis();



            if (currentTime - lastMillis < 1000/CraterBackendThread.UPDATE_RATE) {
                try {
                    sleep((long) (1000/CraterBackendThread.UPDATE_RATE) - (currentTime - lastMillis));
                } catch (InterruptedException e){
                    //pass
                }
            }


            if (! paused) {
                this.backend.update(System.currentTimeMillis() - lastMillis);//todo update this value
            }
            if (System.currentTimeMillis() - lastMillis  >  1000/60f){
                under60++;
                under60s.add((long) (1000f/(System.currentTimeMillis() - lastMillis)));
            }
            //Log.d("BACKEND_THREAD","framerate: " + (1000/( System.currentTimeMillis() - lastMillis)));


            this.lastMillis = System.currentTimeMillis();

            if (System.currentTimeMillis() - debugStartMillis > 10000){
                Log.d("BACKENDTHREAD:","Frames per second:"  + (1000 * updateCount/(double) (System.currentTimeMillis() - debugStartMillis)));
                Log.d("BACKENDTHREAD:","percentage under 60:"  + ((float) under60/updateCount));
                Log.d("BACKENDTHREAD:","under 60s:"  + under60s);

                debugStartMillis = System.currentTimeMillis();
                updateCount = 0;
                under60 = 0;
                under60s.clear();
                //Debug.stopMethodTracing();
                //Debug.startMethodTracing("backend");

            }

        }
        //Debug.stopMethodTracing();


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
