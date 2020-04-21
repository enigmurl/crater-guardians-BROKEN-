package com.enigmadux.craterguardians.guilib;

import android.content.Context;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.guilib.dynamicText.DynamicText;

import java.util.HashMap;

import enigmadux2d.core.quadRendering.GuiRenderer;

/** A GUILayout, Generally contains all that would be on screen at one particular frame
 *
 * Structure for GUI:
 * a GUILayout contains a "scene"
 * GUIClickable and StaticGUI self explanatory, one can be clicked, other is only renedered
 *
 * I'll have package of packages contains the actual GUIs, such as CharacterSelect Layout,
 * In CraterRenderer We only need 1 reference to big GUI, have a class for each Scene.
 * Possibly just make it an array of scenes, so it's easier to deal with
 *
 * @author Manu Bhat
 * @version BETA
 */
public interface GUILayout extends VisibilitySwitch {

    /** Render Components given the parent matrix, and the renderer, as well as text components
     * @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     * @param textRenderer this renders text efficiently as opposed to rendering quads
     */
    void render(float[] uMVPMatrix, GuiRenderer renderer, DynamicText textRenderer);


    /** On a touch event, this processes it
     *
     * @param e the motion event that describes the type, and the position
     * @return true if the event was used (it affected a sub button), false otherwise
     */
    boolean onTouch(MotionEvent e);


    /** Due to complexities with references, this can't be in the constructor
     *
     * @param context a context object used to load resources
     * @param allLayouts a hash map that links layout names with actual objects
     */
    void loadComponents(Context context, HashMap<String,GUILayout> allLayouts);
}
