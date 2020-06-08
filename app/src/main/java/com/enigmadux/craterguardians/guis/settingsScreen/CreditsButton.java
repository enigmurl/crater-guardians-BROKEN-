package com.enigmadux.craterguardians.guis.settingsScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.values.LayoutConsts;

public class CreditsButton extends GUIClickable {


    private SettingsScreen settingsScreen;
    public CreditsButton(Context context,SettingsScreen settingsScreen) {
        super(context, R.drawable.button_background, 1 - 0.85f * LayoutConsts.SCALE_X, 0.85f, 0.5f, 0.15f, true);
        this.updateText("Credits",0.04f);
        this.settingsScreen = settingsScreen;
    }

    @Override
    public boolean onPress(MotionEvent e) {
        this.isDown = true;

        return true;
    }

    @Override
    public boolean onHardRelease(MotionEvent e) {
        this.isDown = false;
        this.settingsScreen.startCredits();

        return true;
    }

    @Override
    public boolean onSoftRelease(MotionEvent e) {
        this.isDown = false;
        return true;
    }
}
