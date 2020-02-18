//package com.enigmadux.craterguardians.GUIs.testingGui;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.MotionEvent;
//
//import com.enigmadux.craterguardians.GUILib.GUILayout;
//
//import enigmadux2d.core.quadRendering.QuadRenderer;
//
///** Tests the GUILayout interface
// *
// * @author Manu Bhat
// * @version BETA
// */
//public class TesterLayout implements GUILayout {
//
//
//    private TesterButton button;
//
//    public TesterLayout(Context context){
//        this.button = new TesterButton(0.5f,0.5f,0.4f,0.4f);
//        this.button.loadGLTexture(context);
//        this.button.setVisibility(true);
//    }
//
//    /** Renders sub components
//     *
//     * @param uMVPMatrix the matrix that describes the model view projection transformations
//     * @param renderer the renderer that will be passed on using recursion, unless it's a level 0 (direct components), where it
//     */
//    @Override
//    public void render(float[] uMVPMatrix, QuadRenderer renderer) {
//        this.button.render(uMVPMatrix,renderer);
//    }
//
//    /** Processes touch events
//     *
//     * @param e the motion event that describes the type, and the position
//     * @return true if the event is used, false otherwise
//     */
//    @Override
//    public boolean onTouch(MotionEvent e) {
//        if (this.button.isPressed(e)) {
//            Log.d("Button:","is pressed");
//
//            if (this.button.isDown() && e.getActionMasked() == MotionEvent.ACTION_UP) {
//                this.button.onHardRelease(e);
//            } else if (e.getActionMasked() == MotionEvent.ACTION_DOWN){
//                this.button.onPress(e);
//            }
//        } else if (this.button.isDown()){
//            this.button.onSoftRelease(e);
//        }
//        return false;
//    }
//}
