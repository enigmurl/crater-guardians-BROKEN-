package com.enigmadux.craterguardians.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.enigmadux.craterguardians.R;

/** All songs and sound effect sare played here
 *
 * todo: reloading stuff
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
    private static MediaPlayer playerShootSoundEffect;
    /** sound effect played when a player's attack hits an enemy*/
    private static MediaPlayer playerAttackLandSoundEffect;
    /** sound effect played a player is killed*/
    private static MediaPlayer playerDeathSoundEffect;
    /** sound effect played when the shield is spawned*/
    private static MediaPlayer playerSpawnShieldSoundEffect;
    /** sound effect played when an enemy attack is blocked by the shield*/
    private static MediaPlayer playerShieldBlockSoundEffect;
    /** sound effect played when a button is selected */
    private static MediaPlayer buttonSelectedSoundEffect;
    /** sound effect played when a button is released */
    private static MediaPlayer buttonReleasedSoundEffect;
    /** sound effect played when the player is damaged by a toxic lake*/
    private static MediaPlayer toxicLakeTickSoundEffect;
    /** sound effect played when the enemies hit the player*/
    private static MediaPlayer enemyDamagePlayerSoundEffect;
    /** sound effect played when the enemies damage the supply*/
    private static MediaPlayer enemyDamageSupplySoundEffect;
    /** sound effect played when the enemies destroy a supply*/
    private static MediaPlayer supplyDeathSoundEffect;
    /** sound effect played when a spawner is damaged */
    private static MediaPlayer spawnerDamagedSoundEffect;
    /** sound effect played when a spawner is killed*/
    private static MediaPlayer spawnerDeathSoundEffect;
    /** sound effect played during the evolving of a character, it should be the approximate length*/
    private static MediaPlayer evolvingSoundEffect;

    /** whether or not the player has the music on*/
    private static boolean playMusic = true;
    /** whether or not the player has the sound effects on*/
    private static boolean playSoundEffects = true;

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

        SoundLib.playerKillSoundEffect = MediaPlayer.create(context,R.raw.player_killed);
        SoundLib.playerShootSoundEffect = MediaPlayer.create(context,R.raw.player_shoot);
        SoundLib.playerAttackLandSoundEffect = MediaPlayer.create(context,R.raw.player_attack_land);
        SoundLib.playerDeathSoundEffect = MediaPlayer.create(context,R.raw.player_death);
        SoundLib.playerSpawnShieldSoundEffect = MediaPlayer.create(context,R.raw.player_spawn_shield);
        SoundLib.playerShieldBlockSoundEffect = MediaPlayer.create(context,R.raw.player_shield_block);
        SoundLib.buttonSelectedSoundEffect = MediaPlayer.create(context,R.raw.button_selected);
        SoundLib.buttonReleasedSoundEffect = MediaPlayer.create(context,R.raw.button_released);
        SoundLib.toxicLakeTickSoundEffect = MediaPlayer.create(context,R.raw.toxic_lake_tick);
        SoundLib.enemyDamagePlayerSoundEffect = MediaPlayer.create(context,R.raw.enemy_damage_player);
        SoundLib.enemyDamageSupplySoundEffect = MediaPlayer.create(context,R.raw.enemy_damage_supply);
        SoundLib.supplyDeathSoundEffect = MediaPlayer.create(context,R.raw.supply_death);
        SoundLib.spawnerDamagedSoundEffect = MediaPlayer.create(context,R.raw.spawner_damaged);
        SoundLib.spawnerDeathSoundEffect = MediaPlayer.create(context,R.raw.spawner_death);
        SoundLib.evolvingSoundEffect = MediaPlayer.create(context,R.raw.evolving_soundeffect);

    }

    /** Pauses all music,
     * unlike muteAllMedia, the songs do not continue to play FIX THIS IT STILL DOES CONTINUE TO PLAY TODO
     *
     */
    public static void pauseAllMedia(){
        try {
            SoundLib.lobbyMusic.setVolume(0,0);
            SoundLib.gameMusic.setVolume(0,0);
            SoundLib.victoryMusic.setVolume(0,0);
            SoundLib.lossMusic.setVolume(0,0);
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
     * todo do it for everything
     */
    public static void muteAllMedia(){
        SoundLib.playMusic = false;

        SoundLib.lobbyMusic.setVolume(0,0);
        SoundLib.gameMusic.setVolume(0,0);
        SoundLib.victoryMusic.setVolume(0,0);
        SoundLib.lossMusic.setVolume(0,0);
    }

    /** All media (only music for now) is un muted
     *
     */
    public static void unMuteAllMedia(){
        SoundLib.playMusic = true;

        SoundLib.lobbyMusic.setVolume(1,1);
        SoundLib.gameMusic.setVolume(1,1);
        SoundLib.victoryMusic.setVolume(1,1);
        SoundLib.lossMusic.setVolume(1,1);
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
                Log.d("SOUND LIB:","Muting victory: ");
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
    public static void playPlayerShootSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.playerShootSoundEffect.isPlaying()) {
            SoundLib.playerShootSoundEffect.start();
        }
    }

    /** Plays the sound effect when a player lands an attack on an enemy or spawner
     *
     */
    public static void playPlayerAttackLandSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.playerAttackLandSoundEffect.isPlaying()) {
            SoundLib.playerAttackLandSoundEffect.start();
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

        if (! SoundLib.buttonSelectedSoundEffect.isPlaying()) {
            SoundLib.buttonSelectedSoundEffect.start();
        }
    }

    /** Plays the sound effect when a button is released
     *
     */
    public static void playButtonReleasedSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.buttonReleasedSoundEffect.isPlaying()) {
            SoundLib.buttonReleasedSoundEffect.start();
        }
    }

    /** Plays the sound effect when a button is selected (pressed, but not fully released)
     *
     */
    public static void playToxicLakeTickSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.toxicLakeTickSoundEffect.isPlaying()) {
            SoundLib.toxicLakeTickSoundEffect.start();
        }
    }

    /** Plays the sound effect when a enemy damages the player
     *
     */
    public static void playEnemyDamagePlayerSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.enemyDamagePlayerSoundEffect.isPlaying()) {
            SoundLib.enemyDamagePlayerSoundEffect.start();
        }
    }

    /** Plays the sound effect when an enemy damages a supply
     *
     */
    public static void playEnemyDamageSupplySoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.enemyDamageSupplySoundEffect.isPlaying()) {
            SoundLib.enemyDamageSupplySoundEffect.start();
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

    /** Plays the sound effect when a spawner is damaged by the player
     */
    public static void playSpawnerDamageSoundEffect(){
        if (! SoundLib.playSoundEffects) return;

        if (! SoundLib.spawnerDamagedSoundEffect.isPlaying()) {
            SoundLib.spawnerDamagedSoundEffect.start();
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
