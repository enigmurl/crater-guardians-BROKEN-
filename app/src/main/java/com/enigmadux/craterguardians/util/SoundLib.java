package com.enigmadux.craterguardians.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.animations.SoundFadeOut;

/** All songs and sound effect sare played here
 *
 * TODO: making it all static is not efficient usage of memory bc all the static variables will never be de assigned
 *
 * @author Manu Bhat
 * @version BETA
 */
public class SoundLib {
    /** music played when in the lobby */
    private static MediaPlayer lobbyMusic;
    /** music played when in game */
    private static MediaPlayer gameMusic;
    /** music played after a victory */
    private static MediaPlayer victoryMusic;
    /** music played after a loss */
    private static MediaPlayer lossMusic;


    /** sound effect played when a player kills*/
    private static MediaPlayer playerKillSoundEffect;
    /** sound effect played when a player shoots an attack*/
    private static MediaPlayer kaiserShoot;
    private static MediaPlayer skippyShoot;
    private static MediaPlayer fissionShoot;
    private static MediaPlayer flamethrowerShoot;
    private static MediaPlayer magnumShoot;

    /** sound effect played a player is killed*/
    private static MediaPlayer playerDeathSoundEffect;
    /** sound effect played when an enemy shoots */
    private static MediaPlayer supplyDamagedSoundEffect;
    /** sound effect played when the shield is spawned*/
    private static MediaPlayer playerSpawnShieldSoundEffect;
    /** sound effect played when an enemy attack is blocked by the shield*/
    private static MediaPlayer playerShieldBlockSoundEffect;
    /** sound effect played when the player is reloading */
    private static MediaPlayer reloadingSoundEffect;
    /** sound effect played when a button is selected */
    private static MediaPlayer buttonSelectedSoundEffect;
    /** sound effect played when the enemies hit the player*/
    private static MediaPlayer playerDamagedSoundEffect;
    /** sound effect played when the enemies destroy a supply*/
    private static MediaPlayer supplyDeathSoundEffect;
    /** sound effect played when a spawner is killed*/
    private static MediaPlayer spawnerDeathSoundEffect;
    /** sound effect played during the evolving of a character, it should be the approximate length*/
    private static MediaPlayer evolvingSoundEffect;
    /** sound effect played when xp is gained and that counter thing is shown */
    private static MediaPlayer xpCounterSoundEffect;
    private static MediaPlayer levelSelectTickEffect;

    /** whether or not the player has the music on*/
    private static boolean playMusic = true;
    /** whether or not the player has the sound effects on*/
    private static boolean playSoundEffects = true;

    private static int numBlaze = 0;

    private static SoundFadeOut flameFadeOut;

    private static SoundPool soundEffects;

    /** This binds the raw music to the music and the sound effects
     *
     * @param context any non null Context, that's used to access resources
     */
    public static void loadMedia(Context context){
        if (! SoundLib.playMusic) return;


        SoundLib.lobbyMusic = MediaPlayer.create(context, R.raw.lobby_music);
        SoundLib.lobbyMusic.setLooping(true);

        SoundLib.gameMusic = MediaPlayer.create(context,R.raw.game_music);
        SoundLib.gameMusic.setLooping(true);

        SoundLib.lossMusic = MediaPlayer.create(context,R.raw.loss_music);
        SoundLib.lossMusic.setLooping(true);

        SoundLib.victoryMusic = MediaPlayer.create(context,R.raw.victory_music);
        SoundLib.victoryMusic.setLooping(true);



        //FINISHED
        SoundLib.flamethrowerShoot = MediaPlayer.create(context,R.raw.flamethrower_shoot);
        SoundLib.kaiserShoot = MediaPlayer.create(context,R.raw.kaiser_shoot);
        SoundLib.fissionShoot = MediaPlayer.create(context,R.raw.fission_shoot);
        SoundLib.skippyShoot = MediaPlayer.create(context,R.raw.skippy_shoot);
        SoundLib.magnumShoot = MediaPlayer.create(context,R.raw.shotgunner_shoot);

        SoundLib.xpCounterSoundEffect = MediaPlayer.create(context,R.raw.xpcounter_sound_effect);
        SoundLib.spawnerDeathSoundEffect = MediaPlayer.create(context,R.raw.spawner_death);
        SoundLib.playerDamagedSoundEffect = MediaPlayer.create(context,R.raw.player_damaged);
        SoundLib.supplyDamagedSoundEffect = MediaPlayer.create(context,R.raw.supply_damaged);
        SoundLib.playerShieldBlockSoundEffect = MediaPlayer.create(context,R.raw.player_shield_block);
        SoundLib.playerSpawnShieldSoundEffect = MediaPlayer.create(context,R.raw.player_spawn_shield);
        SoundLib.supplyDeathSoundEffect = MediaPlayer.create(context,R.raw.supply_death);
        SoundLib.playerKillSoundEffect = MediaPlayer.create(context,R.raw.enemy_death);
        SoundLib.playerDeathSoundEffect = MediaPlayer.create(context,R.raw.player_death);
        SoundLib.reloadingSoundEffect = MediaPlayer.create(context,R.raw.reloading);
        SoundLib.buttonSelectedSoundEffect = MediaPlayer.create(context,R.raw.button_selected);
        SoundLib.evolvingSoundEffect = MediaPlayer.create(context,R.raw.evolving_soundeffect);
        SoundLib.levelSelectTickEffect = MediaPlayer.create(context,R.raw.level_select_tick);
        numBlaze = 0;
        soundEffects = new SoundPool(7, AudioManager.STREAM_MUSIC,0);


        MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d("Sound Lib","Finished Seeking: " + mp.isPlaying());
                mp.start();
            }
        };

        MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("SOUND LIB","MP: " + mp + " Error: " + what + " Extra:" + extra);
                return false;
            }
        };


        //stuff that use seek to
        SoundLib.fissionShoot.setOnSeekCompleteListener(onSeekCompleteListener);
        SoundLib.kaiserShoot.setOnSeekCompleteListener(onSeekCompleteListener);
        SoundLib.skippyShoot.setOnSeekCompleteListener(onSeekCompleteListener);
        SoundLib.magnumShoot.setOnSeekCompleteListener(onSeekCompleteListener);
        SoundLib.flamethrowerShoot.setOnSeekCompleteListener(onSeekCompleteListener);

        SoundLib.playerDamagedSoundEffect.setOnSeekCompleteListener(onSeekCompleteListener);
        SoundLib.buttonSelectedSoundEffect.setOnSeekCompleteListener(onSeekCompleteListener);
        SoundLib.levelSelectTickEffect.setOnSeekCompleteListener(onSeekCompleteListener);

        //TODO DEBUG
        SoundLib.fissionShoot.setOnErrorListener(onErrorListener);
        SoundLib.kaiserShoot.setOnErrorListener(onErrorListener);
        SoundLib.skippyShoot.setOnErrorListener(onErrorListener);
        SoundLib.magnumShoot.setOnErrorListener(onErrorListener);
        SoundLib.flamethrowerShoot.setOnErrorListener(onErrorListener);

        SoundLib.playerDamagedSoundEffect.setOnErrorListener(onErrorListener);
        SoundLib.buttonSelectedSoundEffect.setOnErrorListener(onErrorListener);
        SoundLib.levelSelectTickEffect.setOnErrorListener(onErrorListener);
        SoundLib.levelSelectTickEffect.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("Sound Lib","PREPARED\nPREPARE");
            }
        });
    }

    /** Pauses all music,
     * unlike muteAllMedia, the songs do not continue to play FIX THIS IT STILL DOES CONTINUE TO PLAY
     *
     */
    public static void pauseAllMedia(){
        try {
            SoundLib.lobbyMusic.setVolume(0,0);
            SoundLib.gameMusic.setVolume(0,0);
            SoundLib.victoryMusic.setVolume(0,0);
            SoundLib.lossMusic.setVolume(0,0);
            numBlaze = 0;
            flamethrowerShoot.setVolume(0,0);
        } catch (NullPointerException | IllegalStateException e){
            Log.d("SOUND_LIB","null pointer",e);
        }
    }

    public static void stopAllMedia(){
        try {
            SoundLib.lobbyMusic.release();
            SoundLib.gameMusic.release();
            SoundLib.victoryMusic.release();
            SoundLib.lossMusic.release();

            SoundLib.kaiserShoot.release();
            SoundLib.skippyShoot.release();
            SoundLib.fissionShoot.release();
            SoundLib.flamethrowerShoot.release();
            SoundLib.magnumShoot.release();
            //MAY CHANGE
            SoundLib.xpCounterSoundEffect.release();
            SoundLib.spawnerDeathSoundEffect.release();
            //FINISHED
            SoundLib.playerDamagedSoundEffect.release();
            SoundLib.supplyDamagedSoundEffect.release();
            SoundLib.playerShieldBlockSoundEffect.release();
            SoundLib.playerSpawnShieldSoundEffect.release();
            SoundLib.supplyDeathSoundEffect.release();
            SoundLib.playerKillSoundEffect .release();
            SoundLib.playerDeathSoundEffect.release();
            SoundLib.reloadingSoundEffect.release();
            SoundLib.buttonSelectedSoundEffect .release();
            SoundLib.evolvingSoundEffect.release();
            SoundLib.levelSelectTickEffect.release();

            numBlaze = 0;
        } catch (NullPointerException e){
            Log.d("SOUND_LIB","null pointer",e);
        }
    }

    /** resumes all music
     *
     *
     *
     */
    public static void resumeAllMedia(){
        try {
            if (playMusic) {
                SoundLib.lobbyMusic.setVolume(1, 1);
                SoundLib.gameMusic.setVolume(1, 1);
                SoundLib.victoryMusic.setVolume(1, 1);
                SoundLib.lossMusic.setVolume(1, 1);
            }
        } catch (NullPointerException e){
            Log.d("SOUND_LIB","null pointer",e);
        }
    }

    /** All media (only music for now) is muted, however it keeps playing just at 0 volume
     */
    public static void muteAllMedia(){
        SoundLib.playMusic = false;

        try {
            SoundLib.lobbyMusic.setVolume(0,0);
            SoundLib.gameMusic.setVolume(0,0);
            SoundLib.victoryMusic.setVolume(0,0);
            SoundLib.lossMusic.setVolume(0,0);
            numBlaze = 0;
            flamethrowerShoot.setVolume(0,0);
        } catch (NullPointerException | IllegalStateException e){
            Log.d("SOUND_LIB","null pointer",e);
        }

    }

    /** All media (only music for now) is un muted
     *
     */
    public static void unMuteAllMedia(){
        SoundLib.playMusic = true;

        try {
            SoundLib.lobbyMusic.setVolume(1,1);
            SoundLib.gameMusic.setVolume(1,1);
            SoundLib.victoryMusic.setVolume(1,1);
            SoundLib.lossMusic.setVolume(1,1);
        } catch (NullPointerException | IllegalStateException e){
            Log.d("SOUND_LIB","null pointer",e);
        }
    }

    /** Sets the state of the lobby music
     *
     * @param state true means to start playing, false means to end playing
     */
    public static void setStateLobbyMusic(boolean state){
        if (state){
            SoundLib.lobbyMusic.start();
            if (! SoundLib.playMusic){
                SoundLib.lobbyMusic.setVolume(0,0);
            }
        } else if (SoundLib.lobbyMusic.isPlaying()){
            SoundLib.lobbyMusic.pause();
            SoundLib.lobbyMusic.seekTo(0);
        }
    }

    /** Sets the state of the game music
     *
     * @param state true means to start playing, false means to end playing
     */
    public static void setStateGameMusic(boolean state){
        if (state){
            SoundLib.gameMusic.start();
            if (! SoundLib.playMusic){
                SoundLib.muteAllMedia();
                SoundLib.gameMusic.setVolume(0,0);
            }
        } else if (SoundLib.gameMusic.isPlaying()){
            SoundLib.gameMusic.pause();
            SoundLib.gameMusic.seekTo(0);
        }
    }

    /** Sets the state of the victory music
     *
     * @param state true means to start playing, false means to end playing
     */
    public static void setStateVictoryMusic(boolean state){
        Log.d("SOUND LIB:","MUSIC STATE: " + playMusic);
        if (state){
            SoundLib.victoryMusic.start();
            if (! SoundLib.playMusic){
                SoundLib.muteAllMedia();
                SoundLib.victoryMusic.setVolume(0,0);
            }
        } else if (SoundLib.victoryMusic.isPlaying()) {
            SoundLib.victoryMusic.pause();
            SoundLib.victoryMusic.seekTo(0);
        }
    }

    /** Sets the state of the loss music
     *
     * @param state true means to start playing, false means to end playing
     */
    public static void setStateLossMusic(boolean state){
        if (state){
            SoundLib.lossMusic.start();
            if (! SoundLib.playMusic){
                SoundLib.lossMusic.setVolume(0,0);
            }
        } else if (SoundLib.lossMusic.isPlaying()){
            SoundLib.lossMusic.pause();
            SoundLib.lossMusic.seekTo(0);
        }
    }

    //sound effects don't need to be stopped

    /** Plays the sound effect when a player kills an enemy
     *
     */
    public static void playPlayerKillSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.playerKillSoundEffect.isPlaying()) {
            SoundLib.playerKillSoundEffect.start();
        }
    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playKaiserShoot(){
        if (! SoundLib.playSoundEffects) return;

        if (SoundLib.kaiserShoot.isPlaying()) {
            SoundLib.kaiserShoot.seekTo(0);
        } else {
            SoundLib.kaiserShoot.start();
        }
    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playSkippyShoot(){
        if (! SoundLib.playSoundEffects) return;

        if (SoundLib.skippyShoot.isPlaying()) {
            SoundLib.skippyShoot.seekTo(0);
        } else {
            SoundLib.skippyShoot.start();
        }
    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playFissionShoot(){
        if (! SoundLib.playSoundEffects) return;

        if (SoundLib.fissionShoot.isPlaying()) {
            SoundLib.fissionShoot.seekTo(0);
        } else {
            //started also otherwise
            SoundLib.fissionShoot.start();
        }
    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playBlazeShoot(){
        if (! SoundLib.playSoundEffects) return;


        if (flameFadeOut != null){
            flameFadeOut.cancel();
        }
        SoundLib.flamethrowerShoot.setVolume(1,1);
        if (! SoundLib.flamethrowerShoot.isPlaying()) {
            SoundLib.flamethrowerShoot.seekTo(0);
        }
        SoundLib.numBlaze++;
    }
    public static void stopBlaze(){
        numBlaze--;
        if (numBlaze <= 0){
            numBlaze = 0;
            if (flameFadeOut != null){
                flameFadeOut.cancel();
            }
            flameFadeOut = new SoundFadeOut(SoundFadeOut.DEFAULT_MILLIS,flamethrowerShoot);

        }
    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playMagnumShoot(){
        if (! SoundLib.playSoundEffects) return;

        if (SoundLib.magnumShoot.isPlaying()) {
           SoundLib.magnumShoot.seekTo(0);
        } else {
            SoundLib.magnumShoot.start();
        }
    }



    /** Plays the sound effect when a player lands an attack on an enemy or spawner
     *
     */
    public static void playSupplyDamaged(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.supplyDamagedSoundEffect.isPlaying()) {
            SoundLib.supplyDamagedSoundEffect.start();
        }
    }

    /** Plays the sound effect when a player dies
     *
     */
    public static void playPlayerDeathSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.playerDeathSoundEffect.isPlaying()) {
            SoundLib.playerDeathSoundEffect.start();
        }
    }

    /** Plays the sound effect when a player spawns a shield
     *
     */
    public static void playPlayerSpawnShieldSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.playerSpawnShieldSoundEffect.isPlaying()) {
            SoundLib.playerSpawnShieldSoundEffect.start();
        }
    }

    /** Plays the sound effect when a player's shield partially blocks an enemy's attack
     *
     */
    public static void playPlayerShieldBlockSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.playerShieldBlockSoundEffect.isPlaying()) {
            SoundLib.playerShieldBlockSoundEffect.start();
        }
    }

    /** Plays the sound effect when a button is selected (pressed, but not fully released)
     *
     */
    public static void playButtonSelectedSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (SoundLib.buttonSelectedSoundEffect.isPlaying()) {
            SoundLib.buttonSelectedSoundEffect.seekTo(0);
        } else {
            SoundLib.buttonSelectedSoundEffect.start();
        }
    }


    /** Plays the sound effect when a enemy damages the player
     *
     */
    public static void playPlayerDamagedSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (SoundLib.playerDamagedSoundEffect.isPlaying()) {
            SoundLib.playerDamagedSoundEffect.seekTo(0);
        } else {
            SoundLib.playerDamagedSoundEffect.start();
        }

    }


    /** Plays the sound effect when a supply is killed off by enemies
     *
     */
    public static void playSupplyDeathSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.supplyDeathSoundEffect.isPlaying()) {
            SoundLib.supplyDeathSoundEffect.start();
        }
    }

    /** Plays the sound effect when a player is currentyl evolving
     *
     */
    public static void playPlayerEvolvingSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.evolvingSoundEffect.isPlaying()) {
            SoundLib.evolvingSoundEffect.start();
        }
    }
    /** Plays the sound effect when a player is currentyl evolving
     *
     */
    public static void playPlayerReloading(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.reloadingSoundEffect.isPlaying()) {
            SoundLib.reloadingSoundEffect.start();
        }
    }


    /** Plays the sound effect when a spawner is killed by the player
     */
    public static void playSpawnerDeathSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.spawnerDeathSoundEffect.isPlaying()) {
            SoundLib.spawnerDeathSoundEffect.start();
        }
    }
    /** Plays the sound effect when the xp counter is being shown
     */
    public static void playXpCounterSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.xpCounterSoundEffect.isPlaying()) {
            SoundLib.xpCounterSoundEffect.start();
        }
    }


    /** Plays the sound effect when the xp counter is being shown
     */
    public static void playLevelSelectTick(){
        if (! SoundLib.playSoundEffects) return;

        Log.d("Sound Lib","Length: " + SoundLib.levelSelectTickEffect.getDuration() +
                " pos: " + SoundLib.levelSelectTickEffect.getCurrentPosition() +
                " is playing: " + SoundLib.levelSelectTickEffect.isPlaying());
        if (SoundLib.levelSelectTickEffect.isPlaying()) {
            SoundLib.levelSelectTickEffect.pause();
            SoundLib.levelSelectTickEffect.seekTo(0);
        } else {
            SoundLib.levelSelectTickEffect.start();
        }
    }




    /** Sets whether or not playing music, true means music will be played, false means music wont be played
     *
     * @param playSoundEffects whether or not to play soundEffects
     */
    public static void setPlaySoundEffects(boolean playSoundEffects){
        SoundLib.playSoundEffects = playSoundEffects;
    }

    /** Whether or not music is being played
     *
     * @return Whether or not music is being played
     */
    public static boolean isPlayMusic() {
        return SoundLib.playMusic;
    }

    /** Whether or not sound effects are being played
     *
     * @return Whether or not sound effects are being played
     */
    public static boolean isPlaySoundEffects() {
        return SoundLib.playSoundEffects;
    }

}
