package com.enigmadux.craterguardians.guis.inGameScreen.joystickLayouts;

import android.content.Context;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.guis.inGameScreen.defaultJoystickLayout.EvolveButton;
import com.enigmadux.craterguardians.gamelib.World;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

public abstract class JoystickLayout {



    protected ArrayList<QuadTexture> renderables;

    protected JoyStick movementJoyStick;
    protected JoyStick attackJoyStick;
    protected JoyStick defenseJoyStick;
    protected EvolveButton evolveButton;

    private SparseArray<QuadTexture> pointerLocs;

    protected CraterRenderer craterRenderer;

    public JoystickLayout(CraterRenderer craterRenderer){
        this.renderables = new ArrayList<>();
        this.pointerLocs = new SparseArray<>();
        this.craterRenderer = craterRenderer;

    }

    public SparseArray<QuadTexture> getPointerLocs(){
        return this.pointerLocs;
    }


    public ArrayList<QuadTexture> getRenderables(){
        return this.renderables;
    }


    public boolean onTouch(MotionEvent e) {
        this.dispatchTouch(e);
        this.updateJoySticks();

        return false;
    }

    private void updateJoySticks(){
        this.craterRenderer.getWorld().updateJoysticks( movementJoyStick.deltaX/movementJoyStick.maxRadius,movementJoyStick.deltaY/movementJoyStick.maxRadius,
                 defenseJoyStick.deltaX/movementJoyStick.maxRadius,defenseJoyStick.deltaY/movementJoyStick.maxRadius,
                attackJoyStick.deltaX/movementJoyStick.maxRadius,attackJoyStick.deltaY/movementJoyStick.maxRadius);
    }

    public abstract void dispatchTouch(MotionEvent e);


    protected abstract void initComponents(Context context);


    public void init(Context context){
        this.initComponents(context);

        this.renderables.add(this.movementJoyStick.getBackground());
        this.renderables.add(this.attackJoyStick.getBackground());
        this.renderables.add(this.defenseJoyStick.getBackground());
        this.renderables.add(this.movementJoyStick);
        this.renderables.add(this.attackJoyStick);
        this.renderables.add(this.defenseJoyStick);
        this.renderables.add(this.evolveButton);
    }

    public EvolveButton getEvolveButton(){
        return this.evolveButton;
    }

    public void resetJoySticks(){
        this.movementJoyStick.reset();
        this.attackJoyStick.reset();
        this.defenseJoyStick.reset();
        this.evolveButton.reset();

        this.pointerLocs.clear();

        this.updateJoySticks();
    }

    public abstract void update(World world,long dt);
}
