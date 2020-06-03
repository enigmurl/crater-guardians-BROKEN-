package com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers;

import android.content.Context;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.guis.inGameScreen.defaultJoystickLayout.DefaultJoyStickLayout;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

public class JoystickHelper extends TutorialPauseHelper {
    private static final long minMillis = 2000;
    private static final long animMillis = 750;
    public JoystickHelper(Context context, CraterBackendThread craterBackendThread) {
        super(context, craterBackendThread, minMillis);
    }

    @Override
    ArrayList<QuadTexture> getScalables(Context context) {
        ArrayList<QuadTexture> scalables = new ArrayList<>();
        scalables.add(new QuadTexture(context, R.drawable.movement_joystick_icon, DefaultJoyStickLayout.MOVEMENT_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.MOVEMENT_JOY_STICK_CENTER[1],DefaultJoyStickLayout.JOY_STICK_IMAGE_WIDTH * LayoutConsts.SCALE_X,DefaultJoyStickLayout.JOY_STICK_IMAGE_WIDTH));
        scalables.add(new QuadTexture(context, R.drawable.attack_joystick_icon, DefaultJoyStickLayout.ATTACK_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.ATTACK_JOY_STICK_CENTER[1],DefaultJoyStickLayout.JOY_STICK_IMAGE_WIDTH * LayoutConsts.SCALE_X,DefaultJoyStickLayout.JOY_STICK_IMAGE_WIDTH));
        scalables.add(new QuadTexture(context, R.drawable.defense_joystick_icon, DefaultJoyStickLayout.SHIELD_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.SHIELD_JOY_STICK_CENTER[1],DefaultJoyStickLayout.JOY_STICK_IMAGE_WIDTH * LayoutConsts.SCALE_X,DefaultJoyStickLayout.JOY_STICK_IMAGE_WIDTH));

        return scalables;
    }

    @Override
    ArrayList<Text> getTexts(Context context) {
        ArrayList<Text> texts = new ArrayList<>();
        texts.add(new Text(DefaultJoyStickLayout.MOVEMENT_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.MOVEMENT_JOY_STICK_CENTER[1] + 0.2f,"Movement Stick",0.1f));
        texts.add(new Text(DefaultJoyStickLayout.ATTACK_JOY_STICK_CENTER[0]-0.1f,
                DefaultJoyStickLayout.ATTACK_JOY_STICK_CENTER[1] + 0.2f,"Attack Stick",0.1f));
        texts.add(new Text(DefaultJoyStickLayout.SHIELD_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.SHIELD_JOY_STICK_CENTER[1] -0.2f,"Defense Stick",0.1f));

        float[] red = new float[] {1,0.25f,0.25f,1};
        Text movementSubText = new Text(DefaultJoyStickLayout.MOVEMENT_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.MOVEMENT_JOY_STICK_CENTER[1]  + 0.12f,"Drag to move in any direction!",0.05f);
        Text attackSubText = new Text(DefaultJoyStickLayout.ATTACK_JOY_STICK_CENTER[0]-0.1f,
                DefaultJoyStickLayout.ATTACK_JOY_STICK_CENTER[1] + 0.12f,"Drag to shoot  attacks!",0.05f);
        Text shieldSubText = new Text(DefaultJoyStickLayout.SHIELD_JOY_STICK_CENTER[0],
                DefaultJoyStickLayout.SHIELD_JOY_STICK_CENTER[1]- 0.28f,"Drag to block enemy attacks!",0.05f);

        float[] white = new float[] {1,1,1,1};
        Text titleText = new Text(0,0.7f,"Joystick Controls",0.2f);
        titleText.setColor(white);
        movementSubText.setColor(red);
        attackSubText.setColor(red);
        shieldSubText.setColor(red);
        texts.add(movementSubText);
        texts.add(attackSubText);
        texts.add(shieldSubText);
        texts.add(titleText);
        return texts;
    }

}
