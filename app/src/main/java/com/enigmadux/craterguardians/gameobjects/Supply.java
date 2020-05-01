package com.enigmadux.craterguardians.gameobjects;

import android.content.Context;
import android.opengl.Matrix;

import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.animations.DeathAnim;
import com.enigmadux.craterguardians.animations.ColoredShader;
import com.enigmadux.craterguardians.animations.ScreenShake;
import com.enigmadux.craterguardians.guilib.ProgressBar;
import com.enigmadux.craterguardians.util.MathOps;
import com.enigmadux.craterguardians.util.SoundLib;
import com.enigmadux.craterguardians.gamelib.World;
import com.enigmadux.craterguardians.gamelib.CraterCollectionElem;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

/** This is what the robots are trying to steal
 *
 * @author Manu Bhat
 * @version BETA
 */
public class Supply extends CraterCollectionElem {
    private static final float HEALTH_BAR_PERCENT = 0.75f;



    //the radius in openGL terms
    private float r;
    //the amount of damage it can take dieing
    private int health;

    //matrices
    //scalar matrix scales it according to the radius
    private final float[] translationScalarMatrix = new float[16];

    //turns it red when damaged;
    private ColoredShader currentShader;
    private ArrayList<QuadTexture> renderables;
    private ProgressBar healthBar;

    private static ScreenShake screenShakeAnim;

    /**  Default constructor
     *
     * @param x the center deltX position in openGL terms
     * @param y the center y position in openGL terms
     * @param r the radius in openGL terms
     * @param health the amount of damage it can take dieing
     * @param myVaoKey the id of this supply with respects to the VaoCollection it's in
     */
    public Supply(Context context, float x, float y, float r, int health, int myVaoKey){
        super(myVaoKey);


        this.deltaX = x;
        this.deltaY = y;
        this.r = r;
        this.width = r/2;
        this.height = r/2;
        this.health = health;

        this.renderables = new ArrayList<>();
        this.healthBar = new ProgressBar(context,x,y + r + 0.0375f * HEALTH_BAR_PERCENT,2 * r * HEALTH_BAR_PERCENT,0.075f * HEALTH_BAR_PERCENT,health, R.drawable.hitpoints_bar);
        this.renderables.addAll(healthBar.getRenderables());

        Matrix.setIdentityM(translationScalarMatrix,0);
        Matrix.translateM(translationScalarMatrix,0,this.deltaX,this.deltaY,0);
        Matrix.scaleM(translationScalarMatrix,0,2*r,2*r,1);
    }


    /** Updates the specified matrix. However, OUTSIDE CLASSES SHOULD NOT CALL THIS METHOD. It's only for use by super classes.
     * Instead, call updateInstanceInfo.
     *
     * @param blankInstanceInfo this is where the instance data should be written too. Rather than creating many arrays,
     *                          we can reuse the same one. Anyways, write all data to appropriate locations in this array,
     *                          which should match the format of the VaoCollection you are using
     * @param uMVPMatrix This is a the model view projection matrix. It performs all outside calculations, make sure to
     *                   not modify this matrix, as this will cause other instances to get modified in unexpected ways.
     *                   Rather use method calls like Matrix.translateM(blankInstanceInfo,0,uMVPMatrix,0,dX,dY,dZ), which
     *                   essentially leaves the uMVPMatrix unchanged, but the translated matrix is dumped into the blankInstanceInfo
     */
    @Override
    public void updateInstanceTransform(float[] blankInstanceInfo, float[] uMVPMatrix) {
        Matrix.multiplyMM(blankInstanceInfo,0,uMVPMatrix,0,this.translationScalarMatrix,0);

    }

    /** Sees if the supply has been killed
     *
     * @return whether it has more than 0 health or not
     */
    private boolean isAlive(){
        if (health > 0){
            return true;
        } else {
            SoundLib.playSupplyDeathSoundEffect();
            return false;
        }
    }

    /** When an enemy attack hits the supply, this method is called as to decrease the health (or increase if damage is negative)
     *
     * @param damage the amount to decrease the health by
     */
    public void damage(int damage){
        this.health -= damage;
        if (this.currentShader != null){
            this.currentShader.cancel();
        }
        this.currentShader = new ColoredShader(this, ColoredShader.DEFAULT_LEN, true);
    }

    /** Sees if a line intersects this hitbox
     *
     * @param x0 p1 deltX
     * @param y0 p1 y
     * @param x1 p2 deltX
     * @param y1 p2 y
     * @return whether or not the line intersects this hitbox.
     */
    public boolean collidesWithLine(float x0,float y0,float x1,float y1){
        return MathOps.segmentIntersectsCircle(this.deltaX,this.deltaY,this.r,x0,y0,x1,y1);
    }


    @Override
    public void update(long dt, World world) {
        //nothing needs to be updated
        if (! isAlive()){
            if (screenShakeAnim== null || screenShakeAnim.isFinished()) {
                screenShakeAnim = new ScreenShake(1f / world.getSupplies().size(), world);
            }
            world.getSupplies().delete(this);
            synchronized (World.animationLock) {
                world.getAnims().add(new DeathAnim(this.getDeltaX(),this.getDeltaY(),2 * this.r,2 * this.r));
            }
        }
        this.healthBar.setValue(this.health);
    }


    public ArrayList<QuadTexture> getRenderables(){
        return renderables;
    }
}
