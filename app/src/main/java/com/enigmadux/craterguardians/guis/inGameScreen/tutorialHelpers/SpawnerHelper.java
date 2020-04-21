package com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers;

import android.content.Context;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

public class SpawnerHelper extends TutorialPauseHelper {
    private static final long minMillis = 2000;
    private static final long animMillis = 750;
    public SpawnerHelper(Context context, CraterBackendThread craterBackendThread) {
        super(context, craterBackendThread, minMillis, animMillis);
    }

    @Override
    ArrayList<QuadTexture> getScalables(Context context) {
        ArrayList<QuadTexture> scalables = new ArrayList<>();
        scalables.add(new QuadTexture(context, R.drawable.tutorial_enemy_icon,-0.4f,0,0.5f * LayoutConsts.SCALE_X,0.5f));
        scalables.add(new QuadTexture(context, R.drawable.tutorial_spawner_icon,0.4f,0,0.5f * LayoutConsts.SCALE_X,0.5f));

        return scalables;
    }

    @Override
    ArrayList<Text> getTexts(Context context) {
        ArrayList<Text> texts = new ArrayList<>();
        float[] white = new float[] {1,1,1,1};
        Text titleText = new Text(0,0.7f,"Enemies",0.2f);
        titleText.setColor(white);

        texts.add(titleText);
        texts.add(new Text(-0.4f,-0.37f,"An Enemy",0.1f));
        texts.add(new Text(0.4f,-0.37f,"A Spawner",0.1f));

        Text enemySubtext = new Text(-0.4f,-0.45f,"Aim your attacks at enemies",0.03f);
        Text enemySubtext2 = new Text(-0.4f,-0.51f,"to kill them and charge up ",0.03f);
        Text enemySubtext3 = new Text(-0.4f,-0.57f,"your evolve! ",0.03f);

        Text spawnerSubtext = new Text(0.4f,-0.45f,"Also target spawners, if all",0.03f);
        Text spawnerSubtext2 = new Text(0.4f,-0.51f,"are killed you win the game!",0.03f);
        float[] red = new float[] {1,0.25f,0.25f,1};
        enemySubtext.setColor(red);
        enemySubtext2.setColor(red);
        enemySubtext3.setColor(red);
        spawnerSubtext.setColor(red);
        spawnerSubtext2.setColor(red);

        texts.add(enemySubtext);
        texts.add(enemySubtext2);
        texts.add(enemySubtext3);
        texts.add(spawnerSubtext);
        texts.add(spawnerSubtext2);
        return texts;
    }
}
