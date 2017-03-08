package enjoytoday.com;

import android.app.Activity;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

import enjoytoday.com.control.FlashLight;
import enjoytoday.com.control.FlashLightFactory;
import enjoytoday.com.control.LambStateChangeListener;
import enjoytoday.com.utils.LightUtils;

public class MainActivity extends Activity  implements  LambStateChangeListener{


    protected final int TIMER_CHANGED_TICK=10000;
    protected final int DELAYED_LOADING=10001;
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
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long times=System.currentTimeMillis();
                        String formatTimes=formatTimes(times);
                        timerTextView.setText(formatTimes);
                    }
                });


            }
        }
    };;


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









        new Thread(){
            @Override
            public void run() {

                timerChangedReceiver=new TimerChangedReceiver(MainActivity.this);
                onSeekBarChangeListener=new SeekBarChangListener(MainActivity.this,100);
                MobclickAgent.setScenarioType(MainActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
                typeface= Typeface.createFromAsset(MainActivity.this.getAssets(),"fonts/timer.ttf");
                timerTextView.setTypeface(typeface);

                flashLight= FlashLightFactory.creatFlashLight();

                IntentFilter intentFilter=new IntentFilter();
                intentFilter.addAction(Intent.ACTION_TIME_TICK);
                registerReceiver(timerChangedReceiver,intentFilter);

                seekBar.setMax(255);
                seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);


                if (Build.VERSION.SDK_INT>=21) {
                    controlImageView.setBackground(MainActivity.this.getDrawable(R.drawable.lamb_background));
                }else {
                    controlImageView.setBackground(MainActivity.this.getResources().getDrawable(R.drawable.lamb_background));
                }

                flashLight.turnNormalLightOn(MainActivity.this);

                if (flashLight!=null){
                    flashLight.setLambStateChangeListener(MainActivity.this);
                }else {
                    LogUtils.setDebug("flashLight is null.");
                }


            }
        }.start();



        /**
         * refresh.
         */
        Message.obtain(mHandler,TIMER_CHANGED_TICK).sendToTarget();
        Message message=Message.obtain(mHandler,DELAYED_LOADING);

        mHandler.sendMessageDelayed(message,1000);







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
        int a = LightUtils.getScreenBrightness(this);
        if (a!=oldBright && oldBright!=-1){
            LightUtils.setBrightness(this,oldBright);
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
        String color="red";
        if (view.getId()==R.id.switcher) {
            if (flashLight != null) {
                try {
                    flashLight.switchFlashLight(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (view.getId()==R.id.red){
            color="red";
        }else if(view.getId()==R.id.green){
            color="green";
        }else if (view.getId()==R.id.white){
            color="white";
        }else if (view.getId()==R.id.blue){
            color="blue";
        }

        if (flashLight!=null){
            flashLight.turnLightOff(this);
        }
        Intent intent=new Intent(this,ScreenLightActivity.class);
        intent.putExtra("color",color);
        startActivity(intent);

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




