package com.enigmadux.craterguardians.guis.settingsScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.filestreams.SettingsData;
import com.enigmadux.craterguardians.guilib.OnOffButton;
import com.enigmadux.craterguardians.util.SoundLib;


/** Enables and disables music being played
 *
 * @author Manu Bhat
 * @version BETA
 */
public class MusicSwitch extends OnOffButton {

    /** We use this to adjust the settings based on user input
     *
     */
    private SettingsData settingsData;

    /** Default Constructor
     *
     * @param context        any context that can get resources
     * @param texturePointer a texture pointer in the form of R.drawable.*;
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture (which will be scaled down to accommodate screen size
     * @param h              the height of the texture
     */
    public MusicSwitch(Context context, int texturePointer, float x, float y, float w, float h, SettingsData settingsData){
        super(context, texturePointer, x, y, w, h);

        this.settingsData = settingsData;

        this.setState(SoundLib.isPlayMusic());
        if (this.isOn()){
            SoundLib.unMuteAllMedia();
        } else {
            SoundLib.muteAllMedia();
        }
    }



    /**
     *
     * @param e the motion event that describes the position, and type of the touch event
     * @return true all the time, as the touch event is always processed
     */
    @Override
    public boolean onHardRelease(MotionEvent e) {
        super.onHardRelease(e);
        //tell sound lib
        //SoundLib.setPlayMusic(this.isOn());
        if (this.isOn()){
            SoundLib.unMuteAllMedia();
        } else {
            SoundLib.muteAllMedia();
        }
        //finally write the date
        this.settingsData.writeSettingsFile();


        return true;
    }
}
