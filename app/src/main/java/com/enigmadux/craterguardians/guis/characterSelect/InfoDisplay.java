package com.enigmadux.craterguardians.guis.characterSelect;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.animations.PopUp;
import com.enigmadux.craterguardians.guilib.GUIClickable;
import com.enigmadux.craterguardians.players.Player;

public class InfoDisplay extends GUIClickable {

    private Player player;
    private PopUp popUpAnim;
    private float orgW;
    private float orgH;

    InfoDisplay(Context context, Player player, float x, float y, float w, float h, boolean isRounded) {
        super(context, player.getPlayerInfo(), x, y, w, h, isRounded);
        this.orgW = this.w;
        this.orgH = this.h;
        this.player = player;
    }

    @Override
    public void setVisibility(boolean visible) {
        super.setVisibility(visible);
        if (visible){
            if (popUpAnim != null){
                popUpAnim.cancel();
            }
            this.popUpAnim = new PopUp(PopUp.DEFAULT_MILLIS,orgW,orgH,this, 0);
        }
    }

    @Override
    public boolean onTouch(MotionEvent e) {

        return super.onTouch(e) || this.isVisible;
    }

    @Override
    public boolean onPress(MotionEvent e) {
        this.setVisibility(false);
        this.scale = 1;
        return true;
    }

    @Override
    public boolean onHardRelease(MotionEvent e) {
        //will never come to this state
        this.scale = 1;
        return false;
    }

    @Override
    public boolean onSoftRelease(MotionEvent e) {
        //will never come to this state
        this.scale = 1;
        return false;
    }

    public Player getPlayer(){
        return this.player;
    }

    //want to see if they click outside
    @Override
    public boolean isPressed(MotionEvent e) {
        return this.isVisible && ! super.isPressed(e);
    }

    @Override
    protected void defaultPressAction() {
        //do nothing
    }

    @Override
    protected void defaultReleaseAction() {
        //nothing
    }

    @Override
    protected void defaultSoftRelease() {
        //nothing
    }
}
