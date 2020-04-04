package com.enigmadux.craterguardians.GUIs.inGameScreen.joystickLayouts;

import android.content.Context;
import android.view.MotionEvent;


import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.values.LayoutConsts;

import enigmadux2d.core.quadRendering.QuadTexture;

public class JoyStick extends QuadTexture {


    protected float deltaX;
    protected float deltaY;

    protected float centerX;
    protected float centerY;

    protected float x;
    protected float y;

    protected float minRadius;
    protected float maxRadius;

    private JoystickLayout joystickLayout;

    protected boolean isSelected;

    private int pointerID;

    private QuadTexture background;



    public JoyStick(Context context, int texturePointer, float x, float y, float radius,float maxRadius,float minRadius, JoystickLayout joystickLayout) {
        super(context, texturePointer, x, y,radius * 2 * LayoutConsts.SCALE_X,radius * 2);
        this.background = new QuadTexture(context, R.drawable.joystick_background,x,y,maxRadius * 2 * LayoutConsts.SCALE_X,maxRadius * 2);
        this.background.setAlpha(0.3f);

        this.x = x;
        this.y = y;

        this.maxRadius = maxRadius;
        this.minRadius = minRadius;

        centerX = x;
        centerY = y;

        this.joystickLayout = joystickLayout;
    }

    @Override
    public void dumpOutputMatrix(float[] dumpMatrix, float[] mvpMatrix) {
        this.setCord(this.deltaX + this.centerX,this.deltaY + this.centerY);
        super.dumpOutputMatrix(dumpMatrix, mvpMatrix);

        //Matrix.translateM(dumpMatrix,0,this.finalMatrix,0,this.deltaX,this.deltaY,0 );
    }

    public void onTouch(MotionEvent e){
        if ((e.getActionMasked() == MotionEvent.ACTION_DOWN || e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) && isPressed(e)){
            if (! this.isSelected){
                this.onPress(e);
            }

        } else if ((e.getActionMasked() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_POINTER_UP ||
                e.getActionMasked() == MotionEvent.ACTION_CANCEL) && isSelected){
            if (e.getPointerId(e.getActionIndex()) == this.pointerID) {
                this.onHardRelease(e);
            }

        }

        if (this.isSelected) this.onMove(e);
    }

    private boolean isPressed(MotionEvent e) {
        int id = e.getPointerId(e.getActionIndex());

        return this.joystickLayout.getPointerLocs().get(id, this) == this;
    }

    protected void onMove(MotionEvent e){
        int ind = e.findPointerIndex(this.pointerID);
        float x = MathOps.getOpenGLX(e.getX(ind));
        float y = MathOps.getOpenGLY(e.getY(ind));

        this.deltaX = x - this.centerX;
        this.deltaY = y - this.centerY;

        float hypotenuse = (float) Math.hypot(this.deltaX/LayoutConsts.SCALE_X,this.deltaY/LayoutConsts.SCALE_Y);

        if (hypotenuse > this.maxRadius){
            this.deltaX *= this.maxRadius/hypotenuse;
            this.deltaY *= this.maxRadius/hypotenuse;
        }

        if (hypotenuse < this.minRadius){
            this.deltaX = 0;
            this.deltaY = 0;
        }

    }

    private boolean onPress(MotionEvent e) {
        this.isSelected = true;
        this.pointerID = e.getPointerId(e.getActionIndex());
        int ind = e.findPointerIndex(this.pointerID);
        centerX = MathOps.getOpenGLX(e.getX(ind));
        centerY = MathOps.getOpenGLY(e.getY(ind));
        this.background.setCord(centerX,centerY);


        this.joystickLayout.getPointerLocs().put(e.getPointerId(e.getActionIndex()),this);
        return true;
    }

    private boolean onHardRelease(MotionEvent e) {
        this.joystickLayout.getPointerLocs().remove(e.getPointerId(e.getActionIndex()));
        this.deltaX = 0;
        this.deltaY = 0;
        centerX = x;
        centerY = y;
        this.background.setCord(centerX,centerY);

        this.isSelected = false;
        return true;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public void reset(){
        this.isSelected = false;
        this.deltaX = 0;
        this.deltaY = 0;
    }

    public QuadTexture getBackground(){
        return background;
    }

}
