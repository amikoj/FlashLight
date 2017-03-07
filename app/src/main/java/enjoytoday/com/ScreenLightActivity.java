package enjoytoday.com;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import enjoytoday.com.utils.LightUtils;
import enjoytoday.com.views.MyGLSurfaceView;
import enjoytoday.com.views.MyRender;
import enjoytoday.com.views.MySurfaceView;
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

    //ball
    MySurfaceView msv;
    RatingBar rb;

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

        msv = new MySurfaceView(this);

        rb = (RatingBar) findViewById(R.id.RatingBar01);
        msv.requestFocus();
        msv.setFocusableInTouchMode(true);
        LinearLayout lla = (LinearLayout) findViewById(R.id.lla);
        lla.addView(msv);
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                if (rating >= 0 && rating <= 1) {
                    msv.openLightNum = 1;
                } else if(rating > 1 && rating <= 2){
                    msv.openLightNum = 2;
                }else if(rating > 2 && rating <= 3){
                    msv.openLightNum = 3;
                }else if(rating > 3 && rating <= 4){
                    msv.openLightNum = 4;
                }else if(rating > 4 && rating <= 5){
                    msv.openLightNum = 5;
                }
                Toast.makeText(ScreenLightActivity.this, "开启了" + msv.openLightNum + "盏灯", Toast.LENGTH_SHORT).show();
            }
        });



//        glSurfaceView= (MyGLSurfaceView) findViewById(R.id.gl_surface_view);


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
        msv.onResume();
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
        msv.onPause();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




}
