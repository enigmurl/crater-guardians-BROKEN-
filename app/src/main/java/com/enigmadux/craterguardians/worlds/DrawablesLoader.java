package com.enigmadux.craterguardians.worlds;

import android.content.Context;
import android.util.Log;

import com.enigmadux.craterguardians.R;

import enigmadux2d.core.quadRendering.QuadTexture;

public class DrawablesLoader {
    private static final int[] RESOURCES = new int[]{
            R.drawable.ammo_bar_icon,
            R.drawable.ammo_visual,
            //R.drawable.baloo_bhaina_texture_atlas This one is loaded separately, so it's the only one not necessary
            R.drawable.battle_start,
            R.drawable.button_background,
            R.drawable.character_select_bottom,
            R.drawable.crater_guardians_label,
            R.drawable.death_animation,
            R.drawable.enemy1_attack_spritesheet,
            R.drawable.enemy1_spawner,
            R.drawable.enemy1_sprite_sheet,
            R.drawable.evolve_button,
            R.drawable.flamethrower_attack_spritesheet,
            R.drawable.gui_background,
            R.drawable.health_bar_icon,
            R.drawable.hitpoints_ammo_bar,
            R.drawable.hitpoints_bar,
            R.drawable.hitpoints_bar_holder,
            R.drawable.home_button,
            R.drawable.info_button,
            R.drawable.joystick_background,
            R.drawable.joystick_icon,
            R.drawable.kaiser_attack_spritesheet,
            R.drawable.kaiser_info,
            R.drawable.kaiser_sprite_sheet_e1,
            R.drawable.kaiser_sprite_sheet_e2,
            R.drawable.kaiser_sprite_sheet_e3,
            R.drawable.kaiser_sprite_sheet_e4,
            R.drawable.kaiser_sprite_sheet_e5,
            R.drawable.layout_background,
            R.drawable.level_background_crater,
            R.drawable.level_button_background,
            //R.drawable.loading_screen, This one too, is loaded separately before so no need
            R.drawable.loss_sign,
            R.drawable.materials_bar_background,
            R.drawable.music_on_off_button,
            R.drawable.pause_button,
            R.drawable.plateau,
            R.drawable.resume_button,
            R.drawable.ryze_info,
            R.drawable.settings_button,
            R.drawable.shield,
            R.drawable.skippy_attack_spritesheet,
            R.drawable.sound_effect_on_off_button,
            R.drawable.spawner_fuel_cell,
            R.drawable.spawner_glow,
            R.drawable.supply_top_view,
            R.drawable.toxic_lake_texture,
            R.drawable.tutorial_enemy_icon,
            R.drawable.tutorial_pause_background,
            R.drawable.tutorial_spawner_icon,
            R.drawable.victory_sign,

    };

    private static int resourceInd;

    //returns whether or not its finished
    public static boolean loadResource(Context context){
        if (resourceInd >= RESOURCES.length) return true;
        Log.d("Drawable:","Loading: " + resourceInd);
        QuadTexture.loadAndroidTexturePointer(context,RESOURCES[resourceInd]);
        resourceInd++;
        return false;
    }

}
