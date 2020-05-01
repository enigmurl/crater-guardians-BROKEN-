package com.enigmadux.craterguardians.animations;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

/** Animations that modify components for some time
 *
 */
public abstract class TransitionAnim implements Cancelable {

    //a shared handler that does the delaying for ui animations
    protected static final EventHandler HANDLER = new EventHandler();
    //game animations
    protected static final EventHandler GAME_HANDLER = new EventHandler();


    public static void updateAnims(long dt){
        HANDLER.update(dt);
    }
    public static void updateGameAnims(long dt){
        GAME_HANDLER.update(dt);
    }
    public static void clear(){
        GAME_HANDLER.clear();
        HANDLER.clear();
    }


    public static class EventHandler {
        private PriorityQueue<Event> currentEvents;
        private ArrayList<Event> allEvents;
        private static final Object LOCK = new Object();

        public EventHandler(){
            currentEvents = new PriorityQueue<>();
            allEvents = new ArrayList<>();
        }
        public void clear(){
            synchronized (LOCK) {
                for (Event e:currentEvents){
                    e.r.cancel();
                }
                allEvents.clear();
                currentEvents.clear();
            }
        }

        public void postDelayed(Cancelable r,long millis){
            synchronized (LOCK) {
                Event e = new Event(r, millis);
                currentEvents.add(e);
                allEvents.add(e);
            }
        }

        public void update(long dt){
            synchronized (LOCK) {
                for (int i = 0; i < allEvents.size(); i++) {
                    allEvents.get(i).millis -= dt;
                }

                while (currentEvents.size() > 0 && currentEvents.peek().millis <= 0) {
                    Event e = currentEvents.poll();
                    allEvents.remove(e);
                    e.r.run();
                    if (e.r instanceof FrameTransitionAnim) {
                        ((FrameTransitionAnim) e.r).setDeltaTime(e.orgMillis - e.millis);
                    }
                }
            }
        }

    }
    public static class Event implements Comparable<Event> {
        Cancelable r;
        long millis;
        long orgMillis;
        Event(Cancelable r,long millis){
            this.r = r;
            this.millis = orgMillis = millis;
        }

        @Override
        public int compareTo(Event o) {
            return (int) (millis - o.millis);
        }
    }




}
