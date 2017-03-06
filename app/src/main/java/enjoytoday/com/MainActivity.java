package enjoytoday.com;

import android.annotation.TargetApi;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;
import enjoytoday.com.control.LambStateChangeListener;

public class MainActivity extends Activity  implements  LambStateChangeListener{


    protected final int TIMER_CHANGED_TICK=10000;
    private FlashLight flashLight;
    private TextView timerTextView;
    private ImageView controlImageView;
    private Typeface typeface;
    private TimerChangedReceiver timerChangedReceiver;
    private SeekBar seekBar;
    private SeekBarChangListener onSeekBarChangeListener;
    private SharedPreferences sharedPreferences;



    protected Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            if (msg.what==TIMER_CHANGED_TICK){
                long times=System.currentTimeMillis();
                String formatTimes=formatTimes(times);
                timerTextView.setText(formatTimes);
            }
        }
    };


    /**
     * 格式化时间
     * @param times
     * @return
     */
    private String formatTimes(final long times){
        SimpleDateFormat simpleDataFormat=new SimpleDateFormat("HH:mm");
        Date date=new Date(times);
        return  simpleDataFormat.format(date);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViews();


    }





    private void initViews(){
        timerTextView= (TextView) this.findViewById(R.id.timer);
        controlImageView= (ImageView) this.findViewById(R.id.lamb);
        seekBar= (SeekBar) this.findViewById(R.id.light_seek);

        typeface= Typeface.createFromAsset(this.getAssets(),"fonts/timer.ttf");
        timerTextView.setTypeface(typeface);


        timerChangedReceiver=new TimerChangedReceiver(this);
        onSeekBarChangeListener=new SeekBarChangListener(this);

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timerChangedReceiver,intentFilter);
        seekBar.setMax(255);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        if (Build.VERSION.SDK_INT>=21) {
            controlImageView.setBackground(this.getDrawable(R.drawable.lamb_background));
        }else {
            controlImageView.setBackground(this.getResources().getDrawable(R.drawable.lamb_background));
        }

        /**
         * refresh.
         */
        Message.obtain(mHandler,TIMER_CHANGED_TICK).sendToTarget();


        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        flashLight= FlashLightFactory.creatFlashLight();
        flashLight.turnNormalLightOn(this);

        if (flashLight!=null){
            LogUtils.setDebug("flashLight is not null");
            flashLight.setLambStateChangeListener(this);
        }else {
            LogUtils.setDebug("flashLight is null.");
        }




    }


    @Override
    public void onStateChanged(int state, FlashLight flashLight) {

        LogUtils.setDebug("get state:"+state);
        if (state==0){
            if (Build.VERSION.SDK_INT>=21) {
                controlImageView.setBackground(MainActivity.this.getDrawable(R.drawable.lamb_close_background));
            }else {
                controlImageView.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.lamb_close_background));
            }
        }else if (state==1){
            if (Build.VERSION.SDK_INT>=21) {
                controlImageView.setBackground(MainActivity.this.getDrawable(R.drawable.lamb_background));
            }else {
                controlImageView.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.lamb_background));
            }

        }
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
        int a =getScreenBrightness(this);
        LogUtils.setDebug("oldBright="+oldBright+",a="+a);
        if (a!=oldBright && oldBright!=-1){
            LogUtils.setDebug("a ! = oldbright.");
            setBrightness(this,oldBright);
            a=oldBright;
        }
        seekBar.setProgress(a);
        super.onResume();
    }



    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        if (sharedPreferences==null){
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        }
        int bright=seekBar.getProgress();
        LogUtils.setDebug("bright="+bright);
        sharedPreferences.edit().putInt("bright",bright).commit();
        super.onPause();
    }


    /**
     * open/close flashlight
     * @param view
     */
    public void click(View view){
        if (flashLight!=null){
            try {
                flashLight.switchFlashLight(MainActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onDestroy() {
        if (flashLight!=null){
            flashLight.turnLightOff(this);
        }
        if (timerChangedReceiver!=null) {
            unregisterReceiver(timerChangedReceiver);
        }
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }






    /**
     * 获取屏幕的亮度
     *
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }



    /**
     * 设置亮度
     *
     * @param activity
     * @param brightness
     */
    public static void setBrightness(Activity activity, int brightness) {
        // Settings.System.putInt(activity.getContentResolver(),
        // Settings.System.SCREEN_BRIGHTNESS_MODE,
        // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }











}


/**
 * 检测时间变化广播
 */
class TimerChangedReceiver extends BroadcastReceiver{
    private MainActivity activity;

    protected TimerChangedReceiver(MainActivity activity){
        this.activity=activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();

        if (action.equals(Intent.ACTION_TIME_TICK)){
            Message.obtain(activity.mHandler,activity.TIMER_CHANGED_TICK).sendToTarget();
        }

    }

}



/**
 * seekBar 改变监听
 */
class SeekBarChangListener implements SeekBar.OnSeekBarChangeListener{

    private MainActivity activity;

    public SeekBarChangListener(MainActivity activity){
        this.activity=activity;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        /**
         * seekBar change.
         */


        if (progress < 10) {
        } else {
            activity.setBrightness(activity, progress);
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
