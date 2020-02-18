package com.enigmadux.craterguardians.GUIs.settingsScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.FileStreams.SettingsData;
import com.enigmadux.craterguardians.GUILib.OnOffButton;
import com.enigmadux.craterguardians.SoundLib;

import java.util.Set;

/** Enables and disables music being played
 *
 * @author Manu Bhat
 * @version BETA
 */
public class SoundEffectsSwitch extends OnOffButton {

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
    public SoundEffectsSwitch(Context context, int texturePointer, float x, float y, float w, float h, SettingsData settingsData){
        super(context, texturePointer, x, y, w, h);

        this.settingsData = settingsData;

        this.setState(SoundLib.isPlaySoundEffects());
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
        SoundLib.setPlaySoundEffects(this.isOn());
        //finally write the updated data
        this.settingsData.writeSettingsFile();


        return true;
    }
}
