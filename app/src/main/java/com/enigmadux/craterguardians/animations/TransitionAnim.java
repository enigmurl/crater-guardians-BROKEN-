package com.enigmadux.craterguardians.animations;

import android.os.Handler;
import android.os.Looper;

/** Animations that modify components for some time
 *
 */
public abstract class TransitionAnim implements Runnable {

    //a shared handler that does the delaying
    protected static final Handler HANDLER = new Handler(Looper.getMainLooper());




}
