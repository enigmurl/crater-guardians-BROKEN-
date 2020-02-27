package com.enigmadux.craterguardians.Characters;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.enigmadux.craterguardians.AngleAimers.AngleAimer;
import com.enigmadux.craterguardians.AngleAimers.TriangleAimer;
import com.enigmadux.craterguardians.Animations.EvolveAnimation;
import com.enigmadux.craterguardians.Attacks.KaiserAttack;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.GUILib.ProgressBar;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.shapes.TexturedRect;

/** The main character
 * @see com.enigmadux.craterguardians.BaseCharacter
 * @author Manu Bhat
 * @version BETA
 */
public class Kaiser extends Player {
    //a constant that represents how fast the character is, right now there aren't any particular units which needs to change (see todo)
    private static final float CHARACTER_SPEED = 5f;
    //a constant that represents how many rows the sprite sheet has (how many orientations of rotations
    private static final int NUM_ROTATION_ORIENTATIONS = 1;
    //a constant that represents how many columns the sprite sheet has (how many frames in a single rotation animation)
    private static final int FRAMES_PER_ROTATION = 1;
    //a constant that represents how fast to play the animation in frames per second
    private static final float FPS = 8;

    //a constant that represents the maximum health of KAISER
    private static final int MAXIMUM_HEALTH = 200000;
    //a constant that represents how many attacks kaiser can perform before reloading
    private static final int NUM_ATTACKS  = 5;
    //a constant that represents how long in millis it takes to reload all attacks;
    private static final long MILLIS_PER_RELOAD = 2000;

    //this says how much damage is needed to be dealt, in order to charge an evolution
    private static final int[] NUM_DAMAGE_FOR_EVOLUTION = new int[] {150,400};

    //this says the height of the gun of kaiser
    private static final float GUN_HEIGHT = 0.15f;
    //this says the width of the gun of kaiser
    private static final float GUN_WIDTH = 0.3f;


    //the attack damage at each stage of evolution at level 1
    private static final float[] DAMAGE = new float[] {10,14,50};




    //the level of the this player
    public static int PLAYER_LEVEL = 0;


    //visual is shared by all objects as they all have the same sprite, (all gens are saved here), 3 textures, 1 for each gen
    private static TexturedRect VISUAL_REPRESENTATION = new TexturedRect(-Player.CHARACTER_RADIUS,-Player.CHARACTER_RADIUS,Player.CHARACTER_RADIUS*2,Player.CHARACTER_RADIUS*2,3);
    //visual is shared by all objects as they all have the same sprite, this is the secondary state (gen 1)
    //private static TexturedRect VISUAL_REPRESENTATION_E2  = new TexturedRect(-Player.CHARACTER_RADIUS,-Player.CHARACTER_RADIUS,Player.CHARACTER_RADIUS*2,Player.CHARACTER_RADIUS*2);
    //visual is share by all objects as they all have the same gun, for now same gun for both evolutions, when player is looking to right, the gun
    private static TexturedRect VISUAL_REPRESENTATION_GUN = new TexturedRect(0,-Player.CHARACTER_RADIUS,Kaiser.GUN_WIDTH,Kaiser.GUN_HEIGHT);

    //translates the Character according to delta deltX and delta y. And rotates it based on the offsetAngle
    private float[] translationRotationMatrix = new float[16];

    //parent matrix * translation matrix
    private float[] finalMatrix = new float[16];



    /** Default Constructor
     *
     */
    public Kaiser(){
        super(NUM_ROTATION_ORIENTATIONS,FRAMES_PER_ROTATION,FPS);
    }

    @Override
    public void setFrame(float rotation, int frameNum) {
        if (evolveGen == 0) {
            //don't do anything since texture buffer inst needed
            //VISUAL_REPRESENTATION.loadTextureBuffer(MathOps.getTextureBuffer(rotation, frameNum, framesPerRotation, numRotationOrientations));
        }
        else if (evolveGen == 1) {
            //don't do anything since texture buffer isnt needed
            //VISUAL_REPRESENTATION_E2.loadTextureBuffer(MathOps.getTextureBuffer(rotation, frameNum, framesPerRotation, numRotationOrientations));
        } else if (evolveGen == 2){
            //don't do anything since texture buffer isnt needed
        }
        this.offsetDegrees = MathOps.getOffsetDegrees(rotation,numRotationOrientations);
    }

    /** Creates the angle aimer
     *
     * @return creates the angle aimer which matches the gen 1 attack
     */
    @Override
    protected AngleAimer createAngleAimer() {
        return new TriangleAimer(this.getDeltaX(),this.getDeltaY(), KaiserAttack.SWEEP_ANGLE,0,0.5f);
    }

    /** Creates the attack charge up bar which tells how much damage to add to the current attack
     *
     * @return a progress bar which represents how much of the combo charge up is added
     */
    @Override
    protected ProgressBar createAttackChargeUp() {
        return new ProgressBar(2000 * NUM_ATTACKS,this.getRadius()*2,0.1f);
    }

    /** Loads the texture of the sprite sheet
     *
     * @param context context used to grab the actual image from res
     */
    public static void loadGLTexture(Context context) {
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.kaiser_sprite_sheet_e1,0);
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.kaiser_sprite_sheet_e2,1);
        VISUAL_REPRESENTATION.loadGLTexture(context,R.drawable.kaiser_sprite_sheet_e2,2);

        //VISUAL_REPRESENTATION_E2.loadGLTexture(gl,context,R.drawable.kaiser_sprite_sheet_e2);
        VISUAL_REPRESENTATION_GUN.loadGLTexture(context,R.drawable.kaiser_gun);
    }


    /** This attempts to evolve the character to a superior state
     *
     * @return Whether or not it evolved (true = evolved, false = not)
     */
    @Override
    public boolean attemptEvolve() {
        if (this.evolveGen == 0) {
            this.evolveGen++;
            this.numAttacks = Kaiser.NUM_ATTACKS;
            this.health = getMaxHealth();
            this.evolutionCharge = 0;
            this.attackChargeUp.update(0,0,0);
            this.millisSinceEvolve = 0;
            this.evolveAnimation = new EvolveAnimation(this.getDeltaX(),this.getDeltaY(),EvolveAnimation.STANDARD_DIMENSIONS,EvolveAnimation.STANDARD_DIMENSIONS);
            return true;
        } else if (this.evolveGen == 1){
            this.evolveGen++;
            this.numAttacks = Kaiser.NUM_ATTACKS;
            this.health = getMaxHealth();
            this.evolutionCharge = 0;
            this.attackChargeUp.update(0,0,0);
            this.millisSinceEvolve = 0;
            this.evolveAnimation = new EvolveAnimation(this.getDeltaX(),this.getDeltaY(),EvolveAnimation.STANDARD_DIMENSIONS,EvolveAnimation.STANDARD_DIMENSIONS);
            return true;
        } else if (this.evolveGen == 2){
            this.evolutionCharge = -1;
        }
        return false;
    }

    /** Attacks enemies
     *
     * @param angle the angle at which to attack in radians
     */
    @Override
    public void attack(float angle) {
        super.attack(angle);
        this.attackAngleAimer.hide();

        if (this.numAttacks > 0 && this.attacks.size() == 0) {
            this.numAttacks --;

            int damage = (int) (DAMAGE[this.evolveGen] * (1 + (float) this.attackChargeUp.getCurrentHitPoints()/(NUM_ATTACKS * 1000)));
            if (this.evolveGen == 0)
                this.attacks.add(new KaiserAttack(this.getDeltaX(), this.getDeltaY(), damage, angle, 1f, 250,this));
            else if (this.evolveGen == 1)
                this.attacks.add(new KaiserAttack(this.getDeltaX(), this.getDeltaY(), damage, angle, 1.5f, 250,this));
            else if (this.evolveGen == 2)
                this.attacks.add(new KaiserAttack(this.getDeltaX(), this.getDeltaY(), damage, angle, 1.6f, 300,this));

        }
        //pass for now
    }

    /** Tells how fast this character is
     *
     * @return the speed of this character (no units, see parent class javadoc)
     */
    @Override
    public float getCharacterSpeed() {
        return Kaiser.CHARACTER_SPEED * this.speedMultiplier;
    }

    /** Draws kaiser, and all sub components
     *
     * @param parentMatrix used to translate from model to world space
     */
    @Override
    public void draw(float[] parentMatrix) {
        if (!this.visible) {
            return;
        }

        Matrix.setIdentityM(translationRotationMatrix, 0);
        Matrix.translateM(translationRotationMatrix, 0, this.getDeltaX(), this.getDeltaY(), 0);
        Matrix.rotateM(translationRotationMatrix, 0, this.offsetDegrees, 0, 0, 1);


        Matrix.multiplyMM(finalMatrix, 0, parentMatrix, 0, translationRotationMatrix, 0);
        VISUAL_REPRESENTATION_GUN.draw(finalMatrix);

        VISUAL_REPRESENTATION.setShader(this.shader[0], this.shader[1], this.shader[2], this.shader[3]);

        //if we're in the evolve animations
        if (this.millisSinceEvolve < Player.EVOLVE_MILLIS){
            VISUAL_REPRESENTATION.draw(finalMatrix, this.evolveGen - 1);
        } else {
            VISUAL_REPRESENTATION.draw(finalMatrix, this.evolveGen );
        }

        super.draw(parentMatrix);

    }


    /** This tells the maximum health of any character; what to initialize the health to
     *
     * @return the maximum health of the character
     */
    @Override
    public int getMaxHealth() {
        return Kaiser.MAXIMUM_HEALTH;
    }

    /** This is the amount of attacks it can perform before it needs to reload
     *
     * @return integer amount of attacks it can perform before it needs to reload
     */
    @Override
    public int getNumAttacks() {
        return Kaiser.NUM_ATTACKS;
    }

    /** The time in milliseconds it takes to fully reload
     *
     * @return amount of milliseconds it takes to fully reload
     */
    @Override
    public long getReloadTime() {
        return Kaiser.MILLIS_PER_RELOAD;
    }

    /** When an attack makes contact with an enemy, the evolve ability is charge slighlty, this tells how much to increase it by
     *
     * @param damage  how much damage was dealt to the enemy;
     */
    @Override
    public void gainEvolveCharge(int damage) {
        if (this.evolveGen == 0 || this.evolveGen == 1) {//can't charge it up past max
            this.evolutionCharge += (float) damage / NUM_DAMAGE_FOR_EVOLUTION[this.evolveGen];
        }
        this.attackChargeUp.update(Math.min(this.attackChargeUp.getCurrentHitPoints() + 2000,this.getNumAttacks() * 1000),0,0);

    }

    /** Sets the level of this class
     *
     * @param level the level of this player starting from 0
     */
    @Override
    public void setPlayerLevel(int level){
        Kaiser.PLAYER_LEVEL = level;
    }

    /** Gets the level of this type of player
     *
     * @return the level of this type of player
     */
    @Override
    public int getPlayerLevel() {
        return Kaiser.PLAYER_LEVEL;
    }

    /** Gets a string represnetioatn of this class
     *
     * @return "Kaiser"
     */
    @NonNull
    @Override
    public String toString() {
        return "Kaiser";
    }


    /** Gets the pointer to the kaiser info
     *
     * @return gets the pointer to the kaiser icon
     */
    @Override
    public int getPlayerIcon() {
        return R.drawable.kaiser_info;
    }
}
