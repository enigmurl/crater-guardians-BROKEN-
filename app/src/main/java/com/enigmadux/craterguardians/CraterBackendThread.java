package com.enigmadux.craterguardians;

import android.util.Log;

import com.enigmadux.craterguardians.animations.TransitionAnim;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;
import java.util.List;

public class CraterBackendThread extends Thread {

    //the amount of update calls in a second
    private static final float UPDATE_RATE = 100;

    //whether the thread is running, if its ever set to false after started it will be finished
    private boolean running = false;

    private boolean appPaused = false;
    //whether the thread is temporarily paused. It's still in the loop just not making calls to backend
    private boolean gamePaused = false;



    //the backend object
    private World backend;
    //the milliseconds of the last frame
    private long lastMillis;


    /** Default constructor
     *
     * @param backend backend object, it's ok if it's null so long as before it starts updating it's set
     */
    public CraterBackendThread(World backend){
        super();
        this.backend = backend;

        this.lastMillis = System.currentTimeMillis();
        if (backend!= null && backend.getEnemyMap() != null) {
            backend.setEnemyMap(new EnemyMap(backend.getEnemyMap()));
        }
    }

    /** Sets the backend incase the argument was null in the construcotr
     *
     * @param backend backend object
     */
    public void setBackend(World backend) {
        this.backend = backend;
        if (backend!= null && backend.getEnemyMap() != null) {
            backend.setEnemyMap(new EnemyMap(backend.getEnemyMap()));
        }
    }

    /** Called whenever screen is changed, constantly calls update
     *
     */
    @Override
    public void run() {
        super.run();
        this.running = true;

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

            if (! appPaused) {
                TransitionAnim.updateAnims(System.currentTimeMillis() - lastMillis);
                if (!gamePaused) {
                    TransitionAnim.updateGameAnims(System.currentTimeMillis() - lastMillis);
                    this.backend.update(System.currentTimeMillis() - lastMillis);//todo update this value
                }
            }
            if (System.currentTimeMillis() - lastMillis  >  1000/60f){
                under60++;
                under60s.add((long) (1000f/(System.currentTimeMillis() - lastMillis)));
            }

            this.lastMillis = System.currentTimeMillis();

            if (System.currentTimeMillis() - debugStartMillis > 10000){
                Log.d("BACKENDTHREAD:","Frames per second:"  + (1000 * updateCount/(double) (System.currentTimeMillis() - debugStartMillis)));
                Log.d("BACKENDTHREAD:","percentage under 60:"  + ((float) under60/updateCount));
                Log.d("BACKENDTHREAD:","under 60s:"  + under60s);

                debugStartMillis = System.currentTimeMillis();
                updateCount = 0;
                under60 = 0;
                under60s.clear();
            }

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
    public void setGamePaused(boolean pausedState){
        Log.d("Backend Thread","Setting Paused State: "+ pausedState);
        this.gamePaused = pausedState;
        if (! pausedState){
            this.setAppPaused(false);
        }
        if (backend!= null && backend.getEnemyMap() != null) this.backend.getEnemyMap().setPaused(gamePaused);
    }

    public void setAppPaused(boolean pauseState){
        Log.d("Backend Thread","Setting APP Paused State: "+ pauseState);
        this.appPaused = pauseState;
        if (pauseState){
            this.setGamePaused(true);
        }
    }

    /** Gets the backend object that this thread updates
     *
     * @return the Backend object
     */
    public World getBackend(){
        return this.backend;
    }
}
