package com.enigmadux.craterguardians.GUILib;

import android.view.MotionEvent;

import enigmadux2d.core.quadRendering.QuadRenderer;

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
public interface GUILayout {


    /** Gets the name of the GUILayout, which should be unique to this layout
     *
     * @return the name (id) of this layout
     */
    String getName();

    /** Render Components given the parent matrix, and the renderer
    *  @param uMVPMatrix the matrix that describes the model view projection transformations
    * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     */
    void render(float[] uMVPMatrix, QuadRenderer renderer);


    /** On a touch event, this processes it
     *
     * @param e the motion event that describes the type, and the position
     * @return true if the event was used (it affected a sub button), false otherwise
     */
    boolean onTouch(MotionEvent e);

}
