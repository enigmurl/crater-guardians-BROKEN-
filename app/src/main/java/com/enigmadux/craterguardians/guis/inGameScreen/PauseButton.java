package com.enigmadux.craterguardians.guis.inGameScreen;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.guilib.VisibilityInducedButton;
import com.enigmadux.craterguardians.guilib.VisibilitySwitch;

public class PauseButton extends VisibilityInducedButton {

    private int pointerID;
    /**
     * Default constructor
     *
     * @param context        any context that can get resources
     * @param x              the center x position of the texture
     * @param y              the center y position of the texture
     * @param w              the width of the texture
     * @param h              the height of the texture
     * @param objectToHide   the object that should be hidden
     * @param objectToShow   the object that should be shown when the button is presssed
     */
    public PauseButton(Context context,  float x, float y, float w, float h, VisibilitySwitch objectToHide, VisibilitySwitch objectToShow) {
        super(context, R.drawable.pause_button, x, y, w, h, objectToHide, objectToShow, false);
    }


    @Override
    public boolean onTouch(MotionEvent e) {
        if (this.isPressed(e)) {
            if (this.isDown && (e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_POINTER_UP) || e.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                this.defaultReleaseAction();
                this.onHardRelease(e);
            } else if (e.getActionMasked() == MotionEvent.ACTION_DOWN || e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                this.defaultPressAction();
                this.onPress(e);
                this.pointerID = e.getPointerId(e.getActionIndex());
            }
            return true;
        } else if (this.isDown) {
            if (e.getPointerId(e.getActionIndex()) == pointerID) {
                this.onSoftRelease(e);
                return true;
            }
        }
        return false;
    }
}
