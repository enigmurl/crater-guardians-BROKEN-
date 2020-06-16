package enigmadux2d.core;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/** The base Activity for all enigmadux apps. Provides basic functions such as pausing and resuming.
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class EnigmaduxActivity extends AppCompatActivity {
    protected EnigmaduxGLSurfaceView enigmaduxGLSurfaceView;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.disableAndroidWidgets();

    }

    @Override
    protected void onPause() {
        super.onPause();
        enigmaduxGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enigmaduxGLSurfaceView.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        disableAndroidWidgets();
    }


    private void disableAndroidWidgets(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
}
