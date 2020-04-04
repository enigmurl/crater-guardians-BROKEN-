package com.enigmadux.craterguardians.GUIs.inGameScreen.defaultJoystickLayout;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Animations.ReloadingAmmoBarAnim;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.GUILib.HealthBar;
import com.enigmadux.craterguardians.GUILib.ProgressBar;
import com.enigmadux.craterguardians.GUILib.Tileable;
import com.enigmadux.craterguardians.GUIs.inGameScreen.joystickLayouts.JoyStick;
import com.enigmadux.craterguardians.GUIs.inGameScreen.joystickLayouts.JoystickLayout;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.worlds.World;
import com.enigmadux.craterguardians.players.Player;
import com.enigmadux.craterguardians.util.MathOps;

public class DefaultJoyStickLayout extends JoystickLayout {
    //hypot must be atleast MIN_POWER * radius for it to be dragged out
    private static final float MIN_POWER = 0.3f;

    //the center coordinate of the movement joy stick (openGL coordinates)
    public static final float[] MOVEMENT_JOY_STICK_CENTER = {-0.4f,-0.6f};
    //the center coordinate of attack joy stick (openGL coordinates)
    public static final float[] ATTACK_JOY_STICK_CENTER = {0.7f,-0.4f};
    //the center coordinate of attack joy stick (openGL coordinates)
    public static final float[] SHIELD_JOY_STICK_CENTER = {0.4f,-0.65f};
    //the diameter of the movement and attack joysticks
    public static final float JOY_STICK_IMAGE_WIDTH = 0.2f;
    //the maximum length they can extend too
    private static final float JOY_STICK_MAX_RADIUS = 0.3f;

    //the center coordinate of the evolve button
    public static final float[] EVOLVE_BUTTON_CENTER = {0.6f,ATTACK_JOY_STICK_CENTER[1] + JOY_STICK_IMAGE_WIDTH + JOY_STICK_MAX_RADIUS + 0.1f};

    @Override
    public boolean onTouch(MotionEvent e) {
        return super.onTouch(e);
    }

    //the diameter of the evolve button
    public static final float EVOLVE_BUTTON_WIDTH = 0.4f;


    //center x,y, then w, h
    private static final float[] PROGRESS_BAR_QUAD = new float[] {0,0.8f,1,0.2f};
    private static final float[] AMMO_BAR_QUAD = new float[] {0,0.5f,1,0.1f};


    private ProgressBar playerHealthBar;
    private Tileable numPlayerAttacks;

    private ReloadingAmmoBarAnim currentAmmoBarAnim;
    private boolean handledCurrentReloading = false;


    public DefaultJoyStickLayout(CraterRenderer craterRenderer) {
        super(craterRenderer);

    }

    @Override
    public void dispatchTouch(MotionEvent e) {
        if (e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || e.getActionMasked() == MotionEvent.ACTION_DOWN){
            float x = MathOps.getOpenGLX(e.getX(e.getActionIndex()));
            float y = MathOps.getOpenGLY(e.getY(e.getActionIndex()));

            if (x < 0){
                this.movementJoyStick.onTouch(e);
            } else if (! evolveButton.onTouch(e)){
                float distA = (float) Math.hypot(x - attackJoyStick.getX(),y - attackJoyStick.getY());
                float distD = (float) Math.hypot(x - defenseJoyStick.getX(),y - defenseJoyStick.getY());

                if (distA < distD) {
                    this.attackJoyStick.onTouch(e);
                } else {
                    this.defenseJoyStick.onTouch(e);
                }
            }
        }
        else {
            this.attackJoyStick.onTouch(e);
            this.evolveButton.onTouch(e);
            this.defenseJoyStick.onTouch(e);
            this.movementJoyStick.onTouch(e);
        }
    }

    @Override
    protected void initComponents(Context context) {
        this.attackJoyStick = new JoyStick(context, R.drawable.joystick_icon,ATTACK_JOY_STICK_CENTER[0],ATTACK_JOY_STICK_CENTER[1],
                JOY_STICK_IMAGE_WIDTH/2,JOY_STICK_MAX_RADIUS,JOY_STICK_MAX_RADIUS * MIN_POWER,this);
        this.defenseJoyStick = new JoyStick(context, R.drawable.joystick_icon,SHIELD_JOY_STICK_CENTER[0],SHIELD_JOY_STICK_CENTER[1],
                JOY_STICK_IMAGE_WIDTH/2,JOY_STICK_MAX_RADIUS,JOY_STICK_MAX_RADIUS * MIN_POWER,this);
        this.movementJoyStick = new JoyStick(context, R.drawable.joystick_icon,MOVEMENT_JOY_STICK_CENTER[0],MOVEMENT_JOY_STICK_CENTER[1],
                JOY_STICK_IMAGE_WIDTH/2,JOY_STICK_MAX_RADIUS,0,this);

        this.evolveButton = new EvolveButton(context,R.drawable.evolve_button,
                EVOLVE_BUTTON_CENTER[0],EVOLVE_BUTTON_CENTER[1],EVOLVE_BUTTON_WIDTH,EVOLVE_BUTTON_WIDTH,
                this.craterRenderer,this);

        //max health initialized in update loop
        playerHealthBar = new HealthBar(context,PROGRESS_BAR_QUAD[0],PROGRESS_BAR_QUAD[1],PROGRESS_BAR_QUAD[2],PROGRESS_BAR_QUAD[3],-1);
        this.numPlayerAttacks = new Tileable(context,R.drawable.ammo_visual,AMMO_BAR_QUAD[0],AMMO_BAR_QUAD[1],AMMO_BAR_QUAD[2],AMMO_BAR_QUAD[3],1);
        this.renderables.addAll(playerHealthBar.getRenderables());
        this.renderables.add(this.numPlayerAttacks);
    }

    @Override
    public void update(World world, long dt) {
        //not the best solution but whatever (TODO)
        Player p = world.getPlayer();
        playerHealthBar.setMaxValue(p.getMaxHealth());
        playerHealthBar.setValue(p.getHealth());
        numPlayerAttacks.setMaxAmount(p.getMaxAttacks());
        if (world.getPlayer().getNumLoadedAttacks() == 0 && ! handledCurrentReloading){
            this.currentAmmoBarAnim = new ReloadingAmmoBarAnim(p.getReloadingTime(),numPlayerAttacks);
            this.numPlayerAttacks.setCurrentAmount(p.getMaxAttacks());
            handledCurrentReloading = true;
        } else if (world.getPlayer().getNumLoadedAttacks() > 0){
            handledCurrentReloading = false;
            numPlayerAttacks.setCurrentAmount(p.getNumLoadedAttacks());
        }

        if (world.getPlayer().getEvolveCharge() == 1) {
            evolveButton.setVisibility(true);
            evolveButton.setShader(0,1,0,1);

        } else if (world.getPlayer().getEvolveCharge() < 0){
            evolveButton.setVisibility(false);
        } else {
            evolveButton.setVisibility(true);
            float charge = world.getPlayer().getEvolveCharge();
            evolveButton.setShader(charge,charge,charge,1);
        }


    }
}
