package com.enigmadux.craterguardians.GUILib;

import android.content.Context;

import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.GUILib.dynamicText.DynamicText;
import com.enigmadux.craterguardians.R;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.values.STRINGS;

import java.util.ArrayList;


public class MatieralBar implements RenderableCollection{
    private static final float FONT_SIZE = 0.1f;

    private static final float X = -0.6f;
    private static final float Y = 0.9f;
    private static final float W = 0.8f;
    private static final float H = 0.2f;

    private static ArrayList<ImageText> renderables = new ArrayList<>();

    private static ImageText xpIndicator;



    /**
     *  @param context
     */
    public MatieralBar(Context context){
        //init it all the time anyways just in case
        xpIndicator = new ImageText(context, R.drawable.materials_bar_background,X,Y,W/ LayoutConsts.SCALE_X,H,false);
        xpIndicator.setTextDelta(-0.25f,0);
        update();

        renderables.clear();
        renderables.add(xpIndicator);
    }

    @Override
    public ArrayList<ImageText> getRenderables() {
        return renderables;
    }

    public static void update(){
        xpIndicator.updateText(STRINGS.XP_PREFIX + PlayerData.getExperience(),FONT_SIZE);
    }

    public void renderText(DynamicText textRenderer, float[] mvpMatrix){
        for (int i = 0,size = renderables.size();i<size;i++){
            renderables.get(i).renderText(textRenderer,mvpMatrix);
        }
    }

}
