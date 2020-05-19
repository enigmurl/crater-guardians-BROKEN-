package com.enigmadux.craterguardians.animations;

import android.media.MediaPlayer;

public class SoundFadeOut extends FrameTransitionAnim {
    public static final long DEFAULT_MILLIS = 60;
    private MediaPlayer mediaPlayer;
    public SoundFadeOut(long millis, MediaPlayer mediaPlayer) {
        super(millis);
        this.mediaPlayer = mediaPlayer;
        start();
    }

    @Override
    void step() {
        this.mediaPlayer.setVolume(1 - (float) finishedMillis/totalMillis,1 - (float) finishedMillis/totalMillis);
    }

    @Override
    void finish() {
        super.finish();
        mediaPlayer.setVolume(0,0);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        mediaPlayer.setVolume(1,1);
    }

}
