package enjoytoday.com;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;

import com.umeng.analytics.MobclickAgent;

import enjoytoday.com.utils.LightUtils;
import enjoytoday.com.views.MyGLSurfaceView;
import enjoytoday.com.views.MyRender;
import enjoytoday.com.views.VerticalSeekBar;

/**
 * @date 17-3-7.
 * @className ScreenLightActivity
 * @serial 1.0.0
 *
 * android screen light activity.
 */

public class ScreenLightActivity extends Activity {

    private VerticalSeekBar lightSeekBar;
    private MyGLSurfaceView glSurfaceView;
    private SeekBarChangListener onSeekBarChangeListener;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.screen_light);
        initView();
    }



    private void initView(){
        lightSeekBar= (VerticalSeekBar) findViewById(R.id.light_vertical_seek);
        glSurfaceView= (MyGLSurfaceView) findViewById(R.id.gl_surface_view);


        onSeekBarChangeListener=new SeekBarChangListener(this);
        lightSeekBar.setMax(255);
        lightSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);



    }






    @Override
    protected void onResume() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MobclickAgent.onResume(this);

        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        }
        int oldBright=sharedPreferences.getInt("bright",-1);
        //获取当前亮度的位置
        int a = LightUtils.getScreenBrightness(this);
        LogUtils.setDebug("oldBright="+oldBright+",a="+a);
        if (a!=oldBright && oldBright!=-1){
            LogUtils.setDebug("a ! = oldbright.");
            LightUtils.setBrightness(this,oldBright);
            a=oldBright;
        }
        lightSeekBar.setProgress(a);
        super.onResume();
    }




    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        }
        int bright=lightSeekBar.getProgress();
        LogUtils.setDebug("bright="+bright);
        sharedPreferences.edit().putInt("bright",bright).commit();
        super.onPause();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




}
