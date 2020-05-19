package com.enigmadux.craterguardians.animations;

import com.enigmadux.craterguardians.players.Player;

public class GunRecoil extends FrameTransitionAnim {
    public static final long DEFAULT_MILLIS = 90;
    public static final float DEFAULT_LEN = 0.1f;

    private Player player;
    private float length;
    private float angle;
    //angle = radians
    public GunRecoil(long millis, Player player,float length,float angle) {
        super(millis);
        this.player = player;
        this.length = length;
        this.angle = (float) (angle + Math.PI);
        start();
    }

    @Override
    void step() {
        float t  =  (float) finishedMillis/totalMillis;
        float curLen = this.length * (-(2 * t - 1) * (2 * t - 1) + 1);

        this.player.setGunDelta((float) Math.cos(angle) * curLen,(float) Math.sin(angle) * curLen);
    }

    @Override
    void finish() {
        super.finish();
        this.player.setGunDelta(0,0);
    }

    public void reset(Player p,float angle){
        this.finishedMillis = 0;
        this.millisLeft = totalMillis;
        this.player = p;
        this.angle = (float) (angle + Math.PI);
        start();
    }



}
