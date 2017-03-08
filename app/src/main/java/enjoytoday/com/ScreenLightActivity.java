package enjoytoday.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import enjoytoday.com.utils.LightUtils;
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
    private SeekBarChangListener onSeekBarChangeListener;
    private SharedPreferences sharedPreferences;

    //ball
    private MySurfaceView mySurfaceView;
    private RatingBar rb;
    private String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.screen_light);
        initView();
    }



    private void initView(){
        Intent intent=getIntent();
        if (intent!=null){
            color=intent.getStringExtra("color");
        }

        lightSeekBar= (VerticalSeekBar) findViewById(R.id.light_vertical_seek);
        mySurfaceView = new MySurfaceView(this,color);
        rb = (RatingBar) findViewById(R.id.RatingBar01);





        mySurfaceView.requestFocus();
        RelativeLayout lla = (RelativeLayout) findViewById(R.id.relative);
        lla.addView(mySurfaceView);
        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                if (rating >= 0 && rating <= 1) {
                    mySurfaceView.openLightNum = 1;
                } else if(rating > 1 && rating <= 2){
                    mySurfaceView.openLightNum = 2;
                }else if(rating > 2 && rating <= 3){
                    mySurfaceView.openLightNum = 3;
                }else if(rating > 3 && rating <= 4){
                    mySurfaceView.openLightNum = 4;
                }else if(rating > 4 && rating <= 5){
                    mySurfaceView.openLightNum = 5;
                }
            }
        });


        onSeekBarChangeListener=new SeekBarChangListener(this,100);
        lightSeekBar.setMax(255);
        lightSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);



    }






    @Override
    protected void onResume() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MobclickAgent.onResume(this);

        if (mySurfaceView!=null){
            mySurfaceView.startAnimation();
        }

        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        }
        int oldBright=sharedPreferences.getInt("brights",-1);
        //获取当前亮度的位置
        int a = LightUtils.getScreenBrightness(this);
        if (a!=oldBright && oldBright!=-1){
            LightUtils.setBrightness(this,oldBright);
            a=oldBright;
        }
        lightSeekBar.setProgress(a);
        super.onResume();
        mySurfaceView.onResume();
    }




    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        }
        int bright=lightSeekBar.getProgress();
        sharedPreferences.edit().putInt("brights",bright).commit();
        super.onPause();
        mySurfaceView.stopAnimation();
        mySurfaceView.onPause();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




}
