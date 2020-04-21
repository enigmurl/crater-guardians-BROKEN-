package com.enigmadux.craterguardians.guis.inGameScreen.tutorialHelpers;

import android.content.Context;

import com.enigmadux.craterguardians.CraterBackendThread;
import com.enigmadux.craterguardians.guilib.Text;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadTexture;

public class GameObjectHelper extends TutorialPauseHelper {
    private static final long minMillis = 2000;
    private static final long animMillis = 750;
    public GameObjectHelper(Context context, CraterBackendThread craterBackendThread) {
        super(context, craterBackendThread, minMillis, animMillis);
    }


    @Override
    ArrayList<QuadTexture> getScalables(Context context) {
        ArrayList<QuadTexture> scalables = new ArrayList<>();
        scalables.add(new QuadTexture(context, R.drawable.tutorial_spawner_icon,-0.5f,0,0.5f * LayoutConsts.SCALE_X,0.5f));
        scalables.add(new QuadTexture(context, R.drawable.tutorial_enemy_icon,0,0,0.5f * LayoutConsts.SCALE_X,0.5f));
        scalables.add(new QuadTexture(context, R.drawable.supply_top_view,0.5f,0,0.5f * LayoutConsts.SCALE_X,0.5f));


        return scalables;
    }

    @Override
    ArrayList<Text> getTexts(Context context) {
        ArrayList<Text> texts = new ArrayList<>();
        float[] white = new float[] {1,1,1,1};
        Text titleText = new Text(0,0.7f,"Game Objects",0.2f);
        titleText.setColor(white);

        texts.add(titleText);
        //titles of the objects
        texts.add(new Text(-0.5f,-0.37f,"Spawner",0.1f));
        texts.add(new Text(0,-0.37f,"Enemy ",0.1f));
        texts.add(new Text(0.5f,-0.37f,"Supply",0.1f));

        Text spawnerSubText = new Text(-0.5f,-0.45f,"Aim your attacks at spawners, if all",0.03f);
        Text spawnerSubText2 = new Text(-0.5f,-0.51f,"are killed you win the game!",0.03f);

        Text enemySubText = new Text(0,-0.45f,"Also target enemies to kill",0.03f);
        Text enemySubText2 = new Text(0,-0.51f,"them and charge up your evolve",0.03f);

        Text supplySubText = new Text(0.5f,-0.45f,"Protect these at all costs.",0.03f);
        Text supplySubText2 = new Text(0.5f,-0.52f,"If you or all your supplies",0.03f);
        Text supplySubText3 = new Text(0.5f,-0.59f,"die, the game is lost!",0.03f);

        float[] red = new float[] {1,0.25f,0.25f,1};
        spawnerSubText.setColor(red);
        spawnerSubText2.setColor(red);
        enemySubText.setColor(red);
        enemySubText2.setColor(red);
        supplySubText.setColor(red);
        supplySubText2.setColor(red);
        supplySubText3.setColor(red);


        texts.add(spawnerSubText);
        texts.add(spawnerSubText2);
        texts.add(enemySubText);
        texts.add(enemySubText2);
        texts.add(supplySubText);
        texts.add(supplySubText2);
        texts.add(supplySubText3);
        return texts;
    }
}
