package com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers;

import android.content.Context;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.guilib.HealthBar;
import com.enigmadux.craterguardians.guilib.ProgressBar;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.guilib.Tileable;
import com.enigmadux.craterguardians.guis.inGameScreen.defaultJoystickLayout.DefaultJoyStickLayout;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

public class TelemetryHelper extends TutorialPauseHelper {

    private static final long minMillis = 2000;
    private static final long animMillis = 750;
    public TelemetryHelper(Context context, CraterBackendThread craterBackendThread) {
        super(context, craterBackendThread, minMillis, animMillis);
    }

    @Override
    ArrayList<QuadTexture> getScalables(Context context) {
        QuadTexture evolveButton = new QuadTexture(context, R.drawable.evolve_button, DefaultJoyStickLayout.EVOLVE_BUTTON_CENTER[0],DefaultJoyStickLayout.EVOLVE_BUTTON_CENTER[1],
                DefaultJoyStickLayout.EVOLVE_BUTTON_WIDTH * LayoutConsts.SCALE_X,DefaultJoyStickLayout.EVOLVE_BUTTON_WIDTH);
        evolveButton.setShader(0,1,0,1);

        ProgressBar fakeHealthBar = new HealthBar(context,-0.3f,0.5f,1,0.2f,1);
        Tileable attacks = new Tileable(context,R.drawable.ammo_visual,-0.3f,0f,1,0.2f,10);
        ArrayList<QuadTexture> scalables = new ArrayList<>(fakeHealthBar.getRenderables());
        scalables.add(evolveButton);
        scalables.add(attacks);
        return scalables;
    }

    @Override
    ArrayList<Text> getTexts(Context context) {
        ArrayList<Text> texts = new ArrayList<>();
        float[] white = new float[] {1,1,1,1};
        Text titleText = new Text(0,0.7f,"Other Utils",0.2f);
        titleText.setColor(white);

        texts.add(titleText);
        texts.add(new Text(DefaultJoyStickLayout.EVOLVE_BUTTON_CENTER[0] ,DefaultJoyStickLayout.EVOLVE_BUTTON_CENTER[1] - 0.33f,"Evolve Button",0.1f));
        texts.add(new Text(-0.3f,0.25f,"Health Bar",0.1f));
        texts.add(new Text(-0.3f,-0.25f,"Number of Attacks",0.1f));

        Text evolveText = new Text(DefaultJoyStickLayout.EVOLVE_BUTTON_CENTER[0] ,DefaultJoyStickLayout.EVOLVE_BUTTON_CENTER[1] - 0.4f,"Click to evolve and become stronger!",0.03f);

        float[] red = new float[] {1,0.25f,0.25f,1};
        evolveText.setColor(red);

        texts.add(evolveText);


        return texts;
    }
}