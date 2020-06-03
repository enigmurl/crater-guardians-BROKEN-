package com.enigmadux.craterguardians.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.animations.SoundFadeOut;

/** All songs and sound effect sare played here
 *
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

    private static MediaPlayer flamethrowerShoot;


    /** sound effect played when a player kills*/
    private static int enemyDeathSoundEffect;
    /** sound effect played when a player shoots an attack*/
    private static int kaiserShoot;
    private static int skippyShoot;
    private static int fissionShoot;
    private static int magnumShoot;

    /** sound effect played a player is killed*/
    private static int playerDeathSoundEffect;
    /** sound effect played when an enemy shoots */
    private static int supplyDamagedSoundEffect;
    /** sound effect played when the shield is spawned*/
    private static int playerSpawnShieldSoundEffect;
    /** sound effect played when an enemy attack is blocked by the shield*/
    private static int playerShieldBlockSoundEffect;
    /** sound effect played when the player is reloading */
    private static int reloadingSoundEffect;
    /** sound effect played when a button is selected */
    private static int buttonSelectedSoundEffect;
    /** sound effect played when the enemies hit the player*/
    private static int playerDamagedSoundEffect;
    /** sound effect played when the enemies destroy a supply*/
    private static int supplyDeathSoundEffect;
    /** sound effect played when a spawner is killed*/
    private static int spawnerDeathSoundEffect;
    /** sound effect played during the evolving of a character, it should be the approximate length*/
    private static int evolvingSoundEffect;
    /** sound effect played when xp is gained and that counter thing is shown */
    private static int xpCounterSoundEffect;
    private static int levelSelectTickEffect;

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

        soundEffects = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        SoundLib.kaiserShoot = soundEffects.load(context,R.raw.kaiser_shoot,1);
        SoundLib.fissionShoot =soundEffects.load(context,R.raw.fission_shoot,1);
        SoundLib.skippyShoot = soundEffects.load(context,R.raw.skippy_shoot,1);
        SoundLib.magnumShoot = soundEffects.load(context,R.raw.magnum_shoot,1);

        SoundLib.xpCounterSoundEffect = soundEffects.load(context,R.raw.xpcounter_sound_effect,1);
        SoundLib.spawnerDeathSoundEffect = soundEffects.load(context,R.raw.spawner_death,1);
        SoundLib.playerDamagedSoundEffect = soundEffects.load(context,R.raw.player_damaged,1);
        SoundLib.supplyDamagedSoundEffect = soundEffects.load(context,R.raw.supply_damaged,1);
        SoundLib.playerShieldBlockSoundEffect = soundEffects.load(context,R.raw.player_shield_block,1);
        SoundLib.playerSpawnShieldSoundEffect = soundEffects.load(context,R.raw.player_spawn_shield,1);
        SoundLib.supplyDeathSoundEffect = soundEffects.load(context,R.raw.supply_death,1);
        SoundLib.enemyDeathSoundEffect = soundEffects.load(context,R.raw.enemy_death,1);
        SoundLib.playerDeathSoundEffect = soundEffects.load(context,R.raw.player_death,1);
        SoundLib.reloadingSoundEffect = soundEffects.load(context,R.raw.reloading,1);
        SoundLib.buttonSelectedSoundEffect = soundEffects.load(context,R.raw.button_selected,1);
        SoundLib.evolvingSoundEffect = soundEffects.load(context,R.raw.evolving_soundeffect,1);
        SoundLib.levelSelectTickEffect =soundEffects.load(context,R.raw.level_select_tick,1);
        numBlaze = 0;


        SoundLib.flamethrowerShoot.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mp.start();
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
//            Log.d("SOUND_LIB","null pointer",e);
        }
    }

    public static void stopAllMedia(){
        try {
            SoundLib.lobbyMusic.release();
            SoundLib.gameMusic.release();
            SoundLib.victoryMusic.release();
            SoundLib.lossMusic.release();
            lobbyMusic = gameMusic = victoryMusic = lossMusic = null;

            soundEffects.release();
            soundEffects = null;

            numBlaze = 0;
        } catch (NullPointerException e){
//            Log.d("SOUND_LIB","null pointer",e);
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
//            Log.d("SOUND_LIB","null pointer",e);
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
//            Log.d("SOUND_LIB","null pointer",e);
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
//            Log.d("SOUND_LIB","null pointer",e);
        }
    }

    /** Sets the state of the lobby music
     *
     * @param state true means to start playing, false means to end playing
     */
    public static void setStateLobbyMusic(boolean state){
        if (SoundLib.lobbyMusic == null){
            return;
        }
        if (state){
            if (! SoundLib.lobbyMusic.isPlaying()) {
                SoundLib.lobbyMusic.start();
            }
            if (! SoundLib.playMusic){
                SoundLib.muteAllMedia();
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
        if (SoundLib.gameMusic == null){
            return;
        }

        if (state){
            if (! SoundLib.gameMusic.isPlaying()) {
                SoundLib.gameMusic.start();
            }
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
        if (SoundLib.victoryMusic == null){
            return;
        }
        if (state){
            if (! SoundLib.victoryMusic.isPlaying()) {
                SoundLib.victoryMusic.start();
            }
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
        if (SoundLib.lossMusic == null){
            return;
        }
        if (state){
            if (! SoundLib.lossMusic.isPlaying()) {
                SoundLib.lossMusic.start();
            }
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

        soundEffects.play(enemyDeathSoundEffect,1,1,0,0,1);

    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playKaiserShoot(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(kaiserShoot,1,1,0,0,1);

    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playSkippyShoot(){
        if (! SoundLib.playSoundEffects) return;
        soundEffects.play(skippyShoot,1,1,0,0,1);

    }

    /** Plays the sound effect when a player shoots an attack
     *
     */
    public static void playFissionShoot(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(fissionShoot,1,1,0,0,1);

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

        soundEffects.play(magnumShoot,1,1,0,0,1);

    }



    /** Plays the sound effect when a player lands an attack on an enemy or spawner
     *
     */
    public static void playSupplyDamaged(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(supplyDamagedSoundEffect,1,1,0,0,1);

    }

    /** Plays the sound effect when a player dies
     *
     */
    public static void playPlayerDeathSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(playerDeathSoundEffect,1,1,0,0,1);

    }

    /** Plays the sound effect when a player spawns a shield
     *
     */
    public static void playPlayerSpawnShieldSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(playerSpawnShieldSoundEffect,1,1,0,0,1);

    }

    /** Plays the sound effect when a player's shield partially blocks an enemy's attack
     *
     */
    public static void playPlayerShieldBlockSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(playerShieldBlockSoundEffect,1,1,0,0,1);

    }

    /** Plays the sound effect when a button is selected (pressed, but not fully released)
     *
     */
    public static void playButtonSelectedSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(buttonSelectedSoundEffect,1,1,0,0,1);

    }


    /** Plays the sound effect when a enemy damages the player
     *
     */
    public static void playPlayerDamagedSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(playerDamagedSoundEffect,1,1,0,0,1);

    }


    /** Plays the sound effect when a supply is killed off by enemies
     *
     */
    public static void playSupplyDeathSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(supplyDeathSoundEffect,1,1,0,0,1);

    }

    /** Plays the sound effect when a player is currentyl evolving
     *
     */
    public static void playPlayerEvolvingSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(evolvingSoundEffect,1,1,0,0,1);

    }
    /** Plays the sound effect when a player is currentyl evolving
     *
     */
    public static void playPlayerReloading(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(reloadingSoundEffect,1,1,0,0,1);

    }


    /** Plays the sound effect when a spawner is killed by the player
     */
    public static void playSpawnerDeathSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(spawnerDeathSoundEffect,1,1,0,0,1);

    }
    /** Plays the sound effect when the xp counter is being shown
     */
    public static void playXpCounterSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(xpCounterSoundEffect,1,1,0,0,1);

    }


    /** Plays the sound effect when the xp counter is being shown
     */
    public static void playLevelSelectTick(){
        if (! SoundLib.playSoundEffects) return;

        soundEffects.play(levelSelectTickEffect,1,1,0,0,1);

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
