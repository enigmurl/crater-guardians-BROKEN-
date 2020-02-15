package com.enigmadux.craterguardians.GUILib;

import enigmadux2d.core.renderEngine.MeshRenderer;

/** Any class that wants to be a basic gui component must implement this interface. It basically handles
 * rendering.
 *
 */
public interface StaticGUI {

    /** Render Components given the parent matrix, and the renderer
     *
     * @param uMVPMatrix the matrix that describes the model view projection transformations
     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
     *                 will actually be rendered
     */
    void render(float[] uMVPMatrix, MeshRenderer renderer);

}
