package com.enigmadux.craterguardians.guis.inGameScreen.defaultJoystickLayout;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.guis.inGameScreen.joystickLayouts.JoystickLayout;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

public class EvolveButton extends QuadTexture {
    //millis
    private static final long FLASHING_PERIOD = 1000;

    /**
     * The scale factor when it's pushed down
     */
    private static final float BUTTON_DOWN_SCALEFACTOR = 0.8f;


    //complete override of on touch because this is sensitive to pointer events as well
    private int pointerID=-1;

    private JoystickLayout joystickLayout;
    private CraterRenderer craterRenderer;

    private boolean isDown;

    //amount of millise conds since the evolve charge becomes 1
    private long millisSinceInited = 0;


    /**
     * This is always initialized to scale it as if the button is being pressed down.
     * Only use this variables if the button is being down
     */
    private final float[] scalarMatrix = new float[16];

    /**
     * This is where intermediate dumping of the the matrices is put into
     */
    private final float[] finalMatrix = new float[16];

    public EvolveButton(Context context, int texturePointer, float x, float y, float w, float h, CraterRenderer craterRenderer, JoystickLayout joystickLayout) {
        super(context, texturePointer, x, y, w * LayoutConsts.SCALE_X, h);
        this.joystickLayout = joystickLayout;
        this.craterRenderer = craterRenderer;

        Matrix.setIdentityM(this.scalarMatrix, 0);
        Matrix.scaleM(this.scalarMatrix, 0, EvolveButton.BUTTON_DOWN_SCALEFACTOR, EvolveButton.BUTTON_DOWN_SCALEFACTOR, 0);

    }

    public boolean onTouch(MotionEvent e) {
        if (this.isPressed(e)) {
            if (this.isDown && (e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_POINTER_UP)) {
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

    private boolean isPressed(MotionEvent e) {
        int pointerInd  = e.getActionIndex();
        int id = e.getPointerId(pointerInd);

        float x = MathOps.getOpenGLX(e.getX(pointerInd));
        float y = MathOps.getOpenGLY(e.getY(pointerInd));


        return this.isVisible &&
                x > this.x - this.w / 2 &&
                x < this.x + this.w / 2 &&
                y > this.y - this.h / 2 &&
                y < this.y + this.h / 2 &&
                craterRenderer.getWorld().getCurrentGameState() == World.STATE_INGAME &&
                craterRenderer.getWorld().getPlayer().getEvolveCharge() >= 1 &&
                this.joystickLayout.getPointerLocs().get(id,this) == this;
    }

    public boolean onHardRelease(MotionEvent e){
        craterRenderer.getWorld().getPlayer().evolve(craterRenderer.getWorld());
        this.isDown = false;
        int id = e.getPointerId(e.getActionIndex());
        this.joystickLayout.getPointerLocs().remove(id);
        Log.d("EVOLVE", "Attempting evolve");
        //player.hideAngleAimer();
        return true;
    }

    public boolean onPress(MotionEvent e) {
        this.isDown = true;
        this.joystickLayout.getPointerLocs().put(e.getPointerId(e.getActionIndex()),this);
        return true;
    }

    public boolean onSoftRelease(MotionEvent e) {
        this.isDown = false;
        int id = e.getPointerId(e.getActionIndex());
        this.joystickLayout.getPointerLocs().remove(id);
        return true;
    }

    public void reset(){
        this.isDown = false;
    }

    /**
     * Dumps the output matrix for rendering
     *
     * @param dumpMatrix where the output matrix will be placed
     * @param mvpMatrix  the input matrix 4 by 4
     */
    @Override
    public void dumpOutputMatrix(float[] dumpMatrix, float[] mvpMatrix) {
        if (this.isDown) {
            super.dumpOutputMatrix(this.finalMatrix, mvpMatrix);
            Matrix.multiplyMM(dumpMatrix, 0, this.finalMatrix, 0, this.scalarMatrix, 0);
        } else {
            super.dumpOutputMatrix(dumpMatrix, mvpMatrix);
        }
    }


    /** The default action on button press, just playing the sound effect for now
     *
     */
    private void defaultPressAction(){
        SoundLib.playButtonSelectedSoundEffect();
    }

    /** The default action on button release, just playing the sound effect for now
     *
     */
    private void defaultReleaseAction(){
        SoundLib.playButtonReleasedSoundEffect();
    }

    public void update(World world,long dt){
        if (world.getPlayer().getEvolveCharge() == 1) {
            this.setVisibility(true);
            millisSinceInited+= dt;

            this.setShader(this.getRed(),1,this.getBlue(),1);

        } else if (world.getPlayer().getEvolveCharge() < 0){
            this.setVisibility(false);
            millisSinceInited = 0;
        } else {
            this.setVisibility(true);
            float charge = world.getPlayer().getEvolveCharge();
            this.setShader(1,charge,charge,1);
            millisSinceInited = 0;
        }

    }

    //gets red component when flashing
    //2pi/n = flashingPeriod
    //n = 2pi/flashingPeriod
    private float getRed(){
        return 0;
        //return 0.375f * ((float) Math.sin((float) (2 * Math.PI * millisSinceInited)/FLASHING_PERIOD) + 1);

    }
    private float getBlue(){
        return 0;
        //return 0.375f * ((float) Math.sin((float) (2 * Math.PI * millisSinceInited)/FLASHING_PERIOD) + 1);
    }
}